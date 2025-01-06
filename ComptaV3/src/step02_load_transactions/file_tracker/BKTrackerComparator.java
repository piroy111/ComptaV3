package step02_load_transactions.file_tracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import basicmethods.BasicPrintMsg;
import step00_freeze_transactions.BKFrozenManager;
import step02_load_transactions.objects.file.BKFile;
import step02_load_transactions.objects.file.BKFileManager;

class BKTrackerComparator<F extends BKFile<F, M>, M extends BKFileManager<F, M>> {

	protected BKTrackerComparator(BKTrackerManager _sBKTrackerManager) {
		pBKTrackerManager = _sBKTrackerManager;
	}

	/*
	 * Data
	 */
	private BKTrackerManager pBKTrackerManager;
	private List<F> pListFileComputationMissing;
	private List<F>	pListFileComputationTooMany;
	private List<F> pListFileComputationChanged;

	/**
	 * 
	 */
	public final void compare(M _sBKFileManagerFromFileTracker, M _sBKFileManagerFromComputation) {
		pListFileComputationMissing = checkMissing(_sBKFileManagerFromFileTracker, _sBKFileManagerFromComputation);
		pListFileComputationTooMany = checkMissing(_sBKFileManagerFromComputation, _sBKFileManagerFromFileTracker);
		pListFileComputationChanged = checkChangedTimeStamp(_sBKFileManagerFromFileTracker, _sBKFileManagerFromComputation);
		/*
		 * Compute the list of dates with issue
		 */
		declareDateWithIssue(pListFileComputationMissing);
		declareDateWithIssue(pListFileComputationTooMany);
		declareDateWithIssue(pListFileComputationChanged);
	}

	/**
	 * 
	 * @param _sBKFileManagerReference
	 * @param _sBKFileManagerToCheck
	 */
	private List<F> checkMissing(M _sBKFileManagerReference, M _sBKFileManagerToCheck) {
		BasicPrintMsg.displayTitle(this, "Find the files missing in " + _sBKFileManagerReference.getpSource() + " and present in " + _sBKFileManagerToCheck.getpSource());
		/*
		 * Find the files present in source and missing in target
		 */
		List<F> lListBKFileMissing = new ArrayList<>();
		List<String> lListCom = new ArrayList<>();
		for (F lBKFile : _sBKFileManagerReference.getpMapKeyToBKFile().values()) {
			if (!_sBKFileManagerToCheck.getpMapKeyToBKFile().containsKey(lBKFile.getpKey())) {
				int lDateFY = BKFrozenManager.getpDateFYFrozenToAllocate(lBKFile.getpDateFile());
				if (lDateFY <= pBKTrackerManager.getpDateMaxToCheck()) {
					String lMsg = "FY= " + lDateFY 
							+ "; File present in '" + _sBKFileManagerReference.getpSource() + "'"
							+ " but absent in '" + _sBKFileManagerToCheck.getpSource() + "'"
							+ " = " + lBKFile.getpNameFile();
					lListCom.add(lMsg);
					lListBKFileMissing.add(lBKFile);
				}
			}
		}
		/*
		 * Communication
		 */
		Collections.sort(lListCom);
		for (String lCom : lListCom) {
			BasicPrintMsg.display(this, lCom);			
		}
		String lMsgGlobal = "Number of files present in '" + _sBKFileManagerReference.getpSource() + "'"
				+ " but absent in '" + _sBKFileManagerToCheck.getpSource() + "'"
				+ " = " + lListBKFileMissing.size();
		BasicPrintMsg.display(this, lMsgGlobal);
		return lListBKFileMissing;
	}

	/**
	 * 
	 * @param _sBKFileManagerFromTrackerFile
	 * @param _sBKFileManagerComputed
	 * @return
	 */
	private List<F> checkChangedTimeStamp(M _sBKFileManagerFromTrackerFile, M _sBKFileManagerComputed) {
		BasicPrintMsg.displayTitle(this, "Detect changes in files");
		/*
		 * Compare the time stamp rounded to 1 second
		 */
		List<F> lListBKFileChanged = new ArrayList<>();
		List<String> lListCom = new ArrayList<>();
		for (F lBKFileFromTrackerFile : _sBKFileManagerFromTrackerFile.getpMapKeyToBKFile().values()) {
			F lBKFileComputed = _sBKFileManagerComputed.getpMapKeyToBKFile().get(lBKFileFromTrackerFile.getpKey());
			if (lBKFileComputed != null) {
				/*
				 * Check if the 2 files are the same
				 */
				int lDateFY = BKFrozenManager.getpDateFYFrozenToAllocate(lBKFileFromTrackerFile.getpDateFile());
				if (lDateFY <= pBKTrackerManager.getpDateMaxToCheck()) {
					String lMsgError = lBKFileFromTrackerFile.getpCompareTimeStamp(lBKFileComputed);
					if (!lMsgError.equals("")) {
						lMsgError = "FY= " + lDateFY 
								+ "; Timestamp changed: " + BasicPrintMsg.getJustifiedText("'" + lBKFileFromTrackerFile.getpNameFile() + "'", 80) + lMsgError;
						lListCom.add(lMsgError);
						lListBKFileChanged.add(lBKFileComputed);
					}
				}
			}
		}
		/*
		 * Communication
		 */
		Collections.sort(lListCom);
		for (String lCom : lListCom) {
			BasicPrintMsg.display(this, lCom);			
		}
		BasicPrintMsg.display(this, "Number of files from " + _sBKFileManagerFromTrackerFile.getpSource() + " which have a change= " + lListBKFileChanged.size());
		return lListBKFileChanged;
	}

	/**
	 * 
	 * @param _sDate
	 */
	private void declareDateWithIssue(List<F> _sListBKFile) {
		for (F lBKFile : _sListBKFile) {
			pBKTrackerManager.declareDateWithIssue(lBKFile.getpDateFile());
		}
	}

	/*
	 * Getters & Setters
	 */
	public final List<F> getpListFileComputationMissing() {
		return pListFileComputationMissing;
	}
	public final List<F> getpListFileComputationTooMany() {
		return pListFileComputationTooMany;
	}
	public final List<F> getpListFileComputationChanged() {
		return pListFileComputationChanged;
	}
	public final BKTrackerManager getpBKTrackerManager() {
		return pBKTrackerManager;
	}

}
