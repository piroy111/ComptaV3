package step02_load_transactions.interactivebrokers.createfilestransactions.writefile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticConst;
import step02_load_transactions.interactivebrokers.createfilestransactions.IBManager;
import step02_load_transactions.interactivebrokers.createfilestransactions.transactions.IBTransaction;

public class IBWriteFileManager {

	public IBWriteFileManager(IBManager _sIBManager) {
		pIBManager = _sIBManager;
	}

	/*
	 * Data
	 */
	private IBManager pIBManager;

	/**
	 * 
	 */
	public final void write() {
		BasicPrintMsg.displayTitle(this, "Write 1 file transaction");
		int lDate = pIBManager.getpIBTransactionManager().getpDateStop();
		if (lDate > 0) {
			List<String> lListLineToWrite = new ArrayList<String>();
			List<IBTransaction> lListIBTransaction = pIBManager.getpIBTransactionManager().getpListIBTransaction();
			Collections.sort(lListIBTransaction);
			for (IBTransaction lIBTransaction : lListIBTransaction) {
				String lLine = lIBTransaction.getpDate()
						+ "," + lIBTransaction.getpBKAsset().getpName()
						+ "," + lIBTransaction.getpComment()						
						+ "," + (lIBTransaction.getpAmount() * lIBTransaction.getpMultiplier())
						+ "," + lIBTransaction.getpPrice()
						+ "," + lIBTransaction.getpBKAccount().getpEmail()
						+ "," + lIBTransaction.getpBKIncome();
				lListLineToWrite.add(lLine);
			}
			/*
			 * Write file
			 */
			String lHeader = BKStaticConst.getHEADER_FILE_TRANSACTIONS();
			String lDir = pIBManager.getpIBFindDirAndSuffix().getpDirTransactions();
			String lNameFile = lDate + pIBManager.getpIBFindDirAndSuffix().getpSuffixTransactions();
			BasicFichiers.writeFile(lDir, lNameFile, lHeader, lListLineToWrite);
			/*
			 * Communication
			 */
			BasicPrintMsg.display(this, "File written: " + lDir + lNameFile);
		}
	}

}
