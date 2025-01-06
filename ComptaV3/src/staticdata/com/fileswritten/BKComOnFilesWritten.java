package staticdata.com.fileswritten;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import basicmethods.BasicString;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticConst.com_file_written;
import staticdata.datas.BKStaticDir;

public class BKComOnFilesWritten {

	public BKComOnFilesWritten() {
		
	}
	
	/*
	 * Data
	 */
	private static Map<String, FWCategory> mapNameToFWCategory = new HashMap<>();
	
	/**
	 * Write a unique file with all the files written by the program (or removed) and grouped by category 
	 */
	private static void writeOutPut() {
		/*
		 * Prepare name and directory for the file to be written with the list of all the files written
		 */
		String lNameFromClass = BasicString.insertSeparatorBeforeUpperCase(BKComOnFilesWritten.class.getSimpleName(), "", "_");
		String lDir = BKStaticDir.getOUTPUT() + lNameFromClass + "/";
		BasicFichiers.getOrCreateDirectory(lDir);
		String lNameFile = BasicDateInt.getmToday() + "_" + lNameFromClass + ".csv";
		/*
		 * Initiate
		 */
		List<String> lListLineToWrite = new ArrayList<>();
		/*
		 * 
		 */
		lListLineToWrite.add("");
		String lLineEarliestDate = "Date of the earliest BKTransaction changed or added by Compta= "
				+ "," + BKStaticConst.getDATE_FIRST_CHANGED_TRANSACTIONS();
		lListLineToWrite.add(lLineEarliestDate);
		/*
		 * Build the file content
		 */		
		for (FWCategory lFWCategory : mapNameToFWCategory.values()) {
			lListLineToWrite.add("");
			String lLineZero = lFWCategory.getpName();
			lListLineToWrite.add(lLineZero);
			for (FWFile lFWFile : lFWCategory.getpListFWFile()) {
				String lLine = lLineZero
						+ "," + lFWFile.getpDir()
						+ "," + lFWFile.getpName()
						+ "," + lFWFile.getpDateStr()
						+ "," + lFWFile.getpIsOldFileRemoved()
						+ "," + lFWFile.getpIsWritten();
				lListLineToWrite.add(lLine);
			}			
		}
		/*
		 * 
		 */
		String lHeader = "Category,Dir,Name of file,Date of file,Is old file removed ?,Is new file written?";
		BasicFichiers.writeFile(lDir, lNameFile, lHeader, lListLineToWrite);
	}
	
	/**
	 * 
	 */
	public static final void writeFile(com_file_written _sComOnFileWritten, String _sDir, String _sNameFile, 
			String _sHeader, List<String> _sListLineToWrite) {
		FWCategory lFWCategory = getOrCreate(_sComOnFileWritten);
		FWFile lFWFile = lFWCategory.getpOrCreateFWFile(_sDir, _sNameFile);
		lFWFile.writeFile(_sHeader, _sListLineToWrite);
		writeOutPut();
	}
	
	/**
	 * 
	 * @param _sPathSource
	 * @param _sDirTarget
	 * @param _sIsKillOnError
	 */
	public static final boolean moveFileIfExists(com_file_written _sComOnFileWritten, String _sDir, String _sName) {
		FWCategory lFWCategory = getOrCreate(_sComOnFileWritten);
		FWFile lFWFile = lFWCategory.getpOrCreateFWFile(_sDir, _sName);
		if (lFWFile.getpIsOldFileRemoved()) {
			return false;
		}
		lFWFile.moveFileIfExists();
		writeOutPut();
		return lFWFile.getpIsOldFileRemoved();
	}
	
	/**
	 * 
	 * @param _sPathSource
	 * @param _sDirTarget
	 * @param _sIsKillOnError
	 */
	public static final boolean deleteFileIfExists(com_file_written _sComOnFileWritten, String _sDir, String _sName) {
		FWCategory lFWCategory = getOrCreate(_sComOnFileWritten);
		FWFile lFWFile = lFWCategory.getpOrCreateFWFile(_sDir, _sName);
		if (lFWFile.getpIsOldFileRemoved()) {
			return false;
		}
		lFWFile.deleteFileIfExists();
		writeOutPut();
		return lFWFile.getpIsOldFileRemoved();
	}
	
	/**
	 * 
	 * @param _sName
	 * @return
	 */
	private static FWCategory getOrCreate(com_file_written _sComOnFileWritten) {
		String lName = null;
		if (_sComOnFileWritten != null) {
			lName = BasicString.insertSeparatorBeforeUpperCase(_sComOnFileWritten.toString(), null, " ");
		}
		FWCategory lFWCategory = mapNameToFWCategory.get(lName);
		if (lFWCategory == null) {
			lFWCategory = new FWCategory(lName);
			mapNameToFWCategory.put(lName, lFWCategory);
		}
		return lFWCategory;
	}
	


}
