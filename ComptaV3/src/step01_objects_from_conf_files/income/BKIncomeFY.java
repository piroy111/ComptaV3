package step01_objects_from_conf_files.income;

import java.util.ArrayList;
import java.util.List;

public class BKIncomeFY {

	protected BKIncomeFY(String _sName, BKIncomeFYGroup _sBKIncomeFYGroup) {
		pName = _sName;
		pBKIncomeFYGroup = _sBKIncomeFYGroup;
		/*
		 * Data
		 */
		pListBKIncome = new ArrayList<>();
	}
	
	/*
	 * Data
	 */
	private BKIncomeFYGroup pBKIncomeFYGroup;
	private String pName;
	private List<BKIncome> pListBKIncome;
	
	/**
	 * 
	 * @param _sBKIncome
	 */
	protected final void declareNewBKIncome(BKIncome _sBKIncome) {
		if (!pListBKIncome.contains(_sBKIncome)) {
			pListBKIncome.add(_sBKIncome);
		}
	}

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
	public final List<BKIncome> getpListBKIncome() {
		return pListBKIncome;
	}
	public final BKIncomeFYGroup getpBKIncomeFYGroup() {
		return pBKIncomeFYGroup;
	}
	
	
}
