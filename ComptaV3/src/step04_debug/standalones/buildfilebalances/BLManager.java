package step04_debug.standalones.buildfilebalances;

import java.util.List;

import basicmethods.BasicFichiers;
import step02_load_transactions.objects.transaction.BKTransaction;

public class BLManager {

	public BLManager() {
		pBLMissingDatesFinder = new BLMissingDatesFinder(this);
		pBLTransactionsLoader = new BLTransactionsLoader(this);
		pBLFileWriter = new BLFileWriter(this);
		pBLDirsAndNamesFiles = new BLDirsAndNamesFiles(this);
		pBLBalanceDateManager = new BLBalanceDateManager(this);
	}

	/*
	 * Data
	 */
	private BLMissingDatesFinder pBLMissingDatesFinder;
	private BLTransactionsLoader pBLTransactionsLoader;
	private BLFileWriter pBLFileWriter;
	private BLDirsAndNamesFiles pBLDirsAndNamesFiles;
	private String pDirMain;
	private BLBalanceDateManager pBLBalanceDateManager;

	/**
	 * does not read the transactions from the folders. Take them directly as arguments<br>
	 * then we generate and write a file of balance for all missing dates<br>
	 * @param _sDirMain
	 * @param _sListBKTransaction
	 */
	public final void run(String _sDirMain, List<BKTransaction> _sListBKTransaction) {
		pBLTransactionsLoader.setpListBKTransaction(_sListBKTransaction);
		run(_sDirMain);
	}
	
	
	/**
	 * Will read the transactions from the folder, use the existing structure of BKTransactionManager<br>
	 * then we generate and write a file of balance for all missing dates<br>
	 * @param _sDirMain: example= 'G:\My Drive\Compta_bunker_v3\01_Load_transactions_physical\Broker_OANDA'
	 */
	public final void run(String _sDirMain) {
		pDirMain = BasicFichiers.getDirectoryNameCorrect(_sDirMain);
		/*
		 * Find the names of the DIR and suffix
		 */
		pBLDirsAndNamesFiles.run();
		/*
		 * Find out which dates of files balances are missing
		 */
		pBLMissingDatesFinder.run();
		if (pBLMissingDatesFinder.getpListMissingDates().size() > 0) {
			/*
			 * Load BKTransactions
			 */
			pBLTransactionsLoader.run();
			/*
			 * Compute missing balances
			 */
			pBLBalanceDateManager.run();
			/*
			 * 
			 */
			pBLFileWriter.run();
		}
	}

	/*
	 * Getters & Setters
	 */
	public final BLMissingDatesFinder getpBLMissingDatesFinder() {
		return pBLMissingDatesFinder;
	}
	public final BLTransactionsLoader getpBLTransactionsLoader() {
		return pBLTransactionsLoader;
	}
	public final BLFileWriter getpBLFileWriter() {
		return pBLFileWriter;
	}
	public final String getpDirMain() {
		return pDirMain;
	}
	public final BLDirsAndNamesFiles getpBLDirsAndNamesFiles() {
		return pBLDirsAndNamesFiles;
	}
	public final BLBalanceDateManager getpBLBalanceDateManager() {
		return pBLBalanceDateManager;
	}

}
