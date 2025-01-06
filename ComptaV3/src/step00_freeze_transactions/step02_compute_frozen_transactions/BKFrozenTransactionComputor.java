package step00_freeze_transactions.step02_compute_frozen_transactions;

import java.util.Map;

import basicmethods.AMNumberTools;
import basicmethods.BasicPrintMsg;
import step00_freeze_transactions.BKFrozenManager;
import step00_freeze_transactions.objects.frozen_fiscal_year.BKFrozenFiscalYear;
import step00_freeze_transactions.objects.frozen_transaction.BKFrozenTransaction;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.asset.bar.BKBar;
import step02_load_transactions.objects.entity.BKEntity;
import step02_load_transactions.objects.entity.BKEntityManager;
import step03_partitions.abstracts.partitions.BKHoldingAssetDate;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.partitions.BKPartitionPerBKAccount;
import step03_partitions.partitions.BKPartitionPerBKEntity;
import step03_partitions.partitions.BKPartitionPerBKEntityAndBKAccount;

public class BKFrozenTransactionComputor {

	public BKFrozenTransactionComputor(BKFrozenManager _sBKFrozenManager) {
		pBKFrozenManager = _sBKFrozenManager;
	}

	/*
	 * Data
	 */
	private BKFrozenManager pBKFrozenManager;

