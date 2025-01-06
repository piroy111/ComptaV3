package step01_objects_from_conf_files.income;

public class BKIncome {

	protected BKIncome(String _sName) {
		pName = _sName;
	}
	
	/*
	 * Data
	 */
	private String pName;
	private String pBKIncomeGroup;
	private BKIncomeFY pBKIncomeFY;
	
	/**
	 * Classic toString
	 */
	public String toString() {
		return pName;
	}
	
	/*
	 * Getters & Setters
	 */
	public final String getpName() {
		return pName;
	}
	public final BKIncomeFY getpBKIncomeFY() {
		return pBKIncomeFY;
	}
	public final void setpBKIncomeFY(BKIncomeFY pBKIncomeFY) {
		this.pBKIncomeFY = pBKIncomeFY;
	}
	public final String getpBKIncomeGroup() {
		return pBKIncomeGroup;
	}
	public final void setpBKIncomeGroup(String _sPBKIncomeGroup) {
		pBKIncomeGroup = _sPBKIncomeGroup;
	}
	
}
