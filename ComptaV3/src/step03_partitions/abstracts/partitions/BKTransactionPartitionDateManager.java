package step03_partitions.abstracts.partitions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import basicmethods.BasicDateInt;
import basicmethods.BasicPrintMsg;
import step02_load_transactions.objects.transaction.BKTransaction;

public abstract class BKTransactionPartitionDateManager {

	public BKTransactionPartitionDateManager() {
		/*
		 * 
		 */
		pTreeMapDateToMapKeyToBKTransactionPartitionDate = new TreeMap<>();
		pMapKeyToTreeMapDateToBKTransactionPartitionDate = new HashMap<>();
	}

	/*
	 * 
	 */
	public abstract String getpKeyForPartition(BKTransaction _sBKTransaction);
	/*
	 * Data
	 */
	protected TreeMap<Integer, Map<String, BKTransactionPartitionDate>> pTreeMapDateToMapKeyToBKTransactionPartitionDate;
	protected Map<String, TreeMap<Integer, BKTransactionPartitionDate>> pMapKeyToTreeMapDateToBKTransactionPartitionDate;

	/**
	 * Create the BKTransactionPartitionDate and all the links from one date to the following date<br>
	 * + create the <br>
	 */
	public final void addNewBKTransaction(BKTransaction _sBKTransaction) {
		/*
		 * Initiate
		 */
		String lKeyPartition = getpKeyForPartition(_sBKTransaction);
		int lDateStart = _sBKTransaction.getpDate();
		/*
		 * Create the BKTransactionPartitionDate and all the links from one date to the following date
		 */
		BKTransactionPartitionDate lBKTransactionPartitionDate = getpOrCreateAndComputeBKTransactionPartitionDate(lDateStart, lKeyPartition);
		/*
		 * Compute all the transactions afterwards for the same key (no need to recompute for the other keys since there is no new transaction for them)
		 */
		int lDateStop = pTreeMapDateToMapKeyToBKTransactionPartitionDate.lastKey();
		lBKTransactionPartitionDate.addNewBKTransaction(_sBKTransaction);
		for (int lDate = lDateStart; lDate <= lDateStop; lDate = BasicDateInt.getmPlusDay(lDate, 1)) {
			getpOrCreateAndComputeBKTransactionPartitionDate(lDate, lKeyPartition);
		}
		for (int lDate = lDateStart; lDate <= lDateStop; lDate = BasicDateInt.getmPlusDay(lDate, 1)) {
			getpOrCreateAndComputeBKTransactionPartitionDate(lDate, lKeyPartition).computeNAV();
		}
	}

	/**
	 * 
	 * @param _sDate
	 * @param _sKeyPartition
	 * @return
	 */
	private final BKTransactionPartitionDate getpOrCreateAndComputeBKTransactionPartitionDate(int _sDate, String _sKeyPartition) {
		/*
		 * Case the key does not exist --> create all the missing key for the existing dates --> we don't need to compute the NAV
		 */
		if (!pMapKeyToTreeMapDateToBKTransactionPartitionDate.containsKey(_sKeyPartition)) {
			int lDateYesterday = -1;
			for (int lDate : pTreeMapDateToMapKeyToBKTransactionPartitionDate.keySet()) {
				BKTransactionPartitionDate lBKTransactionPartitionDate = getpOrCreateBKTransactionPartitionDate(lDate, _sKeyPartition);
				if (lDateYesterday > 0) {
					lBKTransactionPartitionDate.setpBKTransactionPartitionDateYesterday(getpOrCreateBKTransactionPartitionDate(lDateYesterday, _sKeyPartition));
				}
				lDateYesterday = lDate;
			}
		}
		/*
		 * Case the Date does not exist and the TreeMap is not empty -> we need to create the BKTransactionPartitionDate for all the KeyPartitions
		 */
		if (pTreeMapDateToMapKeyToBKTransactionPartitionDate.size() > 0 
				&& !pTreeMapDateToMapKeyToBKTransactionPartitionDate.containsKey(_sDate)) {
			int lLastDate = pTreeMapDateToMapKeyToBKTransactionPartitionDate.lastKey();
			if (_sDate > lLastDate) {
				for (String lKeyPartition : pMapKeyToTreeMapDateToBKTransactionPartitionDate.keySet()) {
					int lDateYesterday = lLastDate;
					for (int lDate = BasicDateInt.getmPlusDay(lLastDate, 1); lDate <= _sDate; lDate = BasicDateInt.getmPlusDay(lDate, 1)) {
						BKTransactionPartitionDate lBKTransactionPartitionDate = getpOrCreateBKTransactionPartitionDate(lDate, lKeyPartition);
						lBKTransactionPartitionDate.setpBKTransactionPartitionDateYesterday(getpOrCreateBKTransactionPartitionDate(lDateYesterday, lKeyPartition));
						lDateYesterday = lDate;
						/*
						 * Special case -> we need to compute the NAV for the new dates post the last date
						 */
						lBKTransactionPartitionDate.computeNAV();
					}
				}
			} else {
				int lFirstDate = pTreeMapDateToMapKeyToBKTransactionPartitionDate.firstKey();
				for (String lKeyPartition : pMapKeyToTreeMapDateToBKTransactionPartitionDate.keySet()) {
					int lDateYesterday = _sDate;
					for (int lDate = BasicDateInt.getmPlusDay(_sDate, 1); lDate <= lFirstDate; lDate = BasicDateInt.getmPlusDay(lDate, 1)) {
						BKTransactionPartitionDate lBKTransactionPartitionDate = getpOrCreateBKTransactionPartitionDate(lDate, lKeyPartition);
						lBKTransactionPartitionDate.setpBKTransactionPartitionDateYesterday(getpOrCreateBKTransactionPartitionDate(lDateYesterday, lKeyPartition));
						lDateYesterday = lDate;
					}
				}
			}
		}
		/*
		 * 
		 */
		return getpOrCreateBKTransactionPartitionDate(_sDate, _sKeyPartition);
	}

