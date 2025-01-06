package step01_objects_from_conf_files.conffiles;

import java.util.List;

import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.ReadFile;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;

public class BKConfLoaderDrawCommercial {

	/*
	 * Data
	 */
	private static boolean IS_LOADED = false;
	private static double LIMIT_DRAW_COMMERCIAL;

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
			String lNameFile = BKStaticNameFile.getCONF_DRAW_COMMERCIAL();
			ReadFile lReadFile = new ReadFile(lDir, lNameFile, ReadFile.comReadFile.FULL_COM);
			/*
			 * Read file
			 */
			for (List<String> lLineStr : lReadFile.getmContentList()) {
				/*
				 * Load line
				 */
				int lIdx = -1;
				LIMIT_DRAW_COMMERCIAL = BasicString.getDouble(lLineStr.get(++lIdx));
				/*
				 * 
				 */
				break;
			}
		}
	}

	/*
	 * Getters & Setters
	 */
	public static final double getLIMIT_DRAW_COMMERCIAL() {
		return LIMIT_DRAW_COMMERCIAL;
	}
	
}
