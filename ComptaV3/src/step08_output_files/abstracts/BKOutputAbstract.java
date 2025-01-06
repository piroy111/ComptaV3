package step08_output_files.abstracts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import basicmethods.BasicDateInt;
import basicmethods.BasicDir;
import basicmethods.BasicFichiers;
import basicmethods.BasicFichiersNio;
import basicmethods.BasicFile;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import staticdata.com.BKCom;
import staticdata.com.fileswritten.BKComOnFilesWritten;
import staticdata.datas.BKStaticConst.com_file_written;
import staticdata.datas.BKStaticDir;
import step03_partitions.abstracts.objects.BKPartitionManager;

public abstract class BKOutputAbstract {

	public BKOutputAbstract(BKOutputManager _sBKOutputManager) {
		pBKOutputManager = _sBKOutputManager;
		/*
		 * 
		 */
		if (pBKOutputManager != null) {
			pBKPartitionManager = pBKOutputManager.getpBKLaunchMe().getpBKPartitionManager();
		}
		pSuffixToAdd = "";
		/*
		 * Name from the class
		 */
		pNameFromClass = this.getClass().getSimpleName();
		if (!pNameFromClass.startsWith("BKOutput_")) {
			BKCom.errorCodeLogic();
		}
		pNameFromClass = pNameFromClass.substring("BKOutput_".length());
		pNameFromClass = BasicString.insertSeparatorBeforeUpperCase(pNameFromClass, "", "_");
	}

	/*
	 * Abstract
	 */
	/**
	 * Call the public method 'addNewHeader(..)' and 'addNewLineToWrite(..)' in order to write the file<br>
	 * Generally you would use the BKPartitionManager to access the various holdings for a given date<br>
	 */
	public abstract void buildFileContent();
	/*
	 * Data
	 */
	protected BKOutputManager pBKOutputManager;
	private List<String> pListLineToWrite;
	private List<BKOutputLineSortDouble> pListBKOutputLineSortDoubleToWrite;
	private List<BKOutputLineSortString> pListBKOutputLineSortStringToWrite;
	private String pHeader;
	private String[] pArrayHeader;
	protected BKPartitionManager pBKPartitionManager;
	private String pSuffixToAdd;
	private String pNameFromClass;
	private String pDir;
	private List<String> pListFilesInFolder;

