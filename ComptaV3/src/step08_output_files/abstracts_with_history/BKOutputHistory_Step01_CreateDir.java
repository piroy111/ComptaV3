package step08_output_files.abstracts_with_history;

import java.nio.file.Paths;

import basicmethods.BasicFichiers;
import basicmethods.BasicFichiersNioRaw;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticDir;

class BKOutputHistory_Step01_CreateDir {

	protected BKOutputHistory_Step01_CreateDir(BKOutputHistoryAbstract _sBKOutputHistoryAbstract) {
		pBKOutputHistoryAbstract = _sBKOutputHistoryAbstract;
	}
	
	/*
	 * Data
	 */
	private BKOutputHistoryAbstract pBKOutputHistoryAbstract;
	
	/**
	 * 
	 */
	public final void initiateDirAndFileName() {
		BasicPrintMsg.displayTitle(this, "Initiate Dir");
		String pNameFromClass = pBKOutputHistoryAbstract.getClass().getSimpleName();
		if (!pNameFromClass.startsWith("BKOutputHistory_")) {
			BKCom.errorCodeLogic();
		}
		pNameFromClass = pNameFromClass.substring("BKOutputHistory_".length());
		pNameFromClass = BasicString.insertSeparatorBeforeUpperCase(pNameFromClass, "", "_");
		/*
		 * Name DIR
		 */
		String pDirDifferential = BKStaticDir.getOUTPUT_COMPTA() + pNameFromClass;
		String pDirGlobal = BKStaticDir.getOUTPUT_CLIENT() + pNameFromClass;
		String pSuffixFile = "_" + pNameFromClass + pBKOutputHistoryAbstract.getpSuffixToAdd() + ".csv";
		BasicPrintMsg.display(this, "pDir= " + pDirDifferential);
		BasicPrintMsg.display(this, "pSuffixFile= " + pSuffixFile);
		/*
		 * Create the DIR if it does not exist
		 */
		if (!BasicFichiersNioRaw.getIsAlreadyExist(Paths.get(pDirDifferential))) {
			BasicFichiers.getOrCreateDirectory(pDirDifferential);
			BasicPrintMsg.display(this, "I created the directory because it did not exist");
		} else {
			BasicPrintMsg.display(this, "The directory exists");
		}
		BasicFichiers.getOrCreateDirectory(pDirGlobal);
		/*
		 * 
		 */
		pBKOutputHistoryAbstract.setpNameFromClass(pNameFromClass);
		pBKOutputHistoryAbstract.setpDirDifferential(pDirDifferential);
		pBKOutputHistoryAbstract.setpDirGlobal(pDirGlobal);
		pBKOutputHistoryAbstract.setpSuffixFile(pSuffixFile);
	}
	
	
	
	
	
	
}
