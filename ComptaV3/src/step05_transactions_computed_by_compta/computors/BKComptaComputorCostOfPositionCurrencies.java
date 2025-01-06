package step05_transactions_computed_by_compta.computors;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import basicmethods.AMNumberTools;
import basicmethods.BasicDateInt;
import basicmethods.BasicPrintMsg;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.asset.BKAssetCurrency;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.income.BKIncome;
import step01_objects_from_conf_files.income.BKIncomeManager;
import step03_partitions.abstracts.objects.BKPartitionManager;
import step03_partitions.abstracts.partitions.BKHoldingAssetDate;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.partitions.BKPartitionPerBKAccount;
import step05_transactions_computed_by_compta.abstracts.BKComptaComputorAbstract;
import step05_transactions_computed_by_compta.abstracts.BKComptaComputorManager;

public class BKComptaComputorCostOfPositionCurrencies extends BKComptaComputorAbstract {

	public BKComptaComputorCostOfPositionCurrencies(BKComptaComputorManager _sBKComptaComputorManager) {
		super(_sBKComptaComputorManager);
	}

	/*
	 * Data
	 */
	private Map<BKAccount, Map<BKAssetCurrency, Double>> pMapBKAccountToMapBKCurrencyToInterestToPay;
	private BKPartitionPerBKAccount pBKPartitionPerAccount;
	private BKPartitionManager pBKPartitionManager;
	private BKIncome pBKIncome;

	@Override public void initiateGlobal() {
		BasicPrintMsg.display(this, "Cost of a long of short position of currencies of the clients");
	}

	@Override public void initiateMonth() {
		/*
		 * Load the partitions per BKAccount
		 */
		pBKPartitionManager = pBKLaunchMe.getpBKPartitionManager();
		pBKPartitionPerAccount = pBKPartitionManager.getpBKPartitionPerBKAccount();
		pBKIncome = BKIncomeManager.getpAndCheckBKIncome("Operations_Storage_incoming_from_clients", this.getClass().getSimpleName());
		/*
		 * 
		 */
		pMapBKAccountToMapBKCurrencyToInterestToPay = new HashMap<>();
	}

	@Override public void computeNewTransactionsDaily(int _sDate) {
		/*
		 * Loop on all the accounts
		 */
		for (String lKeyBKAccount : pBKPartitionPerAccount.getpMapKeyToTreeMapDateToBKTransactionPartitionDate().keySet()) {
			if (lKeyBKAccount.equals(BKAccountManager.getpBKAccountBunker().getpKey())) {
				continue;
			}
			TreeMap<Integer, BKTransactionPartitionDate> lTreeMapDateToBKTransactionPartitionDate = pBKPartitionPerAccount
					.getpMapKeyToTreeMapDateToBKTransactionPartitionDate().get(lKeyBKAccount);
			BKAccount lBKAccount = BKAccountManager.getpAndCheckBKAccount(lKeyBKAccount, this.getClass().getSimpleName());
			/*
			 * Get or create
			 */
			Map<BKAssetCurrency, Double> lMapBKAssetCurrencyToInterestToPay = pMapBKAccountToMapBKCurrencyToInterestToPay.get(lBKAccount);
			if (lMapBKAssetCurrencyToInterestToPay == null) {
				lMapBKAssetCurrencyToInterestToPay = new HashMap<>();
				pMapBKAccountToMapBKCurrencyToInterestToPay.put(lBKAccount, lMapBKAssetCurrencyToInterestToPay);
			}			
			/*
			 * Loop on all the currencies
			 */
			for (BKAssetCurrency lBKAssetCurrency : BKAssetManager.getpListBKAssetCurrencySorted()) {
				double lInterestToPay = 0.;
				/*
				 * Initiate
				 */
				BKTransactionPartitionDate lBKTransactionPartitionDate = lTreeMapDateToBKTransactionPartitionDate.get(_sDate);
				if (lBKTransactionPartitionDate != null) {
					BKHoldingAssetDate lBKHoldingAssetDate = lBKTransactionPartitionDate.getpMapBKAssetToBKHoldingAssetDate().get(lBKAssetCurrency);
					if (lBKHoldingAssetDate != null) {
						/*
						 *  Cost of having a long or short position
						 */
						if (AMNumberTools.isGreaterStrict(lBKHoldingAssetDate.getpHolding(), 0)) {
							lInterestToPay += lBKHoldingAssetDate.getpHolding() 
									* lBKAssetCurrency.getpCostOfLongPosition()	/ 360.;
						} 
						/*
						 * Cost of having a short position
						 */
						else if (AMNumberTools.isSmallerStrict(lBKHoldingAssetDate.getpHolding(), 0)) {
							lInterestToPay += -lBKHoldingAssetDate.getpHolding() 
									* lBKAssetCurrency.getpCostOfShortPosition() / 360.;
						}
						/*
						 * Store
						 */
						if (!AMNumberTools.isNaNOrZero(lInterestToPay)) {
							Double lInterestTotal = lMapBKAssetCurrencyToInterestToPay.get(lBKAssetCurrency);
							if (lInterestTotal == null) {
								lInterestTotal = 0.;
							}
							lInterestTotal += lInterestToPay;
							lMapBKAssetCurrencyToInterestToPay.put(lBKAssetCurrency, lInterestTotal);
						}
					}
				}
			}
		}
	}

	/**
	 * Create the BKTransaction
	 */
	@Override public void computeNewTransactionsMonthly(int _sLastDateOfMonth) {
		for (BKAccount lBKAccount : pMapBKAccountToMapBKCurrencyToInterestToPay.keySet()) {
			Map<BKAssetCurrency, Double> lMapBKAssetCurrencyToInterestToPay = pMapBKAccountToMapBKCurrencyToInterestToPay.get(lBKAccount);
			for (BKAssetCurrency lBKAssetCurrency : lMapBKAssetCurrencyToInterestToPay.keySet()) {
				Double lInterestToPay = lMapBKAssetCurrencyToInterestToPay.get(lBKAssetCurrency);
				lInterestToPay = Math.round(lInterestToPay * 1000.) / 1000.;
				if (!AMNumberTools.isNaNOrZero(lInterestToPay)) {
					String lComment = "Interests to be paid for " 
							+ BasicDateInt.getmMonthName(BasicDateInt.getmMonth(_sLastDateOfMonth))
							+ " " + BasicDateInt.getmYear(_sLastDateOfMonth)
							+ " for the currency " + lBKAssetCurrency.getpName(); 
					addNewBKTransaction(createAndComputeNewBKTransactionByCompta(_sLastDateOfMonth, lBKAssetCurrency, -lInterestToPay, Double.NaN, 
							lBKAccount,	lComment, pBKIncome, pBKEntityTransfer));
					lComment += "; for BKAccount " + lBKAccount.getpEmail();
					addNewBKTransaction(createAndComputeNewBKTransactionByCompta(_sLastDateOfMonth, lBKAssetCurrency, lInterestToPay, Double.NaN, 
							BKAccountManager.getpBKAccountBunker(),	lComment, pBKIncome, pBKEntityTransfer));
				}
			}
		}
	}






}
