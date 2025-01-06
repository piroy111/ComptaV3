package step02_load_transactions.transactions_loaders;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicPrintMsg;
import step02_load_transactions.objects.direntity.BKDirEntityManager;
import step02_load_transactions.transactions_loaders.debug.BKDebugStatic;
import step02_load_transactions.transactions_loaders.debug.BKDebugTransactionLoaderPhysical;
import step02_load_transactions.transactions_loaders.loaders.BKComptaManager;
import step02_load_transactions.transactions_loaders.loaders.BKPlatformManager;
import step02_load_transactions.transactions_loaders.loaders.BKTransferManager;
import step02_load_transactions.transactions_loaders.loaders_physical.BKPhysicalClosedManager;
import step02_load_transactions.transactions_loaders.loaders_physical.BKPhysicalOpenedManager;
import tictoc.BasicTicToc;

public class BKTransactionsLoader {

	/**
	 * Load existing transactions from existing files
	 */
	public BKTransactionsLoader() {
		/*
		 * Debug mode - allows to isolate a folder or some transactions
		 */
		if (BKDebugStatic.IS_DEBUG_MODE_TRANSACTION_LOADER) {
			pBKDebugTransactionLoaderPhysical = new BKDebugTransactionLoaderPhysical();
			pListBKDirEntityManager = new ArrayList<>();
			pListBKDirEntityManager.add(pBKDebugTransactionLoaderPhysical);
		} 
		/*
		 * Normal mode
		 */
		else {
			pBKPhysicalClosedManager = new BKPhysicalClosedManager();
			pBKPhysicalOpenedManager = new BKPhysicalOpenedManager();
			pBKPlatformManager = new BKPlatformManager();
			pBKVirtualManager = new BKTransferManager();
			pBKComptaManager = new BKComptaManager();
			/*
			 * 
			 */
			pListBKDirEntityManager = new ArrayList<>();
			pListBKDirEntityManager.add(pBKPhysicalClosedManager);
			pListBKDirEntityManager.add(pBKPhysicalOpenedManager);
			pListBKDirEntityManager.add(pBKPlatformManager);
			pListBKDirEntityManager.add(pBKVirtualManager);
			pListBKDirEntityManager.add(pBKComptaManager);
		}
	}

	/*
	 * Data
	 */
	private boolean pIsLoaded;
	private BKPhysicalClosedManager pBKPhysicalClosedManager;
	private BKPhysicalOpenedManager pBKPhysicalOpenedManager;
	private BKPlatformManager pBKPlatformManager;
	private BKTransferManager pBKVirtualManager;
	private BKComptaManager pBKComptaManager;
	private List<BKDirEntityManager> pListBKDirEntityManager;
	private BKDebugTransactionLoaderPhysical pBKDebugTransactionLoaderPhysical;

	/**
	 * List all files of transaction, balance, proof from every BKEntity
	 */
	public final void ListFilesOfBKEntities() {
		BasicPrintMsg.displaySuperTitle(this, "List all files");
		BasicPrintMsg.display(this, "List all files of transaction, balance, proof from every BKEntity");
		for (BKDirEntityManager lBKDirEntityManager : pListBKDirEntityManager) {
			lBKDirEntityManager.createBKEntities();
		}
	}

	/**
	 * Read all files of transaction
	 */
	public final void readFilesAndCreateBKTransactions() {
		if (!pIsLoaded) {
			pIsLoaded = true;
			/*
			 * Communication + Timer
			 */
			BasicPrintMsg.displaySuperTitle(this, "Load BKTransactions from various files");
			BasicTicToc.Start(this);
			/*
			 * Read all files
			 */
			for (BKDirEntityManager lBKDirEntityManager : pListBKDirEntityManager) {
				lBKDirEntityManager.readFiles();
			}
			/*
			 * Timer
			 */
			BasicTicToc.Stop(this);
			BasicTicToc.displayTotalDurationStr(this);
		}
	}



	/**
	 * @deprecated
	 */
	public final void load() {
		if (!pIsLoaded) {
			pIsLoaded = true;
			/*
			 * Communication + Timer
			 */
			BasicPrintMsg.displaySuperTitle(this, "Load BKTransactions from various files");
			BasicTicToc.Start(this);
			/*
			 * Load BKTransactions
			 */
			pBKPhysicalClosedManager.load();
			pBKPhysicalOpenedManager.load();
			pBKPlatformManager.load();
			pBKVirtualManager.load();
			pBKComptaManager.load();
			/*
			 * Timer
			 */
			BasicTicToc.Stop(this);
			BasicTicToc.displayTotalDurationStr(this);
		}
	}

	/*
	 * Getters & Setters
	 */
	public final BKPlatformManager getpBKPlatformManager() {
		return pBKPlatformManager;
	}
	public final BKTransferManager getpBKVirtualManager() {
		return pBKVirtualManager;
	}
	public final BKComptaManager getpBKComptaManager() {
		return pBKComptaManager;
	}

}
