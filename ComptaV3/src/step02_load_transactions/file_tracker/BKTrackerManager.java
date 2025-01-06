package step02_load_transactions.file_tracker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step00_freeze_transactions.BKFrozenManager;
import step02_load_transactions.objects.file_delivery.BKDeliveryFile;
import step02_load_transactions.objects.file_delivery.BKDeliveryFileManager;
import step02_load_transactions.objects.file_transaction.BKTransactionFile;
import step02_load_transactions.objects.file_transaction.BKTransactionFileManager;
import step02_load_transactions.objects.transaction.BKTransactionManager;
import step10_launchme.BKLaunchMe;

public class BKTrackerManager {

	public BKTrackerManager(BKLaunchMe _sBKLauncheMe) {
		pBKLaunchMe = _sBKLauncheMe;
		/*
		 * 
		 */
		pBKTrackerWriterManager = new BKTrackerWriterManager(this);
	}

	/*
	 * Data
	 */
	private BKLaunchMe pBKLaunchMe;
	private List<Integer> pListDateWithIssue;
	private int pDateWithIssueMin;
	private int pDateWithIssueMax;
	private BKTransactionFileManager pBKTransactionFileManagerFromComputation;
	private BKTransactionFileManager pBKTransactionFileManagerFromTacker;
	private BKDeliveryFileManager pBKDeliveryFileManagerFromComputation;
	private BKDeliveryFileManager pBKDeliveryFileManagerFromTacker;
	private BKTrackerWriterManager pBKTrackerWriterManager;
	private int pDateMaxToCheck;

	/**
	 * 
	 */
	public final void run() {
		BasicPrintMsg.displaySuperTitle(this, "Compare the files detected by the COMPTA to the one stored in the file tracker");
		/*
		 * Case we are not in charge --> we skip
		 */
		String lDir = BKStaticDir.getZZ();
		String lNameFile = BKStaticNameFile.getIS_IN_CHARGE_BKFROZEN();
		boolean lIsCheckFileDates;
		if (!BasicFichiers.getmIsFileAlreadyExist(lDir + lNameFile)) {
			BasicPrintMsg.display(this, "The file conf is not present: '" + lDir + lNameFile + "'");
			BasicPrintMsg.display(this, "Therefore I skip the check for change of date of the BKTransactions prior to the BKFrozen");
			lIsCheckFileDates = false;
		} else {
			BasicPrintMsg.display(this, "This PC is in charge of writing the BKFrozen. I am checking now that no BKTransaction file has changed prior to the date of frozen. I compare with the dates stored in BKTracker");
			lIsCheckFileDates = true;
		}
		/*
		 * Initiate
		 */
		pListDateWithIssue = new ArrayList<>();
		/*
		 * Set the date limit to issue an error -> we take the date of the last FYFrozen
		 */
		pDateMaxToCheck = BKFrozenManager.getLIST_FY_FROZEN().get(BKFrozenManager.getLIST_FY_FROZEN().size() - 1);
		/*
		 * Case of the deliveries
		 */
		pBKDeliveryFileManagerFromComputation = pBKLaunchMe.getpBKDeliveriesManager().getpBKDeliveryFileManager();
		pBKDeliveryFileManagerFromTacker = new BKDeliveryFileManager("FileTracker");
		pBKDeliveryFileManagerFromTacker.readFileTracker();
		BasicPrintMsg.display(this, "Number of files in " + pBKDeliveryFileManagerFromComputation.getpSource() + "= " + pBKDeliveryFileManagerFromComputation.getpMapKeyToBKFile().size());
		BasicPrintMsg.display(this, "Number of files in " + pBKDeliveryFileManagerFromTacker.getpSource() + "= " + pBKDeliveryFileManagerFromTacker.getpMapKeyToBKFile().size());
		if (lIsCheckFileDates) {
			BKTrackerComparator<BKDeliveryFile, BKDeliveryFileManager> lBKTrackerComparatorBKDelivery = new BKTrackerComparator<>(this);
			lBKTrackerComparatorBKDelivery.compare(pBKDeliveryFileManagerFromTacker, pBKDeliveryFileManagerFromComputation);
		}
		/*
		 * Case of the BKTransaction
		 */
		pBKTransactionFileManagerFromComputation = BKTransactionManager.getpBKTransactionFileManager();
		pBKTransactionFileManagerFromTacker = new BKTransactionFileManager("FileTracker");
		pBKTransactionFileManagerFromTacker.readFileTracker();
		BasicPrintMsg.display(this, "Number of files in " + pBKTransactionFileManagerFromComputation.getpSource() + "= " + pBKTransactionFileManagerFromComputation.getpMapKeyToBKFile().size());
		BasicPrintMsg.display(this, "Number of files in " + pBKTransactionFileManagerFromTacker.getpSource() + "= " + pBKTransactionFileManagerFromTacker.getpMapKeyToBKFile().size());
		if (lIsCheckFileDates) {
			BKTrackerComparator<BKTransactionFile, BKTransactionFileManager> lBKTrackerComparatorBKTransaction = new BKTrackerComparator<>(this);
			lBKTrackerComparatorBKTransaction.compare(pBKTransactionFileManagerFromTacker, pBKTransactionFileManagerFromComputation);
		}
		/*
		 * Display date with issues
		 */
		Collections.sort(pListDateWithIssue);
		BasicPrintMsg.displayTitle(this, "Dates with issues");
		BasicPrintMsg.display(this, "Number of dates with issues= " + pListDateWithIssue.size());
		if (pListDateWithIssue.size() > 0) {
			pDateWithIssueMin = pListDateWithIssue.get(0);
			pDateWithIssueMax = pListDateWithIssue.get(pListDateWithIssue.size() - 1);
		} else {
			pDateWithIssueMin = Integer.MAX_VALUE;
			pDateWithIssueMax = -1;
		}
		BasicPrintMsg.display(this, "Earliest date with issue= " + pDateWithIssueMin);
		BasicPrintMsg.display(this, "Latest date with issue  = " + pDateWithIssueMax);
	}

