package step07_reconciliation.reconciliators.platform.loadfiles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.AMNumberTools;
import basicmethods.BasicDir;
import basicmethods.BasicFichiers;
import basicmethods.BasicFile;
import basicmethods.BasicString;
import basicmethods.ReadFile;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step00_freeze_transactions.BKFrozenManager;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step07_reconciliation.reconciliators.platform.RNPlatform;
import step07_reconciliation.reconciliators.platform.objects.platformdate.RNPlatformDate;
import step07_reconciliation.reconciliators.platform.objects.platformdateaccount.RNPlatformDateAccount;
import step07_reconciliation.reconciliators.platform.objects.platformdateaccountasset.RNPlatformDateAccountAsset;

public class RNPlatformLoadFilesBalance {

	public RNPlatformLoadFilesBalance(RNPlatform _sRNPlatform) {
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
		String lDir = BKStaticDir.getRECONCILIATION_PLATFORM_BALANCES();
		String lSuffix = BKStaticNameFile.getSUFFIX_RECONCILIATION_PLATFORM_BALANCE();
		/*
		 * Check the files are here and well written
		 */
		BasicFichiers.checkAllFilesWrittenWithSuffix(lDir, lSuffix);
		/*
		 * Load files
		 */
		BasicDir lBasicDir = new BasicDir(lDir, lSuffix);
		for (BasicFile lBasicFile : lBasicDir.getmTreeMapDateToBasicFile().values()) {
			int lDate = lBasicFile.getmDate();
			/*
			 * Skip if the date is before frozen
			 */
			if (lDate <= BKFrozenManager.getDATE_FY_MIN_TO_CREATE()) {
				continue;
			}
			/*
			 * Create PlatformDate
			 */
			RNPlatformDate lRNPlatformDate = pRNPlatform.getpRNPlatformDateManager().getpOrCreateRNPlatformDate(lDate);
			ReadFile lReadFile = lBasicFile.getmReadFile();
			lRNPlatformDate.setpNameFile(lReadFile.getmNameFile());
			/*
			 * Get the BKAsset in a list from the header of the file platform
			 */
			List<BKAsset> lListBKAsset = new ArrayList<>();
			Map<Integer, BKAsset> lMapIdxColumnToBKAsset = new HashMap<>();
			List<String> lHeader = lReadFile.getmHeadersAndComments().get(0);			
			for (int lIdx = 1; lIdx < lHeader.size(); lIdx++) {
				String lBKAssetStr = lHeader.get(lIdx);
				if (!lBKAssetStr.equals("")) {
					BKAsset lBKAsset = BKAssetManager.getpAndCheckBKAsset(lBKAssetStr, lReadFile.getmDirPlusNameFile());
					lListBKAsset.add(lBKAsset);
					lMapIdxColumnToBKAsset.put(lIdx, lBKAsset);
				}
			}
			Collections.sort(lListBKAsset);
			/*
			 * Read the amount of assets for each account
			 */
			for (List<String> lLine : lBasicFile.getmReadFile().getmContentList()) {
				/*
				 * Identify the BKAccount. Because the platform has many fake account, we skip if the name is not recognized
				 */
				int lIdx = -1;
				String lNameAccount = lLine.get(++lIdx);
				BKAccount lBKAccount = null;
				for (BKAccount lBKAccountLoop : BKAccountManager.getpListBKAccount()) {
					if (lBKAccountLoop.getpEmail().equals(lNameAccount)) {
						lBKAccount = lBKAccountLoop;
						break;
					}
				}
				if (lBKAccount == null) {
					continue;
				}
				RNPlatformDateAccount lRNPlatformDateAccount = lRNPlatformDate.getpOrCreateRNPlatformDateAccount(lBKAccount);
				/*
				 * Store amount of each asset
				 */
				while (++lIdx < lLine.size()) {
					double lAmount = BasicString.getDouble(lLine.get(lIdx));
					if (!AMNumberTools.isNaNOrZero(lAmount)) {
						BKAsset lBKAsset = lMapIdxColumnToBKAsset.get(lIdx);
						RNPlatformDateAccountAsset lRNPlatformDateAccountAsset = lRNPlatformDateAccount.getpOrCreateRNPlatformDateAccountAsset(lBKAsset);
						lRNPlatformDateAccountAsset.addpAmountPlatform(lAmount);
					}
				}
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
