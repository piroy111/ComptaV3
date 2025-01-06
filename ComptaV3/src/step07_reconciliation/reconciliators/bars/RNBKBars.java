package step07_reconciliation.reconciliators.bars;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import staticdata.com.BKCom;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.asset.bar.BKBar;
import step03_partitions.abstracts.partitions.BKHoldingAssetDate;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step07_reconciliation.abstracts.BKReconciliatorAbstract;
import step07_reconciliation.abstracts.BKReconciliatorManager;

public class RNBKBars extends BKReconciliatorAbstract {

	public RNBKBars(BKReconciliatorManager _sBKReconciliatorManager) {
		super(_sBKReconciliatorManager);
	}

	@Override public String getpDetailsOfChecksPerformed() {
		return "Nobody is short a BKBar"
				+ "\na BKBar is held by maximum one person at a time";
	}

	public void computeIsPassTest(List<Integer> _sListDateToReconcile) {
		for (int lDate : _sListDateToReconcile) {
			TreeMap<Integer, Map<String, BKTransactionPartitionDate>> lTreeMapDateToMapKeyToBKTransactionPartitionDate = pBKPartitionManager
					.getpBKPartitionPerBKAccount().getpTreeMapDateToMapKeyToBKTransactionPartitionDate();
			Map<String, BKTransactionPartitionDate> lMapKeyToBKTransactionPartitionDate = lTreeMapDateToMapKeyToBKTransactionPartitionDate
					.get(lDate);
			if (lMapKeyToBKTransactionPartitionDate == null) {
				continue;
			}
			Map<BKBar, BKAccount> lMapBKBarToBKAccount = new HashMap<>();
			/*
			 * Loop on the BKAccount to assign the owner of the BKBars (the one with a holding equal to 1)
			 */
			for (String lKeyBKAccount : lMapKeyToBKTransactionPartitionDate.keySet()) {
				BKTransactionPartitionDate lBKTransactionPartitionDate = lMapKeyToBKTransactionPartitionDate.get(lKeyBKAccount);
				BKAccount lBKAccount = BKAccountManager.getpAndCheckBKAccount(lKeyBKAccount, this);				
				/*
				 * Loop on the physical metals
				 */
				for (BKAssetMetal lBKAssetMetal : BKAssetManager.getpListBKAssetMetalSorted()) {
					BKHoldingAssetDate lBKHoldingAssetDate = lBKTransactionPartitionDate.getpMapBKAssetToBKHoldingAssetDate().get(lBKAssetMetal);
					if (lBKHoldingAssetDate != null) {
						for (BKBar lBKBar : lBKHoldingAssetDate.getpTreeMapBKBarToHolding().keySet()) {
							Integer lHolding = lBKHoldingAssetDate.getpTreeMapBKBarToHolding().get(lBKBar);
							/*
							 * Check the error of multiple assignments
							 */
							if (lHolding > 1 || lHolding < -1) {
								BKCom.error("A BKBar has been assigned more than one time to the same account"
										+ "\nBKBar= " + lBKBar
										+ "\nDate= " + lDate
										+ "\nHolding= " + lHolding
										+ "\nBKAccount= '" + lBKAccount.getpEmail() + "'"
										+ "\nUse 'BKDebugAllBKTransactions' in order to debug");
							}
							/*
							 * Check the holding is >= 0
							 */
							else if (lHolding == -1) {
								BKCom.error("An account is short of a BKBar at the end of the day. This should not happen, a loan should have been put in place in order to prevent that from happening."
										+ "\nBKBar= " + lBKBar
										+ "\nDate= " + lDate
										+ "\nHolding= " + lHolding
										+ "\nBKAccount= '" + lBKAccount.getpEmail() + "'"
										+ "\nUse 'BKDebugAllBKTransactions' in order to debug");
							}
							/*
							 * Case the BKBar is held by the BKAccount
							 */
							else if (lHolding == 1) {
								BKAccount lBKAccountAlreadyStoredToday = lMapBKBarToBKAccount.get(lBKBar);
								/*
								 * Check the error of multiple owners
								 */
								if (lBKAccountAlreadyStoredToday != null) {
									BKCom.error("A BKBar has been assigned to several accounts for the same day"
											+ "\nBKBar= " + lBKBar
											+ "\nDate= " + lDate
											+ "\nHolding= " + lHolding
											+ "\nBKAccount= '" + lBKAccount.getpKey() + "'"
											+ "\nother BKAccount which holds the BKBar simultaneously= '" + lBKAccountAlreadyStoredToday.getpKey() + "'"
											+ "\nUse 'BKDebugAllBKTransactions' in order to debug");
								} 
								/*
								 * Store the owner for further checks of multiple ownership
								 */
								else {
									lMapBKBarToBKAccount.put(lBKBar, lBKAccount);
								}
							}
						}
					}
				}
			}
		}
	}



	
}
