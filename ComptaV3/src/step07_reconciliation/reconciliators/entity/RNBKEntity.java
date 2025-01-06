package step07_reconciliation.reconciliators.entity;

import java.util.List;
import java.util.TreeMap;

import basicmethods.BasicPrintMsg;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step02_load_transactions.objects.entity.BKEntity;
import step02_load_transactions.objects.entity.BKEntityManager;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.partitions.BKPartitionPerBKAccount;
import step03_partitions.partitions.BKPartitionPerBKEntity;
import step07_reconciliation.abstracts.BKReconciliatorAbstract;
import step07_reconciliation.abstracts.BKReconciliatorManager;

public class RNBKEntity extends BKReconciliatorAbstract {

	public RNBKEntity(BKReconciliatorManager _sBKReconciliatorManager) {
		super(_sBKReconciliatorManager);
	}

	@Override public String getpDetailsOfChecksPerformed() {
		return "For each BKASset, we have : Assets in all physical BKEntities == Assets of Bunker + Assets of clients";
	}

	@Override public void computeIsPassTest(List<Integer> _sListDateToReconcile) {
		/*
		 * Initiate
		 */
		int lDateCompta = BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS();
		/*
		 * Initiate Bunker
		 */
		String lKeyBunker = BKPartitionPerBKAccount.getKey(BKAccountManager.getpBKAccountBunker());
		TreeMap<Integer, BKTransactionPartitionDate> lTreeMapDateToBKTransactionPartitionDateBunker = pBKPartitionManager
				.getpBKPartitionPerBKAccount().getpMapKeyToTreeMapDateToBKTransactionPartitionDate().get(lKeyBunker);
		BKTransactionPartitionDate lBKTransactionPartitionDateBunker = null;
		if (lTreeMapDateToBKTransactionPartitionDateBunker != null) {
			lBKTransactionPartitionDateBunker = lTreeMapDateToBKTransactionPartitionDateBunker.get(lDateCompta);
		}
		/*
		 * Initiate BKEntity
		 */
		List<BKEntity> lListBKEntityPhysical = BKEntityManager.getListBKEntityPhysical();
		BKPartitionPerBKEntity lBKPartitionPerBKEntity = pBKPartitionManager.getpBKPartitionPerBKEntity();
		/*
		 * Initiate clients
		 */
		BKPartitionPerBKAccount lBKPartitionPerBKAccount = pBKPartitionManager.getpBKPartitionPerBKAccount();
		/*
		 * Check all assets: Bunker + clients = holding in physical entity
		 */
		String lErrorMsg = "";
		for (BKAsset lBKAsset : BKAssetManager.getpListBKAssetSorted()) {
			/*
			 * Assets of Bunker
			 */
			double lHoldingBunker = 0.;
			if (lBKTransactionPartitionDateBunker != null) {
				lHoldingBunker = lBKTransactionPartitionDateBunker.getpHoldingNoNaNNoNull(lBKAsset);
			}
			/*
			 * Assets of all physical entities
			 */
			double lHoldingBKEntity = 0.;
			for (BKEntity lBKEntity : lListBKEntityPhysical) {
				String lKey = BKPartitionPerBKEntity.getKey(lBKEntity);
				lHoldingBKEntity += lBKPartitionPerBKEntity.getpHoldingBKAssetNotNull(lKey, lDateCompta, lBKAsset);
			}
			/*
			 * Assets of all clients
			 */
			double lHoldingClients = 0.;
			for (BKAccount lBKAccount : BKAccountManager.getpListBKAccountExceptBunker()) {
				String lKey = BKPartitionPerBKAccount.getKey(lBKAccount);
				lHoldingClients += lBKPartitionPerBKAccount.getpHoldingBKAssetNotNull(lKey, lDateCompta, lBKAsset);
			}
			/*
			 * Check
			 */
			double lPriceUSD = lBKAsset.getpPriceUSD(lDateCompta);
			double lError = Math.abs(lHoldingBKEntity-lHoldingBunker-lHoldingClients);
			double lErrorUSD = lError * lPriceUSD;
			if (lErrorUSD > BKStaticConst.getERROR_ACCEPTABLE_RECONCILIATION_ASSETS()) {
				lErrorMsg += "\n"
						+ "\nBKAsset= " + lBKAsset.getpName()
						+ "\nHolding Bunker= " + lHoldingBunker
						+ "\nHolding Clients= " + lHoldingClients
						+ "\nHolding in all physical BKEntities= " + lHoldingBKEntity
						+ "\nPhysical BKEntities - Bunker - Clients= " + (lHoldingBKEntity - lHoldingBunker - lHoldingClients)
						+ "\nError in US$= " + lErrorUSD;
			} else {
				String lCom = "All good: BKAsset= " + lBKAsset.getpName()
						+ "; Physical BKEntities - Bunker - Clients= " + (lHoldingBKEntity - lHoldingBunker - lHoldingClients);
				BasicPrintMsg.display(this, lCom);
			}
		}
		/*
		 * Exit on error
		 */
		if (!lErrorMsg.equals("")) {
			lErrorMsg = "We should have: Asset(all physical BKEntities)= Asset(Bunker) + Asset(Clients)"
					+ "\nIf we don't have this equality, this means that some transactions have been booked outside of the physical entities"
					+ lErrorMsg;
			BKCom.error(lErrorMsg);
		}
	}

}
