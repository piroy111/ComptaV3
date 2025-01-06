package step01_objects_from_conf_files.asset.sample;

import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticDir;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;

public class BKSampleBKAssetManager {

	public static void main(String[] _sArgs) {
		BKStaticDir.detectDIR(_sArgs);
		/*
		 * Display
		 */
		String lTypeBKAsset = "";
		for (BKAsset lBKAsset : BKAssetManager.getpListBKAssetSorted()) {
			if (!lTypeBKAsset.equals(lBKAsset.getpAssetTypeStr())) {
				lTypeBKAsset = lBKAsset.getpAssetTypeStr();
				System.out.println();
				System.out.println(lTypeBKAsset + ":");
			}
			System.out.println("     "
					+ BasicPrintMsg.getJustifiedText(lBKAsset.getpName(), 15) 
					+ "= " + lBKAsset.getpTreeMapDateToPrice());
		}
	}
	
}
