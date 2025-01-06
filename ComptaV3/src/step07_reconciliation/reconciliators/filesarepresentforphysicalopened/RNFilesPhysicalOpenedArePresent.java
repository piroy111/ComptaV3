package step07_reconciliation.reconciliators.filesarepresentforphysicalopened;

import java.util.List;

import basicmethods.BasicFichiersNio;
import basicmethods.BasicPrintMsg;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticDir;
import step07_reconciliation.abstracts.BKReconciliatorAbstract;
import step07_reconciliation.abstracts.BKReconciliatorManager;

public class RNFilesPhysicalOpenedArePresent extends BKReconciliatorAbstract {

	public RNFilesPhysicalOpenedArePresent(BKReconciliatorManager _sBKReconciliatorManager) {
		super(_sBKReconciliatorManager);
	}

	/*
	 * Data
	 */
	private boolean pIsCheckDone;
	
	@Override public String getpDetailsOfChecksPerformed() {
		return "files physical opened are all present for " + BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS();
	}

	@Override public void computeIsPassTest(List<Integer> _sListDateToReconcile) {
		if (!pIsCheckDone) {
			/*
			 * Date which must be present and which we will check for each physical sub-folder
			 */
			String lDateToCheckStr = (BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS() + "").substring(0, 6);
			/*
			 * Get the names of the files
			 */
			String lDirRoot = BKStaticDir.getLOAD_TRANSACTIONS_PHYSICAL();
			List<String> lListSubDir = BasicFichiersNio.getListFilesAndDirectoriesInDirectory(lDirRoot);
			for (String lSubDir : lListSubDir) {
				BasicPrintMsg.display(this, "Check sub dir '" + lSubDir + "'");
				String lDir = lDirRoot + lSubDir + "/" + BKStaticDir.getPHYSICAL_SUBFOLDER_TRANSACTIONS();
				List<String> lListNameFile = BasicFichiersNio.getListFilesAndDirectoriesInDirectory(lDir);
				/*
				 * Check if the date of COMPTA is present
				 */
				boolean lIsFound = false;
				for (String lNameFile : lListNameFile) {
					if (lNameFile.startsWith(lDateToCheckStr)) {
						lIsFound = true;
						break;
					}
				}
				/*
				 * Error message if the date of COMPTA is not present
				 */
				if (!lIsFound) {
					BKCom.error("The file of physical transaction is missing for the date " + lDateToCheckStr
							+ "\nMissing date= " + lDateToCheckStr
							+ "\nFolder with missing date= '" + lDir + "'");
				}
			}
			pIsCheckDone = true;
		}		
	}

	
	
}
