package step08_output_files.client_transactions;

import java.util.TreeMap;

import staticdata.datas.BKStaticDir;
import step01_objects_from_conf_files.account.BKAccount;
import step02_load_transactions.objects.transaction.BKTransaction;
import step03_partitions.abstracts.partitions.BKTransactionPartitionDate;
import step03_partitions.partitions.BKPartitionPerBKAccount;
import step08_output_files.abstracts.BKOutputAbstract;
import step08_output_files.abstracts.BKOutputManager;

public class BKOutput_ClientTransactions extends BKOutputAbstract {

	public BKOutput_ClientTransactions(BKOutputManager _sBKOutputManager, BKAccount _sBKAccount) {
		super(_sBKOutputManager);
		/*
		 * 
		 */
		pBKAccount = _sBKAccount;
		addNewSuffixToNameFile(pBKAccount.getpEmail());
	}

	/*
	 * Data
	 */
	private BKAccount pBKAccount;

	/**
	 * 
	 */
	public final String getpDirRoot() {
		return BKStaticDir.getOUTPUT_CLIENT();
	}

	/**
	 * 
	 */
	@Override public void buildFileContent() {
		String lKey = BKPartitionPerBKAccount.getKey(pBKAccount);
		TreeMap<Integer, BKTransactionPartitionDate> lTreeMapDateToBKTransactionPartitionDate = pBKPartitionManager
				.getpBKPartitionPerBKAccount().getpMapKeyToTreeMapDateToBKTransactionPartitionDate().get(lKey);
		if (lTreeMapDateToBKTransactionPartitionDate == null) {
			return;
		}
		/*
		 * Header
		 */
		addNewHeader("Date,BKAsset,Comment,Quantity,Price");
		/*
		 * Transactions
		 */
		for (BKTransactionPartitionDate lBKTransactionPartitionDate : lTreeMapDateToBKTransactionPartitionDate.values()) {
			for (BKTransaction lBKTransaction : lBKTransactionPartitionDate.getpListBKTransactionToday()) {
				String lLine = lBKTransaction.getpDate()
						+ "," + lBKTransaction.getpBKAsset()
						+ "," + lBKTransaction.getpComment()
						+ "," + lBKTransaction.getpQuantity()
						+ "," + lBKTransaction.getpPrice();
				addNewLineToWrite(lLine);
			}
		}
	}

}
