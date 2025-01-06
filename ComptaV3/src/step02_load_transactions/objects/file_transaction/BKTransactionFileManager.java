package step02_load_transactions.objects.file_transaction;

import step02_load_transactions.objects.file.BKFileManager;

public class BKTransactionFileManager extends BKFileManager<BKTransactionFile, BKTransactionFileManager> {

	public BKTransactionFileManager(String _sSource) {
		super(_sSource);
	}
	
	/*
	 * Data
	 */
	
	/**
	 * Abstract - Factory
	 */
	@Override public BKTransactionFile factoryBKFile(String _sKey) {
		return new BKTransactionFile(_sKey, this);
	}
	
	/*
	 * Getters & Setters
	 */

	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
