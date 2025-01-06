package step04_debug.bars;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import staticdata.com.fileswritten.BKComOnFilesWritten;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticConst.com_file_written;
import staticdata.datas.BKStaticDir;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.asset.bar.BKBar;

public class BKDebugPullOutAllBKBars {

	
	
	public final static String run() {
		int lDate = BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS();
		/*
		 * 
		 */
		List<String> lListLineToWrite = new ArrayList<>();
		for (BKAssetMetal lBKAssetMetal : BKAssetManager.getpListBKAssetMetalSorted()) {
			for (BKBar lBKBar : lBKAssetMetal.getpMapIDToBKBar().values()) {
				String lLine = lDate
						+ "," + lBKAssetMetal.getpName()
						+ "," + lBKBar.getpID()
						+ "," + lBKBar.getpWeightOz()
						+ "," + lBKBar.getpBKBarType().getpNaturalWeightStr()
						+ "," + lBKBar.getpIsDelivered(lDate)
						+ "," + (lBKBar.getpBKAccountOwner(lDate) == null ? "?" : lBKBar.getpBKAccountOwner(lDate).getpEmail())
						+ "," + (lBKBar.getpBKEntity(lDate) == null ? "?" : lBKBar.getpBKEntity(lDate).getpName())
						+ "," + lBKBar.getpListFileNameOrigin().toString().replaceAll(",", ";");
				lListLineToWrite.add(lLine);						
			}
		}
		/*
		 * 
		 */
		String lHeader = "Date,BKAsset,ID of BKBar,Weight (Oz),Natural weight type,Is delivered?"
				+ ",Owner as of " + lDate 
				+ ",BKEntity as of " + lDate
				+ ",File origin";
		String lNameClass = BKDebugPullOutAllBKBars.class.getSimpleName();
		String lDir = BKStaticDir.getOUTPUT_DEBUG() + lNameClass + "/";
		BasicFichiers.getOrCreateDirectory(lDir);
		String lNameFile = BasicDateInt.getmToday() + "_" + lNameClass + ".csv";
		BKComOnFilesWritten.writeFile(com_file_written.Debug, lDir, lNameFile, lHeader, lListLineToWrite);
		return lDir + lNameFile;
	}
	
	
}
