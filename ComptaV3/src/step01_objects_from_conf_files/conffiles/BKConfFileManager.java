package step01_objects_from_conf_files.conffiles;

import basicmethods.BasicPrintMsg;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.income.BKIncomeManager;

public class BKConfFileManager {

	
	public static void run() {
		BasicPrintMsg.displaySuperTitle(null, "Read and load CONF files");
		/*
		 * Time stamp of last COMPTA
		 */
		BKConfLoaderTimeStampPreviousCompta.loadConfFile();
		/*
		 * Accounts
		 */
		BKAccountManager.loadBKAccounts();
		/*
		 * Assets + historical prices
		 */
		BKAssetManager.loadBKAssets();
		/*
		 * Load cost of storage
		 */
		BKConfLoaderCostForBKAssetMetal.loadConfFile();
		BKConfLoaderCostForBKCurrency.loadConfFile();
		/*
		 * BKIncome
		 */
		BKIncomeManager.loadBKIncome();
		/*
		 * BKBar real weight
		 */
		BKConfLoaderBKBarRealWeight.loadConfFile();
		/*
		 * BKAssetLeasing
		 */
		BKConfLoaderLeasingGain.loadConfFile();
		/*
		 * Load limit of expenses for commercial
		 */
		BKConfLoaderDrawCommercial.loadConfFile();
	}
	
}
