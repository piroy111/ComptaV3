package step03_partitions.partitions;

import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step02_load_transactions.objects.entity.BKEntity;
import step02_load_transactions.objects.entity.BKEntityManager;
import step02_load_transactions.objects.transaction.BKTransaction;
import step03_partitions.abstracts.objects.BKPartitionAbstract;
import step03_partitions.abstracts.objects.BKPartitionManager;

public class BKPartitionPerBKEntityAndBKAccount extends BKPartitionAbstract {

	public BKPartitionPerBKEntityAndBKAccount(BKPartitionManager _sBKPartitionManager) {
		super(_sBKPartitionManager);
	}

	@Override public String getpKeyForPartition(BKTransaction _sBKTransaction) {
		return getKey(_sBKTransaction.getpBKEntity(), _sBKTransaction.getpBKAccount());
	}

	/**
	 * 
	 * @param _sBKEntity
	 * @param _sBKAccount
	 * @return
	 */
	public static String getKey(BKEntity _sBKEntity, BKAccount _sBKAccount) {
		return _sBKEntity.getpKey() + ";=;" + _sBKAccount.getpKey();
	}

	/**
	 * 
	 * @param _sKeyPartition
	 * @return
	 */
	public final BKEntity getpBKEntity(String _sKeyPartition) {
		String[] lArrayStr = _sKeyPartition.split(";=;", 2);
		String lKey = lArrayStr[0];
		return BKEntityManager.getpAndCheckBKEntityFromKey(lKey, this.getClass().getSimpleName());
	}

	/**
	 * 
	 * @param _sKeyPartition
	 * @return
	 */
	public final BKAccount getpBKAccount(String _sKeyPartition) {
		String[] lArrayStr = _sKeyPartition.split(";=;", 2);
		String lKey = lArrayStr[1];
		return BKAccountManager.getpAndCheckBKAccount(lKey, this.getClass().getSimpleName());
	}
	
	
	
	
	
}