	/**
	 * 
	 */
	public final void writeFileTracker() {
		pBKTrackerWriterManager.writeFile();
	}

	/**
	 * 
	 * @param _sDate
	 */
	protected final void declareDateWithIssue(int _sDateWithIssue) {
		if (!pListDateWithIssue.contains(_sDateWithIssue)) {
			pListDateWithIssue.add(_sDateWithIssue);
		}
	}

	/*
	 * Getters & Setters
	 */
	/**
	 * List is sorted already in ascending order
	 */
	public final List<Integer> getpListDateWithIssue() {
		return pListDateWithIssue;
	}
	public final BKLaunchMe getpBKLaunchMe() {
		return pBKLaunchMe;
	}
	/**
	 * @return Integer.MAX_VALUE() if there is no date with issue
	 */
	public final int getpDateWithIssueMin() {
		return pDateWithIssueMin;
	}
	/**
	 * @return -1 if there is no date with issue
	 */
	public final int getpDateWithIssueMax() {
		return pDateWithIssueMax;
	}
	public final BKTransactionFileManager getpBKTransactionFileManagerFromComputation() {
		return pBKTransactionFileManagerFromComputation;
	}
	public final BKTransactionFileManager getpBKTransactionFileManagerFromTacker() {
		return pBKTransactionFileManagerFromTacker;
	}
	public final BKDeliveryFileManager getpBKDeliveryFileManagerFromComputation() {
		return pBKDeliveryFileManagerFromComputation;
	}
	public final BKDeliveryFileManager getpBKDeliveryFileManagerFromTacker() {
		return pBKDeliveryFileManagerFromTacker;
	}

	public final int getpDateMaxToCheck() {
		return pDateMaxToCheck;
	}

}
