package step04_debug.standalones.buildbkrozentracker;

import step02_load_transactions.deliveries.BKDeliveriesManager;
import step02_load_transactions.objects.transaction.BKTransactionManager;
import step02_load_transactions.transactions_loaders.BKTransactionsLoader;

public class STFRManager {

	public STFRManager() {
		pBKTransactionsLoader = new BKTransactionsLoader();
		pBKDeliveriesManager = new BKDeliveriesManager();
	}
	
	
	/*
	 * Data
	 */
	private BKTransactionsLoader pBKTransactionsLoader;
	private BKDeliveriesManager pBKDeliveriesManager;
	
	/**
	 * 
	 */	
	public final void run() {
		/*
		 * Load all transactions from the files
		 */
		pBKTransactionsLoader.ListFilesOfBKEntities();
		pBKDeliveriesManager.listFilesBKDelivery();
		/*
		 * Write the files details
		 */
		new STFRWriter(BKTransactionManager.getpBKTransactionFileManager()).run();
		new STFRWriter(pBKDeliveriesManager.getpBKDeliveryFileManager()).run();

	}
	
	
	
	
	
}
