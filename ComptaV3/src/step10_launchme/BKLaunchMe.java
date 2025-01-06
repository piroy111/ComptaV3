package step10_launchme;

import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticDir;
import step00_freeze_transactions.BKFrozenManager;
import step01_objects_from_conf_files.conffiles.BKConfFileManager;
import step01_objects_from_conf_files.conffiles.BKConfLoaderTimeStampPreviousCompta;
import step02_load_transactions.deliveries.BKDeliveriesManager;
import step02_load_transactions.file_tracker.BKTrackerManager;
import step02_load_transactions.transactions_loaders.BKTransactionsLoader;
import step02_load_transactions.transactions_loaders.frozen.BKFrozenTransactionLoader;
import step03_partitions.abstracts.objects.BKPartitionManager;
import step04_debug.abstracts.BKDebugManager;
import step05_transactions_computed_by_compta.abstracts.BKComptaComputorManager;
import step06_operations_after_transactions_created.BKAfterBKTransactionsManager;
import step07_reconciliation.abstracts.BKReconciliatorManager;
import step08_output_files.abstracts.BKOutputManager;
import step08_output_files.abstracts_with_history.BKOutputHistoryManager;
import step09_fiscal_year_end.FYManager;
import tictoc.BasicTicToc;

public class BKLaunchMe {

	public static void main(String[] _sArgs) {
		BKStaticDir.detectDIR(_sArgs);
		BKStaticConst.setIS_SKIP_RECONCILIATION_PLATORM(false);
		BKStaticConst.setIS_SKIP_WRITE_FILES_CLIENTS(false);
		new BKLaunchMe().run();
	}
	
	/**
	 * 
	 */
	public BKLaunchMe() {
		pBKTransactionsLoader = new BKTransactionsLoader();
		pBKPartitionManager = new BKPartitionManager();
		pBKComptaComputorManager = new BKComptaComputorManager(this);
		pBKReconciliatorManager = new BKReconciliatorManager(this);
		pBKOutputManager = new BKOutputManager(this);
		pBKOutputHistoryManager = new BKOutputHistoryManager(this);
		pBKAfterBKTransactionsManager = new BKAfterBKTransactionsManager(this);
		pBKDeliveriesManager = new BKDeliveriesManager();
		pFYManager = new FYManager(this);
		pBKFrozenManager = new BKFrozenManager(this);
		pBKTrackerManager = new BKTrackerManager(this);
		pBKFrozenTransactionLoader = new BKFrozenTransactionLoader(this);
	}
	
	/*
	 * Data
	 */
	private BKTransactionsLoader pBKTransactionsLoader;
	private BKPartitionManager pBKPartitionManager;
	private BKComptaComputorManager pBKComptaComputorManager;
	private BKReconciliatorManager pBKReconciliatorManager;
	private BKOutputManager pBKOutputManager;
	private BKOutputHistoryManager pBKOutputHistoryManager;
	private BKAfterBKTransactionsManager pBKAfterBKTransactionsManager;
	private BKDeliveriesManager pBKDeliveriesManager;
	private FYManager pFYManager;
	private BKFrozenManager pBKFrozenManager;
	private BKTrackerManager pBKTrackerManager;
	private BKFrozenTransactionLoader pBKFrozenTransactionLoader;
	

	/**
	 * 
	 */
	public final void run() {
		/*
		 * Initiate
		 */
		pBKComptaComputorManager.removeFilesOfTheMonth();
		/*
		 * Identify all the files of transaction, balance and proof + create BKEntities + identify the files delivery
		 */
		pBKTransactionsLoader.ListFilesOfBKEntities();
		pBKDeliveriesManager.listFilesBKDelivery();
		pBKFrozenManager.computeListDateFYFrozen();
		pBKFrozenTransactionLoader.listFilesBKFrozenTransaction();
		/*
		 * Check all changes in files from the tracker of file
		 */
		pBKTrackerManager.run();
		/*
		 * Check if there are missing frozen transactions. If it is the case, then it will compute them, write them and terminate the program
		 */
		pBKFrozenManager.checkForMissingFile();
		/*
		 * Load assets + historical prices, CONF files, deliveries
		 */
		BKConfFileManager.run();
		/*
		 * Load the BKFrozen Transaction as of the end of last FY
		 */
		pBKFrozenTransactionLoader.loadBKTransactionFrozen();
		/*
		 * Read all the files to create the BKTransactions (physical, Platform, Transfer, COMPTA)
		 */
		pBKTransactionsLoader.readFilesAndCreateBKTransactions();
		/*
		 * Check if the BKBar is a delivery (we read only once the files of deliveries even if there are many calls)
		 */
		pBKDeliveriesManager.checkAndLoadStatus();
		/*
		 * Continue differently whether we need to write a BKFrozen report or if all the BKFrozen reports are already here
		 */
		if (pBKFrozenManager.getpIsNeedMakeFileFrozen()) {
			runSpecialCaseOfReportFrozenToDo();
		} else {
			runRegularCase();
		}
	}
	
