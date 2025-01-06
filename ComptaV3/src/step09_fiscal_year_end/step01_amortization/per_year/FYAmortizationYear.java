package step09_fiscal_year_end.step01_amortization.per_year;

public class FYAmortizationYear {

	public FYAmortizationYear(int _sYear, FYAmortizationYearManager _sFYAmortizationYearManager) {
		pYear = _sYear;
		pFYAmortizationYearManager = _sFYAmortizationYearManager;
	}
	
	/*
	 * Data
	 */
	private int pYear;
	private FYAmortizationYearManager pFYAmortizationYearManager;
	private double pSumIntangibleAssetsForTheYear;
	private double pCarryingIntangibleAssets;
	private double pSumAmortization;
	private double pAdjustmentToFYIncome;
	
	/**
	 * Add a new amount of amortization for the year pYear
	 * @param _sAmountAmortization
	 */
	public final void addNewAmortization(double _sAmountAmortization) {
		pSumAmortization += _sAmountAmortization;
	}
	
	/**
	 * 
	 * @param _sAmountIA
	 */
	public final void addNewIntangibleAssetForTheYear(double _sAmountIA) {
		pSumIntangibleAssetsForTheYear += _sAmountIA;
	}
	
	public String toString() {
		return "Year= " + pYear
				+ "; pSumIntangibleAssetsForTheYear= " + pSumIntangibleAssetsForTheYear
				+ "; pSumAmortization= " + pSumAmortization
				+ "; pCarryingIntangibleAssets= " + pCarryingIntangibleAssets
				+ "; pAdjustmentToFYIncome= " + pAdjustmentToFYIncome;
	}
	
	/*
	 * Getters & Setters
	 */
	public final int getpYear() {
		return pYear;
	}
	public final FYAmortizationYearManager getpFYAmortizationYearManager() {
		return pFYAmortizationYearManager;
	}
	public final double getpSumIntangibleAssetsForTheYear() {
		return pSumIntangibleAssetsForTheYear;
	}
	public final void setpSumIntangibleAssetsForTheYear(double _sPSumIntangibleAssetsForTheYear) {
		pSumIntangibleAssetsForTheYear = _sPSumIntangibleAssetsForTheYear;
	}
	public final double getpCarryingIntangibleAssets() {
		return pCarryingIntangibleAssets;
	}
	public final void setpCarryingIntangibleAssets(double _sPCarryingIntangibleAssets) {
		pCarryingIntangibleAssets = _sPCarryingIntangibleAssets;
	}
	public final double getpSumAmortization() {
		return pSumAmortization;
	}
	public final void setpSumAmortization(double _sPSumAmortization) {
		pSumAmortization = _sPSumAmortization;
	}
	public final double getpAdjustmentToFYIncome() {
		return pAdjustmentToFYIncome;
	}
	public final void setpAdjustmentToFYIncome(double _sPAdjustmentToFYIncome) {
		pAdjustmentToFYIncome = _sPAdjustmentToFYIncome;
	}

}
