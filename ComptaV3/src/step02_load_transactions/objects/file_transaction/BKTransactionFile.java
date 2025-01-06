package step02_load_transactions.objects.file_transaction;

import step02_load_transactions.objects.file.BKFile;
import step02_load_transactions.objects.transaction.BKTransaction;

public class BKTransactionFile extends BKFile<BKTransactionFile, BKTransactionFileManager> {

	protected BKTransactionFile(String _sKey, BKTransactionFileManager _sBKTransactionFileManager) {
		super(_sKey, _sBKTransactionFileManager);
	}

	/*
	 * Data
	 */
	private BKTransactionFileManager pBKTransactionFileManager;

	/**
	 * 
	 * @param _sBKTransaction
	 */
	public final void declareNewBKTransaction(BKTransaction _sBKTransaction) {
		if (_sBKTransaction == null) {
			return;
		}
		declareNewEvent(_sBKTransaction.getpDate());
	}


	/*
	 * Getters & Setters
	 */
	public final BKTransactionFileManager getpBKTransactionFileManager() {
		return pBKTransactionFileManager;
	}

	

}
