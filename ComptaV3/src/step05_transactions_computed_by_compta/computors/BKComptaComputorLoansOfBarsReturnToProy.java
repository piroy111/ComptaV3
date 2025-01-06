package step05_transactions_computed_by_compta.computors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import basicmethods.AMNumberTools;
import basicmethods.BasicDateInt;
import basicmethods.BasicPrintMsg;
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

public class BKComptaComputorLoansOfBarsReturnToProy extends BKComptaComputorAbstract {

	public BKComptaComputorLoansOfBarsReturnToProy(BKComptaComputorManager _sBKComptaComputorManager) {
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
		BasicPrintMsg.display(this, "Bunker gives back bars to PRoy to offset loans");
	}
	
	@Override public void initiateMonth() {
		/*
		 * Initiate. Load the TreeMapDateToBKTransactionPartitionDate
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
		int lDateStopDueToPROYClient = BasicDateInt.getmFirstDayOfMonth(BKStaticConst.getDATE_PROY_BECOMES_CLIENT());
		if (_sDate >= lDateStopDueToPROYClient) {
			return;
		}
		/*
		 * Get the Partition for Bunker and PRoy. Those partitions will contain the holding of BKBar and the holding of loan for each date
		 */
		BKTransactionPartitionDate lBKTransactionPartitionDateBunker = pTreeMapDateToBKTransactionPartitionDateBunker.get(_sDate);
		BKTransactionPartitionDate lBKTransactionPartitionDatePRoy = pTreeMapDateToBKTransactionPartitionDatePRoy.get(_sDate);
		if (lBKTransactionPartitionDateBunker != null && lBKTransactionPartitionDatePRoy != null) {
			/*
			 * Loop on all the Metals which can have BKBars
			 */
			for (BKAssetMetal lBKAssetMetal : BKAssetManager.getpListBKAssetMetalSorted()) {
				BKAssetPhysical lBKAssetLoan = lBKAssetMetal.getpBKAssetLoan();
				BKHoldingAssetDate lBKHoldingLoanProy = lBKTransactionPartitionDatePRoy.getpOrCreateBKHoldingAssetDate(lBKAssetLoan);
				BKHoldingAssetDate lBKHoldingMetalBunker = lBKTransactionPartitionDateBunker.getpOrCreateBKHoldingAssetDate(lBKAssetMetal);
				/*
				 * Case there is an existing loan and Bunker has some bars --> We check that we can offset the loan 
				 */
				if (AMNumberTools.isPositiveStrict(lBKHoldingLoanProy.getpHolding()) && AMNumberTools.isPositiveStrict(lBKHoldingMetalBunker.getpHolding())) {
					BKIncome lBKIncome = pMapBKAssetMetalToBKIncome.get(lBKAssetMetal);
					/*
					 * Get the list sorted of BKBar in descending order of the size of the BKBar
					 */
					List<BKBar> lListBKBarBunker = new ArrayList<>(lBKHoldingMetalBunker.getpTreeMapBKBarToHolding().keySet());
					if (lListBKBarBunker.size() > 0) {
						/*
						 * Get out if the smallest bar of Bunker can't fill the loan because it is too big
						 */
						if (lListBKBarBunker.get(0).getpWeightOz() > lBKHoldingLoanProy.getpHolding() + BKStaticConst.getERROR_ACCEPTABLE_LOAN_BACK_TO_PROY()) {
							continue;
						}
						/*
						 * Scan the BKBars of Bunker from the largest to the smallest, and return the loan<br>
						 */
						for (int lIdxBKBar = lListBKBarBunker.size() - 1; lIdxBKBar >= 0; lIdxBKBar--) {
							BKBar lBKBar = lListBKBarBunker.get(lIdxBKBar);
							/*
							 * Skip if the BKBar has been delivered or if it is a fake bar, or if Bunker does not hold it
							 */
							if (lBKBar.getpBKEntity(_sDate) == null	&& !lBKBar.getpIsBarFuture()) {
								continue;
							}
							Integer lHoldingBunker = lBKHoldingMetalBunker.getpTreeMapBKBarToHolding().get(lBKBar);
							if (lHoldingBunker != 1) {
								continue;
							}
							/*
							 * If the BKBar of Bunker is of a smaller size than the loan, then Bunker gives the BKBar to PRoy and offset the loan
							 */
							if (lBKBar.getpWeightOz() < lBKHoldingLoanProy.getpHolding() + BKStaticConst.getERROR_ACCEPTABLE_LOAN_BACK_TO_PROY()) {
								/*
								 * Bunker gives the bar and offsets a loan
								 */
								addNewBKTransaction(createAndComputeNewBKTransactionByCompta(_sDate, lBKAssetMetal, -lBKBar.getpWeightOz(), Double.NaN, 
										BKAccountManager.getpBKAccountBunker(),	lBKBar.getpID(), lBKIncome, pBKEntityTransfer));
								addNewBKTransaction(createAndComputeNewBKTransactionByCompta(_sDate, lBKAssetLoan, lBKBar.getpWeightOz(), Double.NaN, 
										BKAccountManager.getpBKAccountBunker(),	lBKBar.getpID(), lBKIncome, pBKEntityTransfer));
								/*
								 * PRoy receives the bar and loses a loan
								 */
								addNewBKTransaction(createAndComputeNewBKTransactionByCompta(_sDate, lBKAssetMetal, lBKBar.getpWeightOz(), Double.NaN, 
										BKAccountManager.getpBKAccountPRoy(), lBKBar.getpID(), lBKIncome, pBKEntityTransfer));
								addNewBKTransaction(createAndComputeNewBKTransactionByCompta(_sDate, lBKAssetLoan, -lBKBar.getpWeightOz(), Double.NaN, 
										BKAccountManager.getpBKAccountPRoy(), lBKBar.getpID(), lBKIncome, pBKEntityTransfer));
							}
						}
					}
				}
			}
		}
	}

	@Override public void computeNewTransactionsMonthly(int _sLastDateOfMonth) {
		
	}







}
