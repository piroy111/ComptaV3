package step09_fiscal_year_end.step02_income.objects;

import java.util.TreeMap;

import step01_objects_from_conf_files.income.BKIncomeFY;

public class FYIncomeFY extends FYIncomeAbstract {

	protected FYIncomeFY(String _sName, FYIncomeFYGroup _sFYIncomeFYGroup) {
		super(_sName);
		pFYIncomeFYGroup = _sFYIncomeFYGroup;
		/*
		 * 
		 */
		pTreeMapNameToFYIncome = new TreeMap<>();
	}
	
	/*
	 * Data
	 */
	private FYIncomeFYGroup pFYIncomeFYGroup;
	private BKIncomeFY pBKIncomeFY;
	private TreeMap<String, FYIncome> pTreeMapNameToFYIncome;

	/**
	 * 
	 * @param _sName
	 * @return
	 */
	protected final FYIncome getpOrCreateFYIncome(String _sName) {
		FYIncome lFYIncome = pTreeMapNameToFYIncome.get(_sName);
		if (lFYIncome == null) {
			lFYIncome = new FYIncome(_sName, this);
			pTreeMapNameToFYIncome.put(_sName, lFYIncome);
		}
		return lFYIncome;
	}
	
	
	/*
	 * Getters & Setters
	 */
	public final FYIncomeFYGroup getpFYIncomeFYGroup() {
		return pFYIncomeFYGroup;
	}
	public final BKIncomeFY getpBKIncomeFY() {
		return pBKIncomeFY;
	}
	public final void setpBKIncomeFY(BKIncomeFY _sPBKIncomeFY) {
		pBKIncomeFY = _sPBKIncomeFY;
	}
	public final void setpFYIncomeFYGroup(FYIncomeFYGroup _sPFYIncomeFYGroup) {
		pFYIncomeFYGroup = _sPFYIncomeFYGroup;
	}
	public final TreeMap<String, FYIncome> getpTreeMapNameToFYIncome() {
		return pTreeMapNameToFYIncome;
	}
	
}
