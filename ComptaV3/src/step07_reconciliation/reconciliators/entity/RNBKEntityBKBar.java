package step07_reconciliation.reconciliators.entity;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import basicmethods.BasicPrintMsg;
import staticdata.com.BKCom;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.asset.bar.BKBar;
import step02_load_transactions.objects.entity.BKEntity;
import step03_partitions.abstracts.partitions.BKHoldingAssetDate;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.partitions.BKPartitionPerBKAccount;
import step07_reconciliation.abstracts.BKReconciliatorAbstract;
import step07_reconciliation.abstracts.BKReconciliatorManager;

public class RNBKEntityBKBar extends BKReconciliatorAbstract {

	public RNBKEntityBKBar(BKReconciliatorManager _sBKReconciliatorManager) {
		super(_sBKReconciliatorManager);
	}

	@Override public String getpDetailsOfChecksPerformed() {
		return "If a BKBar is owned by nobody, then BKEntity == null"
				+ "\nIf a BKBar is owned, then BKEntity != null"
				+ "\nIf a BKBar is delivered, then BKEntity == null & Owner == null"
				+ "\nBKBar.getOwner(..) gives the same result as PartitionHolding(..)"
				+ "\nIf owner of BKBar == null, then PartitionHolding(BKBar) == 0";
	}

	@Override public void computeIsPassTest(List<Integer> _sListDateToReconcile) {
		/*
		 * 
		 */
		TreeMap<Integer, Map<String, BKTransactionPartitionDate>> lTreeMapBKAccount = pBKPartitionManager.getpBKPartitionPerBKAccount().getpTreeMapDateToMapKeyToBKTransactionPartitionDate();
		/*
		 * Loop on the metals and then the bars of each metal, and thus for each date
		 */
		for (BKAssetMetal lBKAssetMetal : BKAssetManager.getpListBKAssetMetalSorted()) {
			BasicPrintMsg.display(this, "Reconcile for " + lBKAssetMetal.getpName());
			for (int lDate : _sListDateToReconcile) {
				/*
				 * Initiate : Load the partitions
				 */
				Map<String, BKTransactionPartitionDate> lMapKeyToBKTransactionPartitionDateBKAccount = lTreeMapBKAccount.get(lDate);
				/*
				 * 
				 */
				for (BKBar lBKBar : lBKAssetMetal.getpMapIDToBKBar().values()) {
					/*
					 * Load BKBar
					 */
					BKAccount lBKAccount = lBKBar.getpBKAccountOwner(lDate);
					BKEntity lBKEntity = lBKBar.getpBKEntity(lDate);
					boolean lIsDelivered = lBKBar.getpIsDelivered(lDate);
					boolean lIsBarFuture = lBKBar.getpIsBarFuture();
					/*
					 * Check integrity of BKEntity
					 */
					String lErrorMsg = "";
					if (lBKAccount == null && lBKEntity != null) {
						lErrorMsg = "The BKBar is owned by nobody. Then BKEntity should be null";
					} else if (lIsDelivered && (lBKEntity != null || lBKAccount != null)) {
						lErrorMsg = "The BKBar is delivered. We should have BKentity==null and Owner==null";
					}
					/*
					 * if BKBar is owned by somebody, then the BKBar must be somewhere
					 */
					if (lBKAccount != null && lBKEntity == null) {
						lErrorMsg = "The BKBar is owned by somebody. Thus the BKEntity should not be null. It means that the BKBar is nowhere (it should be in a vault)";
					}
					/*
					 * Check integrity with the holding of BKAccount in COMPTA
					 */
					if (lBKAccount != null) {
						int lHolding = 0;
						if (lMapKeyToBKTransactionPartitionDateBKAccount != null) {
							BKTransactionPartitionDate lBKTransactionPartitionDateBKAccount = lMapKeyToBKTransactionPartitionDateBKAccount.get(BKPartitionPerBKAccount.getKey(lBKAccount));
							if (lBKTransactionPartitionDateBKAccount != null) {
								BKHoldingAssetDate lBKHoldingAssetDate = lBKTransactionPartitionDateBKAccount.getpMapBKAssetToBKHoldingAssetDate().get(lBKAssetMetal);
								if (lBKHoldingAssetDate != null) {
									if (lBKHoldingAssetDate.getpHolding(lBKBar) == 1) {
										lHolding = 1;
									}
								}
							}
						}
						if (lHolding == 0) {
							lErrorMsg = "Compta sees a holding at 0 for the BKAccount who is the owner of the BKBar";
						}
					}
					/*
					 * Check the BKBar is owned by nobody in COMPTA if owner == null
					 */
					if (lBKAccount == null) {
						if (lMapKeyToBKTransactionPartitionDateBKAccount != null) {
							for (BKTransactionPartitionDate lBKTransactionPartitionDate : lMapKeyToBKTransactionPartitionDateBKAccount.values()) {
								BKHoldingAssetDate lBKHoldingAssetDate = lBKTransactionPartitionDate.getpMapBKAssetToBKHoldingAssetDate().get(lBKAssetMetal);
								if (lBKHoldingAssetDate != null) {
									if (lBKHoldingAssetDate.getpHolding(lBKBar) == 1) {
										lErrorMsg = "The owner of the BKBar is null, but the Compta sees a positive holding for a BKAccount via the partitions"
												+ "\nBKaccount with a holding == 1 in Compta= " + lBKTransactionPartitionDate.getpKeyPartition();
										break;
									}
								}
							}
						}
					}
					/*
					 * Error message
					 */
					if (!lErrorMsg.equals("")) {
						lErrorMsg += "\nDate= " + lDate
								+ "\nBKBar= " + lBKBar
								+ "\nOwner= " + lBKAccount
								+ "\nBKEntity= " + lBKEntity
								+ "\nIs delivered?= " + lIsDelivered
								+ "\nIs Bar FUTURE?= " + lIsBarFuture;
						BKCom.error(lErrorMsg);
					}
				}
			}
		}
	}

}
