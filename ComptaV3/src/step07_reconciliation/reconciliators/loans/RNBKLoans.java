package step07_reconciliation.reconciliators.loans;

import java.util.List;
import java.util.TreeMap;

import basicmethods.AMNumberTools;
import basicmethods.BasicDateInt;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.asset.assetpaperorphysical.BKAssetPhysical;
import step01_objects_from_conf_files.asset.bar.BKBar;
import step03_partitions.abstracts.partitions.BKHoldingAssetDate;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step07_reconciliation.abstracts.BKReconciliatorAbstract;
import step07_reconciliation.abstracts.BKReconciliatorManager;

public class RNBKLoans extends BKReconciliatorAbstract {

	public RNBKLoans(BKReconciliatorManager _sBKReconciliatorManager) {
		super(_sBKReconciliatorManager);
	}

	@Override public String getpDetailsOfChecksPerformed() {
		return "Loans made to PRoy are not negative"
				+ "\nLoans of PRoy cannot > 0 if Bunker has a BKBar";
	}

	public void computeIsPassTest(List<Integer> _sListDateToReconcile) {
		/*
		 * Initiate
		 */
		String lKeyBKAccountPRoy = BKAccountManager.getpBKAccountPRoy().getpKey();
		String lKeyBKAccountBunker = BKAccountManager.getpBKAccountBunker().getpKey();
		TreeMap<Integer, BKTransactionPartitionDate> lTreeMapDateToBKTransactionPartitionDateProy = pBKPartitionManager.getpBKPartitionPerBKAccount().getpMapKeyToTreeMapDateToBKTransactionPartitionDate().get(lKeyBKAccountPRoy);
		TreeMap<Integer, BKTransactionPartitionDate> lTreeMapDateToBKTransactionPartitionDateBunker = pBKPartitionManager.getpBKPartitionPerBKAccount().getpMapKeyToTreeMapDateToBKTransactionPartitionDate().get(lKeyBKAccountBunker);
		int lDateStopCheckingReimbursment = BasicDateInt.getmFirstDayOfMonth(BKStaticConst.getDATE_PROY_BECOMES_CLIENT());
		/*
		 * 
		 */
		for (BKAssetMetal lBKAssetMetal : BKAssetManager.getpListBKAssetMetalSorted()) {
			BKAssetPhysical lBKAssetLoan = lBKAssetMetal.getpBKAssetLoan();
			for (int lDate : _sListDateToReconcile) {
				if (!lTreeMapDateToBKTransactionPartitionDateProy.containsKey(lDate)) {
					continue;
				}
				BKTransactionPartitionDate lBKTransactionPartitionDateProy = lTreeMapDateToBKTransactionPartitionDateProy.get(lDate);
				BKHoldingAssetDate lBKHoldingAssetDatePRoy = lBKTransactionPartitionDateProy.getpMapBKAssetToBKHoldingAssetDate().get(lBKAssetLoan);
				if (lBKHoldingAssetDatePRoy != null) {
					double lHoldingLoanPRoy = lBKHoldingAssetDatePRoy.getpHolding();
					/*
					 * Check PRoy cannot have negative loan
					 */
					if (lHoldingLoanPRoy < -BKStaticConst.getERROR_ACCEPTABLE_LOAN_BACK_TO_PROY() * 1.0001) {
						BKCom.error("PRoy should not have a negative loan"
								+ "\nBKAssetLoan= " + lBKAssetLoan.getpName()
								+ "\nDate= " + lDate
								+ "\nQuantity of loan of PRoy= " + lHoldingLoanPRoy);
					}
					/*
					 * Check Bunker reimbursed automatically the loans
					 */
					else if (lDate < lDateStopCheckingReimbursment && AMNumberTools.isPositiveStrict(lHoldingLoanPRoy)) {
						BKTransactionPartitionDate lBKTransactionPartitionDateBunker = lTreeMapDateToBKTransactionPartitionDateBunker.get(lDate);
						if (lBKTransactionPartitionDateBunker != null) {
							BKHoldingAssetDate lBKHoldingAssetDateBunker = lBKTransactionPartitionDateBunker.getpMapBKAssetToBKHoldingAssetDate().get(lBKAssetMetal);
							if (lBKHoldingAssetDateBunker != null) {
								for (BKBar lBKBar : lBKHoldingAssetDateBunker.getpTreeMapBKBarToHolding().keySet()) {
									int lHoldingBunker = lBKHoldingAssetDateBunker.getpTreeMapBKBarToHolding().get(lBKBar);
									if (lBKBar.getpBKEntity(lDate) != null
											&& AMNumberTools.isSmallerOrEqual(lHoldingBunker * lBKBar.getpWeightOz(), lHoldingLoanPRoy)) {
										BKCom.error("PRoy holds a loan and Bunker holds at least one BKBar. Bunker should have reimbursed PRoy's loan automatically"
												+ "\nDate= " + lDate
												+ "\nBKAssetLoan= " + lBKAssetLoan.getpName()
												+ "\nBKAssetMetal= " + lBKAssetMetal.getpName()
												+ "\nQuantity of loan detained by PRoy= " + lHoldingLoanPRoy
												+ "\nBKBar detained by Bunker for the same loan= " + lBKBar);
									}
								}
							}
						}
					}
				}
			}
		}
	}

}
