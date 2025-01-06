package step07_reconciliation.reconciliators.platform.reconciliator;

import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step07_reconciliation.reconciliators.platform.RNPlatform;
import step07_reconciliation.reconciliators.platform.objects.platformdate.RNPlatformDate;
import step07_reconciliation.reconciliators.platform.objects.platformdateaccount.RNPlatformDateAccount;
import step07_reconciliation.reconciliators.platform.objects.platformdateaccountasset.RNPlatformDateAccountAsset;

public class RNPlatformBalanceReconciliator {

	public RNPlatformBalanceReconciliator(RNPlatform _sRNPlatform) {
		pRNPlatform = _sRNPlatform;
	}
	
	/*
	 * Data
	 */
	private RNPlatform pRNPlatform;

	/**
	 * 
	 */
	public final void run() {
		for (RNPlatformDate lRNPlatformDate : pRNPlatform.getpRNPlatformDateManager().getpTreeMapDateToRNPlatformDate().values()) {
			int lDate = lRNPlatformDate.getpDate();
			String lErrorMsg = "";
			for (BKAccount lBKAccount : lRNPlatformDate.getpMapBKAccountToRNPlatformDateAccount().keySet()) {
				if (!getpIsBKAccountValidForReconciliation(lBKAccount, lDate).equals("Ok")) {
					continue;
				}
				RNPlatformDateAccount lRNPlatformDateAccount = lRNPlatformDate.getpMapBKAccountToRNPlatformDateAccount().get(lBKAccount);
				for (BKAsset lBKAsset : lRNPlatformDateAccount.getpMapBKAssetToRNPlatformDateAccountAsset().keySet()) {
					if (!getpIsBKAssetValidForReconciliation(lBKAsset).equals("Ok")) {
						continue;
					}
					RNPlatformDateAccountAsset lRNPlatformDateAccountAsset = lRNPlatformDateAccount.getpMapBKAssetToRNPlatformDateAccountAsset().get(lBKAsset);
					/*
					 * Holding in COMPTA --> we add the loan if the BKAsset is a metal 
					 */
					double lHoldingCompta = lRNPlatformDateAccountAsset.getpAmountCompta();
					/*
					 * Retrieve and compare the holdings
					 */
					double lHoldingPlatform = lRNPlatformDateAccountAsset.getpAmountPlatform();
					double lErrorAcceptable = lRNPlatformDateAccountAsset.getpAndComputeErrorAcceptable();
					if (Math.abs(lHoldingCompta - lHoldingPlatform) > lErrorAcceptable) {
						lErrorMsg += "\nBKAccount= " + lBKAccount.getpEmail()
							+ "; BKAsset= " + lBKAsset.getpName()
							+ "; Balance Compta= " + lHoldingCompta
							+ "; Balance Platform= " + lHoldingPlatform
							+ "; Error acceptable= " + lErrorAcceptable;
					}
				}
			}
			/*
			 * Case of an error --> We crash and we write the file 
			 */
			if (!lErrorMsg.equals("")) {
				pRNPlatform.getpRNPlatformWriteFile().run(lDate);
				lErrorMsg = "Some Accounts dont have the same balance in Compta and in Platform"
						+ "\nDate= " + lDate
						+ lErrorMsg
						+ "\n"
						+ "\nUse this file to know the errors= '" + pRNPlatform.getpRNPlatformWriteFile().getpDirPlusNameFile()
						+ "\nUse this file to debug= '" + pRNPlatform.getpRNPlatformWriteFileTransactionsBKCompta().getpDirPlusNameFile();
				BKCom.error(lErrorMsg);
			}
		}
	}
	
	/**
	 * 
	 * @param _sBKAccount
	 * @return "OK" if the BKAccount is valid
	 */
	public static String getpIsBKAccountValidForReconciliation(BKAccount _sBKAccount, int _sDateCompta) {
		if (_sBKAccount.equals(BKAccountManager.getpBKAccountBunker())) {
			return "Bunker";
		} else if (_sBKAccount.equals(BKAccountManager.getpBKAccountPRoy()) && _sDateCompta < BKStaticConst.getDATE_PROY_BECOMES_CLIENT()) {
			return "PRoy";
		} else if (!BKAccountManager.getpListBKAccount().contains(_sBKAccount)) {
			return "Unknown in Compta";
		} else {
			return "Ok";
		}
	}
	
	/**
	 * 
	 * @param _sBKAsset
	 * @return "OK" if the BKAccount is valid
	 */
	public static String getpIsBKAssetValidForReconciliation(BKAsset _sBKAsset) {
		return "Ok";
//		if (_sBKAsset instanceof BKAssetCurrency || _sBKAsset instanceof BKAssetMetal) {
//			return "Ok";
//		} else {
//			return _sBKAsset.getpAssetTypeStr();
//		}
	}
	
	/*
	 * Getters & Setters
	 */
	public final RNPlatform getpRNPlatform() {
		return pRNPlatform;
	}
	
}
