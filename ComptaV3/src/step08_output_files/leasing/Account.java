package step08_output_files.leasing;

import step01_objects_from_conf_files.account.BKAccount;

class Account extends Item {

	protected Account(BKAccount _sBKAccount) {
		super(_sBKAccount);
	}

	/*
	 * Data
	 */
	private BKAccount pBKAccount;

	/*
	 * Getters & Setters
	 */
	public final BKAccount getpBKAccount() {
		return pBKAccount;
	}
}
