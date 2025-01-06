package step05_transactions_computed_by_compta.computors;

import java.util.ArrayList;
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
import step01_objects_from_conf_files.income.BKIncome;
import step01_objects_from_conf_files.income.BKIncomeManager;
import step02_load_transactions.objects.entity.BKEntityManager;
import step03_partitions.abstracts.objects.BKPartitionManager;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.partitions.BKPartitionPerBKAccount;
import step03_partitions.partitions.BKPartitionPerBKEntity;
import step05_transactions_computed_by_compta.abstracts.BKComptaComputorAbstract;
import step05_transactions_computed_by_compta.abstracts.BKComptaComputorManager;

public class BKComptaComputorLoansCostOfCurrenciesFromPRoy extends BKComptaComputorAbstract {

	public BKComptaComputorLoansCostOfCurrenciesFromPRoy(BKComptaComputorManager _sBKComptaComputorManager) {
		super(_sBKComptaComputorManager);
	}

	/*
	 * Data
	 */
	private BKIncome pBKIncome;
	private BKPartitionManager pBKPartitionManager;
	private String pKeyProy;
	private String pKeyUOB;
	private String pKeyCondorPRoy;
	private BKPartitionPerBKAccount pBKPartitionPerAccount;
	private List<String> pListLineToWrite;
	private BKAssetCurrency pBKAssetCurrencyPRoy;
	private double pCostOfLoanOverMonth;

	@Override public void initiateGlobal() {
		BasicPrintMsg.display(this, "PRoy lends currencies to Bunker if Bunker is short (in order to avoid bankruptcy)");
	}

	@Override public void initiateMonth() {
		/*
		 * Load
		 */
		pBKPartitionManager = pBKLaunchMe.getpBKPartitionManager();
		pBKPartitionPerAccount = pBKPartitionManager.getpBKPartitionPerBKAccount();
		pKeyProy = BKPartitionPerBKAccount.getKey(BKAccountManager.getpBKAccountPRoy());
		pBKAssetCurrencyPRoy = BKAccountManager.getpBKAccountPRoy().getpBKAssetCurrency();
		pKeyUOB = BKPartitionPerBKEntity.getKey(BKEntityManager.getpAndCheckBKEntityFromName(BKStaticConst.getBKENTITY_UOB(), this));
		pKeyCondorPRoy = BKPartitionPerBKEntity.getKey(BKEntityManager.getpAndCheckBKEntityFromName(BKStaticConst.getBKENTITY_CONDOR_PROY(), this));
		/*
		 * BKIncome
		 */
		pBKIncome = BKIncomeManager.getpAndCheckBKIncome("Loan_cost_currencies", this.getClass().getSimpleName());
		/*
		 * 
		 */
		pCostOfLoanOverMonth = 0.;
		pListLineToWrite = new ArrayList<>();
	}

