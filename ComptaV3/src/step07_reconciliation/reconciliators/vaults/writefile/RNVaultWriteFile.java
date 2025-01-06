package step07_reconciliation.reconciliators.vaults.writefile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import basicmethods.AMDebug;
import basicmethods.BasicDateInt;
import basicmethods.BasicPrintMsg;
import staticdata.com.BKCom;
import staticdata.com.fileswritten.BKComOnFilesWritten;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import staticdata.datas.BKStaticConst.com_file_written;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step07_reconciliation.reconciliators.vaults.RNVault;
import step07_reconciliation.reconciliators.vaults.bartypeinvault.RNBarTypeMetalVaultDate;
import step07_reconciliation.reconciliators.vaults.vault.RNVaultDate;

public class RNVaultWriteFile {

	public RNVaultWriteFile(RNVault _sRNVault) {
		pRNVault = _sRNVault;
	}

	/*
	 * Data
	 */
	private RNVault pRNVault;
	private String pDirPlusNameFile;

	/**
	 * 
	 */
	public final void writeFile(Integer _sDate) {
		BasicPrintMsg.display(this, "Write file RNVault for date= " + _sDate);
		/*
		 * Choose the date to write. If _sDate == null, then we take the last date
		 */
		if (pRNVault.getpRNVaultDateManager().getpTreeMapDateToListRNVaultDate().size() == 0) {
			return;
		}
		int lDate;
		if (_sDate != null) {
			lDate = _sDate;
			if (!pRNVault.getpRNVaultDateManager().getpTreeMapDateToListRNVaultDate().containsKey(lDate)) {
				BKCom.errorCodeLogic();
			}
		} else {
			lDate = pRNVault.getpRNVaultDateManager().getpTreeMapDateToListRNVaultDate().lastKey();
		}
		List<RNVaultDate> lListRNVaultDate = pRNVault.getpRNVaultDateManager().getpTreeMapDateToListRNVaultDate().get(lDate);
		Collections.sort(lListRNVaultDate);
		/*
		 * Initiate file
		 */
		String lDir = BKStaticDir.getOUTPUT_RECONCILIATION_VAULTS();
		String lNameFile = BasicDateInt.getmToday() + BKStaticNameFile.getSUFFIX_RECONCILIATOR_VAULTS();
		pDirPlusNameFile = lDir + lNameFile;
		String lHeader = "Date,BKVault,BKAsset,BKBarType"
				+ ",Amount according to Compta (Oz),Amount according to Vault (Oz)"
				+ ",Value$ according to Compta (US$),Value$ according to Vault (US$)"
				+ ",Number of bars according to Compta,Number of bars according to Vault";
		/*
		 * Build file content
		 */
		List<String> lListLineToWrite = new ArrayList<>();
		for (RNVaultDate lRNVaultDate : lListRNVaultDate) {
			for (BKAssetMetal lBKAssetMetal : lRNVaultDate.getpTreeMapBKAssetMetalToListRNBarTypeMetalVaultDate().keySet()) {
				List<RNBarTypeMetalVaultDate> lListRNBarTypeMetalVaultDate = lRNVaultDate
						.getpTreeMapBKAssetMetalToListRNBarTypeMetalVaultDate().get(lBKAssetMetal);
				Collections.sort(lListRNBarTypeMetalVaultDate);
				for (RNBarTypeMetalVaultDate lRNBarTypeMetalVaultDate : lListRNBarTypeMetalVaultDate) {
					/*
					 * Write details + value in vault according to COMPTA and according to vault
					 */
					String lLine = lDate
							+ "," + lRNVaultDate.getpBKEntity().getpName()
							+ "," + lBKAssetMetal.getpName()
							+ "," + lRNBarTypeMetalVaultDate.getpBKBarType().getpName()
							+ "," + lRNBarTypeMetalVaultDate.getpComptaBarsWeightOz()
							+ "," + lRNBarTypeMetalVaultDate.getpVaultBarsWeightOz()
							+ "," + (lRNBarTypeMetalVaultDate.getpComptaBarsWeightOz() * lBKAssetMetal.getpPriceUSD(lDate))
							+ "," + (lRNBarTypeMetalVaultDate.getpVaultBarsWeightOz() * lBKAssetMetal.getpPriceUSD(lDate))
							+ "," + lRNBarTypeMetalVaultDate.getpComptaBarsQuantity()
							+ "," + lRNBarTypeMetalVaultDate.getpVaultBarsQuantity();
					lListLineToWrite.add(lLine);
				}
			}
		}
		/*
		 * Write file
		 */
		BKComOnFilesWritten.writeFile(com_file_written.Reconciliator, lDir, lNameFile, lHeader, lListLineToWrite);
	}

	/*
	 * Getters & Setters
	 */
	public final RNVault getpRNVault() {
		return pRNVault;
	}
	public final String getpDirPlusNameFile() {
		return pDirPlusNameFile;
	}



}
