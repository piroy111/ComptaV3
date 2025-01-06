package step04_debug.abstracts;

import java.util.ArrayList;
import java.util.List;

import step02_load_transactions.objects.transaction.BKTransaction;
import step04_debug.bars.BKDebugPullOutAllBKBars;
import step04_debug.debuggors.BKDebugAllBKTransactions;

public class BKDebugManager {

	/*
	 * Data
	 */
	private static List<BKDebugTransactionAbstract> pListBKDebugTransactionAbstract = new ArrayList<>();
	/*
	 * DEBUGGORS
	 */
	protected static BKDebugAllBKTransactions pBKDebugAllBKTransactions = new BKDebugAllBKTransactions();
	

	/**
	 * 
	 * @param _sBKTransaction
	 */
	public static final void addNewBKTransaction(BKTransaction _sBKTransaction) {
		for (BKDebugTransactionAbstract lBKDebugTransactionAbstract : pListBKDebugTransactionAbstract) {
			lBKDebugTransactionAbstract.addNewBKTransaction(_sBKTransaction);
		}
	}

	/**
	 * 
	 */
	public static final String flush() {
		String lMsgFlush = "\n\nList of files written to help debugging:";
		for (BKDebugTransactionAbstract lBKDebugTransactionAbstract : pListBKDebugTransactionAbstract) {
			lMsgFlush += "\n      '" + lBKDebugTransactionAbstract.flushFile() + "'";
		}
		lMsgFlush += "\n      '" + BKDebugPullOutAllBKBars.run() + "'";
		lMsgFlush += "\n";
		return lMsgFlush;
	}

	/**
	 * 
	 * @param _sBKDebugTransactionAbstract
	 */
	protected static final void declareNewBKDebugTransactionAbstract(BKDebugTransactionAbstract _sBKDebugTransactionAbstract) {
		pListBKDebugTransactionAbstract.add(_sBKDebugTransactionAbstract);
	}

	
}
