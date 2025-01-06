package step04_debug.standalones.buildfilebalances;

import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;

class BLDirsAndNamesFiles {

	protected BLDirsAndNamesFiles(BLManager _sBLManager) {
		pBLManager = _sBLManager;
	}
	
	/*
	 * Data
	 */
	private BLManager pBLManager;
	private String pDirBalance;
	private String pDirTransactions;
	private String pMidFix;
	private String pSuffixBalance;
	private String pSuffixTransactions;
	
	/**
	 * 
	 */
	public final void run() {
		BasicPrintMsg.displayTitle(this, "Find dir and suffix");
		/*
		 * DIR
		 */
		pDirTransactions = pBLManager.getpDirMain() + BKStaticDir.getPHYSICAL_SUBFOLDER_TRANSACTIONS();
		pDirBalance = pBLManager.getpDirMain() + BKStaticDir.getPHYSICAL_SUBFOLDER_BALANCES();
		BasicPrintMsg.display(this, "pDirBalance= " + pDirBalance);
		BasicPrintMsg.display(this, "pDirTransactions = " + pDirTransactions);
		/*
		 * MIDFIX
		 */
		String[] lWords = pBLManager.getpDirMain().split("/", -1);
		pMidFix = lWords[lWords.length - 2];
		BasicPrintMsg.display(this, "pMidFix= " + pMidFix);
		/*
		 * SUFFIX
		 */
		String[] lWords2 = lWords[lWords.length - 3].split("_", -1);
		String lType = lWords2[lWords2.length - 1];
		lType = lType.substring(0, 1).toUpperCase() + lType.substring(1);
		pSuffixTransactions = "_" + pMidFix + "_" + lType + BKStaticNameFile.getSUFFIX_TRANSACTIONS();
		pSuffixBalance = "_" + pMidFix + "_" + lType + BKStaticNameFile.getSUFFIX_BALANCES();
		BasicPrintMsg.display(this, "pSuffixTransactions= " + pSuffixTransactions);
		BasicPrintMsg.display(this, "pSuffixBalance= " + pSuffixBalance);
	}

	/*
	 * Getters & Setters
	 */
	public final BLManager getpBLManager() {
		return pBLManager;
	}
	public final String getpDirBalance() {
		return pDirBalance;
	}
	public final String getpDirTransactions() {
		return pDirTransactions;
	}
	public final String getpMidFix() {
		return pMidFix;
	}
	public final String getpSuffixBalance() {
		return pSuffixBalance;
	}
	public final String getpSuffixTransactions() {
		return pSuffixTransactions;
	}
	
	
}