	/**
	 * 
	 * @param _sDate
	 * @param _sKeyPartition
	 * @return
	 */
	private final BKTransactionPartitionDate getpOrCreateBKTransactionPartitionDate(int _sDate, String _sKeyPartition) {
		/*
		 * Get the map from the date
		 */
		Map<String, BKTransactionPartitionDate> lMapKeyToBKTransactionPartitionDate = pTreeMapDateToMapKeyToBKTransactionPartitionDate.get(_sDate);
		if (lMapKeyToBKTransactionPartitionDate == null) {
			lMapKeyToBKTransactionPartitionDate = new HashMap<>();
			pTreeMapDateToMapKeyToBKTransactionPartitionDate.put(_sDate, lMapKeyToBKTransactionPartitionDate);
		}
		/*
		 * Get or create the BKTransactionPartitionDate
		 */
		BKTransactionPartitionDate lBKTransactionPartitionDate = lMapKeyToBKTransactionPartitionDate.get(_sKeyPartition);
		if (lBKTransactionPartitionDate == null) {
			lBKTransactionPartitionDate = new BKTransactionPartitionDate(_sDate, _sKeyPartition, this);
			lMapKeyToBKTransactionPartitionDate.put(_sKeyPartition, lBKTransactionPartitionDate);
			/*
			 * Fill the Map by the key
			 */
			TreeMap<Integer, BKTransactionPartitionDate> lTreeMapDateToBKTransactionPartitionDate = pMapKeyToTreeMapDateToBKTransactionPartitionDate.get(_sKeyPartition);
			if (lTreeMapDateToBKTransactionPartitionDate == null) {
				lTreeMapDateToBKTransactionPartitionDate = new TreeMap<>();
				pMapKeyToTreeMapDateToBKTransactionPartitionDate.put(_sKeyPartition, lTreeMapDateToBKTransactionPartitionDate);
			}
			lTreeMapDateToBKTransactionPartitionDate.put(_sDate, lBKTransactionPartitionDate);
		}
		return lBKTransactionPartitionDate;
	}


