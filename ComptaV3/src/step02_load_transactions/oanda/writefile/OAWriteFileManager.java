package step02_load_transactions.oanda.writefile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicDateInt;
import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.income.BKIncome;
import step01_objects_from_conf_files.income.BKIncomeManager;
import step02_load_transactions.oanda.OAManager;
import step02_load_transactions.oanda.loadreports.OAReport;
import step02_load_transactions.oanda.loadreports.OAReportManager;
import step02_load_transactions.oanda.loadreports.OATransaction;

public class OAWriteFileManager {

	public OAWriteFileManager(OAManager _sOAManager) {
		pOAManager = _sOAManager;
		/*
		 * 
		 */
		pMapNameToOAFileToWrite = new HashMap<>();
	}

	/*
	 * Data
	 */
	private OAManager pOAManager;
	private Map<String, OAFileToWrite> pMapNameToOAFileToWrite;
	
	/**
	 * 
	 */
	public final void run() {
		BasicPrintMsg.displayTitle(this, "Write files OANDA of BKTransactions");
		/*
		 * thanks to the ID of OANDA, we made sure that an OATransaction cannot be duplicated in several reports
		 */
		List<OATransaction> lListOATransaction = new ArrayList<>();
		for (OAReportManager lOAReportManager : pOAManager.getpListOAReportManager()) {
			for (OAReport lOAReport : lOAReportManager.getpListOAReport()) {
				lListOATransaction.addAll(lOAReport.getpListOATransaction());
			}
		}
		Collections.sort(lListOATransaction);
		/*
		 * Write file content
		 */
		for (OATransaction lOATransaction : lListOATransaction) {
			/*
			 * Load the file on which we should write
			 */
			int lDate = lOATransaction.getpDate();
			if (lDate < pOAManager.getpOAFirstDateManager().getpDateStart()) {
				continue;
			}
			if (lDate > BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS()) {
				continue;
			}
			OAFileToWrite lOAFileToWrite = getpOrCreateOAFileToWrite(lDate);
			/*
			 * BKIncome
			 */
			String lBKIComment = "";
			if (lOATransaction.getpIsTradeOrCashFlow()) {
				lBKIComment = BKStaticConst.getBKINCOME_OANDA_TRADES();
			} else if (lOATransaction.getpComment().contains("Deposit")) {
				lBKIComment = BKStaticConst.getBKINCOME_OANDA_CASH_IN();
			}  else if (lOATransaction.getpComment().contains("Commissions")) {
				lBKIComment = BKStaticConst.getBKINCOME_OANDA_COMMISSIONS();
			} else {
				lBKIComment = BKStaticConst.getBKINCOME_OANDA_INTERESTS();
			}
			BKIncome lBKIncome = BKIncomeManager.getpAndCheckBKIncome(lBKIComment, this);
			/*
			 * Build List
			 */
			String lLineStr = lOATransaction.getpDate()
					+ "," + lOATransaction.getpBKAsset().getpName()
					+ "," + lOATransaction.getpComment()					
					+ "," + lOATransaction.getpQuantity()
					+ "," + lOATransaction.getpPrice()
					+ "," + BKAccountManager.getpBKAccountBunker().getpEmail()
					+ "," + lBKIncome;
			/*
			 * Store line to write
			 */
			lOAFileToWrite.addNewLineToWrite(lLineStr);			
		}
		/*
		 * Write file
		 */
		for (OAFileToWrite lOAFileToWrite : pMapNameToOAFileToWrite.values()) {
			lOAFileToWrite.addNewHeader(BKStaticConst.getHEADER_FILE_TRANSACTIONS());
			lOAFileToWrite.writeFile();
		}
	}

	/**
	 * 
	 * @param _sDate
	 */
	public final OAFileToWrite getpOrCreateOAFileToWrite(int _sDate) {
		String lName = BasicDateInt.getmEndOfMonth(_sDate) + pOAManager.getpSuffixOutput();
		OAFileToWrite lOAFileToWrite = pMapNameToOAFileToWrite.get(lName);
		if (lOAFileToWrite == null) {
			lOAFileToWrite = new OAFileToWrite(this, lName);
			pMapNameToOAFileToWrite.put(lName, lOAFileToWrite);
		}
		return lOAFileToWrite;
	}

	/*
	 * Getters & Setters
	 */
	public final OAManager getpOAManager() {
		return pOAManager;
	}
}
