package step09_fiscal_year_end.step01_amortization;

import basicmethods.BasicPrintMsg;
import step09_fiscal_year_end.FYManager;
import step09_fiscal_year_end.step01_amortization.intangible_asset.FYIntangibleAssetManager;
import step09_fiscal_year_end.step01_amortization.per_year.FYAmortizationYearManager;

public class FYAmortizationManager {

	public FYAmortizationManager(FYManager _sFYManager) {
		pFYManager = _sFYManager;
		/*
		 * 
		 */
		pFYIntangibleAssetManager = new FYIntangibleAssetManager(this);
		pFYAmortizationYearManager = new FYAmortizationYearManager(this);
	}
	
	/*
	 * Data
	 */
	private FYManager pFYManager;
	private FYIntangibleAssetManager pFYIntangibleAssetManager;
	private FYAmortizationYearManager pFYAmortizationYearManager;

	/**
	 * 
	 */
	public final void run() {
		BasicPrintMsg.displayTitle(this, "FY - Amortization");
		pFYIntangibleAssetManager.loadConfFile();
		pFYAmortizationYearManager.compute();
	}
	
	/*
	 * Getters & Setters
	 */
	public final FYManager getpFYManager() {
		return pFYManager;
	}
	public final FYIntangibleAssetManager getpFYIntangibleAssetManager() {
		return pFYIntangibleAssetManager;
	}

	public final FYAmortizationYearManager getpFYAmortizationYearManager() {
		return pFYAmortizationYearManager;
	}
	
}
