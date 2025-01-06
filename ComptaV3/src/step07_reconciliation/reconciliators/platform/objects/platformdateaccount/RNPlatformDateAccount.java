package step07_reconciliation.reconciliators.platform.objects.platformdateaccount;

import java.util.HashMap;
import java.util.Map;

import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step07_reconciliation.reconciliators.platform.objects.platformdate.RNPlatformDate;
import step07_reconciliation.reconciliators.platform.objects.platformdateaccountasset.RNPlatformDateAccountAsset;

public class RNPlatformDateAccount {

	public RNPlatformDateAccount(BKAccount _sBKAccount, RNPlatformDate _sRNPlatformDate) {
		pBKAccount = _sBKAccount;
		pRNPlatformDate = _sRNPlatformDate;
		/*
		 * 
		 */
		pMapBKAssetToRNPlatformDateAccountAsset = new HashMap<>();
	}

	/*
	 * Data
	 */
	private BKAccount pBKAccount;
	private RNPlatformDate pRNPlatformDate;
	private Map<BKAsset, RNPlatformDateAccountAsset> pMapBKAssetToRNPlatformDateAccountAsset;

	/**
	 * 
	 * @return
	 */
	public final RNPlatformDateAccountAsset getpOrCreateRNPlatformDateAccountAsset(BKAsset _sBKAsset) {
		RNPlatformDateAccountAsset lRNPlatformDateAccountAsset = pMapBKAssetToRNPlatformDateAccountAsset.get(_sBKAsset);
		if (lRNPlatformDateAccountAsset == null) {
			lRNPlatformDateAccountAsset = new RNPlatformDateAccountAsset(_sBKAsset, this);
			pMapBKAssetToRNPlatformDateAccountAsset.put(_sBKAsset, lRNPlatformDateAccountAsset);
		}
		return lRNPlatformDateAccountAsset;
	}

	/*
	 * Getters & Setters
	 */
	public final BKAccount getpBKAccount() {
		return pBKAccount;
	}
	public final RNPlatformDate getpRNPlatformDate() {
		return pRNPlatformDate;
	}
	public final Map<BKAsset, RNPlatformDateAccountAsset> getpMapBKAssetToRNPlatformDateAccountAsset() {
		return pMapBKAssetToRNPlatformDateAccountAsset;
	}
	
	
	
	
	
	
	
	
}
