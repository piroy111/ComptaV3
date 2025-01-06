package step08_output_files.leasing_deprecated;

import basicmethods.AMNumberTools;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;

class Account implements Comparable<Account> {

	protected Account(BKAccount _sBKAccount) {
		pBKAccount = _sBKAccount;
		/*
		 * 
		 */
		pNAV = 0.;
		pIsBunker = pBKAccount.equals(BKAccountManager.getpBKAccountBunker());
	}
	
	/*
	 * Data
	 */
	private BKAccount pBKAccount;
	private double pNAV;
	private BKTransactionPartitionDate pBKTransactionPartitionDate;
	private boolean pIsBunker;

	
	@Override public int compareTo(Account _sAccount) {
		if (_sAccount.pIsBunker) {
			if (pIsBunker) {
				return 0;
			} else {
				return 1;
			}
		} else if (pIsBunker) {
			return -1;
		}
		return -Double.compare(pNAV, _sAccount.pNAV);
		
	}
	
	/**
	 * 
	 * @param _sNAVToAdd
	 */
	public final void addNAV(double _sNAVToAdd) {
		if (!AMNumberTools.isNaNOrZero(_sNAVToAdd)) {
			pNAV += _sNAVToAdd;
		}
	}

	/*
	 * Getters & Setters
	 */
	public final BKAccount getpBKAccount() {
		return pBKAccount;
	}
	public final double getpNAV() {
		return pNAV;
	}
	public final BKTransactionPartitionDate getpBKTransactionPartitionDate() {
		return pBKTransactionPartitionDate;
	}
	public final void setpBKTransactionPartitionDate(BKTransactionPartitionDate _sPBKTransactionPartitionDate) {
		pBKTransactionPartitionDate = _sPBKTransactionPartitionDate;
	}
	
}
