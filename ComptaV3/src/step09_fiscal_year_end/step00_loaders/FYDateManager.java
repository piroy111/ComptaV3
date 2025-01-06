package step09_fiscal_year_end.step00_loaders;

import basicmethods.BasicDateInt;
import basicmethods.BasicPrintMsg;
import step09_fiscal_year_end.FYManager;

public class FYDateManager {

	public FYDateManager(FYManager _sFYManager) {
		pFYManager = _sFYManager;
	}
	
	/*
	 * Data
	 */
	private FYManager pFYManager;
	private int pYearFYPrevious;
	private int pYearFYCurrent;
	private int pDateFYPrevious;
	private int pDateFYCurrent;
	private int pDateFYPastPrevious;
	
	public final void run() {
		BasicPrintMsg.displayTitle(this, "Find dates for FY");
		/*
		 * Find the year freeze (FY that we will not touch anymore)
		 */
		int lYearFreeze = BasicDateInt.getmYear(pFYManager.getpFYPreviousFYIncomeManager().getpTreeMapFYDateToFYIncome().lastKey());
		BasicPrintMsg.display(this, "Last fiscal year report validated = " + lYearFreeze);
		BasicPrintMsg.display(this, "Therefore the date freeze for FY reports is " + lYearFreeze);
		/*
		 * Set dates
		 */
		pYearFYPrevious = lYearFreeze;
		pYearFYCurrent = pYearFYPrevious +1;
		pDateFYPrevious = BasicDateInt.getmDateInt(pYearFYPrevious, 3, 31);
		pDateFYCurrent = BasicDateInt.getmDateInt(pYearFYCurrent, 3, 31);
		pDateFYPastPrevious  = BasicDateInt.getmDateInt(pYearFYPrevious - 1, 3, 31);
		/*
		 * Communication
		 */
		BasicPrintMsg.display(this, "pDateFYPastPrevious= " + pDateFYPastPrevious);
		BasicPrintMsg.display(this, "pDateFYPrevious= " + pDateFYPrevious);
		BasicPrintMsg.display(this, "pDateFYCurrent= " + pDateFYCurrent);		
	}

	/*
	 * Getters & Setters
	 */
	public final FYManager getpFYManager() {
		return pFYManager;
	}
	public final int getpYearFYPrevious() {
		return pYearFYPrevious;
	}
	public final int getpYearFYCurrent() {
		return pYearFYCurrent;
	}
	public final int getpDateFYPrevious() {
		return pDateFYPrevious;
	}
	public final int getpDateFYCurrent() {
		return pDateFYCurrent;
	}
	public final int getpDateFYPastPrevious() {
		return pDateFYPastPrevious;
	}
	
}
