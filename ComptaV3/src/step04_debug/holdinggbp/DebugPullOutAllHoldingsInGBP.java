package step04_debug.holdinggbp;

import java.util.Map;

import basicmethods.BasicTime;
import staticdata.com.BKCom;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.asset.BKAssetCurrency;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.asset.bar.BKBar;
import step03_partitions.abstracts.objects.BKPartitionManager;
import step03_partitions.abstracts.partitions.BKHoldingAssetDate;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;

public class DebugPullOutAllHoldingsInGBP {

	public DebugPullOutAllHoldingsInGBP(BKPartitionManager _sBKPartitionManager) {
		pBKPartitionManager = _sBKPartitionManager;
	}


	/*
	 * Static
	 */
	private static String CURRENCY_TO_PULL = "GBP";
	private static int DATE_TO_PULL = 20200901;
	/*
	 * Data
	 */
	private BKPartitionManager pBKPartitionManager;

	/**
	 * 
	 */
	public final void run() {
		BasicTime.sleep(50);
		System.err.println();
		Map<String, BKTransactionPartitionDate> lMapKeyToBKTransactionPartitionDate = pBKPartitionManager.getpBKPartitionPerBKAccount()
				.getpTreeMapDateToMapKeyToBKTransactionPartitionDate().get(DATE_TO_PULL);
		if (lMapKeyToBKTransactionPartitionDate == null) {
			BKCom.errorCodeLogic();
		}
		String lFileNameOrigin = this.getClass().getSimpleName();
		BKAssetCurrency lBKAssetCurrencyToPull = BKAssetManager.getpAndCheckBKAssetCurrency(CURRENCY_TO_PULL, lFileNameOrigin);
		/*
		 * Loop on the BKAccounts
		 */
		for (String lKeyAccount : lMapKeyToBKTransactionPartitionDate.keySet()) {
			BKAccount lBKAccount = BKAccountManager.getpAndCheckBKAccount(lKeyAccount, lFileNameOrigin);
			if (lBKAccount.getpBKAssetCurrency().equals(lBKAssetCurrencyToPull)) {
				BKTransactionPartitionDate lBKTransactionPartitionDate = lMapKeyToBKTransactionPartitionDate.get(lKeyAccount);
				for (BKAssetMetal lBKAssetMetal : BKAssetManager.getpListBKAssetMetalSorted()) {
					BKHoldingAssetDate lBKHoldingAssetDate = lBKTransactionPartitionDate.getpMapBKAssetToBKHoldingAssetDate().get(lBKAssetMetal);
					for (BKBar lBKBar : lBKHoldingAssetDate.getpTreeMapBKBarToHolding().keySet()) {
						Integer lHolding = lBKHoldingAssetDate.getpTreeMapBKBarToHolding().get(lBKBar);
						if (lHolding != 0) {
							String lLineSell = DATE_TO_PULL
									+ "," + lBKAssetMetal.getpName()
									+ "," + lBKBar.getpID()
									+ "," + (-lHolding * lBKBar.getpWeightOz())
									+ "," + "NaN"
									+ "," + lBKAccount.getpEmail() 
									+ "," + "Bars";
							String lLineBuy = DATE_TO_PULL
									+ "," + lBKAssetMetal.getpName()
									+ "," + lBKBar.getpID()
									+ "," + (+lHolding * lBKBar.getpWeightOz())
									+ "," + "NaN"
									+ ","
									+ "," + "Bars";
							System.err.println(lLineSell);
							System.err.println(lLineBuy);
						}
					}
				}
			}
		}
		System.err.println("Done " + CURRENCY_TO_PULL + " @ " + DATE_TO_PULL);
		System.exit(-1);
	}


}
