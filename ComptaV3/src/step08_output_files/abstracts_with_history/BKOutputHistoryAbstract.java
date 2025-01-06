package step08_output_files.abstracts_with_history;

import java.util.List;

import basicmethods.BasicDateInt;
import basicmethods.BasicDir;
import basicmethods.BasicFile;
import basicmethods.BasicPrintMsg;
import step03_partitions.abstracts.objects.BKPartitionManager;

public abstract class BKOutputHistoryAbstract {

	public BKOutputHistoryAbstract(BKOutputHistoryManager _sBKOutputHistoryManager) {
		pBKOutputHistoryManager = _sBKOutputHistoryManager;
		/*
		 * 
		 */
		pBKPartitionManager = pBKOutputHistoryManager.getpBKLaunchMe().getpBKPartitionManager();
	}

	/*
	 * Abstract
	 */
	public abstract String getpHeader();
	public abstract List<String> getpListLineToWrite(int _sDateEndOfMonthToDo);
	/*
	 * Data
	 */
	private BKOutputHistoryManager pBKOutputHistoryManager;
	private String pDirDifferential;
	private String pDirGlobal;
	private String pSuffixFile;
	private String pNameFromClass;
	private BasicDir pBasicDir;
	private List<Integer> pListDateToDo;
	private List<String> pListLineToWriteDifferential;
	private List<String> pListLineToWriteGlobal;
	private String pSuffixToAdd;
	protected BKPartitionManager pBKPartitionManager;
	
	/**
	 * 
	 */
	public final void writeFile() {
		BasicPrintMsg.displaySuperTitle(this, "Write file output with history for '" + this.getClass().getSimpleName() + "'");
		new BKOutputHistory_Step01_CreateDir(this).initiateDirAndFileName();		
		new BKOutputHistory_Step02_IdentifyDates(this).identifyDatesToDo();
		new BKOutputHistory_Step03_ReadFiles(this).readFiles();
		new BKOutputHistory_Step04_CallSonToCompute(this).CallSonToCompute();
		new BKOutputHistory_Step05_WriteNewFile(this).WriteNewFile();
	}
	
	/**
	 * 
	 * @param _sSuffixToAdd
	 */
	public final void addNewSuffixToNameFile(String _sSuffixToAdd) {
		if (pSuffixToAdd == null) {
			pSuffixToAdd = "";
		}
		pSuffixToAdd += "_" + _sSuffixToAdd;
	}
	
	/**
	 * 
	 * @param _sDateReportCurrent
	 * @return
	 */
	public final BasicFile getpBKFileReportPrevious(int _sDateReportCurrent) {
		return pBasicDir.getmBasicFile(BasicDateInt.getmPlusDay(_sDateReportCurrent, -1));		
	}
	
	
	/*
	 * Getters & Setters
	 */
	public final BKOutputHistoryManager getpBKOutputHistoryManager() {
		return pBKOutputHistoryManager;
	}
	public final void setpBKOutputHistoryManager(BKOutputHistoryManager pBKOutputHistoryManager) {
		this.pBKOutputHistoryManager = pBKOutputHistoryManager;
	}
	public final String getpSuffixFile() {
		return pSuffixFile;
	}
	public final void setpSuffixFile(String pSuffixFile) {
		this.pSuffixFile = pSuffixFile;
	}
	public final String getpNameFromClass() {
		return pNameFromClass;
	}
	public final void setpNameFromClass(String pNameFromClass) {
		this.pNameFromClass = pNameFromClass;
	}
	public final BasicDir getpBasicDir() {
		return pBasicDir;
	}
	public final void setpBasicDir(BasicDir pBasicDir) {
		this.pBasicDir = pBasicDir;
	}
	public final List<Integer> getpListDateToDo() {
		return pListDateToDo;
	}
	public final void setpListDateToDo(List<Integer> pListDateToDo) {
		this.pListDateToDo = pListDateToDo;
	}
	public final String getpSuffixToAdd() {
		return pSuffixToAdd;
	}
	public final String getpDirDifferential() {
		return pDirDifferential;
	}
	public final void setpDirDifferential(String pDirDifferential) {
		this.pDirDifferential = pDirDifferential;
	}
	public final void setpSuffixToAdd(String pSuffixToAdd) {
		this.pSuffixToAdd = pSuffixToAdd;
	}
	public final String getpDirGlobal() {
		return pDirGlobal;
	}
	public final void setpDirGlobal(String pDirGlobal) {
		this.pDirGlobal = pDirGlobal;
	}
	public final List<String> getpListLineToWriteDifferential() {
		return pListLineToWriteDifferential;
	}
	public final void setpListLineToWriteDifferential(List<String> pListLineToWriteDifferential) {
		this.pListLineToWriteDifferential = pListLineToWriteDifferential;
	}
	public final List<String> getpListLineToWriteGlobal() {
		return pListLineToWriteGlobal;
	}
	public final void setpListLineToWriteGlobal(List<String> pListLineToWriteGlobal) {
		this.pListLineToWriteGlobal = pListLineToWriteGlobal;
	}
	public final void addpListLineToWriteDifferential(List<String> pListLineToWriteDifferential) {
		this.pListLineToWriteDifferential.addAll(pListLineToWriteDifferential);
	}
	public final void addpListLineToWriteGlobal(List<String> pListLineToWriteGlobal) {
		this.pListLineToWriteGlobal.addAll(pListLineToWriteGlobal);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
}
