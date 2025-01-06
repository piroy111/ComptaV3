package step02_load_transactions.objects.balances;

import step02_load_transactions.objects.direntity.BKEntityFilesDate;

public abstract class BKBalanceFileAbstract {

	public BKBalanceFileAbstract(BKEntityFilesDate _sBKEntityFilesDate) {
		pBKEntityFilesDate = _sBKEntityFilesDate;
	}
	
	/*
	 * Abstract
	 */
	/**
	 * @param _sBKHoldingManagerFromBKTransactions
	 * @return Error message if it does not reconcile, or "" if all is good
	 */
	public abstract String getpIsReconcile();
	/*
	 * Data
	 */
	protected BKEntityFilesDate pBKEntityFilesDate;

	/*
	 * Getters & Setters
	 */
	
}
