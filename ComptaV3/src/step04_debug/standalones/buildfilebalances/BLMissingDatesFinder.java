package step04_debug.standalones.buildfilebalances;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicDir;
import basicmethods.BasicPrintMsg;

class BLMissingDatesFinder {

	protected BLMissingDatesFinder(BLManager _sBLManager) {
		pBLManager = _sBLManager;
	}
	
	/*
	 * Data
	 */
	private BLManager pBLManager;
	private List<Integer> pListMissingDates;
	
	/**
	 * 
	 */
	public final void run() {
		BasicPrintMsg.displayTitle(this, "Find the missing dates for the files balance");
		/*
		 * Load
		 */
		String lDirTransaction = pBLManager.getpBLDirsAndNamesFiles().getpDirTransactions();
		String lSuffixTransaction = pBLManager.getpBLDirsAndNamesFiles().getpSuffixTransactions();
		String lDirBalance = pBLManager.getpBLDirsAndNamesFiles().getpDirBalance();
		String lSuffixBalance = pBLManager.getpBLDirsAndNamesFiles().getpSuffixBalance();
		/*
		 * Basic DIR
		 */
		BasicDir lBasicDirTransaction = new BasicDir(lDirTransaction, lSuffixTransaction);
		BasicDir lBasicDirBalance = new BasicDir(lDirBalance, lSuffixBalance);
		/*
		 * Check the missing dates
		 */
		pListMissingDates = new ArrayList<>();
		for (int lDate : lBasicDirTransaction.getmListDate()) {
			if (!lBasicDirBalance.getmListDate().contains(lDate)) {
				BasicPrintMsg.display(this, "Missing date= " + lDate);
				pListMissingDates.add(lDate);
			}
		}
		if (pListMissingDates.size() == 0) {
			BasicPrintMsg.display(this, "No date to do --> All good --> Abort");
		}
	}

	/*
	 * Getters & Setters
	 */
	public final BLManager getpBLManager() {
		return pBLManager;
	}
	public final List<Integer> getpListMissingDates() {
		return pListMissingDates;
	}
	
}
