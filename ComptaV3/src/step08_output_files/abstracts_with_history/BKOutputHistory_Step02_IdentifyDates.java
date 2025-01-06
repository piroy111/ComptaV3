package step08_output_files.abstracts_with_history;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicDateInt;
import basicmethods.BasicDir;
import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticConst;

class BKOutputHistory_Step02_IdentifyDates {

	protected BKOutputHistory_Step02_IdentifyDates(BKOutputHistoryAbstract _sBKOutputHistoryAbstract) {
		pBKOutputHistoryAbstract = _sBKOutputHistoryAbstract;
	}

	/*
	 * Data
	 */
	private BKOutputHistoryAbstract pBKOutputHistoryAbstract;

	/**
	 * 
	 */
	public final void identifyDatesToDo() {
		BasicPrintMsg.displayTitle(this, "Identify the dates to do");
		BasicDir pBasicDir = new BasicDir(pBKOutputHistoryAbstract.getpDirDifferential(), pBKOutputHistoryAbstract.getpSuffixFile());
		BasicPrintMsg.display(this, "List of report already there= " + pBasicDir.getmListDate());
		List<Integer> lListDateToDo = new ArrayList<>();
		/*
		 * Date stop = last of the month of COMPTA
		 */
		int lDateToDoStop = BasicDateInt.getmEndOfMonth(BasicDateInt.getmPlusMonth(BasicDateInt.getmToday(), -1));
		/*
		 * Date start = date of last report missing
		 */
		int lDateLastFile = pBasicDir.getmLastDate();
		int lDateLastReport = Math.max(lDateLastFile, pBKOutputHistoryAbstract.getpBKOutputHistoryManager().getpDateFYFrozenToDownload());
		int lDateToDoStart = BasicDateInt.getmEndOfMonth(BasicDateInt.getmPlusMonth(lDateLastReport, 1));
		lDateToDoStart = Math.min(lDateToDoStart, lDateToDoStop);  // case the report of the month is already there
		/*
		 * Check if we need to do the last report
		 */
		boolean lIsDoReports = false;
		if (!pBasicDir.getmListDate().contains(lDateToDoStop)) {
			BasicPrintMsg.display(this, "The last report is missing; Date missing= " + lDateToDoStop);
			lIsDoReports = true;
		} else {
			if (BKStaticConst.getDATE_FREEZE_COMPTA_MIN() >= BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS()) {
				BasicPrintMsg.display(this, "The last report is present but the conf file of dates frozen ask me to redo it");
				BasicPrintMsg.display(this, "The conf file is asking because DATE_FREEZE_COMPTA_MIN >= DATE_STOP_COUNTING_IN_TRANSACTIONS");
				BasicPrintMsg.display(this, "DATE_FREEZE_COMPTA_MIN= " + BKStaticConst.getDATE_FREEZE_COMPTA_MIN());
				BasicPrintMsg.display(this, "DATE_STOP_COUNTING_IN_TRANSACTIONS= " + BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS());
				lIsDoReports = true;
			}
		}
		if (lIsDoReports) {
			for (int lDate = lDateToDoStart; lDate <= lDateToDoStop; lDate = BasicDateInt.getmEndOfMonth(BasicDateInt.getmPlusMonth(lDate, 1))) {
				lListDateToDo.add(lDate);
			}
		}
		BasicPrintMsg.display(this, "List of dates to do= " + lListDateToDo);
		/*
		 * Pass to parent
		 */
		pBKOutputHistoryAbstract.setpBasicDir(pBasicDir);
		pBKOutputHistoryAbstract.setpListDateToDo(lListDateToDo);
	}















}
