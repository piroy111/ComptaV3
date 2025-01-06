package step09_fiscal_year_end.step03_balancesheet;

import java.util.TreeMap;

import step09_fiscal_year_end.FYManager;
import step09_fiscal_year_end.step03_balancesheet.assets.FYEntityAssetGroup;
import step09_fiscal_year_end.step03_balancesheet.assets.FYEntityAssetGroupManager;
import step09_fiscal_year_end.step03_balancesheet.liabilities.FYLiabilitiesGroup;
import step09_fiscal_year_end.step03_balancesheet.liabilities.FYLiabilitiesGroupManager;

public class FYBalanceSheetManager {

	public FYBalanceSheetManager(FYManager _sFYManager) {
		pFYManager = _sFYManager;
		/*
		 * 
		 */
		pFYEntityAssetGroupManager = new FYEntityAssetGroupManager(this);
		pFYLiabilitiesGroupManager = new FYLiabilitiesGroupManager(this);
	}
	
	/*
	 * Data
	 */
	private FYManager pFYManager;
	private FYEntityAssetGroupManager pFYEntityAssetGroupManager;
	private FYLiabilitiesGroupManager pFYLiabilitiesGroupManager;

	/**
	 * Load assets and liabilities into grouping objects, the way we want to group them in the final report<br>
	 * Compute the NAV and the holdings<br>
	 */
	public final void compute() {
		pFYEntityAssetGroupManager.run();
		pFYLiabilitiesGroupManager.run();
	}
	
	
	/*
	 * Getters & Setters
	 */
	public final FYManager getpFYManager() {
		return pFYManager;
	}
	public final TreeMap<String, FYEntityAssetGroup> getpTreeMapNameToFYEntityAssetGroup() {
		return pFYEntityAssetGroupManager.getpTreeMapNameToFYEntityAssetGroup();
	}
	public final FYEntityAssetGroupManager getpFYEntityAssetGroupManager() {
		return pFYEntityAssetGroupManager;
	}
	public final FYLiabilitiesGroupManager getpFYLiabilitiesGroupManager() {
		return pFYLiabilitiesGroupManager;
	}
	public final TreeMap<String, FYLiabilitiesGroup> getpTreeMapNameToFYLiabilitiesGroup() {
		return pFYLiabilitiesGroupManager.getpTreeMapNameToFYLiabilitiesGroup();
	}
	
}
