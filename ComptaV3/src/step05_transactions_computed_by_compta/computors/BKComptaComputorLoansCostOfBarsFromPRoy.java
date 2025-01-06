package step05_transactions_computed_by_compta.computors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.AMNumberTools;
import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import staticdata.com.fileswritten.BKComOnFilesWritten;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticConst.com_file_written;
import staticdata.datas.BKStaticDir;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.asset.BKAssetCurrency;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.asset.assetpaperorphysical.BKAssetPhysical;
import step01_objects_from_conf_files.income.BKIncome;
import step01_objects_from_conf_files.income.BKIncomeManager;
import step03_partitions.abstracts.objects.BKPartitionManager;
import step03_partitions.abstracts.partitions.BKHoldingAssetDate;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.partitions.BKPartitionPerBKAccount;
import step05_transactions_computed_by_compta.abstracts.BKComptaComputorAbstract;
import step05_transactions_computed_by_compta.abstracts.BKComptaComputorManager;

public class BKComptaComputorLoansCostOfBarsFromPRoy extends BKComptaComputorAbstract {

	public BKComptaComputorLoansCostOfBarsFromPRoy(BKComptaComputorManager _sBKComptaComputorManager) {
		super(_sBKComptaComputorManager);
	}

	/*
	 * Data
	 */
	private Map<BKAssetMetal, Double> pMapBKAssetMetalToCostofLoan;
	private BKPartitionManager pBKPartitionManager;
	private BKPartitionPerBKAccount pBKPartitionPerAccount;
	private BKAssetCurrency pBKAssetCurrencyUSD;
	private BKIncome pBKIncome;
	private String pKeyProy;
	private List<String> pListLineToWrite;

	@Override public void initiateGlobal() {
		BasicPrintMsg.display(this, "PRoy lends currencies to Bunker if Bunker is short (in order to avoid bankruptcy)");
	}

	@Override public void initiateMonth() {
		/*
		 * Load
		 */
		pBKPartitionManager = pBKLaunchMe.getpBKPartitionManager();
		pBKPartitionPerAccount = pBKPartitionManager.getpBKPartitionPerBKAccount();
		pKeyProy = BKAccountManager.getpBKAccountPRoy().getpKey();
		pBKAssetCurrencyUSD = BKAssetManager.getpAndCheckBKAssetCurrency("USD", this.getClass().getSimpleName());
		/*
		 * BKIncome
		 */
		pBKIncome = BKIncomeManager.getpAndCheckBKIncome("Loan_cost_currencies", this.getClass().getSimpleName());
		/*
		 * 
		 */
		pMapBKAssetMetalToCostofLoan = new HashMap<>();
		pListLineToWrite = new ArrayList<>();
	}

