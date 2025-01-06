package step09_fiscal_year_end.step01_amortization.intangible_asset;

public class FYIntangibleAssetYear {

	public FYIntangibleAssetYear(int _sYearDebit, FYIntangibleAsset _sFYIntangibleAsset) {
		pYearDebit = _sYearDebit;
		pFYIntangibleAsset = _sFYIntangibleAsset;
	}
	
	/*
	 * Data
	 */
	private FYIntangibleAsset pFYIntangibleAsset;
	private double pAmountUSD;
	private int pYearDebit;
	private int pNumberOfYearsSpreading;
	
	/*
	 * Getters & Setters
	 */
	public final FYIntangibleAsset getpFYIntangibleAsset() {
		return pFYIntangibleAsset;
	}
	public final double getpAmountUSD() {
		return pAmountUSD;
	}
	public final String getpName() {
		return pFYIntangibleAsset.getpName();
	}
	public final int getpNumberOfYearsSpreading() {
		return pNumberOfYearsSpreading;
	}
	public final void setpAmountUSD(double _sPAmountUSD) {
		pAmountUSD = _sPAmountUSD;
	}
	public final void setpNumberOfYearsSpreading(int _sPNumberOfYearsSpreading) {
		pNumberOfYearsSpreading = _sPNumberOfYearsSpreading;
	}
	public final int getpYearDebit() {
		return pYearDebit;
	}
	
}
