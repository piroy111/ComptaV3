package step08_output_files.client_nav_v2;

class BKOutput_OneNAV implements Comparable<BKOutput_OneNAV> {

	protected BKOutput_OneNAV(int _sDate) {
		pDate = _sDate;
	}
	
	/*
	 * Data
	 */
	private int pDate;
	private double pIncomingFunds;
	private double pNAV;

	/**
	 * Sort in ascending order of the dates
	 * @param _sBKOutput_OneNAV
	 * @return
	 */
	@Override public int compareTo(BKOutput_OneNAV _sBKOutput_OneNAV) {
		return Integer.compare(pDate, _sBKOutput_OneNAV.pDate);
	}

	/*
	 * Getters & Setters
	 */
	public final int getpDate() {
		return pDate;
	}
	public final double getpIncomingFunds() {
		return pIncomingFunds;
	}
	public final double getpNAV() {
		return pNAV;
	}
	public final void setpIncomingFunds(double pIncomingFunds) {
		this.pIncomingFunds = pIncomingFunds;
	}
	public final void setpNAV(double pNAV) {
		this.pNAV = pNAV;
	}
	
	
	
	
}
