package step07_reconciliation.reconciliators.platform.bars_outside_platform.per_date;

import java.util.ArrayList;
import java.util.List;

import step01_objects_from_conf_files.asset.bar.BKBar;
import step07_reconciliation.reconciliators.platform.bars_outside_platform.RNPlatformOutsideManager;

public class RNPlatformOutsideDate {

	public RNPlatformOutsideDate(int _sDate, RNPlatformOutsideManager _sRNPlatformOutsideManager) {
		pDate = _sDate;
		pRNPlatformOutsideManager = _sRNPlatformOutsideManager;
		/*
		 * 
		 */
		pListBKBarOutsideOfPlatform = new ArrayList<>();
	}
	
	/*
	 * Data
	 */
	private int pDate;
	private RNPlatformOutsideManager pRNPlatformOutsideManager;
	private List<BKBar> pListBKBarOutsideOfPlatform;
	
	/**
	 * 
	 * @param _sBKBar
	 */
	public final void declareNewBKBarOutsideOfPlatform(BKBar _sBKBar) {
		if (!pListBKBarOutsideOfPlatform.contains(_sBKBar)) {
			pListBKBarOutsideOfPlatform.add(_sBKBar);
		}
	}
	
	
	/*
	 * Getters & Setters
	 */
	public final int getpDate() {
		return pDate;
	}
	public final RNPlatformOutsideManager getpRNPlatformOutsideManager() {
		return pRNPlatformOutsideManager;
	}
	public final List<BKBar> getpListBKBarOutsideOfPlatform() {
		return pListBKBarOutsideOfPlatform;
	}
	
	
	
	
}
