package step03_partitions.partitions;

import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step02_load_transactions.objects.transaction.BKTransaction;
import step03_partitions.abstracts.objects.BKPartitionAbstract;
import step03_partitions.abstracts.objects.BKPartitionManager;

public class BKPartitionPerBKAccount extends BKPartitionAbstract {

	public BKPartitionPerBKAccount(BKPartitionManager _sBKPartitionManager) {
		super(_sBKPartitionManager);
	}

	@Override public String getpKeyForPartition(BKTransaction _sBKTransaction) {
		return getKey(_sBKTransaction.getpBKAccount());
	}
	
	/**
	 * 
	 * @param _sKeyPartition
	 * @return
	 */
	public final BKAccount getpBKAccount(String _sKeyPartition) {
		return BKAccountManager.getpAndCheckBKAccount(_sKeyPartition, this.getClass().getSimpleName());
	}

	/**
	 * 
	 * @param _sBKAccount
	 * @return
	 */
	public static String getKey(BKAccount _sBKAccount) {
		return _sBKAccount.getpKey();
	}
	
	/**
	 * get and compute the NAV for the given date
	 * @return
	 */
	public final double getpNAV(BKAccount _sBKAccount, int _sDate) {
		String lKey = getKey(_sBKAccount);
		return getpNAV(lKey, _sDate);
	}
	
}
