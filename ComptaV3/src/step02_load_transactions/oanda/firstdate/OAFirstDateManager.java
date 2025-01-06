package step02_load_transactions.oanda.firstdate;

import java.util.List;

import basicmethods.BasicDateInt;
import basicmethods.BasicDir;
import basicmethods.BasicFichiers;
import basicmethods.BasicFichiersNio;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticNameFile;
import step02_load_transactions.oanda.OAManager;

public class OAFirstDateManager {

	public OAFirstDateManager(OAManager _sOAManager) {
		pOAManager = _sOAManager;
	}

	/*
	 * Data
	 */
	private OAManager pOAManager;
	private int pDateStart;

	/**
	 * Determine the first date from which we can read and download the OANDA reports<br>
	 * This is the latest date of the transaction files present in the physical transactions folder<br>
	 * Check also that the suffix are well written<br>
	 */
	public final void run() {
		/*
		 * Check date
		 */
		String lDir = pOAManager.getpDirOutput();
		String lSuffix = pOAManager.getpSuffixOutput();
		BasicDir lBasicDir = new BasicDir(lDir, lSuffix);
		if (lBasicDir.getmTreeMapDateToBasicFile().size() > 0) {
			pDateStart = lBasicDir.getmTreeMapDateToBasicFile().lastKey();
			pDateStart = BasicDateInt.getmPlusDay(pDateStart, 1);
		}
		/*
		 * Check files names
		 */
		BasicFichiers.checkAllFilesBeginWithDate(pOAManager.getpDirInput());
		List<String> lListNameFiles = BasicFichiersNio.getListFilesAndDirectoriesInDirectory(pOAManager.getpDirInput());
		for (String lNameFile : lListNameFiles) {
			if (!lNameFile.endsWith(BKStaticNameFile.getSUFFIX_OANDA())
					&& !lNameFile.endsWith(BKStaticNameFile.getSUFFIX_OANDA_CRYPTOS())) {
				String lErrorMsg = "The name of the file does not finish with one of the expected suffix"
						+ "\nExpected suffix= '" + BKStaticNameFile.getSUFFIX_OANDA() + "' or '" + BKStaticNameFile.getSUFFIX_OANDA_CRYPTOS() + "'"
						+ "\nFile= '" + pOAManager.getpDirInput() + lNameFile + "'";
				BKCom.error(lErrorMsg);
			}
		}
		
	}

	/*
	 * Getters & Setters
	 */
	public final OAManager getpOAManager() {
		return pOAManager;
	}
	public final int getpDateStart() {
		return pDateStart;
	}
}
