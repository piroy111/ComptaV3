package step04_debug.debuggors;

import step02_load_transactions.objects.transaction.BKTransaction;
import step04_debug.abstracts.BKDebugTransactionAbstract;

public class BKDebugAllBKTransactions extends BKDebugTransactionAbstract {

	@Override public boolean getpIsKeepBKTransaction(BKTransaction _sBKTransaction) {
		return true;
	}

}