	/**
	 * If true, then we will display each line of the file in the console (useful for debugs)<br>
	 * to be used with override<br>
	 * @return
	 */
	public boolean getpIsDisplayFileInConsole() {
		return false;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean getpIsAddTodayInFrontOfNameFile() {
		return true;
	}
	
	/**
	 * Should be used in case that we cannot compute the file and we want to use the data of the previous file<br>
	 * The program will take the most recent file and take the exact same content
	 * @return
	 */
	public boolean getpIsRetakePreviousFile() {
		return false;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getpExtension() {
		return "csv";
	}
	
	/**
	 * Add new item to the header (not a new line, but a new item on the same first line of the header)
	 * @param _sHeader
	 */
	public final void addNewHeader(String _sHeader) {
		if (pHeader == null) {
			pHeader = _sHeader;
		} else {
			pHeader += "," + _sHeader;
		}
		pArrayHeader = getpArray(pHeader);
	}

	/**
	 * 
	 * @param _sLine
	 */
	public final void addNewLineToWrite(String _sLine) {
		/*
		 * Display in the console
		 */
		if (getpIsDisplayFileInConsole()) {
			if (pArrayHeader != null && pArrayHeader.length > 0) {
				String[] lArray = getpArray(_sLine);
				String lDisplay = "";
				for (int lIdx = 0; lIdx < lArray.length; lIdx++) {
					String lHeader = "";
					if (lIdx < pArrayHeader.length) {
						lHeader += pArrayHeader[lIdx] + "= ";
					}
					if (lIdx > 0) {
						lDisplay += "; ";
					}
					lDisplay += lHeader + lArray[lIdx];
				}
				System.out.println(lDisplay);
			} else {
				System.out.println(_sLine);
			}
		}
		/*
		 * Add the the list to print the file later on
		 */
		pListLineToWrite.add(_sLine);
	}

	/**
	 * 
	 * @param _sLine
	 * @param _sKeySort
	 */
	public final void addNewLineToWrite(String _sLine, String _sKeySort) {
		pListBKOutputLineSortStringToWrite.add(new BKOutputLineSortString(_sLine, _sKeySort));
	}
	
	/**
	 * 
	 * @param _sLine
	 * @param _sKeySort
	 */
	public final void addNewLineToWrite(String _sLine, double _sKeySort) {
		pListBKOutputLineSortDoubleToWrite.add(new BKOutputLineSortDouble(_sLine, _sKeySort));
	}
	
	/**
	 * 
	 * @param _sSuffixToAdd
	 */
	public final void addNewSuffixToNameFile(String _sSuffixToAdd) {
		if (_sSuffixToAdd != null) {
			pSuffixToAdd += "_" + _sSuffixToAdd;
		}
	}

	/**
	 * Can be overridden
	 * @return
	 */
	public String getpDirRoot() {
		return BKStaticDir.getOUTPUT_COMPTA();
	}
	
	/**
	 * 
	 */
	public final void writeFile() {
		writeFile(true);
	}
	
	/**
	 * 
	 */
	public final void writeFileWithoutTitle() {
		writeFile(false);
	}
	
	/**
	 * 
	 * @return
	 */
	public final String getpDir() {
		if (pDir == null) {
			pDir = getpDirRoot() + pNameFromClass + "/";
		}
		return pDir;
	}
	
	/**
	 * Read the list of the files in the folder
	 * @return
	 */
	public final List<String> getpListFilesInFolder() {
		if (pListFilesInFolder == null) {
			pListFilesInFolder = BasicFichiersNio.getListFilesInDirectory(getpDir());
		}
		return pListFilesInFolder;
	}
	
	/**
	 * 
	 * @return
	 */
	public final String getpSuffixForFile() {
		return pNameFromClass + pSuffixToAdd + "." + getpExtension();
	}
	
	/**
	 * 
	 * @param _sIsComOnTitle : if true, then we display a title in the console, if false, we just display a line in the console (for each file created)
	 */
	private final void writeFile(boolean _sIsComOnTitle) {
		/*
		 * Reset
		 */
		pHeader = null;
		pArrayHeader = null;
		pListLineToWrite = new ArrayList<>();
		pListBKOutputLineSortDoubleToWrite = new ArrayList<>();
		pListBKOutputLineSortStringToWrite = new ArrayList<>();
		/*
		 * Directory
		 */
		String lDir = getpDir();
		BasicFichiers.getOrCreateDirectory(lDir);
		/*
		 * Name of the file
		 */
		String lSuffix = getpSuffixForFile();
		String lNameFile;
		if (getpIsAddTodayInFrontOfNameFile()) {
			lNameFile = BasicDateInt.getmToday() + "_" + lSuffix;
		} else {
			lNameFile = lSuffix;
		}
		/*
		 * Communication
		 */
		if (_sIsComOnTitle) {
			BasicPrintMsg.displayTitle(this, "Output file for '" + pNameFromClass + "'");
		} else {
			BasicPrintMsg.display(this, "Write file '" + lNameFile + "'");
		}
		/*
		 * Call the parent to build the file content
		 */
		if (getpIsRetakePreviousFile()) {
			BasicDir lBasicDir = new BasicDir(lDir, lSuffix);
			BasicFile lBasicFile = lBasicDir.getmBasicFile(BasicDateInt.getmPlusDay(BasicDateInt.getmToday(), -1));
			pListLineToWrite = new ArrayList<>();
			pListLineToWrite.add("#" + lBasicFile.getmReadFile().getmHeader());
			pListLineToWrite.addAll(lBasicFile.getmReadFile().getmContent());
		} else {
			buildFileContent();
		}
		/*
		 * Fill lListLineToWrite
		 */
		Collections.sort(pListBKOutputLineSortDoubleToWrite);
		for (BKOutputLineSortDouble lBKOutputLineSortDouble : pListBKOutputLineSortDoubleToWrite) {
			pListLineToWrite.add(lBKOutputLineSortDouble.getpLine());
		}
		Collections.sort(pListBKOutputLineSortStringToWrite);
		for (BKOutputLineSortString lBKOutputLineSortString : pListBKOutputLineSortStringToWrite) {
			pListLineToWrite.add(lBKOutputLineSortString.getpLine());
		}
		/*
		 * Write the actual file 
		 */
		BKComOnFilesWritten.writeFile(com_file_written.OutputFiles, lDir, lNameFile, pHeader, pListLineToWrite);
	}

	/**
	 * 
	 * @param _sLine
	 * @return
	 */
	private String[] getpArray(String _sLine) {
		if (_sLine != null && !_sLine.equals("")) {
			return _sLine.split(",", -1);
		} else {
			return new String[]{};
		}
	}

	/*
	 * Getters & Setters
	 */
	public final BKPartitionManager getpBKPartitionManager() {
		return pBKPartitionManager;
	}
	public final String getpNameFromClass() {
		return pNameFromClass;
	}
	public final List<Integer> getpListDateEndOfMonth() {
		return pBKOutputManager.getpBKLaunchMe().getpBKAfterBKTransactionsManager().getpBKAfterComputeDates().getpListDateEndOfMonth();
	}
	public final int getpDateFirst() {
		return pBKOutputManager.getpBKLaunchMe().getpBKAfterBKTransactionsManager().getpBKAfterComputeDates().getpDateFirst();
	}
	

}
