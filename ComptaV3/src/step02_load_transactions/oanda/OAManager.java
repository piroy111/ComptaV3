package step02_load_transactions.oanda;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step02_load_transactions.oanda.firstdate.OAFirstDateManager;
import step02_load_transactions.oanda.loadreports.OAReportManager;
import step02_load_transactions.oanda.writefile.OAWriteFileManager;
import step02_load_transactions.transactions_loaders.loaders_physical.BKPhysicalManager;

public class OAManager {

	public static void main(String[] _sArgs) {
		BKStaticDir.detectDIR(_sArgs);
		new OAManager().run();
	}
	
	public OAManager() {
		pOAWriteFileManager = new OAWriteFileManager(this);
		pOAFirstDateManager = new OAFirstDateManager(this);
		pListOAReportManager = new ArrayList<>();
		pListOAReportManager.add(new OAReportManager(this, BKStaticNameFile.getSUFFIX_OANDA()));		// 	Normal account
		pListOAReportManager.add(new OAReportManager(this, BKStaticNameFile.getSUFFIX_OANDA_CRYPTOS()));	//	sub-account which was used for CRYPTOS and is no longer used
	}

	/*
	 * Data
	 */
	private List<OAReportManager> pListOAReportManager;
	private OAWriteFileManager pOAWriteFileManager;
	private OAFirstDateManager pOAFirstDateManager;
	private String pDirOutput;
	private String pDirInput;
	private String pSuffixOutput;
	
	/**
	 * 
	 */
	public final void run() {
		BasicPrintMsg.displayTitle(this, "Read new reports of OANDA and create new BKTransactions' file");
		/*
		 * Build the names of directories and suffix
		 */
		pDirInput = BKStaticDir.getLOAD_TRANSACTIONS_PHYSICAL() 
				+ BKStaticConst.getBKENTITY_OANDA() + "/" + BKStaticDir.getSUB_DIR_OANDA_REPORTS();
		pDirOutput = BKStaticDir.getLOAD_TRANSACTIONS_PHYSICAL() 
				+ BKStaticConst.getBKENTITY_OANDA() + "/" + BKStaticDir.getPHYSICAL_SUBFOLDER_TRANSACTIONS();
		String lMidFix = BKPhysicalManager.getMidFix(BKPhysicalManager.class.getSimpleName());
		pSuffixOutput = "_" + BKStaticConst.getBKENTITY_OANDA()
			+ "_" + lMidFix
			+ BKStaticNameFile.getSUFFIX_TRANSACTIONS();
		/*
		 * Check the first date to download reports
		 */
		pOAFirstDateManager.run();
		/*
		 * Read files report and create transactions
		 */
		for (OAReportManager lOAReportManager : pListOAReportManager) {
			lOAReportManager.run();
		}
		/*
		 * Write file
		 */
		pOAWriteFileManager.run();
	}

	/*
	 * Getters & Setters
	 */
	public final List<OAReportManager> getpListOAReportManager() {
		return pListOAReportManager;
	}
	public final OAWriteFileManager getpOAWriteFileManager() {
		return pOAWriteFileManager;
	}
	public final String getpDirOutput() {
		return pDirOutput;
	}
	public final String getpDirInput() {
		return pDirInput;
	}
	public final String getpSuffixOutput() {
		return pSuffixOutput;
	}
	public final OAFirstDateManager getpOAFirstDateManager() {
		return pOAFirstDateManager;
	}
	
	
}
