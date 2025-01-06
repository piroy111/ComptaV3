package step02_load_transactions.transactions_loaders.debug;

import step02_load_transactions.transactions_loaders.loaders_physical.BKPhysicalManager;

public class BKDebugTransactionLoaderPhysical extends BKPhysicalManager {

	public BKDebugTransactionLoaderPhysical() {
		super(BKDebugStatic.DIR_DEBUG);
	}
	

}
