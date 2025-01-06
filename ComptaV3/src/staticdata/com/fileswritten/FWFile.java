package staticdata.com.fileswritten;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import basicmethods.BasicFichiersNio;
import basicmethods.BasicFichiersNioRaw;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.ReadFile;
import basicmethods.ReadFile.comReadFile;
import staticdata.datas.BKStaticDir;
import staticdata.dateloaders.BKStaticDateEarliestTransactionLoader;

class FWFile {

	protected FWFile(FWCategory _sFWCategory, String _sDir, String _sName) {
		pFWCategory = _sFWCategory;
		pDir = _sDir;
		pName = _sName;
		/*
		 * 
		 */
		pDateStr = BasicString.getDate(pName);
		pKey = getKey(pDir, pName);
	}

	/*
	 * Data
	 */
	private FWCategory pFWCategory;
	private String pKey;
	private String pDir;
	private String pName;
	private boolean pIsWritten;
	private boolean pIsOldFileRemoved;
	private String pDateStr;

	/**
	 * 
	 * @param _sDir
	 * @param _sName
	 * @return
	 */
	protected static String getKey(String _sDir, String _sName) {
		return _sDir + ";;" + _sName;
	}

	/**
	 * 
	 * @param _sHeader
	 * @param _sListLineToWrite
	 */
	public final void writeFile(String _sHeader, List<String> _sListLineToWrite) {
		BasicFichiers.writeFile(pDir, pName, _sHeader, _sListLineToWrite);
		pIsWritten = true;
	}

	/**
	 * 
	 * @param _sPathSource
	 * @param _sDirTarget
	 * @param _sIsKillOnError
	 */
	public final void moveFileIfExists() {
		Path lPath = Paths.get(pDir + pName);
		if (BasicFichiersNioRaw.getIsAlreadyExist(lPath)) {
			/*
			 * We must read the file in order to check the date of the earliest transaction which we touch
			 */
			BKStaticDateEarliestTransactionLoader.readFileAndUpdateDate(new ReadFile(lPath, comReadFile.COM_ONLY_IF_ERROR));
			/*
			 * Move file
			 */
			String lNewDir = BKStaticDir.getOLD()
					+ BasicDateInt.getmToday() + "/"
					+ pFWCategory.getpName() + "/";
			BasicFichiers.getOrCreateDirectory(lNewDir);
			BasicFichiersNio.moveFile(lPath, lNewDir, true);
			BasicPrintMsg.display(this, "File '" + pName + "' moved to '" + lNewDir + "...'");
			pIsOldFileRemoved = true;
		}
	}
	
	/**
	 * 
	 * @param _sPathSource
	 * @param _sDirTarget
	 * @param _sIsKillOnError
	 */
	public final void deleteFileIfExists() {
		Path lPath = Paths.get(pDir + pName);
		if (BasicFichiersNioRaw.getIsAlreadyExist(lPath)) {
			/*
			 * We must read the file in order to check the date of the earliest transaction which we touch
			 */
			BKStaticDateEarliestTransactionLoader.readFileAndUpdateDate(new ReadFile(lPath, comReadFile.COM_ONLY_IF_ERROR));
			/*
			 * Move file
			 */
			BasicFichiersNio.deleteFile(lPath, true);
			BasicPrintMsg.display(this, "Deleted file '" + pName + "'");
			pIsOldFileRemoved = true;
		}
	}

	/*
	 * Getters & Setters
	 */
	public final String getpDir() {
		return pDir;
	}
	public final String getpName() {
		return pName;
	}
	public final boolean getpIsWritten() {
		return pIsWritten;
	}
	public final boolean getpIsOldFileRemoved() {
		return pIsOldFileRemoved;
	}
	public final String getpKey() {
		return pKey;
	}

	public final FWCategory getpFWCategory() {
		return pFWCategory;
	}

	public final String getpDateStr() {
		return pDateStr;
	}

}
