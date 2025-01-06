package step09_fiscal_year_end.step02_income.objects;

abstract class FYIncomeAbstract {

	protected FYIncomeAbstract(String _sName) {
		pName = _sName;
	}
	
	/*
	 * Data
	 */
	private String pName;
	private double pNAVCurrent;
	private double pNAVPrevious;
	private double pNAVPastPrevious;
	private double pIncomeCurrent;
	private double pIncomePrevious;
	
	/**
	 * 
	 * @param _sNAVCurrent
	 */
	public final void addToNAV(double _sNAVCurrent, double _sNAVPrevious, double _sNAVPastPrevious) {
		pNAVCurrent += _sNAVCurrent;
		pNAVPrevious += _sNAVPrevious;
		pNAVPastPrevious += _sNAVPastPrevious;
		/*
		 * 
		 */
		pIncomeCurrent = pNAVCurrent - pNAVPrevious;
		pIncomePrevious = pNAVPrevious - pNAVPastPrevious;
	}	
	
	/*
	 * Getters & Setters
	 */
	public final String getpName() {
		return pName;
	}
	public final void setpName(String _sPName) {
		pName = _sPName;
	}
	public final double getpNAVCurrent() {
		return pNAVCurrent;
	}
	public final double getpNAVPrevious() {
		return pNAVPrevious;
	}
	public final double getpNAVPastPrevious() {
		return pNAVPastPrevious;
	}
	public final double getpIncomeCurrent() {
		return pIncomeCurrent;
	}
	public final double getpIncomePrevious() {
		return pIncomePrevious;
	}

	
}