	public void computeNewTransactionsDaily(int _sDate) {
		double lNNNPRoy = 0.;
		/*
		 * Retrieve the NNN of PRoy in its own currency
		 */
		Map<String, BKTransactionPartitionDate> lMapKeyToBKTransactionPartitionDatePRoy = pBKPartitionPerAccount.getpTreeMapDateToMapKeyToBKTransactionPartitionDate().get(_sDate);
		if (lMapKeyToBKTransactionPartitionDatePRoy != null) {
			BKTransactionPartitionDate lBKTransactionPartitionDate = lMapKeyToBKTransactionPartitionDatePRoy.get(pKeyProy);
			if (lBKTransactionPartitionDate != null) {
				lNNNPRoy = lBKTransactionPartitionDate.getpHoldingNoNaNNoNull(pBKAssetCurrencyPRoy);
			}
		}
		/*
		 * Retrieve the same amount in UOB. This is the liquid part of BUNKER
		 */
		double lNNNLiquidBunkerPlusClients = 0.;
		Map<String, BKTransactionPartitionDate> lMapKeyToBKTransactionPartitionDateBunker = pBKPartitionManager.getpBKPartitionPerBKEntity()
				.getpTreeMapDateToMapKeyToBKTransactionPartitionDate().get(_sDate);
		if (lMapKeyToBKTransactionPartitionDateBunker != null) {
			BKTransactionPartitionDate lBKTransactionPartitionDateBunker = lMapKeyToBKTransactionPartitionDateBunker.get(pKeyUOB);
			if (lBKTransactionPartitionDateBunker != null) {
				lNNNLiquidBunkerPlusClients = lBKTransactionPartitionDateBunker.getpHoldingNoNaNNoNull(pBKAssetCurrencyPRoy);
			}
		}
		/*
		 * Retrieve the amount of USD of PRoy in Condor_Proy
		 */
		double lNNNPRoyAtCondor = 0.;
		lMapKeyToBKTransactionPartitionDatePRoy = pBKPartitionManager.getpBKPartitionPerBKEntity()
				.getpTreeMapDateToMapKeyToBKTransactionPartitionDate().get(_sDate);
		if (lMapKeyToBKTransactionPartitionDateBunker != null) {
			BKTransactionPartitionDate lBKTransactionPartitionDatePRoy = lMapKeyToBKTransactionPartitionDateBunker.get(pKeyCondorPRoy);
			if (lBKTransactionPartitionDatePRoy != null) {
				lNNNPRoyAtCondor = lBKTransactionPartitionDatePRoy.getpNAV();
			}
		}
		/*
		 * Compute the amount of the loan
		 */
		double lNNNBorrowed = Math.min(0, lNNNLiquidBunkerPlusClients + lNNNPRoyAtCondor - lNNNPRoy);
		double lCostOfLoanToday = lNNNBorrowed
				* pBKAssetCurrencyPRoy.getpCostOfBorrowToPRoy() / 360.;
		pCostOfLoanOverMonth += lCostOfLoanToday;
		/*
		 * Write the line for the file
		 */
		String lLine = _sDate
				+ "," + pBKAssetCurrencyPRoy.getpName()
				+ "," + lNNNLiquidBunkerPlusClients
				+ "," + lNNNPRoyAtCondor
				+ "," + lNNNPRoy
				+ "," + lNNNBorrowed
				+ "," + pBKAssetCurrencyPRoy.getpCostOfBorrowToPRoy()
				+ "," + lCostOfLoanToday;
		pListLineToWrite.add(lLine);
	}

	/**
	 * Create the BKTransaction
	 */
	@Override public void computeNewTransactionsMonthly(int _sLastDateOfMonth) {
		/*
		 * Create transactions
		 */
		if (!AMNumberTools.isNaNOrZero(pCostOfLoanOverMonth)) {
			String lComment = "Cost of loan of currencies from PRoy for ";
			lComment += BasicDateInt.getmMonthName(BasicDateInt.getmMonth(_sLastDateOfMonth))
					+ " " + BasicDateInt.getmYear(_sLastDateOfMonth);
			addNewBKTransaction(createAndComputeNewBKTransactionByCompta(_sLastDateOfMonth, pBKAssetCurrencyPRoy, pCostOfLoanOverMonth, Double.NaN, 
					BKAccountManager.getpBKAccountBunker(),	lComment, pBKIncome, pBKEntityTransfer));
			addNewBKTransaction(createAndComputeNewBKTransactionByCompta(_sLastDateOfMonth, pBKAssetCurrencyPRoy, -pCostOfLoanOverMonth, Double.NaN, 
					BKAccountManager.getpBKAccountPRoy(), lComment, pBKIncome, pBKEntityTransfer));
		}
		/*
		 * Write file
		 */
		String lHeader = "Date,BKCurrency"
				+ ",Holding of " + pBKAssetCurrencyPRoy.getpName() + " at " + BKStaticConst.getBKENTITY_UOB()
				+ ",Holding PRoy at " + BKStaticConst.getBKENTITY_CONDOR_PROY()
				+ ",Total holding PRoy in " + pBKAssetCurrencyPRoy.getpName()
				+ ",NNN borrowed by Bunker to PRoy (" + pBKAssetCurrencyPRoy.getpName() + ")"
				+ ",Cost of loan in % (as per the conf file)"
				+ ",Cost of loan (" + pBKAssetCurrencyPRoy.getpName() + ")";
		String lNameFromClass = this.getClass().getSimpleName().substring("BKComptaComputor".length());
		lNameFromClass = BasicString.insertSeparatorBeforeUpperCase(lNameFromClass, null, "_");
		String lDir = BKStaticDir.getOUTPUT_COMPTA() + lNameFromClass + "/";
		BasicFichiers.getOrCreateDirectory(lDir);
		String lNameFile = BasicDateInt.getmToday() + "_" + lNameFromClass + ".csv";
		BKComOnFilesWritten.writeFile(com_file_written.OutputFiles, lDir, lNameFile, lHeader, pListLineToWrite);
	}




}
