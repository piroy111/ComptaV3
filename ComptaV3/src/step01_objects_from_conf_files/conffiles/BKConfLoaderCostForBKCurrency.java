package step01_objects_from_conf_files.conffiles;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.ReadFile;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step01_objects_from_conf_files.asset.asset.BKAssetCurrency;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;

public class BKConfLoaderCostForBKCurrency {

	/*
	 * Data
	 */
	private static boolean IS_LOADED = false;

	/**
	 * 
	 */
	public static void loadConfFile() {
		if (!IS_LOADED) {
			IS_LOADED = true;
			Object lSender = new BKConfLoaderCostForBKCurrency();
			BasicPrintMsg.displayTitle(lSender, "Load cost of storage from conf file");
			/*
			 * 
			 */
			String lDir = BKStaticDir.getCONF();
			String lNameFile = BKStaticNameFile.getCONF_BKCURRENCY();
			ReadFile lReadFile = new ReadFile(lDir, lNameFile, ReadFile.comReadFile.FULL_COM);
			/*
			 * Read file
			 */
			Map<String, List<Double>> lMapNameCurrencyToListCost = new HashMap<>();
			for (List<String> lLineStr : lReadFile.getmContentList()) {
				/*
				 * Load line
				 */
				int lIdx = -1;
				String lNameCurrency = lLineStr.get(++lIdx);
				double lCostBorrowFromPRoy = BasicString.getDouble(lLineStr.get(++lIdx));
				double lCostShortPosition = BasicString.getDouble(lLineStr.get(++lIdx));
				double lCostLongPosition = BasicString.getDouble(lLineStr.get(++lIdx));
				/*
				 * 
				 */
				if (lCostBorrowFromPRoy < 0) {
					BKCom.error("The cost of borrow from the conf file cannot be negative"
							+ "\nCurrency= " + lNameCurrency
							+ "\nCost of borrow= " + lCostBorrowFromPRoy
							+ "\nConf file= " + lReadFile.getmDirPlusNameFile());
				}
				if (lCostShortPosition < 0) {
					BKCom.error("The cost of borrow from the conf file cannot be negative"
							+ "\nCurrency= " + lNameCurrency
							+ "\nCost of short position= " + lCostShortPosition
							+ "\nConf file= " + lReadFile.getmDirPlusNameFile());
				}
				if (lCostLongPosition < 0) {
					BKCom.error("The cost of borrow from the conf file cannot be negative"
							+ "\nCurrency= " + lNameCurrency
							+ "\nCost of long position= " + lCostLongPosition
							+ "\nConf file= " + lReadFile.getmDirPlusNameFile());
				}
				/*
				 * Store the cost of storage + create the BKAssetMetal
				 */
				List<Double> lListCost = new ArrayList<>();
				lListCost.add(lCostBorrowFromPRoy);
				lListCost.add(lCostShortPosition);
				lListCost.add(lCostLongPosition);
				lMapNameCurrencyToListCost.put(lNameCurrency, lListCost);
				BKAssetManager.getpAndCheckBKAssetCurrency(lNameCurrency, lReadFile.getmDirPlusNameFile());
			}
			/*
			 * Check all BKAssetMetal has a cost of storage 
			 */
			for (BKAssetCurrency lBKAssetCurrency : BKAssetManager.getpListBKAssetCurrencySorted()) {
				String lNameCurrency = lBKAssetCurrency.getpName();
				List<Double> lListCost = lMapNameCurrencyToListCost.get(lNameCurrency);
				if (lListCost == null) {
					BKCom.error("The BKAssetCurrency is not in the CONF file. You must add it to the conf file"
							+ "\nCurrency name= '" + lNameCurrency + "'"
							+ "\nConf file to update= " + lReadFile.getmDirPlusNameFile());
				}
				lBKAssetCurrency.setpCostOfBorrowToPRoy(lListCost.get(0));
				lBKAssetCurrency.setpCostOfShortPosition(lListCost.get(1));
				lBKAssetCurrency.setpCostOfLongPosition(lListCost.get(2));
				BasicPrintMsg.display(lSender, "Costs for the currency " + lBKAssetCurrency.getpName() + "= " + lListCost);
			}
			/*
			 * Communication
			 */
			BasicPrintMsg.display(lSender, "All BKAssetCurrency have been assigned with their costs of borrow");
		}
	}


}
