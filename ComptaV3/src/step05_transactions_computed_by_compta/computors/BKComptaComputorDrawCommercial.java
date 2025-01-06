package step05_transactions_computed_by_compta.computors;

import basicmethods.AMNumberTools;
import basicmethods.BasicDateInt;
import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.asset.BKAssetCurrency;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.conffiles.BKConfLoaderDrawCommercial;
import step01_objects_from_conf_files.income.BKIncome;
import step01_objects_from_conf_files.income.BKIncomeManager;
import step03_partitions.abstracts.objects.BKPartitionManager;
import step03_partitions.partitions.BKPartitionPerBKIncomeAndBKAccount;
import step05_transactions_computed_by_compta.abstracts.BKComptaComputorAbstract;
import step05_transactions_computed_by_compta.abstracts.BKComptaComputorManager;

public class BKComptaComputorDrawCommercial extends BKComptaComputorAbstract {

	public BKComptaComputorDrawCommercial(BKComptaComputorManager _sBKComptaComputorManager) {
		super(_sBKComptaComputorManager);
		// TODO Auto-generated constructor stub
	}

	/*
	 * Data
	 */
	private BKPartitionManager pBKPartitionManager;
	private BKPartitionPerBKIncomeAndBKAccount pBKPartitionPerBKIncomeAndBKAccount;


	@Override public void initiateGlobal() {
		BasicPrintMsg.display(this, "Create transfer commercial draw -> cost");	

	}

	@Override public void initiateMonth() {
		pBKPartitionManager = pBKLaunchMe.getpBKPartitionManager();
		pBKPartitionPerBKIncomeAndBKAccount = pBKPartitionManager.getpBKPartitionPerBKIncomeAndBKAccount();
	}

	@Override public void computeNewTransactionsDaily(int _sDate) {}


	@Override public void computeNewTransactionsMonthly(int _sLastDateOfMonth) {
		/*
		 * Initiate
		 */
		BKAccount lBKAccountBunker = BKAccountManager.getpBKAccountBunker();
		BKIncome lBKIncomeDraw = BKIncomeManager.getpAndCheckBKIncome(BKStaticConst.getBKINCOME_COMMERCIAL_DRAW(), this);
		BKIncome lBKIncomeCost = BKIncomeManager.getpAndCheckBKIncome(BKStaticConst.getBKINCOME_COMMERCIAL_COST(), this);
		String lKey = BKPartitionPerBKIncomeAndBKAccount.getKey(lBKIncomeDraw, lBKAccountBunker);
		/*
		 * Initiate date
		 */
		int lDateLastMonth = BasicDateInt.getmEndOfMonth(BasicDateInt.getmPlusMonth(_sLastDateOfMonth, -1));
		/*
		 * 
		 */
		double lDraw = 0.;
		double lAmountToTransfer = 0.;
		for (BKAssetCurrency lBKAssetCurrency : BKAssetManager.getpListBKAssetCurrencySorted()) {
			double lDrawEndOfMonth = pBKPartitionPerBKIncomeAndBKAccount.getpHoldingBKAssetNotNull(lKey, _sLastDateOfMonth, lBKAssetCurrency);
			double lDrawEndOfLastMonth = pBKPartitionPerBKIncomeAndBKAccount.getpHoldingBKAssetNotNull(lKey, lDateLastMonth, lBKAssetCurrency);
			double lDrawCurrency = lDrawEndOfMonth - lDrawEndOfLastMonth;
			/*
			 * Compute amount to give back to cost
			 */
			double lDrawUSD = lDrawCurrency * lBKAssetCurrency.getpPriceUSD(_sLastDateOfMonth);
			lDraw += lDrawUSD;
		}
		lAmountToTransfer += Math.max(-BKConfLoaderDrawCommercial.getLIMIT_DRAW_COMMERCIAL(), Math.min(0, lDraw));
		/*
		 * Create the transaction
		 */
		if (!AMNumberTools.isNaNOrNullOrZero(lAmountToTransfer)) {
			/*
			 * Data of transfer
			 */
			String lComment = "Transfer back from draw to cost (limit= " + BKConfLoaderDrawCommercial.getLIMIT_DRAW_COMMERCIAL() + ")";
			/*
			 * Transfer
			 */
			BKAssetCurrency lBKAssetUSD = BKAssetManager.getpBKAssetCurrencyReference();
			addNewBKTransaction(createAndComputeNewBKTransactionByCompta(_sLastDateOfMonth, lBKAssetUSD, 
					lAmountToTransfer, Double.NaN, 
					lBKAccountBunker,	lComment, lBKIncomeCost, pBKEntityTransfer));
			addNewBKTransaction(createAndComputeNewBKTransactionByCompta(_sLastDateOfMonth, lBKAssetUSD, 
					-lAmountToTransfer, Double.NaN, 
					lBKAccountBunker,	lComment, lBKIncomeDraw, pBKEntityTransfer));
		}
	}

}
