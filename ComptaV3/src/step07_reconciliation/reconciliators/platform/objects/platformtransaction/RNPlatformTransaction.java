package step07_reconciliation.reconciliators.platform.objects.platformtransaction;

import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;

public class RNPlatformTransaction {

	protected RNPlatformTransaction(int _sDate,
			BKAsset _sBKAsset,
			String _sComment,
			double _sQuantity,
			BKAccount _sBKAccount) {
		pDate = _sDate;
		pBKAsset = _sBKAsset;
		pComment = _sComment;
		pQuantity = _sQuantity;
		pBKAccount = _sBKAccount;
		/*
		 * 
		 */
		pKey = getKey(pDate, pBKAsset, pComment, pQuantity, pBKAccount);
	}

	/*
	 * Data
	 */
	private int pDate;
	private BKAsset pBKAsset;
	private String pComment;
	private double pQuantity;
	private BKAccount pBKAccount;
	private String pKey;

	/**
	 * 
	 * @param _sDate
	 * @param _sBKAsset
	 * @param _sComment
	 * @param _sQuantity
	 * @param _sBKAccount
	 * @return
	 */
	public static String getKey(int _sDate,
			BKAsset _sBKAsset,
			String _sComment,
			double _sQuantity,
			BKAccount _sBKAccount) {
		return _sDate + ";;" + _sBKAsset.getpName() + ";;" + _sComment + ";;" + _sQuantity + ";;" + _sBKAccount.getpKey();
	}

	/*
	 * Getters & Setters
	 */
	public final int getpDate() {
		return pDate;
	}
	public final BKAsset getpBKAsset() {
		return pBKAsset;
	}
	public final String getpComment() {
		return pComment;
	}
	public final double getpQuantity() {
		return pQuantity;
	}
	public final BKAccount getpBKAccount() {
		return pBKAccount;
	}
	public final String getpKey() {
		return pKey;
	}

}
