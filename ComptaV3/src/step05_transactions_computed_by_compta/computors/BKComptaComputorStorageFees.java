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
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.income.BKIncome;
import step01_objects_from_conf_files.income.BKIncomeManager;
import step03_partitions.abstracts.objects.BKPartitionManager;
import step03_partitions.abstracts.partitions.BKHoldingAssetDate;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.partitions.BKPartitionPerBKAccount;
import step05_transactions_computed_by_compta.abstracts.BKComptaComputorAbstract;
import step05_transactions_computed_by_compta.abstracts.BKComptaComputorManager;

public class BKComptaComputorStorageFees extends BKComptaComputorAbstract {

	public BKComptaComputorStorageFees(BKComptaComputorManager _sBKComptaComputorManager) {
		super(_sBKComptaComputorManager);
	}

	/*
	 * Data
	 */
	private Map<BKAccount, Double> pMapBKAccountToStorageFee;
	private BKPartitionManager pBKPartitionManager;
	private BKPartitionPerBKAccount pBKPartitionPerAccount;
	private BKIncome pBKIncome;

	@Override public void initiateGlobal() {
		BasicPrintMsg.display(this, "Compute cost of storages for all the cients (including PRoy)");
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
		pMapBKAccountToStorageFee = new HashMap<>();
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
			 * Initiate
			 */
			BKTransactionPartitionDate lBKTransactionPartitionDate = lTreeMapDateToBKTransactionPartitionDate.get(_sDate);
			if (lBKTransactionPartitionDate != null) {
				for (BKAssetMetal lBKAssetMetal : BKAssetManager.getpListBKAssetMetalSorted()) {
					BKHoldingAssetDate lBKHoldingAssetDate = lBKTransactionPartitionDate.getpMapBKAssetToBKHoldingAssetDate().get(lBKAssetMetal);
					if (lBKHoldingAssetDate != null) {
						/*
						 * Aggregate the storage fee for each date for each metal 
						 */
						double lHolding = lBKHoldingAssetDate.getpHolding();
						double lNNNUSD = lHolding * lBKAssetMetal.getpPriceUSD(_sDate);
						if (!AMNumberTools.isNaNOrZero(lNNNUSD) && AMNumberTools.isGreaterStrict(lNNNUSD, 0)) {
							/*
							 * Compute and store the storage fee for the month
							 */
							Double lStorageFeeUSD = pMapBKAccountToStorageFee.get(lBKAccount);
							if (lStorageFeeUSD == null) {
								lStorageFeeUSD = 0.;
							}
							lStorageFeeUSD += lBKAssetMetal.getpCostStorage() * lNNNUSD / 360.;
							pMapBKAccountToStorageFee.put(lBKAccount, lStorageFeeUSD);
						}
					}
				}
			}
		}
	}

	@Override public void computeNewTransactionsMonthly(int _sLastDateOfMonth) {
		/*
		 * Loop on all the accounts
		 */
		for (BKAccount lBKAccount : pMapBKAccountToStorageFee.keySet()) {
			BKAssetCurrency lBKAssetCurrency = lBKAccount.getpBKAssetCurrency();
			double lStorageFee = pMapBKAccountToStorageFee.get(lBKAccount);
			/*
			 * Compute the storage fee in local currency
			 */
			double lStorageFeeLocalCurrency = lStorageFee / lBKAssetCurrency.getpPriceUSD(_sLastDateOfMonth);
			lStorageFeeLocalCurrency = Math.round(lStorageFeeLocalCurrency * 1000.) / 1000.;
			/*
			 * Create the BKTransaction
			 */			
			if (!AMNumberTools.isNaNOrZero(lStorageFeeLocalCurrency)) {
				/*
				 * Comment
				 */
				String lComment = "Storage fee accounted for ";
				lComment += BasicDateInt.getmMonthName(BasicDateInt.getmMonth(_sLastDateOfMonth))
						+ " " + BasicDateInt.getmYear(_sLastDateOfMonth);
				/*
				 * Create transaction for the month
				 */
				addNewBKTransaction(createAndComputeNewBKTransactionByCompta(_sLastDateOfMonth, lBKAssetCurrency, -lStorageFeeLocalCurrency, Double.NaN, 
						lBKAccount,	lComment, pBKIncome, pBKEntityTransfer));
				lComment += "; for BKAccount " + lBKAccount.getpEmail();
				addNewBKTransaction(createAndComputeNewBKTransactionByCompta(_sLastDateOfMonth, lBKAssetCurrency, lStorageFeeLocalCurrency, Double.NaN, 
						BKAccountManager.getpBKAccountBunker(),	lComment, pBKIncome, pBKEntityTransfer));
			}
		}
	}

	




}
