package step07_reconciliation.reconciliators.filesarepresentforbrokers;

import java.util.List;

import basicmethods.BasicDateInt;
import basicmethods.BasicDir;
import basicmethods.BasicFichiersNio;
import basicmethods.BasicString;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step07_reconciliation.abstracts.BKReconciliatorAbstract;
import step07_reconciliation.abstracts.BKReconciliatorManager;

public class RNFilesFromOndaAndIbArePresent extends BKReconciliatorAbstract {

	public RNFilesFromOndaAndIbArePresent(BKReconciliatorManager _sBKReconciliatorManager) {
		super(_sBKReconciliatorManager);
	}

	/*
	 * Data
	 */
	
	@Override public String getpDetailsOfChecksPerformed() {
		return "Report of OANDA and IB are present for " + BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS();
	}

	@Override public void computeIsPassTest(List<Integer> _sListDateToReconcile) {
		int lDate = BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS();
		String lErrorMsg = "";
		/*
		 * Check OANDA
		 */
		String lDirOANDA = BKStaticDir.getLOAD_TRANSACTIONS_PHYSICAL()
				+ BKStaticDir.getSUB_DIR_OANDA()
				+ BKStaticDir.getSUB_DIR_OANDA_REPORTS();
		BasicDir lBasicDir = new BasicDir(lDirOANDA, BKStaticNameFile.getSUFFIX_OANDA());
		if (lBasicDir.getmListDate().size() == 0 || lBasicDir.getmLastDate() < lDate) {
			lErrorMsg += "\n" 
					+ "\nThe report OANDA is missing for the date " + lDate
					+ "\nPlease put it in the folder '" + lDirOANDA + "'";
		}
		/*
		 * Check IB
		 */
		String lDirIB = BKStaticDir.getLOAD_TRANSACTIONS_PHYSICAL()
				+ BKStaticDir.getSUB_DIR_IB()
				+ BKStaticDir.getSUB_DIR_IB_REPORTS();
		List<String> lListNameFiles = BasicFichiersNio.getListFilesInDirectory(lDirIB);
		int lDateMax = -1;
		for (String lNameFileReportIB : lListNameFiles) {
			if (!lNameFileReportIB.endsWith(BKStaticNameFile.getSUFFIX_IB())) {
				lErrorMsg += "\n"
						+ "\nA report IB does not have the correct name"
						+ "\nFile name in error= '" + lNameFileReportIB + "'"
						+ "\nFolder where the file is= '" + lDirIB + "'";
			} else {
				int lDate1 = BasicString.getInt(lNameFileReportIB.substring(0, 8));
				int lDate2 = BasicString.getInt(lNameFileReportIB.substring(9, 17));
				lDateMax = Math.max(lDateMax, lDate1);
				lDateMax = Math.max(lDateMax, lDate2);
			}
		}
		if (lDateMax < lDate && (lDateMax == -1 || BasicDateInt.getmNumberBusinessDays(lDateMax, lDate) > 2)) {
			lErrorMsg += "\n" 
					+ "\nThe report Interactive Brokers is missing for the date " + lDate
					+ "\nPlease put it in the folder '" + lDirIB + "'";
		}
		/*
		 * Error message
		 */
		if (!lErrorMsg.equals("")) {
			lErrorMsg = "Some broker files are missing" + lErrorMsg;
			BKCom.error(lErrorMsg);
		}
	}

	
	
}
