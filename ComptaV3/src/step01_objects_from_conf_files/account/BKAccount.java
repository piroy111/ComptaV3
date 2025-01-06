package step01_objects_from_conf_files.account;

import step01_objects_from_conf_files.asset.asset.BKAssetCurrency;

public class BKAccount {

	protected BKAccount(String _sEmail, BKAssetCurrency _sBKAssetCurrency) {
		pEmail = _sEmail;
		pBKAssetCurrency = _sBKAssetCurrency;
		/*
		 * 
		 */
		pKey = getKey(pEmail, pBKAssetCurrency);
	}
	
	/*
	 * Data
	 */
	private String pEmail;
	private String pOwner;
	private String pJoint;
	private String pSource;
	private BKAssetCurrency pBKAssetCurrency;
	private String pKey;

	/**
	 * 
	 * @param _sEmail
	 * @param _sBKAssetCurrency
	 * @return
	 */
	public static String getKey(String _sEmail, BKAssetCurrency _sBKAssetCurrency) {
		return _sEmail + "; " + _sBKAssetCurrency;
	}
	
	/**
	 * Classic toString
	 */
	public String toString() {
		return pKey;
	}	
	
	/*
	 * Getters & Setters
	 */
	public final String getpEmail() {
		return pEmail;
	}
	public final String getpOwner() {
		return pOwner;
	}
	public final String getpJoint() {
		return pJoint;
	}
	public final String getpSource() {
		return pSource;
	}
	public final String getpKey() {
		return pKey;
	}
	public final BKAssetCurrency getpBKAssetCurrency() {
		return pBKAssetCurrency;
	}
	protected final void setpOwner(String pOwner) {
		this.pOwner = pOwner;
	}
	protected final void setpJoint(String pJoint) {
		this.pJoint = pJoint;
	}
	protected final void setpSource(String pSource) {
		this.pSource = pSource;
	}
	
	
	
	
	
}
