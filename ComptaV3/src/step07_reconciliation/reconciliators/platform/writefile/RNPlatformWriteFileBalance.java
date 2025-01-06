package step07_reconciliation.reconciliators.platform.writefile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import basicmethods.BasicDateInt;
import basicmethods.BasicStringSortDouble;
import staticdata.com.fileswritten.BKComOnFilesWritten;
import staticdata.datas.BKStaticConst.com_file_written;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step07_reconciliation.reconciliators.platform.RNPlatform;
import step07_reconciliation.reconciliators.platform.objects.platformdate.RNPlatformDate;
import step07_reconciliation.reconciliators.platform.objects.platformdateaccount.RNPlatformDateAccount;
import step07_reconciliation.reconciliators.platform.objects.platformdateaccountasset.RNPlatformDateAccountAsset;
import step07_reconciliation.reconciliators.platform.reconciliator.RNPlatformBalanceReconciliator;

public class RNPlatformWriteFileBalance {

	public RNPlatformWriteFileBalance(RNPlatform _sRNPlatform) {
		pRNPlatform = _sRNPlatform;
	}
	
	/*
	 * Data
	 */
	private RNPlatform pRNPlatform;
	private String pDirPlusNameFile;

	/**
	 * 
	 */
	public final void run(Integer _sDate) {
		/*
		 * Load RNPlatformDate --> if _sDate == null, then we take the latest date
		 */
		int lDate;
		if (_sDate != null) {
			lDate = _sDate;
		} else {
			lDate = pRNPlatform.getpRNPlatformDateManager().getpTreeMapDateToRNPlatformDate().lastKey();
		}
		RNPlatformDate lRNPlatformDate = pRNPlatform.getpRNPlatformDateManager().getpTreeMapDateToRNPlatformDate().get(lDate);
		/*
		 * Collect data to write in file --> holding for each account and asset according to COMPTA and Platform
		 */
		List<BasicStringSortDouble> lListLine = new ArrayList<>();
		for (BKAccount lBKAccount : lRNPlatformDate.getpMapBKAccountToRNPlatformDateAccount().keySet()) {
			RNPlatformDateAccount lRNPlatformDateAccount = lRNPlatformDate.getpMapBKAccountToRNPlatformDateAccount().get(lBKAccount);
			for (BKAsset lBKAsset : lRNPlatformDateAccount.getpMapBKAssetToRNPlatformDateAccountAsset().keySet()) {
				RNPlatformDateAccountAsset lRNPlatformDateAccountAsset = lRNPlatformDateAccount.getpMapBKAssetToRNPlatformDateAccountAsset().get(lBKAsset);
				double lHoldingCompta = lRNPlatformDateAccountAsset.getpAmountCompta();
				double lHoldingPlatform = lRNPlatformDateAccountAsset.getpAmountPlatform();
				double lError = Math.abs(lHoldingCompta - lHoldingPlatform);
				double lErrorUSD = lError * lBKAsset.getpPriceUSD(lDate);
				double lErrorAcceptable = lRNPlatformDateAccountAsset.getpAndComputeErrorAcceptable();
				String lIsValidStr = RNPlatformBalanceReconciliator.getpIsBKAccountValidForReconciliation(lBKAccount, lDate);
				if (lIsValidStr.equals("Ok")) {
					lIsValidStr = RNPlatformBalanceReconciliator.getpIsBKAssetValidForReconciliation(lBKAsset);
				}
				String lIsPassStr = lError <= lErrorAcceptable ? "Ok" : "FAILED";
				/*
				 * 
				 */
				String lLineStr = _sDate
						+ "," + lBKAccount.getpEmail()
						+ "," + lBKAccount.getpBKAssetCurrency().getpName()
						+ "," + lBKAsset.getpName()
						+ "," + lIsValidStr
						+ "," + lHoldingCompta
						+ "," + lHoldingPlatform
						+ "," + lErrorUSD
						+ "," + lError
						+ "," + lErrorAcceptable
						+ "," + lIsPassStr;
				BasicStringSortDouble lLine = new BasicStringSortDouble(lLineStr, -lErrorUSD);
				lListLine.add(lLine);
			}
		}
		/*
		 * Sort to put the largest differences first
		 */
		Collections.sort(lListLine);
		List<String> lListLineToWrite = new ArrayList<>();
		for (BasicStringSortDouble lLine : lListLine) {
			lListLineToWrite.add(lLine.getmString());
		}
		/*
		 * Write file
		 */
		String lDir = BKStaticDir.getOUTPUT_RECONCILIATION_PLATFORM_BALANCES();
		String lNameFile = BasicDateInt.getmToday() + BKStaticNameFile.getSUFFIX_OUTPUT_RECONCILIATION_PLATFORM_BALANCES();
		pDirPlusNameFile = lDir + lNameFile;
		String lHeader = "Date,BKAccount,Currency of BKAccount,BKAsset,Is valid for reconciliation?"
				+ ",Holding according to Compta,Holding according to Platform"
				+ ",Error= Abs(Compta-Platform) in US$,Error= Abs(Compta-Platform),Error acceptable,Is pass reconciliation test?";
		BKComOnFilesWritten.writeFile(com_file_written.Reconciliator, lDir, lNameFile, lHeader, lListLineToWrite);
	}
	
//	/**
//	 * 
//	 */
//	private class line implements Comparable<line> {
//		private line(String _sLine, double _sKeySort) {
//			pLine = _sLine;
//			pKeySort = _sKeySort;
//		}
//		/*
//		 * Data
//		 */
//		private String pLine;
//		private double pKeySort;
//		
//		@Override public int compareTo(line _sLine) {
//			return -Double.compare(pKeySort, _sLine.pKeySort);
//		}
//	}
	
	
	/*
	 * Getters & Setters
	 */
	public final RNPlatform getpRNPlatform() {
		return pRNPlatform;
	}
	public final String getpDirPlusNameFile() {
		return pDirPlusNameFile;
	}
	
}
