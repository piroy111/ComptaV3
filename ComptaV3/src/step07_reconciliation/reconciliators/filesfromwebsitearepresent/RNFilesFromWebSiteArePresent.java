package step07_reconciliation.reconciliators.filesfromwebsitearepresent;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import basicmethods.BasicFichiersNioRaw;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step07_reconciliation.abstracts.BKReconciliatorAbstract;
import step07_reconciliation.abstracts.BKReconciliatorManager;

public class RNFilesFromWebSiteArePresent extends BKReconciliatorAbstract {

	public RNFilesFromWebSiteArePresent(BKReconciliatorManager _sBKReconciliatorManager) {
		super(_sBKReconciliatorManager);
	}

	/*
	 * Data
	 */
	private boolean pIsCheckDone;
	
	@Override public String getpDetailsOfChecksPerformed() {
		return "3 files from website are present for " + BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS();
	}

	@Override public void computeIsPassTest(List<Integer> _sListDateToReconcile) {
		if (!pIsCheckDone) {
			checkFileIsHere(BKStaticDir.getPLATFORM_TRANSACTIONS(), BKStaticNameFile.getSUFFIX_PURCHASES_FROM_CLIENTS());
			checkFileIsHere(BKStaticDir.getRECONCILIATION_PLATFORM_BALANCES(), BKStaticNameFile.getSUFFIX_RECONCILIATION_PLATFORM_BALANCE());
			checkFileIsHere(BKStaticDir.getRECONCILIATION_PLATFORM_TRANSACTIONS(), BKStaticNameFile.getSUFFIX_RECONCILIATION_PLATFORM_TRANSACTIONS());
			pIsCheckDone = true;
		}		
	}

	/**
	 * 
	 * @param _sDir
	 * @param _sSuffixFile
	 */
	private void checkFileIsHere(String _sDir, String _sSuffixFile) {
		String lNameFile = BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS() + _sSuffixFile;
		Path lPath = Paths.get(_sDir + lNameFile);
		if (!BasicFichiersNioRaw.getIsAlreadyExist(lPath)) {
			BKCom.error("The file from the website is missing. Please check with IT, so that he upload the file"
					+ "\nMissing file= '" + _sDir + lNameFile + "'");
		}
	}
	
	
}
