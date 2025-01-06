package step02_load_transactions.interactivebrokers.createfilestransactions;

import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticDir;
import step02_load_transactions.interactivebrokers.createfilestransactions.files.IBFileManager;
import step02_load_transactions.interactivebrokers.createfilestransactions.files.IBFindDirAndSuffix;
import step02_load_transactions.interactivebrokers.createfilestransactions.reports.IBReportManager;
import step02_load_transactions.interactivebrokers.createfilestransactions.transactions.IBTransactionManager;
import step02_load_transactions.interactivebrokers.createfilestransactions.writefile.IBWriteFileManager;

public class IBManager {

	
	public static void main(String[] _sArray) {
		BKStaticDir.detectDIR(new String[] {"G:/My Drive/Compta_bunker_v3/11_Program_to_run/"});
		new IBManager().run();		
	}
	
	public IBManager() {
		pIBFileManager = new IBFileManager(this);
		pIBReportManager = new IBReportManager(this);
		pIBTransactionManager = new IBTransactionManager(this);
		pIBWriteFileManager = new IBWriteFileManager(this);
		pIBFindDirAndSuffix = new IBFindDirAndSuffix(this);
	}
	
	
	/*
	 * Data
	 */
	private IBFileManager pIBFileManager;
	private IBReportManager pIBReportManager;
	private IBTransactionManager pIBTransactionManager;
	private IBWriteFileManager pIBWriteFileManager;
	private IBFindDirAndSuffix pIBFindDirAndSuffix;
	
	/**
	 * 
	 */
	public final void run() {
		BasicPrintMsg.displaySuperTitle(this, "Read the reports of InteractiveBrokers and create file of latest transactions");
		runForStandAlone();
		pIBWriteFileManager.write();
	}
	
	/**
	 * Load the IBTransaction without writing a file treated
	 */
	public final void runForStandAlone() {
		pIBFindDirAndSuffix.run();
		pIBFileManager.checkFiles();
		pIBReportManager.run();
		pIBTransactionManager.createIBTransactions();
	}
	
	/*
	 * Getters & Setters
	 */
	public final IBFileManager getpIBFileManager() {
		return pIBFileManager;
	}
	public final IBReportManager getpIBReportManager() {
		return pIBReportManager;
	}
	public final IBTransactionManager getpIBTransactionManager() {
		return pIBTransactionManager;
	}
	public final IBWriteFileManager getpIBWriteFileManager() {
		return pIBWriteFileManager;
	}

	public final IBFindDirAndSuffix getpIBFindDirAndSuffix() {
		return pIBFindDirAndSuffix;
	}


	
	
	
	
}
