package step04_debug.standalones.buildfilesformalca;

import staticdata.datas.BKStaticDir;

class STL_Files_builder_for_Malca_SG {

	public static void main(String[] _sArgs) {
		BKStaticDir.detectDIR(_sArgs);
		new STL_Files_builder_for_Malca_SG().run();
	}
	
	protected STL_Files_builder_for_Malca_SG() {
		pMSLoadFilesTransactions = new MSLoadFilesTransactions(this);
		pMSFileToWriteManager = new MSFileToWriteManager();
	}
	
	/*
	 * Static
	 */
	private static String DIR_OUTPUT_HEDGE = "G:/My Drive/Compta_bunker_v3/03_Load_transactions_transfer/Hedge_refiners/Transfers in compta format/";
	private static String SUFFIX_HEDGE = "_Hedge_refiners_Transfer_Transactions.csv";
	private static String DIR_OUTPUT_TRANSACTIONS = "G:/My Drive/Compta_bunker_v3/01_Load_transactions_physical/Vault_Malca_SG/Physical transactions in compta format/";
	private static String DIR_PREFIX_INPUT ="Refiner_";
	/*
	 * Data
	 */
	private MSLoadFilesTransactions pMSLoadFilesTransactions;
	private MSFileToWriteManager pMSFileToWriteManager;
	
	/**
	 * 
	 */
	public final void run() {		
		pMSLoadFilesTransactions.run();
		pMSFileToWriteManager.writeFiles();
	}

	/*
	 * Getters & Setters
	 */
	public static final String getDIR_OUTPUT_HEDGE() {
		return DIR_OUTPUT_HEDGE;
	}
	public static final String getSUFFIX_HEDGE() {
		return SUFFIX_HEDGE;
	}
	public static final String getDIR_OUTPUT_TRANSACTIONS() {
		return DIR_OUTPUT_TRANSACTIONS;
	}
	public static final String getDIR_PREFIX_INPUT() {
		return DIR_PREFIX_INPUT;
	}
	public final MSFileToWriteManager getpMSFileToWriteManager() {
		return pMSFileToWriteManager;
	}
	
}
