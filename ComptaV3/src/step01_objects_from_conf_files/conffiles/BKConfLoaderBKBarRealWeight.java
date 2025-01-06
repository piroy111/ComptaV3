package step01_objects_from_conf_files.conffiles;

import java.util.List;

import basicmethods.BasicFichiersNio;
import basicmethods.BasicString;
import basicmethods.ReadFile;
import basicmethods.ReadFile.comReadFile;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;

public class BKConfLoaderBKBarRealWeight {


	private static boolean IS_LOADED = false;

	/**
	 * 
	 */
	public final static void loadConfFile() {
		if (!IS_LOADED) {
			IS_LOADED = true;
			String lDir = BKStaticDir.getBKBAR_TRUE_WEIGHT();
			List<String> lListNameFile = BasicFichiersNio.getListFilesAndDirectoriesInDirectory(lDir);
			for (String lNameFile : lListNameFile) {
				/*
				 * Check for error in the name of the file
				 */
				if (!lNameFile.startsWith(BKStaticNameFile.getPREFIX_BKBAR_REAL_WEIGHT())) {
					BKCom.error("The file name does not begin with the expected prefix"
							+ "\nDir= '" + lDir + "'"
							+ "\nFile name= '" + lNameFile + "'"
							+ "\nExpected prefix= '" + BKStaticNameFile.getPREFIX_BKBAR_REAL_WEIGHT() + "'");
				}
				/*
				 * Read file
				 */
				ReadFile lReadFile = new ReadFile(lDir, lNameFile, comReadFile.FULL_COM);
				for (List<String> lLine : lReadFile.getmContentList()) {
					if (lLine.size() >= 3) {
						/*
						 * Load line
						 */
						int lIdx = -1;
						BKAssetMetal lBKAssetMetal = BKAssetManager.getpAndCheckBKAssetMetal(lLine.get(++lIdx), lReadFile.getmDirPlusNameFile());
						String lID = lLine.get(++lIdx);
						double lWeight = BasicString.getDouble(lLine.get(++lIdx));
						/*
						 * 
						 */
						lBKAssetMetal.getpOrCreateAndCheckBKBar(lID, lWeight, lReadFile.getmDirPlusNameFile());
					}
				}
			}
		}
	}


}
