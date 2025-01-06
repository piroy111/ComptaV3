package step09_fiscal_year_end.step02_income.objects;

import step01_objects_from_conf_files.income.BKIncome;

public class FYIncome extends FYIncomeAbstract {

	protected FYIncome(String _sName, FYIncomeFY _sFYIncomeFY) {
		super(_sName);
		pFYIncomeFY = _sFYIncomeFY;
	}
	
	/*
	 * Data
	 */
	private FYIncomeFY pFYIncomeFY;
	private BKIncome pBKIncome;

	/*
	 * Getters & Setters
	 */
	public final FYIncomeFY getpFYIncomeFY() {
		return pFYIncomeFY;
	}
	public final BKIncome getpBKIncome() {
		return pBKIncome;
	}
	public final void setpBKIncome(BKIncome _sPBKIncome) {
		pBKIncome = _sPBKIncome;
	}


}
