package step02_load_transactions.interactivebrokers.createfilestransactions.files;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicDir;
import basicmethods.BasicFichiersNioRaw;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import step02_load_transactions.interactivebrokers.createfilestransactions.IBManager;

public class IBFileManager {

	public IBFileManager(IBManager _sIBManager) {
		pIBManager = _sIBManager;
	}
	
	/*
	 * Data
	 */
	private IBManager pIBManager;
	private int pDateLastReportTreated;
	private List<IBFile> pListIBFile;
	private IBFile pIBFileLastReportTreated;
	private Map<Integer, IBFile> pMapDateEndToIBFile;
	private List<IBFile> pListIBFileNew;
	
	/**
	 * 
	 */
	public final void checkFiles() {
		BasicPrintMsg.displayTitle(this, "Read files IB");
		computeDateLastReport();
		readIBFiles();
		checks();
		createListIBFileNew();
	}
	
	/**
	 * Compute the date of the last report from the files treated
	 */
	private void computeDateLastReport() {
		String lDir = pIBManager.getpIBFindDirAndSuffix().getpDirTransactions();
		String lSuffix = pIBManager.getpIBFindDirAndSuffix().getpSuffixTransactions();
		BasicDir lBasicDir = new BasicDir(lDir, lSuffix);
		List<Integer> lListDates = lBasicDir.getmListDate();
		if (lListDates.size() > 0) {
			pDateLastReportTreated = lListDates.get(lListDates.size() - 1);
		} else {
			pDateLastReportTreated = -1;
		}
		BasicPrintMsg.display(this, "pDateLastReportTreated= " + pDateLastReportTreated);
	}
	
	/**
	 * Read the IBFiles
	 */
	private void readIBFiles() {
		/*
		 * Initiate
		 */
		pListIBFile = new ArrayList<IBFile>();
		pMapDateEndToIBFile = new HashMap<Integer, IBFile>();
		String lDir = pIBManager.getpIBFindDirAndSuffix().getpDirInput();
		String lSuffix = pIBManager.getpIBFindDirAndSuffix().getpSuffixInput();
		/*
		 * 
		 */
		List<Path> lListPath = BasicFichiersNioRaw.getListPath(Paths.get(lDir));
		for (Path lPath : lListPath) {
			String lNameFile = lPath.getFileName().toString();
			if (lNameFile.endsWith(lSuffix)) {
				int lDateStart = BasicString.getInt(lNameFile.substring(0, 8));
				int lDateStop = BasicString.getInt(lNameFile.substring(9, 17));
				IBFile lIBFile = new IBFile(lDateStart, lDateStop, lPath, this);
				/*
				 * Put in Map & List
				 */
				if (pMapDateEndToIBFile.containsKey(lDateStop)) {
					BasicPrintMsg.error("There are two reports with the same end date. This should not happen"
							+ "\nDir= " + lDir
							+ "\nFile1= " + pMapDateEndToIBFile.get(lDateStop).getpPath().getFileName().toString()
							+ "\nFile2= " + lNameFile);
				} else {
					pListIBFile.add(lIBFile);
					pMapDateEndToIBFile.put(lDateStop, lIBFile);
				}
				/*
				 * Check the file from the previous report
				 */
				if (lDateStop == pDateLastReportTreated) {
					pIBFileLastReportTreated = lIBFile;
				}
			}
		}
	}

	/**
	 * Check the last report is there
	 */
	private void checks() {
		if (pDateLastReportTreated > 0 && pIBFileLastReportTreated == null) {
			BasicPrintMsg.error("The last report from which we computed the csv treated is missing; Date= " + pDateLastReportTreated);
		}
	}

	/**
	 * IBFileNew
	 */
	private void createListIBFileNew() {
		pListIBFileNew = new ArrayList<IBFile>();
		List<Integer> lListDate = new ArrayList<Integer>(pMapDateEndToIBFile.keySet());
		Collections.sort(lListDate);
		for (int lDate : lListDate) {
			if (lDate > pDateLastReportTreated) {
				pListIBFileNew.add(pMapDateEndToIBFile.get(lDate));
			}
		}
	}

	/*
	 * Getters & Setters
	 */
	public final List<IBFile> getpListIBFileNew() {
		return pListIBFileNew;
	}
	public final IBFile getpIBFileLastReport() {
		return pIBFileLastReportTreated;
	}
	public final IBManager getpIBManager() {
		return pIBManager;
	}
	public final List<IBFile> getpListIBFile() {
		return pListIBFile;
	}
	
	
	
	
	
	
	
	
	
}