	@Override public void computeNewTransactionsDaily(int _sDate) {
		/*
		 * We don't create any loan nor any reimbursement of loan after the date that PROY became a client
		 */
		if (_sDate >= BKStaticConst.getDATE_PROY_BECOMES_CLIENT()) {
			return;
		}
		/*
		 * Loop on all the loans of bars
		 */
		String lLine = _sDate + "";
		for (BKAssetMetal lBKAssetMetal : BKAssetManager.getpListBKAssetMetalSorted()) {
			BKAssetPhysical lBKAssetLoan = lBKAssetMetal.getpBKAssetLoan();
			/*
			 * Loop on all the dates
			 */
			double lNNNUSDLoan = 0.;
			Map<String, BKTransactionPartitionDate> lMapKeyToBKTransactionPartitionDate = pBKPartitionPerAccount.getpTreeMapDateToMapKeyToBKTransactionPartitionDate().get(_sDate);
			double lCostLoanAsset = 0.;
			/*
			 * If the holding of the metal loan exists, then we take the NNN
			 */
			BKTransactionPartitionDate lBKTransactionPartitionDate = lMapKeyToBKTransactionPartitionDate.get(pKeyProy);
			if (lBKTransactionPartitionDate != null) {
				BKHoldingAssetDate lBKHoldingAssetDate = lBKTransactionPartitionDate.getpMapBKAssetToBKHoldingAssetDate().get(lBKAssetLoan);
				if (lBKHoldingAssetDate != null) {
					lNNNUSDLoan = lBKHoldingAssetDate.getpHolding() * lBKAssetMetal.getpPriceUSD(_sDate);
					if (!AMNumberTools.isNaNOrZero(lNNNUSDLoan)) {
						/*
						 * Compute and store
						 */
						Double lCostOfLoan = pMapBKAssetMetalToCostofLoan.get(lBKAssetMetal);
						if (lCostOfLoan == null) {
							lCostOfLoan = 0.;
						}
						double lCostLoanNow = lNNNUSDLoan
								* lBKAssetMetal.getpCostOfBorrowFromProy() / 360.;
						lCostOfLoan += lCostLoanNow;
						lCostLoanAsset += lCostLoanNow;
						pMapBKAssetMetalToCostofLoan.put(lBKAssetMetal, lCostOfLoan);
					}
				}
			}
			/*
			 * Write line
			 */
			lLine += "," + (-lNNNUSDLoan)
					+ "," + lBKAssetMetal.getpCostOfBorrowFromProy()
					+ "," + (-lCostLoanAsset);
		}
		pListLineToWrite.add(lLine);
	}

	/**
	 * Create the BKTransaction
	 */
	@Override public void computeNewTransactionsMonthly(int _sLastDateOfMonth) {
		/*
		 * Create transactions
		 */
		for (BKAssetMetal lBKAssetMetal : pMapBKAssetMetalToCostofLoan.keySet()) {
			Double lCostOfLoan = pMapBKAssetMetalToCostofLoan.get(lBKAssetMetal);
			if (!AMNumberTools.isNaNOrZero(lCostOfLoan)) {
				String lComment = "Cost of loan of "
						+ lBKAssetMetal.getpName()
						+ " from PRoy  for " 
						+ BasicDateInt.getmMonthName(BasicDateInt.getmMonth(_sLastDateOfMonth))
						+ " " + BasicDateInt.getmYear(_sLastDateOfMonth);
				addNewBKTransaction(createAndComputeNewBKTransactionByCompta(_sLastDateOfMonth, pBKAssetCurrencyUSD, -lCostOfLoan, Double.NaN, 
						BKAccountManager.getpBKAccountBunker(),	lComment, pBKIncome, pBKEntityTransfer));
				addNewBKTransaction(createAndComputeNewBKTransactionByCompta(_sLastDateOfMonth, pBKAssetCurrencyUSD, lCostOfLoan, Double.NaN, 
						BKAccountManager.getpBKAccountPRoy(), lComment, pBKIncome, pBKEntityTransfer));
			}
		}
		/*
		 * Write file
		 */
		String lHeader = "Date";
		for (BKAssetMetal lBKAssetMetal : BKAssetManager.getpListBKAssetMetalSorted()) {
			lHeader += "," + lBKAssetMetal.getpName() + " - " + "NNN U$ borrowed by Bunker"
					+ "," + lBKAssetMetal.getpName() + " - " + "rate% charged"
					+ "," + lBKAssetMetal.getpName() + " - " + "cost US$ charged";
		}
		String lNameFromClass = this.getClass().getSimpleName().substring("BKComptaComputor".length());
		lNameFromClass = BasicString.insertSeparatorBeforeUpperCase(lNameFromClass, null, "_");
		String lDir = BKStaticDir.getOUTPUT_COMPTA() + lNameFromClass + "/";
		BasicFichiers.getOrCreateDirectory(lDir);
		String lNameFile = BasicDateInt.getmToday() + "_" + lNameFromClass + ".csv";
		BKComOnFilesWritten.writeFile(com_file_written.OutputFiles, lDir, lNameFile, lHeader, pListLineToWrite);
	}




}
