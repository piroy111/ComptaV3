package step09_fiscal_year_end.step02_income.objects;

import java.util.TreeMap;

import step01_objects_from_conf_files.income.BKIncome;
import step09_fiscal_year_end.step02_income.FYIncomeManager;

public class FYIncomeGroupManager {

	public FYIncomeGroupManager(FYIncomeManager _sFYIncomeManager) {
		pFYIncomeManager = _sFYIncomeManager;
		/*
		 * 
		 */
		pTreeMapNameToFYIncomeFYGroup = new TreeMap<>();
	}

	/*
	 * Data
	 */
	private FYIncomeManager pFYIncomeManager;
	private TreeMap<String, FYIncomeFYGroup> pTreeMapNameToFYIncomeFYGroup;

	/**
	 * 
	 * @param _sNAVFYPrevious
	 * @param _sNAVFYCurrent
	 */
	public final void declareNAVForBKIncome(BKIncome _sBKIncome, double _sNAVFYPastPrevious, double _sNAVFYPrevious, double _sNAVFYCurrent) {
		/*
		 * Names
		 */
		String lNameFYIncome = _sBKIncome.getpName();
		String lNameFYIncomeFY = _sBKIncome.getpBKIncomeFY().getpName();
		String lNameFYIncomeFYGroup = _sBKIncome.getpBKIncomeFY().getpBKIncomeFYGroup().getpName();
		/*
		 * 
		 */
		FYIncomeFYGroup lFYIncomeFYGroup = getpOrCreateFYIncomeFYGroup(lNameFYIncomeFYGroup);
		lFYIncomeFYGroup.setpBKIncomeFYGroup(_sBKIncome.getpBKIncomeFY().getpBKIncomeFYGroup());
		lFYIncomeFYGroup.addToNAV(_sNAVFYCurrent, _sNAVFYPrevious, _sNAVFYPastPrevious);
		/*
		 * 
		 */
		FYIncomeFY lFYIncomeFY = lFYIncomeFYGroup.getpOrCreateFYIncomeFY(lNameFYIncomeFY);
		lFYIncomeFY.setpBKIncomeFY(_sBKIncome.getpBKIncomeFY());
		lFYIncomeFY.addToNAV(_sNAVFYCurrent, _sNAVFYPrevious, _sNAVFYPastPrevious);
		/*
		 * 
		 */
		FYIncome lFYIncome = lFYIncomeFY.getpOrCreateFYIncome(lNameFYIncome);
		lFYIncome.setpBKIncome(_sBKIncome);
		lFYIncome.addToNAV(_sNAVFYCurrent, _sNAVFYPrevious, _sNAVFYPastPrevious);
	}
	

	/**
	 * 
	 * @param _sName
	 * @return
	 */
	private final FYIncomeFYGroup getpOrCreateFYIncomeFYGroup(String _sName) {
		FYIncomeFYGroup lFYIncomeGroup = pTreeMapNameToFYIncomeFYGroup.get(_sName);
		if (lFYIncomeGroup == null) {
			lFYIncomeGroup = new FYIncomeFYGroup(_sName, this);
			pTreeMapNameToFYIncomeFYGroup.put(_sName, lFYIncomeGroup);
		}
		return lFYIncomeGroup;
	}

	/*
	 * Getters & Setters
	 */
	public final FYIncomeManager getpFYIncomeManager() {
		return pFYIncomeManager;
	}
	public final TreeMap<String, FYIncomeFYGroup> getpTreeMapNameToFYIncomeFYGroup() {
		return pTreeMapNameToFYIncomeFYGroup;
	}


	
	
	
	
	
	
	
	
	
	
}
