package step02_load_transactions.interactivebrokers.createfilestransactions.files;

import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step02_load_transactions.interactivebrokers.createfilestransactions.IBManager;
import step02_load_transactions.transactions_loaders.loaders_physical.BKPhysicalManager;

public class IBFindDirAndSuffix {

	public IBFindDirAndSuffix(IBManager _sIBManager) {
		pIBManager = _sIBManager;
	}

	/*
	 * Data
	 */
	private IBManager pIBManager;
	private String pDirInput;
	private String pDirTransactions;
	private String pSuffixTransactions;
	private String pSuffixInput;

	/**
	 * 
	 */
	public final void run() {
		BasicPrintMsg.displayTitle(this, "Find dir and suffix");
		/*
		 * DIR
		 */
		pDirInput = BKStaticDir.getLOAD_TRANSACTIONS_PHYSICAL() 
				+ BKStaticConst.getBKENTITY_IB() + "/" + BKStaticDir.getSUB_DIR_IB_REPORTS();
		pDirTransactions = BKStaticDir.getLOAD_TRANSACTIONS_PHYSICAL() 
				+ BKStaticConst.getBKENTITY_IB() + "/" + BKStaticDir.getPHYSICAL_SUBFOLDER_TRANSACTIONS();
		BasicPrintMsg.display(this, "pDirInput= " + pDirInput);
		BasicPrintMsg.display(this, "pDirTransactions= " + pDirTransactions);
		/*
		 * SUFFIX
		 */
		String lMidFix = BKPhysicalManager.getMidFix(BKPhysicalManager.class.getSimpleName());
		pSuffixTransactions = "_" + BKStaticConst.getBKENTITY_IB()
				+ "_" + lMidFix
				+ BKStaticNameFile.getSUFFIX_TRANSACTIONS();
		pSuffixInput = BKStaticNameFile.getSUFFIX_IB();
		BasicPrintMsg.display(this, "pSuffixTransactions= " + pSuffixTransactions);
		BasicPrintMsg.display(this, "pSuffixInput= " + pSuffixInput);
	}

	/*
	 * Getters & Setters
	 */
	public final String getpDirTransactions() {
		return pDirTransactions;
	}
	public final String getpSuffixTransactions() {
		return pSuffixTransactions;
	}
	public final IBManager getpIBManager() {
		return pIBManager;
	}
	public final String getpDirInput() {
		return pDirInput;
	}
	public final String getpSuffixInput() {
		return pSuffixInput;
	}

}
