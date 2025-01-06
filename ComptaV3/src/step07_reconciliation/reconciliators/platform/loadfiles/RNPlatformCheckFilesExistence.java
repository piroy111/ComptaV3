package step07_reconciliation.reconciliators.platform.loadfiles;

import basicmethods.BasicDir;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;

public class RNPlatformCheckFilesExistence {

	/**
	 * 
	 */
	public final void run() {
		String lErrorMsg = "";
		/*
		 * Check balances
		 */
		String lDirBalance = BKStaticDir.getRECONCILIATION_PLATFORM_BALANCES();
		String lSuffixBalance = BKStaticNameFile.getSUFFIX_RECONCILIATION_PLATFORM_BALANCE();
		lErrorMsg += getpErrorMsg(lDirBalance, lSuffixBalance);
		/*
		 * Check transactions
		 */
		String lDirTransactions = BKStaticDir.getRECONCILIATION_PLATFORM_TRANSACTIONS();
		String lSuffixTransactions = BKStaticNameFile.getSUFFIX_RECONCILIATION_PLATFORM_TRANSACTIONS();
		lErrorMsg += getpErrorMsg(lDirTransactions, lSuffixTransactions);
		/*
		 * Publish error
		 */
		if (!lErrorMsg.equals("")) {
			lErrorMsg = "Some necessary files are missing! Those files should have been dropped by the internet platform automatically. Check with IT."
					+ lErrorMsg;
			BKCom.error(lErrorMsg);
		}
	}

	/**
	 * 
	 * @param _sDir
	 * @param _sNameFile
	 * @return
	 */
	private String getpErrorMsg(String _sDir, String _sSuffix) {
		BasicDir lBasicDir = new BasicDir(_sDir, _sSuffix);
		int lDateWished = BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS();
		if (lBasicDir.getmListDate().contains(lDateWished)) {
			return "";
		} else {
			return "\nMissing file= '" + _sDir + lDateWished + _sSuffix + "'";
		}
	}
	
	
}