	/**
	 * Load all the transactions of BKTransactionManager at once<br>
	 * Useful to optimize the time of processing<br>
	 */
	public final void resetAndAddNewBKtransaction(List<BKTransaction> _sListBKTransactionSorted) {
		BasicPrintMsg.displayTitle(this, "Reset Partitions and load BKTransactions faster for " + this.getClass().getSimpleName());
		if (_sListBKTransactionSorted == null || _sListBKTransactionSorted.size() == 0) {
			BasicPrintMsg.displayLn(this, "The list of BKTransaction is empty --> nothing to do --> Aborted");
			return;
		}
		BasicPrintMsg.display(this, "Number of transactions to load= " + _sListBKTransactionSorted.size());
		/*
		 * Initiate
		 */
		BasicPrintMsg.display(this, "Reset");
		pTreeMapDateToMapKeyToBKTransactionPartitionDate = new TreeMap<>();
		pMapKeyToTreeMapDateToBKTransactionPartitionDate = new HashMap<>();
		Map<String, BKTransactionPartitionDate> lMapKeyToBKTransactionPartitionDateYesterday = null;
		Map<String, BKTransactionPartitionDate> lMapKeyToBKTransactionPartitionDateToday = null;
		/*
		 * Loop on all the dates in a continuous manner, so we link accurately the previous partitions
		 */
		BasicPrintMsg.display(this, "Load all the BKTransactions (this may take some time)");
		int lIdxBKTransaction = 0;
		int lDateStart = _sListBKTransactionSorted.get(lIdxBKTransaction).getpDate();
		int lDateStop = BasicDateInt.getmToday();
		for (int lDate = lDateStart; lDate <= lDateStop; lDate = BasicDateInt.getmPlusDay(lDate, 1)) {
			/*
			 * Create the Map of BKTransactionPartitionDate for today
			 */
			lMapKeyToBKTransactionPartitionDateYesterday = lMapKeyToBKTransactionPartitionDateToday;
			lMapKeyToBKTransactionPartitionDateToday = new HashMap<>();
			pTreeMapDateToMapKeyToBKTransactionPartitionDate.put(lDate, lMapKeyToBKTransactionPartitionDateToday);
			/*
			 * Instantiate all the previous keys and link the yesterday to the today
			 */
			if (lMapKeyToBKTransactionPartitionDateYesterday != null) {
				for (String lKeyYesterday : lMapKeyToBKTransactionPartitionDateYesterday.keySet()) {
					BKTransactionPartitionDate lBKTransactionPartitionDateYesterday = lMapKeyToBKTransactionPartitionDateYesterday.get(lKeyYesterday);
					BKTransactionPartitionDate lBKTransactionPartitionDateToday = new BKTransactionPartitionDate(lDate, lKeyYesterday, this);
					lMapKeyToBKTransactionPartitionDateToday.put(lKeyYesterday, lBKTransactionPartitionDateToday);
					lBKTransactionPartitionDateToday.setpBKTransactionPartitionDateYesterday(lBKTransactionPartitionDateYesterday);
				}
			}
			/*
			 * Declare all BKTransaction + create BKHoldingAsset
			 */
			while (lIdxBKTransaction < _sListBKTransactionSorted.size() 
					&& _sListBKTransactionSorted.get(lIdxBKTransaction).getpDate() == lDate) {
				/*
				 * Load and move on
				 */
				BKTransaction lBKTransaction = _sListBKTransactionSorted.get(lIdxBKTransaction);
				String lKey = getpKeyForPartition(lBKTransaction);
				lIdxBKTransaction++;				
				/*
				 * Get or create BKTransactionPartitionDate for today
				 */
				BKTransactionPartitionDate lBKTransactionPartitionDateToday = lMapKeyToBKTransactionPartitionDateToday.get(lKey);
				if (lBKTransactionPartitionDateToday == null) {
					lBKTransactionPartitionDateToday = new BKTransactionPartitionDate(lDate, lKey, this);
					lMapKeyToBKTransactionPartitionDateToday.put(lKey, lBKTransactionPartitionDateToday);
				}
				/*
				 * Declare the transaction
				 */
				lBKTransactionPartitionDateToday.addNewBKTransaction(lBKTransaction);
			}
		}
		/*
		 * Reorder the TreeMap to get the first key per key and the second per date
		 */
		BasicPrintMsg.display(this, "Reorder the TreeMaps");
		for (int lDate = lDateStart; lDate <= lDateStop; lDate = BasicDateInt.getmPlusDay(lDate, 1)) {
			Map<String, BKTransactionPartitionDate> lMapKeyToBKTransactionPartitionDate = pTreeMapDateToMapKeyToBKTransactionPartitionDate.get(lDate);
			for (String lKey : lMapKeyToBKTransactionPartitionDate.keySet()) {
				BKTransactionPartitionDate lBKTransactionPartitionDate = lMapKeyToBKTransactionPartitionDate.get(lKey);
				/*
				 * Get or create
				 */
				TreeMap<Integer, BKTransactionPartitionDate> lTreeMapDateToBKTransactionPartitionDate = pMapKeyToTreeMapDateToBKTransactionPartitionDate.get(lKey);
				if (lTreeMapDateToBKTransactionPartitionDate == null) {
					lTreeMapDateToBKTransactionPartitionDate = new TreeMap<>();
					pMapKeyToTreeMapDateToBKTransactionPartitionDate.put(lKey, lTreeMapDateToBKTransactionPartitionDate);
				}
				/*
				 * Assign
				 */
				lTreeMapDateToBKTransactionPartitionDate.put(lDate, lBKTransactionPartitionDate);
			}
		}
		/*
		 * Compute NAV
		 */
		BasicPrintMsg.display(this, "ComputeNAV (this may take some time)");
		for (int lDate = lDateStart; lDate <= lDateStop; lDate = BasicDateInt.getmPlusDay(lDate, 1)) {
			Map<String, BKTransactionPartitionDate> lMapKeyToBKTransactionPartitionDate = pTreeMapDateToMapKeyToBKTransactionPartitionDate.get(lDate);
			for (BKTransactionPartitionDate lBKTransactionPartitionDate : lMapKeyToBKTransactionPartitionDate.values()) {
				lBKTransactionPartitionDate.computeNAV();
			}
		}
		BasicPrintMsg.displayLn(this, "All done");
	}

	/**
	 * 
	 */
	public String toString() {
		return this.getClass().getSimpleName();
	}
	
	/*
	 * Getters & Setters
	 */
	public final TreeMap<Integer, Map<String, BKTransactionPartitionDate>> getpTreeMapDateToMapKeyToBKTransactionPartitionDate() {
		return pTreeMapDateToMapKeyToBKTransactionPartitionDate;
	}
	public final Map<String, TreeMap<Integer, BKTransactionPartitionDate>> getpMapKeyToTreeMapDateToBKTransactionPartitionDate() {
		return pMapKeyToTreeMapDateToBKTransactionPartitionDate;
	}








}
