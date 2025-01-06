package step07_reconciliation.reconciliators.platform.bars_outside_platform;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicFichiersNioRaw;
import basicmethods.ReadFile;
import basicmethods.ReadFile.comReadFile;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticDir;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.asset.bar.BKBar;
import step07_reconciliation.reconciliators.platform.bars_outside_platform.per_date.RNPlatformOutsideDate;

public class RNPlatformOutsideManager {

	public RNPlatformOutsideManager() {
		pMapDateToRNPlatformOutsideDate = new HashMap<>();
	}

	/*
	 * Data
	 */
	private Map<Integer, RNPlatformOutsideDate> pMapDateToRNPlatformOutsideDate;

	/**
	 * 
	 * @param _sDate
	 * @return
	 */
	public final RNPlatformOutsideDate getpOrCreateRNPlatformOutsideDate(int _sDate) {
		RNPlatformOutsideDate lRNPlatformOutsideDate = pMapDateToRNPlatformOutsideDate.get(_sDate);
		if (lRNPlatformOutsideDate == null) {
			lRNPlatformOutsideDate = new RNPlatformOutsideDate(_sDate, this);
			pMapDateToRNPlatformOutsideDate.put(_sDate, lRNPlatformOutsideDate);
		}
		return lRNPlatformOutsideDate;
	}

	/**
	 * 
	 * @param _sDate
	 * @return
	 */
	public final RNPlatformOutsideDate getpAndCheckRNPlatformOutsideDate(int _sDate) {
		RNPlatformOutsideDate lRNPlatformOutsideDate = pMapDateToRNPlatformOutsideDate.get(_sDate);
		if (lRNPlatformOutsideDate == null) {
			/* 
			 * Initiate data for the file to read and load
			 */
			String lDir = BKStaticDir.getRECONCILIATION_BARS_OUTSIDE_PLATFORM();
			String lNameFile = getNameFile(_sDate);
			Path lPath = Paths.get(lDir + lNameFile);
			/*
			 * if the file exists then we read it and fill the RNPlatformOutsideDate
			 */
			if (BasicFichiersNioRaw.getIsAlreadyExist(lPath)) {
				lRNPlatformOutsideDate = getpOrCreateRNPlatformOutsideDate(_sDate);
				ReadFile lReadFile = new ReadFile(lPath, comReadFile.FULL_COM);
				for (List<String> lLineStr : lReadFile.getmContentList()) {
					int lIdx = -1;
					String lIDBKBar = lLineStr.get(++lIdx);
					String lBKAssetStr = lLineStr.get(++lIdx);					
					/*
					 * 
					 */
					BKAssetMetal lBKAssetMetal = BKAssetManager.getpAndCheckBKAssetMetal(lBKAssetStr, lNameFile);
					BKBar lBKBar = lBKAssetMetal.getpMapIDToBKBar().get(lIDBKBar);
					if (lBKBar == null) {
						BKCom.error("The BKBar does not exist. The ID must be wrong."
								+ "\nBKBar ID= " + lIDBKBar
								+ "\nFile origin with error= " + lReadFile.getmDirPlusNameFile());
					}
					/*
					 * Declare the BKBar as outside of platform 
					 */
					lRNPlatformOutsideDate.declareNewBKBarOutsideOfPlatform(lBKBar);
				}
			}
			/*
			 * Else we crash with error
			 */
			else {
				BKCom.error("The file of BKBars outside of platform does not exist for the date " + _sDate
						+ "\nYou must create a file and put the BKBars which are outside of platform so I can make the reconciliation platform/COMPTA"
						+ "\nDir= '" + BKStaticDir.getRECONCILIATION_BARS_OUTSIDE_PLATFORM() + "'"
						+ "\nFile missing= '" + getNameFile(_sDate) + "'");
			}
		}
		return lRNPlatformOutsideDate;
	}

	/**
	 * 
	 * @param _sDate
	 * @return
	 */
	public String getNameFile(int _sDate) {
		return _sDate + "_" + BKStaticConst.getBKINCOME_BARS_OUTSIDE_PLATFORM() + ".csv";
	}

}
