package step06_operations_after_transactions_created.dates;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicDateInt;
import staticdata.datas.BKStaticConst;
import step02_load_transactions.objects.transaction.BKTransactionManager;
import step06_operations_after_transactions_created.BKAfterBKTransactionsManager;

public class BKAfterComputeDates {

	public BKAfterComputeDates(BKAfterBKTransactionsManager _sBKAfterBKTransactionsManager) {
		pBKAfterBKTransactionsManager = _sBKAfterBKTransactionsManager;
	}
	
	/*
	 * Data
	 */
	private BKAfterBKTransactionsManager pBKAfterBKTransactionsManager;
	private List<Integer> pListDateEndOfMonth;
	private int pDateFirst;
	
	/**
	 * 
	 */
	public final void run() {
		pListDateEndOfMonth = new ArrayList<>();
		if (BKTransactionManager.getpTreeMapDateToListBKTransaction().size() == 0) {
			return;
		}
		pDateFirst = BKTransactionManager.getpTreeMapDateToListBKTransaction().firstKey();
		int lDateEnd = BasicDateInt.getmEndOfMonth(BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS());
		int lDate = BasicDateInt.getmEndOfMonth(pDateFirst);
		while (lDate <= lDateEnd) {
			pListDateEndOfMonth.add(lDate);
			lDate = BasicDateInt.getmEndOfMonth(BasicDateInt.getmPlusDay(lDate, 1));
		}
	}

	/*
	 * Getters & Setters
	 */
	public final BKAfterBKTransactionsManager getpBKAfterBKTransactionsManager() {
		return pBKAfterBKTransactionsManager;
	}
	public final List<Integer> getpListDateEndOfMonth() {
		return pListDateEndOfMonth;
	}
	public final int getpDateFirst() {
		return pDateFirst;
	}


}
