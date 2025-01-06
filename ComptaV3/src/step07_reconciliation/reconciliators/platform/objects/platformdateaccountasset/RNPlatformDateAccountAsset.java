package step07_reconciliation.reconciliators.platform.objects.platformdateaccountasset;

import basicmethods.AMNumberTools;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step07_reconciliation.reconciliators.platform.objects.platformdateaccount.RNPlatformDateAccount;

public class RNPlatformDateAccountAsset {

	public RNPlatformDateAccountAsset(BKAsset _sBKAsset, RNPlatformDateAccount _sRNPlatformDateAccount) {
		pBKAsset = _sBKAsset;
		pRNPlatformDateAccount = _sRNPlatformDateAccount; 
	}

	/*
	 * Data
	 */
	private BKAsset pBKAsset;
	private RNPlatformDateAccount pRNPlatformDateAccount;
	private double pAmountCompta;
	private double pAmountPlatform;
	private int pNbBKBarInCompta;

	/**
	 * 
	 * @return
	 */
	public final double getpAndComputeErrorAcceptable() {
		if (pBKAsset instanceof BKAssetMetal) {
			double lErrorAcceptable = BKStaticConst.getRECONCILIATION_ERROR_ACCEPTABLE_WEIGHT_PER_BAR()
					* pNbBKBarInCompta;
			if (!AMNumberTools.isZero(lErrorAcceptable)) {
				return lErrorAcceptable;
			}
			if (!AMNumberTools.isZero(pAmountCompta) || !AMNumberTools.isZero(pAmountPlatform)) {
				return BKStaticConst.getRECONCILIATION_ERROR_ACCEPTABLE_WEIGHT_PER_BAR();
			} else {
				return 0;
			}
		} else {
			return 0.01 / pBKAsset.getpPriceUSD(pRNPlatformDateAccount.getpRNPlatformDate().getpDate());
		}
	}

	/*
	 * Getters & Setters
	 */
	public final BKAsset getpBKAsset() {
		return pBKAsset;
	}
	public final RNPlatformDateAccount getpRNPlatformDateAccount() {
		return pRNPlatformDateAccount;
	}
	public final double getpAmountCompta() {
		return pAmountCompta;
	}
	public final double getpAmountPlatform() {
		return pAmountPlatform;
	}
	public final void addpAmountCompta(double _sPAmountCompta) {
		if (!AMNumberTools.isNaNOrNullOrZero(_sPAmountCompta)) {
			pAmountCompta += _sPAmountCompta;
		}		
	}
	public final void addpAmountPlatform(double _sPAmountPlatform) {
		if (!AMNumberTools.isNaNOrNullOrZero(_sPAmountPlatform)) {
			pAmountPlatform += _sPAmountPlatform;
		}
	}
	public final void incpNbBKBarInCompta() {
		pNbBKBarInCompta++;
	}


}
