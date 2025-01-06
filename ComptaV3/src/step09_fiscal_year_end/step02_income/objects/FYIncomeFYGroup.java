package step09_fiscal_year_end.step02_income.objects;

import java.util.TreeMap;

import step01_objects_from_conf_files.income.BKIncomeFYGroup;

public class FYIncomeFYGroup extends FYIncomeAbstract {

	public FYIncomeFYGroup(String _sName, FYIncomeGroupManager _sFYIncomeGroupManager) {
		super(_sName);
		pFYIncomeGroupManager = _sFYIncomeGroupManager;
		/*
		 * 
		 */
		pTreeMapNameToFYIncomeFY = new TreeMap<>();
	}

	/*
	 * Data
	 */
	private BKIncomeFYGroup pBKIncomeFYGroup;
	private FYIncomeGroupManager pFYIncomeGroupManager;
	private TreeMap<String, FYIncomeFY> pTreeMapNameToFYIncomeFY;

	/**
	 * 
	 * @param _sName
	 * @return
	 */
	protected final FYIncomeFY getpOrCreateFYIncomeFY(String _sName) {
		FYIncomeFY lFYIncome = pTreeMapNameToFYIncomeFY.get(_sName);
		if (lFYIncome == null) {
			lFYIncome = new FYIncomeFY(_sName, this);
			pTreeMapNameToFYIncomeFY.put(_sName, lFYIncome);
		}
		return lFYIncome;
	}

	/*
	 * Getters & Setters
	 */
	public final BKIncomeFYGroup getpBKIncomeFYGroup() {
		return pBKIncomeFYGroup;
	}
	public final void setpBKIncomeFYGroup(BKIncomeFYGroup _sPBKIncomeFYGroup) {
		pBKIncomeFYGroup = _sPBKIncomeFYGroup;
	}
	public final FYIncomeGroupManager getpFYIncomeGroupManager() {
		return pFYIncomeGroupManager;
	}
	public final TreeMap<String, FYIncomeFY> getpTreeMapNameToFYIncomeFY() {
		return pTreeMapNameToFYIncomeFY;
	}
	
}
