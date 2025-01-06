package step05_transactions_computed_by_compta.computors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import basicmethods.BasicPrintMsg;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.asset.assetpaperorphysical.BKAssetPhysical;
import step01_objects_from_conf_files.asset.bar.BKBar;
import step01_objects_from_conf_files.income.BKIncome;
import step01_objects_from_conf_files.income.BKIncomeManager;
import step03_partitions.abstracts.objects.BKPartitionManager;
import step03_partitions.abstracts.partitions.BKHoldingAssetDate;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.partitions.BKPartitionPerBKAccount;
import step05_transactions_computed_by_compta.abstracts.BKComptaComputorAbstract;
import step05_transactions_computed_by_compta.abstracts.BKComptaComputorManager;

public class BKComptaComputorLoansOfBarsFromProy extends BKComptaComputorAbstract {

	public BKComptaComputorLoansOfBarsFromProy(BKComptaComputorManager _sBKComptaComputorManager) {
		super(_sBKComptaComputorManager);
	}

	/*
	 * Data
	 */
	private TreeMap<Integer, BKTransactionPartitionDate> pTreeMapDateToBKTransactionPartitionDateBunker;
	private TreeMap<Integer, BKTransactionPartitionDate> pTreeMapDateToBKTransactionPartitionDatePRoy;
	private BKPartitionManager pBKPartitionManager;
	private Map<BKAssetMetal, BKIncome> pMapBKAssetMetalToBKIncome;
	
	@Override public void initiateGlobal() {
		BasicPrintMsg.display(this, "PRoy lends its bars to Bunker if Bunker has sold them");
	}

	@Override public void initiateMonth() {
		/*
		 * Load
		 */
		pBKPartitionManager = pBKLaunchMe.getpBKPartitionManager();
		BKPartitionPerBKAccount lBKPartitionPerAccount = pBKPartitionManager.getpBKPartitionPerBKAccount();
		String lKeyBunker = BKAccountManager.getpBKAccountBunker().getpKey();
		pTreeMapDateToBKTransactionPartitionDateBunker = lBKPartitionPerAccount
				.getpMapKeyToTreeMapDateToBKTransactionPartitionDate().get(lKeyBunker);
		String lKeyProy = BKAccountManager.getpBKAccountPRoy().getpKey();
		pTreeMapDateToBKTransactionPartitionDatePRoy = lBKPartitionPerAccount
				.getpMapKeyToTreeMapDateToBKTransactionPartitionDate().get(lKeyProy);
		/*
		 * Correspondence BKAssetMetal to BKIncome
		 */
		pMapBKAssetMetalToBKIncome = new HashMap<>();
		for (BKAssetMetal lBKAssetMetal : BKAssetManager.getpListBKAssetMetalSorted()) {
			BKIncome lBKIncome = BKIncomeManager.getpAndCheckBKIncome("Loan " + lBKAssetMetal.getpNameMetal(), this.getClass().getSimpleName());
			pMapBKAssetMetalToBKIncome.put(lBKAssetMetal, lBKIncome);
		}
	}
	
	@Override public void computeNewTransactionsDaily(int _sDate) {
		/*
		 * We don't create any loan nor any reimbursement of loan after the date that PROY became a client
		 */
		if (_sDate >= BKStaticConst.getDATE_PROY_BECOMES_CLIENT()) {
			return;
		}
		/*
		 * 
		 */
		BKTransactionPartitionDate lBKTransactionPartitionDateBunker = pTreeMapDateToBKTransactionPartitionDateBunker.get(_sDate);
		if (lBKTransactionPartitionDateBunker != null) {
			for (BKAssetMetal lBKAssetMetal : BKAssetManager.getpListBKAssetMetalSorted()) {
				BKHoldingAssetDate lBKHoldingAssetDateBunker = lBKTransactionPartitionDateBunker.getpMapBKAssetToBKHoldingAssetDate().get(lBKAssetMetal);
				if (lBKHoldingAssetDateBunker != null) {
					List<BKBar> lListBKBar = new ArrayList<>(lBKHoldingAssetDateBunker.getpTreeMapBKBarToHolding().keySet());
					for (BKBar lBKBar : lListBKBar) {
						int lHoldingBunker = lBKHoldingAssetDateBunker.getpTreeMapBKBarToHolding().get(lBKBar);
						if (lHoldingBunker == -1) {
							/*
							 * Finds the BKBar in PRoy
							 */
							BKHoldingAssetDate lBKHoldingAssetDatePRoy = pTreeMapDateToBKTransactionPartitionDatePRoy
									.get(_sDate).getpMapBKAssetToBKHoldingAssetDate().get(lBKAssetMetal);
							/*
							 * Check
							 */
							if (lBKHoldingAssetDatePRoy == null) {
								BKCom.error("Bunker has become debtor of a BKBar, whereas PRoy has never had this bar. It should not happen");
							}
							int lHoldingPRoy = lBKHoldingAssetDatePRoy.getpHolding(lBKBar);
							if (lHoldingPRoy != 1) {
								BKCom.error("Bunker has become debtor of a BKBar, whereas PRoy is not the owner of the BKBar. It should not happen"
										+ "\nlBKBar= " + lBKBar
										+ "\nlDate= " + _sDate);
							}
							BKIncome lBKIncome = pMapBKAssetMetalToBKIncome.get(lBKAssetMetal);
							if (lBKIncome == null) {
								BKCom.error("Missing BKIncome loan for BKAssetMetal= " + lBKAssetMetal.getpName());
							}
							BKAssetPhysical lBKAssetLoan = lBKAssetMetal.getpBKAssetLoan();
							/*
							 * Bunker receives the bar and gives a loan
							 */
							addNewBKTransaction(createAndComputeNewBKTransactionByCompta(_sDate, lBKAssetMetal, lBKBar.getpWeightOz(), Double.NaN, 
									BKAccountManager.getpBKAccountBunker(),	lBKBar.getpID(), lBKIncome, pBKEntityTransfer));
							addNewBKTransaction(createAndComputeNewBKTransactionByCompta(_sDate, lBKAssetLoan, -lBKBar.getpWeightOz(), Double.NaN, 
									BKAccountManager.getpBKAccountBunker(),	lBKBar.getpID(), lBKIncome, pBKEntityTransfer));
							/*
							 * PRoy gives the bar and receives a loan
							 */
							addNewBKTransaction(createAndComputeNewBKTransactionByCompta(_sDate, lBKAssetMetal, -lBKBar.getpWeightOz(), Double.NaN, 
									BKAccountManager.getpBKAccountPRoy(), lBKBar.getpID(), lBKIncome, pBKEntityTransfer));
							addNewBKTransaction(createAndComputeNewBKTransactionByCompta(_sDate, lBKAssetLoan, lBKBar.getpWeightOz(), Double.NaN, 
									BKAccountManager.getpBKAccountPRoy(), lBKBar.getpID(), lBKIncome, pBKEntityTransfer));
						}
					}
				}
			}
		}
	}

	@Override public void computeNewTransactionsMonthly(int _sLastDateOfMonth) {
		
	}


	

	






}
