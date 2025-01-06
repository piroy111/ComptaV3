package step03_partitions.partitions;

import step02_load_transactions.objects.entity.BKEntity;
import step02_load_transactions.objects.entity.BKEntityManager;
import step02_load_transactions.objects.transaction.BKTransaction;
import step03_partitions.abstracts.objects.BKPartitionAbstract;
import step03_partitions.abstracts.objects.BKPartitionManager;

public class BKPartitionPerBKEntity extends BKPartitionAbstract {

	public BKPartitionPerBKEntity(BKPartitionManager _sBKPartitionManager) {
		super(_sBKPartitionManager);
	}

	@Override public String getpKeyForPartition(BKTransaction _sBKTransaction) {
		return getKey(_sBKTransaction.getpBKEntity());
	}

	/**
	 * 
	 * @param _sBKEntity
	 * @param _sBKAccount
	 * @return
	 */
	public static String getKey(BKEntity _sBKEntity) {
		return _sBKEntity.getpKey();
	}

	/**
	 * 
	 * @param _sKeyPartition
	 * @return
	 */
	public final BKEntity getpBKEntity(String _sKeyPartition) {
		return BKEntityManager.getpAndCheckBKEntityFromKey(_sKeyPartition, this);
	}

	
	
	
	
}
