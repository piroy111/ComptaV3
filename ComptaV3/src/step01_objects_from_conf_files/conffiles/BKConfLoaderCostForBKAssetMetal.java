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
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;

public class BKConfLoaderCostForBKAssetMetal {

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
			Object lSender = new BKConfLoaderCostForBKAssetMetal();
			BasicPrintMsg.displayTitle(lSender, "Load cost of storage from conf file");
			/*
			 * 
			 */
			String lDir = BKStaticDir.getCONF();
			String lNameFile = BKStaticNameFile.getCONF_BKASSETMETAL();
			ReadFile lReadFile = new ReadFile(lDir, lNameFile, ReadFile.comReadFile.FULL_COM);
			/*
			 * Read file
			 */
			Map<String, List<Double>> lMapNameMetalToListCost = new HashMap<>();
			for (List<String> lLineStr : lReadFile.getmContentList()) {
				/*
				 * Load line
				 */
				int lIdx = -1;
				String lNameMetal = lLineStr.get(++lIdx);
				double lCostStorage = BasicString.getDouble(lLineStr.get(++lIdx));
				double lCostOfBorrowFromPRoy = BasicString.getDouble(lLineStr.get(++lIdx));
				/*
				 * 
				 */
				if (lCostStorage < 0) {
					BKCom.error("The cost of storage cannot be < 0"
							+ "\nCost of storage= " + lCostStorage
							+ "\nBKAssetMetal= " + lNameMetal
							+ "\nConf file= '" + lDir + lNameFile + "'");
				}
				if (lCostOfBorrowFromPRoy < 0) {
					BKCom.error("The cost of borrow from PRoy cannot be < 0"
							+ "\nCost of borrow from PRoy= " + lCostOfBorrowFromPRoy
							+ "\nBKAssetMetal= " + lNameMetal
							+ "\nConf file= '" + lDir + lNameFile + "'");
				}
				/*
				 * Store the cost of storage + create the BKAssetMetal
				 */
				List<Double> lListCost = new ArrayList<>();
				lListCost.add(lCostStorage);
				lListCost.add(lCostOfBorrowFromPRoy);
				lMapNameMetalToListCost.put(lNameMetal, lListCost);
				/*
				 * Check the metal exists
				 */
				String lNameBKAssetMetal = lNameMetal + " BAR OZ";
				BKAssetManager.getpAndCheckBKAssetMetal(lNameBKAssetMetal, lReadFile.getmDirPlusNameFile());
			}
			/*
			 * Check all BKAssetMetal has a cost of storage 
			 */
			for (BKAssetMetal lBKAssetMetal : BKAssetManager.getpListBKAssetMetalSorted()) {
				String lNameMetal = lBKAssetMetal.getpNameMetal();
				List<Double> lListCost = lMapNameMetalToListCost.get(lNameMetal);
				if (lListCost == null) {
					BKCom.error("The BKAssetMetal is not in the CONF file. You must add it to the conf file"
							+ "\nMetal name= '" + lNameMetal + "'"
							+ "\nConf file to update= " + lReadFile.getmDirPlusNameFile());
				}
				lBKAssetMetal.setpCostStorage(lListCost.get(0));
				lBKAssetMetal.setpCostOfBorrowFromProy(lListCost.get(1));
				BasicPrintMsg.display(lSender, "Costs for the metal " + lBKAssetMetal.getpName() + "= " + lListCost);
			}
			/*
			 * Communication
			 */
			BasicPrintMsg.display(lSender, "All BKAssetMetal have been assigned with a cost of storage");
		}
	}


}
