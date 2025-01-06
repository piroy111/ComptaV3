package step00_freeze_transactions.step03_merge_frozen_transactions;

import java.util.Map;

import basicmethods.AMNumberTools;
import basicmethods.BasicPrintMsg;
import step00_freeze_transactions.BKFrozenManager;
import step00_freeze_transactions.objects.frozen_fiscal_year.BKFrozenFiscalYear;
import step00_freeze_transactions.objects.frozen_transaction.BKFrozenTransaction;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step02_load_transactions.objects.entity.BKEntity;
import step03_partitions.abstracts.objects.BKPartitionManager;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.partitions.BKPartitionPerBKEntity;
import step03_partitions.partitions.BKPartitionPerBKEntityAndBKAccount;

public class BKFrozenMerger {

	public BKFrozenMerger(BKFrozenManager _sBKFrozenManager) {
		pBKFrozenManager = _sBKFrozenManager;
	}

	/*
	 * Data
	 */
	private BKFrozenManager pBKFrozenManager;

	/**
	 * 
	 * @param _sDateFYToDo
	 */
	public final void run(int _sDateFYToDo) {
		BasicPrintMsg.displayTitle(this, "Re-allocates transaction TRANSFER to transaction PHYSICAL");
		/*
		 * load various BKPartitions
		 */
		BKPartitionManager lBKPartitionManager = pBKFrozenManager.getpBKLaunchMe().getpBKPartitionManager();
		BKPartitionPerBKEntityAndBKAccount lBKPartitionPerBKEntityAndBKAccount = lBKPartitionManager.getpBKPartitionPerBKEntityAndBKAccount();
		BKPartitionPerBKEntity lBKPartitionPerBKEntity = lBKPartitionManager.getpBKPartitionPerBKEntity();
		Map<String, BKTransactionPartitionDate> lMapKeyToBKTransactionPartitionDate = lBKPartitionPerBKEntityAndBKAccount.getpTreeMapDateToMapKeyToBKTransactionPartitionDate().get(_sDateFYToDo);
		/*
		 * Initiate the merging manager
		 */
		BKAllocatorBKEntity lBKAllocatorBKEntity = new BKAllocatorBKEntity(lBKPartitionPerBKEntity, _sDateFYToDo);
		BKFrozenFiscalYear lBKFrozenFiscalYear = pBKFrozenManager.getpBKFrozenFiscalYearManager().getpOrCreateBKFrozenFiscalYear(_sDateFYToDo);
		int lNbTransferBKBars = 0;
		int lNbTransferBKAssets = 0;
		/*
		 * 
		 */
		for (String lKeyBKEntityBKAccount : lMapKeyToBKTransactionPartitionDate.keySet()) {
			BKTransactionPartitionDate lBKTransactionPartitionDate = lMapKeyToBKTransactionPartitionDate.get(lKeyBKEntityBKAccount);
			/*
			 * load basics
			 */
			BKEntity lBKEntity = lBKPartitionPerBKEntityAndBKAccount.getpBKEntity(lKeyBKEntityBKAccount);
			BKAccount lBKAccount = lBKPartitionPerBKEntityAndBKAccount.getpBKAccount(lKeyBKEntityBKAccount);
			/*
			 * Loop on all possible BKAsset
			 */
			for (BKAsset lBKAsset : BKAssetManager.getpListBKAssetSorted()) {
				/*
				 * We skip the paper metals otherwise we need to handle the case that the quantity is zero, which is complicated
				 */
				if (lBKAsset.getpIsPaper()) {
					continue;
				}
				/*
				 * We skip the metals as the BKBars have been directly allocated, and there is only one BKBar
				 */
				if (BKAssetManager.getpIsPhysicalMetal(lBKAsset)) {
					continue;
				}
				/*
				 * We skip if the quantity is zero
				 */
				double lHolding = lBKTransactionPartitionDate.getpHoldingNoNaNNoNull(lBKAsset);
				if (AMNumberTools.isNaNOrNullOrZero(lHolding)) {
					continue;
				}
				/*
				 * Load data
				 */
				BKEntity lBKEntityToMergeInto = lBKAllocatorBKEntity.getpBKEntityToMerge(lBKAsset, lBKEntity);
				BKFrozenTransaction lBKFrozenTransactionToMergeInto = lBKFrozenFiscalYear.getpOrCreateBKFrozenTransaction(lBKAsset, lBKAccount, null, lBKEntityToMergeInto);
				BKFrozenTransaction lBKFrozenTransactionTransfer = lBKFrozenFiscalYear.getpOrCreateBKFrozenTransaction(lBKAsset, lBKAccount, null, lBKEntity);
				/*
				 * Transfer the holding from the BKEntity TRANSFER to the BKEntity PHYSICAL which is selected for final holding (we chose the one with largest holding)
				 */
				if (!AMNumberTools.isNaNOrNullOrZero(lHolding) && lBKEntityToMergeInto != null && !lBKEntityToMergeInto.equals(lBKEntity)) {
					double lQuantity = lBKFrozenTransactionTransfer.getpQuantity();
					double lPrice = lBKFrozenTransactionTransfer.getpPrice();
					lBKFrozenTransactionToMergeInto.addQuantityPrice(lQuantity, lPrice);
					lBKFrozenTransactionTransfer.addQuantityPrice(-lQuantity, lPrice);
					lNbTransferBKAssets++;
				}
			}
		}
		BasicPrintMsg.display(this, "Number of internal transfers of BKBars= " + lNbTransferBKBars);
		BasicPrintMsg.display(this, "Number of internal transfers of BKAsset which are not physical metals= " + lNbTransferBKAssets);
	}

	/*
	 * Getters & Setters
	 */
	public final BKFrozenManager getpBKFrozenManager() {
		return pBKFrozenManager;
	}























}
