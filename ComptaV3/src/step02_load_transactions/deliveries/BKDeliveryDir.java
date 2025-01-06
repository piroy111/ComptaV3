package step02_load_transactions.deliveries;

public class BKDeliveryDir {

	protected BKDeliveryDir(String _sDir, String _sSuffix) {
		pDir = _sDir;
		pSuffix = _sSuffix;
	}
	
	/*
	 * Data
	 */
	private String pDir;
	private String pSuffix;
	
	/*
	 * Getters & Setters
	 */
	public final String getpDir() {
		return pDir;
	}
	public final String getpSuffix() {
		return pSuffix;
	}
	
}
