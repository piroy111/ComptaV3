package step00_freeze_transactions.objects.frozen_fiscal_year;

import java.util.List;
import java.util.TreeMap;

import step00_freeze_transactions.BKFrozenManager;

public class BKFrozenFiscalYearManager {

	public BKFrozenFiscalYearManager(BKFrozenManager _sBKFrozenManager) {
		pBKFrozenManager = _sBKFrozenManager;
		/*
		 * 
		 */
		pTreeMapDateToBKFrozenFiscalYear = new TreeMap<>();
	}

	/*
	 * Data
	 */
	private BKFrozenManager pBKFrozenManager;
	private TreeMap<Integer, BKFrozenFiscalYear> pTreeMapDateToBKFrozenFiscalYear;
	private List<BKFrozenFiscalYear> pListBKFrozenFiscalYearToDo;

	/**
	 * 
	 * @param _sDateFY
	 * @return
	 */
	public final BKFrozenFiscalYear getpOrCreateBKFrozenFiscalYear(int _sDateFY) {
		BKFrozenFiscalYear lBKFrozenFiscalYear = pTreeMapDateToBKFrozenFiscalYear.get(_sDateFY);
		if (lBKFrozenFiscalYear == null) {
			lBKFrozenFiscalYear = new BKFrozenFiscalYear(_sDateFY, this);
			pTreeMapDateToBKFrozenFiscalYear.put(_sDateFY, lBKFrozenFiscalYear);
		}
		return lBKFrozenFiscalYear;
	}

	/*
	 * Getters & Setters
	 */
	public final BKFrozenManager getpBKFrozenManager() {
		return pBKFrozenManager;
	}
	public final TreeMap<Integer, BKFrozenFiscalYear> getpTreeMapDateToBKFrozenFiscalYear() {
		return pTreeMapDateToBKFrozenFiscalYear;
	}
	public final List<BKFrozenFiscalYear> getpListBKFrozenFiscalYearToDo() {
		return pListBKFrozenFiscalYearToDo;
	}


}
