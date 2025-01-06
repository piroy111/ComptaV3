package step07_reconciliation.reconciliators.platform.objects.platformdate;

import java.util.HashMap;
import java.util.Map;

import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step07_reconciliation.reconciliators.platform.bars_outside_platform.per_date.RNPlatformOutsideDate;
import step07_reconciliation.reconciliators.platform.objects.platformdateaccount.RNPlatformDateAccount;
import step07_reconciliation.reconciliators.platform.objects.platformdateaccountasset.RNPlatformDateAccountAsset;

public class RNPlatformDate {

	public RNPlatformDate(int _sDate, RNPlatformDateManager _sRNPlatformDateManager) {
		pDate = _sDate;
		pRNPlatformDateManager = _sRNPlatformDateManager;
		/*
		 * 
		 */
		pMapBKAccountToRNPlatformDateAccount = new HashMap<>();
		pRNPlatformOutsideDate = pRNPlatformDateManager.getpRNPlatform().getpRNPlatformOutsideManager().getpAndCheckRNPlatformOutsideDate(pDate);
	}
	
	/*
	 * Data
	 */
	private int pDate;
	private RNPlatformDateManager pRNPlatformDateManager;
	private String pNameFile;
	private Map<BKAccount, RNPlatformDateAccount> pMapBKAccountToRNPlatformDateAccount;
	private RNPlatformOutsideDate pRNPlatformOutsideDate;
	
	/**
	 * 
	 * @param _sBKAccount
	 * @return
	 */
	public final RNPlatformDateAccount getpOrCreateRNPlatformDateAccount(BKAccount _sBKAccount) {
		RNPlatformDateAccount lRNPlatformDateAccount = pMapBKAccountToRNPlatformDateAccount.get(_sBKAccount);
		if (lRNPlatformDateAccount == null) {
			lRNPlatformDateAccount = new RNPlatformDateAccount(_sBKAccount, this);
			pMapBKAccountToRNPlatformDateAccount.put(_sBKAccount, lRNPlatformDateAccount);
		}
		return lRNPlatformDateAccount;
	}

	/**
	 * 
	 * @param _sBKAccount
	 * @param _sBKAsset
	 * @return
	 */
	public final RNPlatformDateAccountAsset getpOrCreateRNPlatformDateAccountAsset(BKAccount _sBKAccount, BKAsset _sBKAsset) {
		RNPlatformDateAccount lRNPlatformDateAccount = getpOrCreateRNPlatformDateAccount(_sBKAccount);
		return lRNPlatformDateAccount.getpOrCreateRNPlatformDateAccountAsset(_sBKAsset);
	}
	
	/**
	 * 
	 * @param _sBKAccount
	 * @param _sBKAsset
	 * @return
	 */
	public final double getpAmountPlatform(BKAccount _sBKAccount, BKAsset _sBKAsset) {
		double lHolding = 0.;
		RNPlatformDateAccount lRNPlatformDateAccount = pMapBKAccountToRNPlatformDateAccount.get(_sBKAccount);
		if (lRNPlatformDateAccount != null) {
			RNPlatformDateAccountAsset lRNPlatformDateAccountAsset = lRNPlatformDateAccount.getpMapBKAssetToRNPlatformDateAccountAsset().get(_sBKAsset);
			if (lRNPlatformDateAccountAsset != null) {
				lHolding = lRNPlatformDateAccountAsset.getpAmountPlatform();
			}
		}
		return lHolding;
	}

	/*
	 * Getters & Setters
	 */
	public final int getpDate() {
		return pDate;
	}
	public final Map<BKAccount, RNPlatformDateAccount> getpMapBKAccountToRNPlatformDateAccount() {
		return pMapBKAccountToRNPlatformDateAccount;
	}
	public final String getpNameFile() {
		return pNameFile;
	}
	public final void setpNameFile(String _sPNameFile) {
		pNameFile = _sPNameFile;
	}
	public final RNPlatformOutsideDate getpRNPlatformOutsideDate() {
		return pRNPlatformOutsideDate;
	}
	
}