	/**
	 * 
	 */
	private void runRegularCase() {
		/*
		 * Create all the partitions and feed them with the BKTransactions which have just been created
		 */
		pBKPartitionManager.declareAndComputeAllBKTransaction();
		/*
		 * Create the BKTransactions from the COMPTA + write the files
		 */
		pBKComptaComputorManager.computeAndWriteFileOfMonth();
		/*
		 * After all BKTransaction have been created, we compute certain data (owner of BKBars, etc.)
		 */
		pBKAfterBKTransactionsManager.run();
		/*
		 * Compute for fiscal year end: income, balance sheet, amortisation + write the reports
		 */
		pFYManager.run();
		/*
		 * Reconciliation: 
		 */
		pBKReconciliatorManager.run();
		/*
		 * Output files
		 */
		pBKOutputManager.run();
		pBKOutputHistoryManager.run();
		/*
		 * Display the times of computation
		 */
		BasicTicToc.displayResults();
		/*
		 * Debug files + file of COMPTA to know the time stamp
		 */
		System.out.println(BKDebugManager.flush());
		BKConfLoaderTimeStampPreviousCompta.writeFile();
		System.out.println("\nAll good. All finished");
	}
	
	/**
	 * Read BKTransaction and BKDeliveries within the range of FY to do frozen<br>
	 * compute the BKTransactionFrozen and write them in a file<br>
	 * update the file tracker<br>
	 */
	private void runSpecialCaseOfReportFrozenToDo() {
		/*
		 * Compute BKPartition
		 */
		pBKPartitionManager.declareAndComputeAllBKTransaction();
		/*
		 * Check if we need to write a FY frozen report. If this is the case, then we do it and we kill the program. Otherwise
		 */
		pBKFrozenManager.computeAndWriteBKFrozenTransaction();
		/*
		 * Call the tracker to write the file tracker
		 */
		pBKTrackerManager.writeFileTracker();
		/*
		 * Kill program
		 */
		BKCom.error(pBKFrozenManager.getpMsgKill());
	}
	
	/*
	 * Getters & Setters
	 */
	public final BKTransactionsLoader getpBKTransactionsLoader() {
		return pBKTransactionsLoader;
	}
	public final BKPartitionManager getpBKPartitionManager() {
		return pBKPartitionManager;
	}
	public final BKComptaComputorManager getpBKComptaComputorManager() {
		return pBKComptaComputorManager;
	}
	public final BKReconciliatorManager getpBKReconciliatorManager() {
		return pBKReconciliatorManager;
	}
	public final BKOutputManager getpBKOutputManager() {
		return pBKOutputManager;
	}
	public final BKAfterBKTransactionsManager getpBKAfterBKTransactionsManager() {
		return pBKAfterBKTransactionsManager;
	}
	public final BKDeliveriesManager getpBKDeliveriesManager() {
		return pBKDeliveriesManager;
	}
	public final FYManager getpFYManager() {
		return pFYManager;
	}
	public final BKTrackerManager getpBKTrackerManager() {
		return pBKTrackerManager;
	}
	public final BKFrozenManager getpBKFrozenManager() {
		return pBKFrozenManager;
	}
	public final BKOutputHistoryManager getpBKOutputHistoryManager() {
		return pBKOutputHistoryManager;
	}
	
}
