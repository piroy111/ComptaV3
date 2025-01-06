package step03_partitions.partitions;

import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.income.BKIncome;
import step01_objects_from_conf_files.income.BKIncomeManager;
import step02_load_transactions.objects.transaction.BKTransaction;
import step03_partitions.abstracts.objects.BKPartitionAbstract;
import step03_partitions.abstracts.objects.BKPartitionManager;

public class BKPartitionPerBKIncomeAndBKAccount extends BKPartitionAbstract {

	public BKPartitionPerBKIncomeAndBKAccount(BKPartitionManager _sBKPartitionManager) {
		super(_sBKPartitionManager);
	}

	@Override public String getpKeyForPartition(BKTransaction _sBKTransaction) {
		return getKey(_sBKTransaction.getpBKIncome(), _sBKTransaction.getpBKAccount());
	}

	/**
	 * 
	 * @param _sBKIncome
	 * @param _sBKAccount
	 * @return
	 */
	public static String getKey(BKIncome _sBKIncome, BKAccount _sBKAccount) {
		return _sBKIncome.getpName() + ";=;" + _sBKAccount.getpKey();
	}

	/**
	 * 
	 * @param _sKeyPartition
	 * @return
	 */
	public final BKIncome getpBKIncome(String _sKeyPartition) {
		String[] lArrayStr = _sKeyPartition.split(";=;", 2);
		String lKey = lArrayStr[0];
		return BKIncomeManager.getpAndCheckBKIncome(lKey, this.getClass().getSimpleName());
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
