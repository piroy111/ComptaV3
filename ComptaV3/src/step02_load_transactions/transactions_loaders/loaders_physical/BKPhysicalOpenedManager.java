package step02_load_transactions.transactions_loaders.loaders_physical;

import staticdata.datas.BKStaticDir;

public class BKPhysicalOpenedManager extends BKPhysicalManager {

	public BKPhysicalOpenedManager() {
		super(BKStaticDir.getLOAD_TRANSACTIONS_PHYSICAL());
	}

}
