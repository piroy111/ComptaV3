package step07_reconciliation.reconciliators.platform.objects.platformdate;

import java.util.TreeMap;

import step07_reconciliation.reconciliators.platform.RNPlatform;

public class RNPlatformDateManager {

	public RNPlatformDateManager(RNPlatform _sRNPlatform) {
		pRNPlatform = _sRNPlatform;
		/*
		 * 
		 */
		pTreeMapDateToRNPlatformDate = new TreeMap<>();
	}
	
	/*
	 * Data
	 */
	private RNPlatform pRNPlatform;
	private TreeMap<Integer, RNPlatformDate> pTreeMapDateToRNPlatformDate;
	
	/**
	 * 
	 * @param _sDate
	 * @return
	 */
	public final RNPlatformDate getpOrCreateRNPlatformDate(int _sDate) {
		RNPlatformDate lRNPlatformDate = pTreeMapDateToRNPlatformDate.get(_sDate);
		if (lRNPlatformDate == null) {
			lRNPlatformDate = new RNPlatformDate(_sDate, this);
			pTreeMapDateToRNPlatformDate.put(_sDate, lRNPlatformDate);
		}
		return lRNPlatformDate;
	}
	
	/*
	 * Getters & Setters
	 */
	public final RNPlatform getpRNPlatform() {
		return pRNPlatform;
	}
	public final TreeMap<Integer, RNPlatformDate> getpTreeMapDateToRNPlatformDate() {
		return pTreeMapDateToRNPlatformDate;
	}
	
}
