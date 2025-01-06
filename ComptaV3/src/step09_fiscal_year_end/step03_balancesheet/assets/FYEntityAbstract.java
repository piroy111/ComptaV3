package step09_fiscal_year_end.step03_balancesheet.assets;

class FYEntityAbstract {

	public FYEntityAbstract(String _sName) {
		pName = _sName;
	}
	
	/*
	 * Data
	 */
	private String pName;
	private double pNAVCurrent;
	private double pNAVPrevious;
	
	/**
	 * 
	 * @param _sNAVPrevious
	 * @param _sNAVCurrent
	 */
	public final void addNAV(double _sNAVPrevious, double _sNAVCurrent) {
		pNAVCurrent += _sNAVCurrent;
		pNAVPrevious += _sNAVPrevious;
	}
	
	/**
	 * Classic toString
	 */
	public String toString() {
		return getpName();
	}
	
	/*
	 * Getters & Setters
	 */
	public final double getpNAVCurrent() {
		return pNAVCurrent;
	}
	public final double getpNAVPrevious() {
		return pNAVPrevious;
	}
	public final String getpName() {
		return pName;
	}
	
	
}