	/**
	 * Launch the computation of the BKEntity/BKAccount partition + compute all the BKFrozenTransaction for the missing report
	 */
	public final void run(int _sDateFYToDo) {
		/*
		 * Load partition at the date of the frozen report to do
		 */
		BKPartitionPerBKEntityAndBKAccount lBKPartitionPerBKEntityAndBKAccount = pBKFrozenManager.getpBKLaunchMe().getpBKPartitionManager().getpBKPartitionPerBKEntityAndBKAccount();
		BKPartitionPerBKEntity lBKPartitionPerBKEntity = pBKFrozenManager.getpBKLaunchMe().getpBKPartitionManager().getpBKPartitionPerBKEntity();
		BKPartitionPerBKAccount lBKPartitionPerBKAccount = pBKFrozenManager.getpBKLaunchMe().getpBKPartitionManager().getpBKPartitionPerBKAccount();
		/*
		 * Date to do
		 */
		BKFrozenFiscalYear lBKFrozenFiscalYear = pBKFrozenManager.getpBKFrozenFiscalYearManager().getpOrCreateBKFrozenFiscalYear(_sDateFYToDo);
		/*
		 * Map of BKPartition
		 */
		Map<String, BKTransactionPartitionDate> lMapKeyToBKTransactionPartitionDate = lBKPartitionPerBKEntityAndBKAccount.getpTreeMapDateToMapKeyToBKTransactionPartitionDate().get(_sDateFYToDo);
		Map<String, BKTransactionPartitionDate> lMapKeyToBKTransactionPartitionDateBKEntity = lBKPartitionPerBKEntity.getpTreeMapDateToMapKeyToBKTransactionPartitionDate().get(_sDateFYToDo);
		Map<String, BKTransactionPartitionDate> lMapKeyToBKTransactionPartitionDateBKAccount = lBKPartitionPerBKAccount.getpTreeMapDateToMapKeyToBKTransactionPartitionDate().get(_sDateFYToDo);
		/*
		 * Special case of Metals and BKBars --> We allocate the bar to the owner in the entity where the bar is physically
		 */
		BasicPrintMsg.displayTitle(this, "Create BKFrozenTransaction - special case of BKBars");
		int lNbBarsAllocated = 0;
		for (BKEntity lBKEntity : BKEntityManager.getListBKEntityPhysical()) {
			for (BKAccount lBKAccount : BKAccountManager.getpListBKAccount()) {
				/*
				 * Get partition
				 */
				String lKeyBKEntity = BKPartitionPerBKEntity.getKey(lBKEntity);
				String lKeyBKAccount = BKPartitionPerBKAccount.getKey(lBKAccount);
				BKTransactionPartitionDate lBKTransactionPartitionDateBKEntity = lMapKeyToBKTransactionPartitionDateBKEntity.get(lKeyBKEntity);
				BKTransactionPartitionDate lBKTransactionPartitionDateBKAccount = lMapKeyToBKTransactionPartitionDateBKAccount.get(lKeyBKAccount);
				/*
				 * Loop over the BKAssetMetal
				 */
				if (lBKTransactionPartitionDateBKEntity != null && lBKTransactionPartitionDateBKAccount != null) {
					for (BKAssetMetal lBKAssetMetal : BKAssetManager.getpListBKAssetMetalSorted()) {
						BKHoldingAssetDate lBKHoldingAssetDateBKEntity = lBKTransactionPartitionDateBKEntity.getpMapBKAssetToBKHoldingAssetDate().get(lBKAssetMetal);
						BKHoldingAssetDate lBKHoldingAssetDateBKAccount = lBKTransactionPartitionDateBKAccount.getpMapBKAssetToBKHoldingAssetDate().get(lBKAssetMetal);
						/*
						 * Loop on all the bars associated with the lBKAssetMetal
						 */
						if (lBKHoldingAssetDateBKEntity != null && lBKHoldingAssetDateBKAccount != null) {
							for (BKBar lBKBar : lBKHoldingAssetDateBKEntity.getpTreeMapBKBarToHolding().keySet()) {
								if (lBKHoldingAssetDateBKAccount.getpTreeMapBKBarToHolding().containsKey(lBKBar)) {
									/*
									 * We allocate the lBKBar if it is owned only by lBKAccount and if the lBKBar is in the lBKEntity
									 */
									int lHoldingBKEntity = lBKHoldingAssetDateBKEntity.getpTreeMapBKBarToHolding().get(lBKBar);
									int lHoldingBKAccount = lBKHoldingAssetDateBKAccount.getpTreeMapBKBarToHolding().get(lBKBar);
									if (lHoldingBKEntity == 1 && lHoldingBKAccount == 1) {
										/*
										 * Create a BKFrozenTransaction to give the lBKBar to lBKAccount in the lBKEntity
										 */
										BKFrozenTransaction lBKFrozenTransaction = lBKFrozenFiscalYear.getpOrCreateBKFrozenTransaction(lBKAssetMetal, 
												lBKAccount.getpEmail(), lBKAccount.getpBKAssetCurrency().getpName(), 
												lBKBar.getpID(), 
												lBKEntity.getpName(), lBKEntity.getpTypeEntity().toString());
										lBKFrozenTransaction.addQuantityPrice(lBKBar.getpWeightOz(), Double.NaN);
										lNbBarsAllocated++;
									}
								}
							}
						}					
					}			
				}
			}
		}
		BasicPrintMsg.display(this, "Number of BKBars allocated= " + lNbBarsAllocated);
		/*
		 * Communication
		 */
		BasicPrintMsg.displayTitle(this, "Create BKFrozenTransaction - case of non metal assets");
		BasicPrintMsg.display(this, "We allocated the amount of ");
		int lNbBKFrozenCreated = 0;
		/*
		 * Loop on all partition BKEntity/BKAccount
		 */
		for (String lKey : lMapKeyToBKTransactionPartitionDate.keySet()) {
			BKTransactionPartitionDate lBKTransactionPartitionDate = lMapKeyToBKTransactionPartitionDate.get(lKey);
			/*
			 * load basics
			 */
			BKEntity lBKEntity = lBKPartitionPerBKEntityAndBKAccount.getpBKEntity(lKey);
			BKAccount lBKAccount = lBKPartitionPerBKEntityAndBKAccount.getpBKAccount(lKey);
			String lBKAccountEmail = lBKAccount.getpEmail();
			String lBKAccountCurrency = lBKAccount.getpBKAssetCurrency().getpName();
			String lBKEntityName = lBKEntity.getpName();
			String lBKEntityType = lBKEntity.getpTypeEntity().toString();
			/*
			 * We loop on all the BKAsset non metal (with no BKBars)
			 */
			for (BKAsset lBKAsset : BKAssetManager.getpListBKAssetSorted()) {
				if (BKAssetManager.getpIsPhysicalMetal(lBKAsset)) {
					continue;
				}
				double lQuantity = lBKTransactionPartitionDate.getpHoldingNoNaNNoNull(lBKAsset);
				if (AMNumberTools.isNaNOrNullOrZero(lQuantity) && !lBKAsset.getpIsPaper()) {
					continue;
				}
				BKFrozenTransaction lBKFrozenTransaction = lBKFrozenFiscalYear.getpOrCreateBKFrozenTransaction(lBKAsset, lBKAccountEmail, lBKAccountCurrency, null, lBKEntityName, lBKEntityType);
				/*
				 * Special case of paper -> we need to input a price from the NAV + case the quantity is null -> we need to add to USD
				 */
				if (lBKAsset.getpIsPaper()) {
					/*
					 * Case quantity is zero -> we add the NAV to the balance in US$
					 */
					if (AMNumberTools.isNaNOrNullOrZero(lQuantity)) {
						BKAsset lBKAssetUSD = BKAssetManager.getpBKAssetCurrencyReference();
						lQuantity = -lBKTransactionPartitionDate.getpMapBKAssetToBKHoldingAssetDate().get(lBKAsset).getpNNNUSDExec();						
						lBKFrozenTransaction = lBKFrozenFiscalYear.getpOrCreateBKFrozenTransaction(lBKAssetUSD, lBKAccountEmail, lBKAccountCurrency, null, lBKEntityName, lBKEntityType);
						lBKFrozenTransaction.addQuantityPrice(lQuantity, Double.NaN);
						lNbBKFrozenCreated++;
					} 
					/*
					 * Case quantity is not zero -> we add the asset with the price = NAV / quantity
					 */
					else {
						double lPrice = lBKTransactionPartitionDate.getpMapBKAssetToBKHoldingAssetDate().get(lBKAsset).getpNNNUSDExec() / lQuantity;
						lBKFrozenTransaction = lBKFrozenFiscalYear.getpOrCreateBKFrozenTransaction(lBKAsset, lBKAccountEmail, lBKAccountCurrency, null, lBKEntityName, lBKEntityType);
						lBKFrozenTransaction.addQuantityPrice(lQuantity, lPrice);
					}
				}
				/*
				 * Usual case -> we add the quantity with a price NaN
				 */
				else {
					lBKFrozenTransaction = lBKFrozenFiscalYear.getpOrCreateBKFrozenTransaction(lBKAsset, lBKAccountEmail, lBKAccountCurrency, null, lBKEntityName, lBKEntityType);
					lBKFrozenTransaction.addQuantityPrice(lQuantity, Double.NaN);
					lNbBKFrozenCreated++;
				}
			}
		}
		BasicPrintMsg.display(this, "Nb BKFrozenTransaction created (non metals)= " + lNbBKFrozenCreated);
	}




}
