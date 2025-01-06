package step07_reconciliation.reconciliators.vaults.loadfilesbalances;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicDir;
import basicmethods.BasicFichiers;
import basicmethods.BasicFichiersNio;
import basicmethods.BasicFile;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.ReadFile;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.asset.bar.BKBarType;
import step01_objects_from_conf_files.asset.bar.BKBarTypeManager;
import step02_load_transactions.objects.entity.BKEntity;
import step07_reconciliation.reconciliators.vaults.RNVault;
import step07_reconciliation.reconciliators.vaults.bartypeinvault.RNBarTypeMetalVaultDate;
import step07_reconciliation.reconciliators.vaults.vault.RNVaultDate;

public class RNVaultLoadFileBalancesManager {

	public RNVaultLoadFileBalancesManager(RNVault _sRNVaultManager) {
		pRNVaultManager = _sRNVaultManager;
	}

	/*
	 * Data
	 */
	private RNVault pRNVaultManager;

	/**
	 * 
	 */
	public final void readAndLoadFiles(List<Integer> _sListDateToReconcile) {
		BasicPrintMsg.display(this, null);
		BasicPrintMsg.display(this, "Read files of balances of BKVaults");
		for (BKEntity lBKEntity : pRNVaultManager.getpListBKEntityToCheck()) {
			BasicPrintMsg.display(this, lBKEntity.toString());
			/*
			 * Get DIR balance --> if the DIR does not exist, it means there is no file reconciliation, and we skip
			 */
			String lDirBalance = BKStaticDir.getVAULT_BALANCES() 
					+ lBKEntity.getpName() + "/"
					+ BKStaticDir.getSUB_VAULT_BALANCE();
			if (!BasicFichiersNio.isExist(lDirBalance)) {
				BasicPrintMsg.display(this, "The DIR of balances does not exist; Dir= '" + lDirBalance + "'");
				continue;
			}			
			String lSuffixBalance = "_" + lBKEntity.getpName() + BKStaticNameFile.getSUFFIX_BALANCES();
			BasicDir lBasicDirBalances = new BasicDir(lDirBalance, lSuffixBalance);
			/*
			 * Get DIR Proof
			 */
			String lDirProof = BKStaticDir.getVAULT_BALANCES() 
					+ lBKEntity.getpName() + "/"
					+ BKStaticDir.getSUB_VAULT_PROOF();
			String lSuffixProof = "_" + lBKEntity.getpName() + BKStaticNameFile.getSUFFIX_PROOF();
			BasicDir lBasicDirProof = new BasicDir(lDirProof, lSuffixProof);
			/*
			 * Check names are correct
			 */
			BasicFichiers.checkAllFilesWrittenWithSuffix(lDirBalance, lSuffixBalance);
			BasicFichiers.checkAllFilesWrittenWithSuffix(lDirProof, lSuffixProof.substring(0, lSuffixProof.length() - ".csv".length()) + "*");
			/*
			 * List dates with a file evidence
			 */
			List<Integer> lListDateWithAFileProof = new ArrayList<>();
			List<String> lListFileNameProof = BasicFichiersNio.getListFilesAndDirectoriesInDirectory(lDirProof);
			for (String lNameFile : lListFileNameProof) {
				int lDateInNameFile = BasicString.getInt(lNameFile.substring(0, 8));
				if (!lListDateWithAFileProof.contains(lDateInNameFile)) {
					lListDateWithAFileProof.add(lDateInNameFile);
				}
			}
			/*
			 * Check there is a proof file for each balance file
			 */
			for (BasicFile lBasicFileBalances : lBasicDirBalances.getmTreeMapDateToBasicFile().values()) {
				if (!lListDateWithAFileProof.contains(lBasicFileBalances.getmDate())) {
					BKCom.error("The file proof is missing for the file balance"
							+ "\nFile balance= '" + lBasicDirBalances.getmDir() + lBasicFileBalances.getmNameFile() + "'"
							+ "\nDate missing= " + lBasicFileBalances.getmDate()
							+ "\nDir where the file proof is missing= '" + lBasicDirProof.getmDir() + "'");
				}
			}
			/*
			 * Check header is correct
			 */
			String lErrorMsg = "";
			String lExpectedHeader = "Date,BKAsset,Comment,Quantity,Weight,WeightUnit";
			for (BasicFile lBasicFileBalances : lBasicDirBalances.getmTreeMapDateToBasicFile().values()) {
				String lHeader = lBasicFileBalances.getmReadFile().getmHeader();
				if (!lHeader.equals(lExpectedHeader)) {
					lErrorMsg += "\nFile with a wrong header= '" + lBasicFileBalances.getmReadFile().getmDirPlusNameFile() + "'";
				}
			}
			if (!lErrorMsg.equals("")) {
				lErrorMsg = "Some files have a wrong header"
						+ "\nExpected header= '" + lExpectedHeader + "'"
						+ lErrorMsg;
				BKCom.error(lErrorMsg);
			}
			/*
			 * Read the files balances
			 */
			for (BasicFile lBasicFileBalances : lBasicDirBalances.getmTreeMapDateToBasicFile().values()) {
				/*
				 * If date is outside of the range of date which we should check, then we pass
				 */
				if (!_sListDateToReconcile.contains(lBasicFileBalances.getmDate())) {
					continue;
				}
				/*
				 * Read file
				 */
				ReadFile lReadFileBalances = lBasicFileBalances.getmReadFile();
				/*
				 * Read balance + create RNVaultDate
				 */
				BasicPrintMsg.display(this, "Reading file '" + lReadFileBalances + "'");
				for (List<String> lLine : lReadFileBalances.getmContentList()) {
					/*
					 * Load line
					 */
					int lIdx = -1;
					int lDate = BasicString.getInt(lLine.get(++lIdx));
					BKAssetMetal lBKAssetMetal = BKAssetManager.getpAndCheckBKAssetMetal(lLine.get(++lIdx), lBasicFileBalances.getmNameFile());
					++lIdx;
					int lQuantity = BasicString.getInt(lLine.get(++lIdx));
					double lWeightTotal = BasicString.getDouble(lLine.get(++lIdx));
					String lUnitWeightStr = lLine.get(++lIdx).toUpperCase();
					/*
					 * Check Multiplier from unit
					 */
					double lMultiplierToOz = 1;
					if (lUnitWeightStr.equals("GRAM")) {
						lMultiplierToOz = 1 / BKStaticConst.getOZ();
					} else if (lUnitWeightStr.equals("KG")) {
						lMultiplierToOz = 1000 / BKStaticConst.getOZ();
					} else if (lUnitWeightStr.equals("TOZ") || lUnitWeightStr.equals("OZ")) {
						lMultiplierToOz = 1.;
					} else {
						BKCom.error("I dont know the weight unit. You must change the code here"
								+ "\nWeight unit= '" + lUnitWeightStr + "'"
								+ "\nFile= '" + lReadFileBalances.getmDirPlusNameFile() + "'");
					}
					/*
					 * Determine the BarType (which is always associated with a metal by the constructor)
					 */
					if (lQuantity == 0) {
						continue;
					}
					double lWeightTotalOz = lWeightTotal * lMultiplierToOz;
					double lWeightPerBarOz = lWeightTotalOz / lQuantity;
					BKBarType lBKBarType = BKBarTypeManager.getpBKBarType(lWeightPerBarOz, lBKAssetMetal);
					/*
					 * Create objects
					 */
					RNVaultDate lRNVaultDate = pRNVaultManager.getpRNVaultDateManager().getpOrCreateRNVaultDate(lBKEntity, lDate);
					RNBarTypeMetalVaultDate lRNBarTypeMetalVaultDate = lRNVaultDate.getpOrCreateRNBarTypeMetalVaultDate(lBKBarType);
					lRNBarTypeMetalVaultDate.addNewDataInVault(lQuantity, lWeightTotalOz);
				}
			}
		}
		pRNVaultManager.getpRNVaultDateManager().sortListRNVaultDate();
	}


}
