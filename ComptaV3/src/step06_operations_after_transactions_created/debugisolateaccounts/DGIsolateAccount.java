package step06_operations_after_transactions_created.debugisolateaccounts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import basicmethods.BasicDateInt;
import staticdata.com.fileswritten.BKComOnFilesWritten;
import staticdata.datas.BKStaticConst.com_file_written;
import staticdata.datas.BKStaticDir;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.asset.bar.BKBar;
import step03_partitions.abstracts.objects.BKPartitionManager;
import step03_partitions.abstracts.partitions.BKHoldingAssetDate;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step06_operations_after_transactions_created.BKAfterBKTransactionsManager;

public class DGIsolateAccount {

	public DGIsolateAccount(BKAfterBKTransactionsManager _sBKAfterBKTransactionsManager) {
		pBKAfterBKTransactionsManager = _sBKAfterBKTransactionsManager;
	}
	
	/*
	 * Static
	 */
	private static boolean IS_ON = true;
	private static int DATE = 20200901;	
	private static String CURRENCY = "GBP";
	/*
	 * Data
	 */
	private BKAfterBKTransactionsManager pBKAfterBKTransactionsManager;
	
	/**
	 * 
	 */
	public final void run() {
		if (!IS_ON) {
			return;
		}
		/*
		 * Initiate
		 */
		BKPartitionManager lBKPartitionManager = pBKAfterBKTransactionsManager.getpBKLaunchMe().getpBKPartitionManager();
		Map<String, BKTransactionPartitionDate> lMapKeyToBKTransactionPartitionDate = lBKPartitionManager
				.getpBKPartitionPerBKAccount().getpTreeMapDateToMapKeyToBKTransactionPartitionDate().get(DATE);
		String lDir = BKStaticDir.getDEBUG();
		String lNameFile = BasicDateInt.getmToday() + "_" + this.getClass().getSimpleName() + ".csv";
		/*
		 * 
		 */
		List<String> lListLineToWrite = new ArrayList<>();
		for (String lKey : lMapKeyToBKTransactionPartitionDate.keySet()) {
			/*
			 * Filter the BKAccount with the wished CURRENCY
			 */
			BKAccount lBKAccount = BKAccountManager.getpAndCheckBKAccount(lKey, this.getClass().getSimpleName());
			if (lBKAccount.getpBKAssetCurrency().getpName().equals(CURRENCY)) {
				BKTransactionPartitionDate lBKTransactionPartitionDate = lMapKeyToBKTransactionPartitionDate.get(lKey);
				/*
				 * Chase the BKBar held by the BKAccount
				 */
				for (BKAssetMetal lBKAssetMetal : BKAssetManager.getpListBKAssetMetalSorted()) {
					BKHoldingAssetDate lBKHoldingAssetDate = lBKTransactionPartitionDate.getpMapBKAssetToBKHoldingAssetDate().get(lBKAssetMetal);
					if (lBKHoldingAssetDate != null) {
						for (BKBar lBKBar : lBKHoldingAssetDate.getpTreeMapBKBarToHolding().keySet()) {
							Integer lHolding = lBKHoldingAssetDate.getpTreeMapBKBarToHolding().get(lBKBar);
							if (lBKBar.getpBKEntity(DATE) != null
									&& lHolding == 1) {
								String lLine = DATE
										+ "," + lBKAssetMetal.getpName()
										+ "," + lBKBar.getpID()
										+ "," + (-lBKBar.getpWeightOz())
										+ "," + "NaN"
										+ "," + lBKAccount.getpEmail()
										+ "," + "Bars";
								lListLineToWrite.add(lLine);
								String lLineOpp = DATE
										+ "," + lBKAssetMetal.getpName()
										+ "," + lBKBar.getpID()
										+ "," + lBKBar.getpWeightOz()
										+ "," + "NaN"
										+ "," + "Re:" + lBKAccount.getpEmail()
										+ "," + "Bars";
								lListLineToWrite.add(lLineOpp);
							}
						}
					}
				}
			}
		}
		/*
		 * Write file
		 */
		String lHeader = "Date of the transaction,BKASset (as per names in the conf file 'Prices_histo_assets.csv'),Comment,Quantity,Price (for physical assets write NaN; for paper assets we must have a price executed),BKAccount (email as per the emails in the conf file 'Accounts and currency.csv'),BKIncome (as per names in the conf file 'BKIncome.csv')";
		BKComOnFilesWritten.writeFile(com_file_written.Debug, lDir, lNameFile, lHeader, lListLineToWrite);
	}
	
	
}
