package step08_output_files.incomingfunds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import basicmethods.AMNumberTools;
import basicmethods.BasicDateInt;
import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step08_output_files.abstracts.BKOutputAbstract;
import step08_output_files.abstracts.BKOutputManager;

public class BKOutput_IncomingFundsFromClients extends BKOutputAbstract {

	public BKOutput_IncomingFundsFromClients(BKOutputManager _sBKOutputManager) {
		super(_sBKOutputManager);
	}

	/*
	 * Data
	 */
	private OPIncomingFundDateManager pOPIncomingFundDateManager;
	
	@Override public void buildFileContent() {
		pOPIncomingFundDateManager = new OPIncomingFundDateManager(this);
		/*
		 * Find the dates on which we will publish the incoming funds --> Each end of month
		 */
		List<Integer> lListDateToPublish = new ArrayList<>();
		for (int lDate : pBKPartitionManager.getpBKPartitionPerBKIncomeAndBKAccount().getpTreeMapDateToMapKeyToBKTransactionPartitionDate().keySet()) {
			if (lDate > BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS()) {
				break;
			}
			int lDateEOMonth = BasicDateInt.getmEndOfMonth(lDate);
			if (!lListDateToPublish.contains(lDateEOMonth)) {
				lListDateToPublish.add(lDateEOMonth);
			}
		}
		/*
		 * Compute + link the OPSource to the previous one, in order to be able to compute the difference
		 */
		OPIncomingFundDate lOPIncomingFundDatePrevious = null;
		for (int lDate : lListDateToPublish) {
			OPIncomingFundDate lOPIncomingFundDate = pOPIncomingFundDateManager.getpOrCreateOPIncomingFunDate(lDate);
			lOPIncomingFundDate.setpOPIncomingFundDatePrevious(lOPIncomingFundDatePrevious);
			lOPIncomingFundDate.createAndFillOPIncomingFundDate();
			lOPIncomingFundDatePrevious = lOPIncomingFundDate;
		}
		/*
		 * Header
		 */
		addNewHeader("Date");
		addNewHeader("Total incoming funds since beginning (US$)");
		addNewHeader("Incoming fund for the month (US$)");
		List<String> lListSourceStr = new ArrayList<>();
		for (BKAccount lBKAccount : BKAccountManager.getpListBKAccountExceptBunkerAndPRoy()) {
			if (!lListSourceStr.contains(lBKAccount.getpSource())) {
				lListSourceStr.add(lBKAccount.getpSource());
			}
		}
		Collections.sort(lListSourceStr);
		for (String lSourceStr : lListSourceStr) {
			addNewHeader("Incoming funds from " + lSourceStr + " (US$)");
		}
		addNewHeader("Details of incoming funds per BKAccount");
		/*
		 * Write file content
		 */
		for (int lDateEOMonth : lListDateToPublish) {
			OPIncomingFundDate lOPIncomingFundDate = pOPIncomingFundDateManager.getpOrCreateOPIncomingFunDate(lDateEOMonth);
			/*
			 * Date of the month in plain letters
			 */
			String lMonthToPrint = BasicDateInt.getmMonthName(BasicDateInt.getmMonth(lDateEOMonth));
			lMonthToPrint = lMonthToPrint.substring(0, 3);
			int lYear = BasicDateInt.getmYear(lDateEOMonth);
			int lYearToPrint = lYear - lYear / 100 * 100;
			String lLine = "01-" + lMonthToPrint + "-" + lYearToPrint;
			/*
			 * Write the total of the incoming fund and the differential
			 */
			lLine += "," + lOPIncomingFundDate.getpStockIncomingFundUSD();
			lLine += "," + lOPIncomingFundDate.getpFlowIncomingFundUSD();
			/*
			 * Write the flow of incoming funds for each source
			 */
			for (String lSourceStr : lListSourceStr) {
				lLine += "," + lOPIncomingFundDate.getpOrCreateOPSourceDate(lSourceStr).getpFlowIncomingFundUSD();
			}
			/*
			 * Write the details per account of the flow of incoming funds
			 */
			List<OPAccountDate> lListOPAccountDateToWrite = new ArrayList<>();
			for (OPAccountDate lOPAccountDate : lOPIncomingFundDate.getpMapBKAccountToAccountDate().values()) {
				if (!AMNumberTools.isNaNOrNullOrZero(lOPAccountDate.getpFlowIncomingFundUSD())) {
					lListOPAccountDateToWrite.add(lOPAccountDate);
				}
			}
			Collections.sort(lListOPAccountDateToWrite);
			for (int lIdx = 0; lIdx < lListOPAccountDateToWrite.size(); lIdx++) {
				OPAccountDate lOPAccountDate = lListOPAccountDateToWrite.get(lIdx);
				lLine += "," + lOPAccountDate.getpBKAccount().getpEmail() 
						+ "(" + lOPAccountDate.getpBKAccount().getpSource() + ")"
						+ "= " + BasicPrintMsg.afficheIntegerWithComma(lOPAccountDate.getpFlowIncomingFundUSD()).replaceAll(",", " ")
						+ "$";
			}
			/*
			 * 
			 */
			addNewLineToWrite(lLine);
		}
	}

	
	
	
	
	
	
	
	
	
	
	
}
