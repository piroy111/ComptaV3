package step07_reconciliation.reconciliators.vaults.loadcompta;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicPrintMsg;
import staticdata.com.BKCom;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;
import step01_objects_from_conf_files.asset.bar.BKBar;
import step01_objects_from_conf_files.asset.bar.BKBarType;
import step02_load_transactions.objects.entity.BKEntity;
import step07_reconciliation.reconciliators.vaults.RNVault;
import step07_reconciliation.reconciliators.vaults.bartypeinvault.RNBarTypeMetalVaultDate;
import step07_reconciliation.reconciliators.vaults.vault.RNVaultDate;

public class RNVaultLoadComptaManager {

	public RNVaultLoadComptaManager(RNVault _sRNVaultManager) {
		pRNVaultManager = _sRNVaultManager;
	}

	/*
	 * Data
	 */
	private RNVault pRNVaultManager;

	/**
	 * 
	 */
	public final void loadCompta() {
		BasicPrintMsg.display(this, null);
		BasicPrintMsg.display(this, "Count the number and the total weight of the BKBars in the vault according to the COMPTA");
		/*
		 * Loop on all the Date / Vault / AssetMetal / Bars
		 */
		List<Integer> lListDateToCheck = new ArrayList<>(pRNVaultManager.getpRNVaultDateManager()
				.getpTreeMapDateToListRNVaultDate().keySet());
		for (int lDate : lListDateToCheck) {
			BasicPrintMsg.display(this, "Check date= " + lDate);
			for (BKAssetMetal lBKAssetMetal : BKAssetManager.getpListBKAssetMetalSorted()) {
				/*
				 * Count the number and the total weight of the BKBars in the vault according to the COMPTA
				 */
				for (BKBar lBKBar : lBKAssetMetal.getpMapIDToBKBar().values()) {
					if (lBKBar.getpBKEntity(lDate) != null) {
						/*
						 * Load data of the BKBar
						 */
						BKBarType lBKBarType = lBKBar.getpBKBarType();
						BKEntity lBKEntity = lBKBar.getpBKEntity(lDate);
						/*
						 * Error handler --> no BKEntity for the BKBar
						 */
						if (lBKEntity == null) {
							String lErrorMsg = "A BKBar is located nowhere at the date. It should be in a vault"
									+ "\nBKBar ID= " + lBKBar.getpID()
									+ "\nBKBAr= " + lBKBar.toString()
									+ "\nDate= " + lDate
									+ "\nIsDelivered(" + lDate + ")= " + lBKBar.getpIsDelivered(lDate)
									+ "\nBKEntity(" + lDate + ")= " + lBKEntity;
							BKCom.error(lErrorMsg);
						}
						/*
						 * Get or create the correct RN place to put the BKBar of COMPTA
						 */
						RNVaultDate lRNVaultDate = pRNVaultManager.getpRNVaultDateManager().getpOrCreateRNVaultDate(lBKEntity, lDate);
						RNBarTypeMetalVaultDate lRNBarTypeMetalVaultDate = lRNVaultDate.getpOrCreateRNBarTypeMetalVaultDate(lBKBarType);
						/*
						 * Add the weight of the BKBar to the RNVault and RNBarType
						 */
						lRNBarTypeMetalVaultDate.addNewDataInCompta(1, lBKBar.getpWeightOz());
					}
				}
			}
		}
	}	

	/*
	 * Getters & Setters
	 */
	public final RNVault getpRNVaultManager() {
		return pRNVaultManager;
	}



}
