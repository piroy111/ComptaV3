package step06_operations_after_transactions_created;

import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticConst;
import step02_load_transactions.objects.transaction.BKTransactionManager;
import step06_operations_after_transactions_created.dates.BKAfterComputeDates;
import step10_launchme.BKLaunchMe;

public class BKAfterBKTransactionsManager {

	public BKAfterBKTransactionsManager(BKLaunchMe _sBKLaunchMe) {
		pBKLaunchMe = _sBKLaunchMe;
		/*
		 * 
		 */
		pBKAfterComputeDates = new BKAfterComputeDates(this);
	}

	/*
	 * Data
	 */
	private BKLaunchMe pBKLaunchMe;
	private BKAfterComputeDates pBKAfterComputeDates;
	
	/**
	 * 
	 */
	public final void run() {
		BasicPrintMsg.displaySuperTitle(this, "Computations to be done once all BKTransactions have been created");
		/*
		 * We forbid any further creation of BKTransactions
		 */
		BasicPrintMsg.display(this, "We forbid any further creation of new BKTransaction");
		BKTransactionManager.setIS_NO_MORE_BKTRANSACTION(true);
		BasicPrintMsg.display(this, "Date earliest transaction modified= " + BKStaticConst.getDATE_FIRST_CHANGED_TRANSACTIONS());
		BasicPrintMsg.display(this, "Date Compta= " + BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS());
		/*
		 * We compute the list of dates end of month (useful for the output files)
		 */
		pBKAfterComputeDates.run();
	}

	/*
	 * Getters & Setters
	 */
	public final BKLaunchMe getpBKLaunchMe() {
		return pBKLaunchMe;
	}
	public final BKAfterComputeDates getpBKAfterComputeDates() {
		return pBKAfterComputeDates;
	}
	
	
	
	
	
	
	
	
}
