package step00_freeze_transactions.step01_check_file_missing_or_for_change;

import basicmethods.BasicPrintMsg;
import step02_load_transactions.file_tracker.BKTrackerManager;

public class BKFrozenFileChangeChecker {

	public BKFrozenFileChangeChecker(BKFrozenDateChooser _sBKFrozenDateChooser) {
		pBKFrozenDateChooser = _sBKFrozenDateChooser;
	}
	
	/*
	 * Data
	 */
	private BKFrozenDateChooser pBKFrozenDateChooser;
	private int pDateFYFrozenToDo;
	
	/**
	 * Check that:<br>
	 *  - there is no new file of BKTransaction in the past
	 *  - there is not a file of BKTransaction in the past which disappear
	 *  - the time stamp of all existing files of BKTransaction is the same as last time the program was run
	 */
	public final void check() {
		BasicPrintMsg.displayTitle(this, "Check that the source files have not changed (BKTransaction, BKDelivery)");
		BKTrackerManager lBKTrackerManager = pBKFrozenDateChooser.getpBKFrozenManager().getpBKLaunchMe().getpBKTrackerManager();
		for (int lDateFYFrozen : pBKFrozenDateChooser.getpListDateFYFrozen()) {
			/*
			 * Check if the file itself which we are writing has changed
			 */
			if (lDateFYFrozen > lBKTrackerManager.getpDateWithIssueMin()) {
				BasicPrintMsg.display(this, "some files source have changed before the FYFrozen date= " + lDateFYFrozen + " --> I need to re-compute the file frozen");
				pBKFrozenDateChooser.declareNewDateFYFrozenToDo(lDateFYFrozen);
			} else {
				BasicPrintMsg.display(this, "All file sources are the same for FYFrozen date= " + lDateFYFrozen + " --> All good");
			}
		}
	}

	/*
	 * Getters & Setters
	 */
	public final BKFrozenDateChooser getpBKFrozenDateChooser() {
		return pBKFrozenDateChooser;
	}
	public final int getpDateFYFrozenToDo() {
		return pDateFYFrozenToDo;
	}
	
	
}
