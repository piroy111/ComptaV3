package step03_partitions.abstracts.objects;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicPrintMsg;
import step02_load_transactions.objects.transaction.BKTransaction;
import step02_load_transactions.objects.transaction.BKTransactionManager;
import step03_partitions.partitions.BKPartitionPerBKAccount;
import step03_partitions.partitions.BKPartitionPerBKEntity;
import step03_partitions.partitions.BKPartitionPerBKEntityAndBKAccount;
import step03_partitions.partitions.BKPartitionPerBKIncomeAndBKAccount;
import tictoc.BasicTicToc;

public class BKPartitionManager {

	public BKPartitionManager() {
		pListBKBKPartitionAbstract = new ArrayList<>();
	}

	/*
	 * Data
	 */
	private List<BKPartitionAbstract> pListBKBKPartitionAbstract;
	private BKPartitionPerBKAccount pBKPartitionPerBKAccount;
	private BKPartitionPerBKIncomeAndBKAccount pBKPartitionPerBKIncomeAndBKAccount;
	private BKPartitionPerBKEntity pBKPartitionPerBKEntity;
	private BKPartitionPerBKEntityAndBKAccount pBKPartitionPerBKEntityAndBKAccount;

	/**
	 * To be filled by the programmer
	 */
	private final void createBKPartitions() {
		pBKPartitionPerBKIncomeAndBKAccount = new BKPartitionPerBKIncomeAndBKAccount(this);
		pBKPartitionPerBKAccount = new BKPartitionPerBKAccount(this);
		pBKPartitionPerBKEntity = new BKPartitionPerBKEntity(this);
		pBKPartitionPerBKEntityAndBKAccount = new BKPartitionPerBKEntityAndBKAccount(this);
	}

	/**
	 * 
	 */
	public final void declareAndComputeAllBKTransaction() {
		BasicPrintMsg.displaySuperTitle(this, "Create, feed and compute the various partitions");
		BasicTicToc.Start(this);
		/*
		 * 
		 */
		createBKPartitions();
		/*
		 * 
		 */
		for (BKPartitionAbstract lBKPartitionAbstract : pListBKBKPartitionAbstract) {
			lBKPartitionAbstract.resetAndAddNewBKtransaction(BKTransactionManager.getpListBKTransactionSorted());
		}
		BasicTicToc.Stop(this);
		BasicTicToc.displayTotalDurationStr(this);
	}

	/**
	 * in the case of frozen transactions, we will compute BKPartition only for BKEntity / BKAccount, but for all existing transactions until the date of FY frozen
	 */
	public final void declareAndComputeAllBKTransactionSpecialCaseFrozen() {
		BasicPrintMsg.displaySuperTitle(this, "Create, feed and compute the partition " + BKPartitionPerBKEntityAndBKAccount.class.getSimpleName());
		BasicTicToc.Start(this);
		/*
		 * Create only the BKPartition for Entity and BKAccount
		 */
		pBKPartitionPerBKEntity = new BKPartitionPerBKEntity(this);
		pBKPartitionPerBKEntityAndBKAccount = new BKPartitionPerBKEntityAndBKAccount(this);
		pBKPartitionPerBKAccount = new BKPartitionPerBKAccount(this);
		/*
		 * Compute the BKPartitions
		 */
		pBKPartitionPerBKEntity.resetAndAddNewBKtransaction(BKTransactionManager.getpListBKTransactionSorted());
		pBKPartitionPerBKEntityAndBKAccount.resetAndAddNewBKtransaction(BKTransactionManager.getpListBKTransactionSorted());
		pBKPartitionPerBKAccount.resetAndAddNewBKtransaction(BKTransactionManager.getpListBKTransactionSorted());
		/*
		 * Communicate
		 */
		BasicTicToc.Stop(this);
		BasicTicToc.displayTotalDurationStr(this);
	}

	/**
	 * 
	 * @param _sBKTransaction
	 */
	public final void declareAndComputeNewBKTransaction(BKTransaction _sBKTransaction) {
		for (BKPartitionAbstract lBKPartitionAbstract : pListBKBKPartitionAbstract) {
			lBKPartitionAbstract.addNewBKTransaction(_sBKTransaction);
		}
	}

	/**
	 * 
	 * @param _sBKPartitionManager
	 */
	protected void declareNewBKPartitionAbstract(BKPartitionAbstract _sBKPartitionAbstract) {
		if (!pListBKBKPartitionAbstract.contains(_sBKPartitionAbstract)) {
			pListBKBKPartitionAbstract.add(_sBKPartitionAbstract);
			String lMsg = _sBKPartitionAbstract.getClass().getSimpleName() + " created";
			BasicPrintMsg.display(this, lMsg);
		}
	}

	/*
	 * Getters & Setters
	 */
	public final BKPartitionPerBKAccount getpBKPartitionPerBKAccount() {
		return pBKPartitionPerBKAccount;
	}
	public final List<BKPartitionAbstract> getpListBKBKPartitionAbstract() {
		return pListBKBKPartitionAbstract;
	}
	public final BKPartitionPerBKIncomeAndBKAccount getpBKPartitionPerBKIncomeAndBKAccount() {
		return pBKPartitionPerBKIncomeAndBKAccount;
	}
	public final BKPartitionPerBKEntity getpBKPartitionPerBKEntity() {
		return pBKPartitionPerBKEntity;
	}
	public final BKPartitionPerBKEntityAndBKAccount getpBKPartitionPerBKEntityAndBKAccount() {
		return pBKPartitionPerBKEntityAndBKAccount;
	}





}
