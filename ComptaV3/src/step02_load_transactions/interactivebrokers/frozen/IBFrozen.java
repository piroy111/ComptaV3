package step02_load_transactions.interactivebrokers.frozen;

import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.asset.asset.BKAssetCurrency;
import step01_objects_from_conf_files.asset.asset.BKAssetPaperCurrency;
import step01_objects_from_conf_files.asset.asset.BKAssetPaperMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step02_load_transactions.interactivebrokers.createfilestransactions.ibstatic.IBStatic;
import step02_load_transactions.interactivebrokers.createfilestransactions.transactions.IBTransactionCreateFromTrades;

public class IBFrozen {

	
	
	
	
	
	
	
	public static final String getpCommentFromBKAsset(BKAsset _sBKAsset) {
		if (_sBKAsset instanceof BKAssetCurrency) {
			return IBStatic.getCOMMENT_FOREX();
		} else if (_sBKAsset instanceof BKAssetPaperCurrency) {
			return IBStatic.getCOMMENT_FOREX_SWAPS();
		} else if (_sBKAsset instanceof BKAssetPaperMetal) {
			return IBStatic.getCOMMENT_METAL_SWAPS();
		} else if (_sBKAsset.getpName().equals(BKStaticConst.getNAME_BKASSET_OIL())) {
			return IBStatic.getCOMMENT_OIL();
		} else if (_sBKAsset.getpName().equals(IBStatic.getMINI_GOLD())) {
			return IBStatic.getCOMMENT_MINI_GOLD();
		} else {
			BKCom.error("Missing IB comment for the BKAsset"
					+ "\nBKasset= " + _sBKAsset.getpName()
					+ "\nPlease check in hte class '" + IBTransactionCreateFromTrades.class.getSimpleName() + "' to get the examples");
			return null;
		}
	}
	
	
	
}
