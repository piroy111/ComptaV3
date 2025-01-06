package step07_reconciliation.reconciliators.platform.loadfiles;

import java.util.List;

import basicmethods.BasicDir;
import basicmethods.BasicFile;
import basicmethods.BasicString;
import basicmethods.ReadFile;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step07_reconciliation.reconciliators.platform.RNPlatform;

public class RNPlatformLoadFilesTransaction {

	public RNPlatformLoadFilesTransaction(RNPlatform _sRNPlatform) {
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
		String lDir = BKStaticDir.getRECONCILIATION_PLATFORM_TRANSACTIONS();
		String lSuffix = BKStaticNameFile.getSUFFIX_RECONCILIATION_PLATFORM_TRANSACTIONS();
		BasicDir lBasicDir = new BasicDir(lDir, lSuffix);
		for (BasicFile lBasicFile : lBasicDir.getmMapDateToBasicFile().values()) {
			ReadFile lReadFile = lBasicFile.getmReadFile();
			for (List<String> lLine : lReadFile.getmContentList()) {
				/*
				 * Load line
				 */
				int lIdx = -1;
				int lDate = BasicString.getInt(lLine.get(++lIdx));
				BKAsset lBKAsset = BKAssetManager.getpAndCheckBKAsset(lLine.get(++lIdx), lReadFile.getmDirPlusNameFile());
				String lComment = lLine.get(++lIdx);
				double lQuantity = BasicString.getDouble(lLine.get(++lIdx));
				++lIdx;
				String lEmail = lLine.get(++lIdx);
				/*
				 * Skip if CAROUSELL and BUNKER
				 */
				if (lEmail.equals(BKStaticConst.getACCOUNT_CAROUSELL())
						|| lEmail.equals(BKStaticConst.getACCOUNT_BUNKER())) {
					continue;
				}
				BKAccount lBKAccount = BKAccountManager.getpAndCheckBKAccount(lEmail, lReadFile.getmDirPlusNameFile());
				/*
				 * 
				 */
				pRNPlatform.getpRNPlatformTransactionManager().getpOrCreateRNPlatformTransaction(lDate, 
						lBKAsset, lComment, lQuantity, lBKAccount);
			}
		}
	}
	
	/*
	 * Getters & Setters
	 */
	public final RNPlatform getpRNPlatform() {
		return pRNPlatform;
	}
}
