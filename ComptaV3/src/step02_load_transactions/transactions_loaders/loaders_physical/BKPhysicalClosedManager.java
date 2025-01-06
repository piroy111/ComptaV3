package step02_load_transactions.transactions_loaders.loaders_physical;

import staticdata.datas.BKStaticDir;

public class BKPhysicalClosedManager extends BKPhysicalManager {

	public BKPhysicalClosedManager() {
		super(BKStaticDir.getLOAD_TRANSACTIONS_PHYSICAL_CLOSED());
	}

}
