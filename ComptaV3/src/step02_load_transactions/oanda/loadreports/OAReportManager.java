package step02_load_transactions.oanda.loadreports;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicDir;
import basicmethods.BasicFile;
import step02_load_transactions.oanda.OAManager;

public class OAReportManager {

	public OAReportManager(OAManager _sOAManager, String _sSuffixNameFile) {
		pOAManager = _sOAManager;
		pSuffixNameFile = _sSuffixNameFile;
	}

	/*
	 * Data
	 */
	private OAManager pOAManager;
	private String pSuffixNameFile;
	private List<OAReport> pListOAReport;
	private List<String> pListIDAlreadyDoneOldVersion;
	private List<String> pListIDAlreadyDoneNewVersion;
	private OAMigrationManager pOAMigrationManager;

	/**
	 * loadImportFilesAndCreatOAReports
	 */
	public final void run() {
		/*
		 * Initiate
		 */
		pListOAReport = new ArrayList<OAReport>();
		pListIDAlreadyDoneOldVersion = new ArrayList<String>();
		pListIDAlreadyDoneNewVersion = new ArrayList<String>();
		pOAMigrationManager = new OAMigrationManager(this);
		/*
		 * Load reports
		 */
		BasicDir lBasicDir = new BasicDir(pOAManager.getpDirInput(), pSuffixNameFile);
		for (BasicFile lBasicFile : lBasicDir.getmTreeMapDateToBasicFile().values()) {
			if (lBasicFile.getmDate() >= pOAManager.getpOAFirstDateManager().getpDateStart()) {
				OAReport lOAReport = new OAReport(lBasicFile.getmReadFile(), this);
				lOAReport.loadFile();
				pListOAReport.add(lOAReport);
			}
		}
		/*
		 * Check migration
		 */
		pOAMigrationManager.check();
	}

	/**
	 * we use this in order to not to duplicate transactions in two reports
	 * @param _sID
	 */
	protected final boolean checkAndStoreNewID(String _sID, boolean _sIsNewVersion) {
		List<String> lListDone = _sIsNewVersion ? pListIDAlreadyDoneNewVersion : pListIDAlreadyDoneOldVersion;
		if (lListDone.contains(_sID)) {
			return false;
		} else {
			lListDone.add(_sID);
			return true;
		}
	}

	/**
	 * 
	 */
	public String toString() {
		return pSuffixNameFile;
	}
	
	/*
	 * Getters & Setters
	 */
	public final List<OAReport> getpListOAReport() {
		return pListOAReport;
	}
	public final OAMigrationManager getpOAMigrationManager() {
		return pOAMigrationManager;
	}























}
