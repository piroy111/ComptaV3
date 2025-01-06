package step01_objects_from_conf_files.asset.bar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicString;
import basicmethods.ReadFile;
import basicmethods.ReadFile.comReadFile;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;

public class BKBarTypeManager {

	/*
	 * Data
	 */
	private static Map<String, BKBarType> pMapNameToBKBarType;

	/**
	 * 
	 */
	private static void loadFileConf() {
		if (pMapNameToBKBarType == null) {
			pMapNameToBKBarType = new HashMap<String, BKBarType>();
			/*
			 * Load file
			 */
			String lDir = BKStaticDir.getCONF();
			String lNameFile = BKStaticNameFile.getCONF_BKBARTYPE();
			ReadFile lReadFile = new ReadFile(lDir, lNameFile, comReadFile.FULL_COM);
			/*
			 * Fill the list
			 */
			for (List<String> lLineStr : lReadFile.getmContentList()) {
				/*
				 * Load line
				 */
				int lIdx = -1;
				String lName = lLineStr.get(++lIdx);
				String lOz = lLineStr.get(++lIdx);
				String lGrams = lLineStr.get(++lIdx);
				/*
				 * Load weight
				 */
				double lWeightOz = Double.NaN;
				double lWeightGrams = Double.NaN;
				String lNaturalUnit = "";
				double lNaturalWeight = Double.NaN;
				if (!lOz.equals("")) {
					lNaturalUnit = "Oz";
					lWeightOz = BasicString.getDouble(lOz);
					lWeightGrams = lWeightOz * BKStaticConst.getOZ();
					lNaturalWeight = lWeightOz;
				} else if (!lGrams.equals("")) {
					lNaturalUnit = "g";
					lWeightGrams = BasicString.getDouble(lGrams);
					lWeightOz = lWeightGrams / BKStaticConst.getOZ();
					lNaturalWeight = lWeightGrams;
				} else {
					BKCom.error("Only one column can be empty"
							+ "\nLine in error = " + lLineStr.toString()
							+ "\nFile= " + lReadFile.getmDirPlusNameFile());
				}
				/*
				 * Create BKBarType
				 */
				for (BKAssetMetal lBKAssetMetal : BKAssetManager.getpMapNameToBKAssetMetal().values()) {
					String lNameFromMetal = lBKAssetMetal.getpNameMetal() + " " + lName;
					BKBarType lBKBarType = pMapNameToBKBarType.get(lNameFromMetal);
					if (lBKBarType == null) {
						lBKBarType = new BKBarType(lNameFromMetal, 
								lWeightOz, lWeightGrams, lNaturalUnit, lNaturalWeight, lBKAssetMetal);
						pMapNameToBKBarType.put(lNameFromMetal, lBKBarType);
					}
				}
			}
		}
	}

	/**
	 * get the nearest BKBarType
	 * @param _sWeightOz
	 * @return
	 */
	public static final BKBarType getpBKBarType(double _sWeightOz, BKAssetMetal _sBKAssetMetal) {
		loadFileConf();
		BKBarType lBKBarTypeNear = null;
		double lDistanceMin = Double.NaN;
		for (BKBarType lBKBarType : pMapNameToBKBarType.values()) {
			if (lBKBarType.getpBKAssetMetal().equals(_sBKAssetMetal)) {
				double lDistance = Math.abs(_sWeightOz - lBKBarType.getpWeightOz());
				if (Double.isNaN(lDistanceMin) || lDistance < lDistanceMin) {
					lDistanceMin = lDistance;
					lBKBarTypeNear = lBKBarType;
				}
			}
		}
		return lBKBarTypeNear;
	}

	/*
	 * Getters & Setters
	 */
	public static final Map<String, BKBarType> getpMapNameToBKBarType() {
		loadFileConf(); return pMapNameToBKBarType;
	}

}
