package step04_debug.standalones.buildbalancesformalca;

import java.util.List;
import java.util.Map;

import basicmethods.BasicString;
import basicmethods.ReadFile;
import basicmethods.ReadFile.comReadFile;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;

class MSFileDate {

	protected MSFileDate(int _sDate, MSFolder _sMSFolder) {
		pDate = _sDate;
		pMSFolder = _sMSFolder;
	}
	
	/*
	 * Data
	 */
	private int pDate;
	private String pNameFile;
	private String pDir;
	private MSFolder pMSFolder;
	private Map<BKAsset, Double> pMapBKAssetToBalance;
	private ReadFile pReadFile;
	
	/**
	 * Read file + compute balances
	 */
	public final void run() {
		pReadFile = new ReadFile(pDir, pNameFile, comReadFile.FULL_COM);
		for (List<String> lLine : pReadFile.getmContentList()) {
			/*
			 * Load line
			 */
			int lIdx = -1;
			int lDate = BasicString.getInt(lLine.get(++lIdx));
			BKAsset lBKAsset = BKAssetManager.getpAndCheckBKAsset(lLine.get(++lIdx), pReadFile.getmDirPlusNameFile());
			lIdx++;
			double lQuantity = BasicString.getDouble(lLine.get(++lIdx));
			/*
			 * 
			 */
			pMSFolder.addFlow(lDate, lBKAsset, lQuantity);
		}
	}
	
	
	/**
	 * 
	 * @param _sBKAsset
	 * @return
	 */
	public final double getpOrCreateBalance(BKAsset _sBKAsset) {
		Double lBalance = pMapBKAssetToBalance.get(_sBKAsset);
		if (lBalance == null) {
			lBalance = 0.;
			pMapBKAssetToBalance.put(_sBKAsset, lBalance);
		}
		return lBalance;
	}
	
	/**
	 * 
	 * @param _sBKAsset
	 * @param _sBalanceToAdd
	 */
	public final void addBalance(BKAsset _sBKAsset, double _sBalanceToAdd) {
		Double lBalance = getpOrCreateBalance(_sBKAsset);
		lBalance += _sBalanceToAdd;
		pMapBKAssetToBalance.put(_sBKAsset, lBalance);
	}
	
	/*
	 * Getters & Setters
	 */
	public final int getpDate() {
		return pDate;
	}
	public final MSFolder getpMSFolder() {
		return pMSFolder;
	}
	public final Map<BKAsset, Double> getpMapBKAssetToBalance() {
		return pMapBKAssetToBalance;
	}
	public final ReadFile getpReadFile() {
		return pReadFile;
	}
	public final String getpNameFile() {
		return pNameFile;
	}
	public final String getpDir() {
		return pDir;
	}
	public final void setpDir(String _sPDir) {
		pDir = _sPDir;
	}
	public final void setpNameFile(String _sPNameFile) {
		pNameFile = _sPNameFile;
	}
	
	
}
