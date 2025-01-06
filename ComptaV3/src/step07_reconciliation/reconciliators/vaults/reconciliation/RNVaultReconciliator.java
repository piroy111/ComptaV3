package step07_reconciliation.reconciliators.vaults.reconciliation;

import java.util.List;

import basicmethods.BasicPrintMsg;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step07_reconciliation.reconciliators.vaults.RNVault;
import step07_reconciliation.reconciliators.vaults.bartypeinvault.RNBarTypeMetalVaultDate;
import step07_reconciliation.reconciliators.vaults.vault.RNVaultDate;

public class RNVaultReconciliator {

	public RNVaultReconciliator(RNVault _sRNVault) {
		pRNVault = _sRNVault;
	}

	/*
	 * Data
	 */
	private RNVault pRNVault;

	/**
	 * 
	 */
	public final void checkReconciliation() {
		BasicPrintMsg.display(this, null);
		BasicPrintMsg.display(this, "Reconciliation Vaults / Compta");
		for (int lDate : pRNVault.getpRNVaultDateManager().getpTreeMapDateToListRNVaultDate().keySet()) {
			/*
			 * Initiate
			 */
			BasicPrintMsg.display(this, "Reconcile for date= " + lDate);
			String lErrorMsg = "";
			List<RNVaultDate> lListRNVaultDate = pRNVault.getpRNVaultDateManager().getpTreeMapDateToListRNVaultDate().get(lDate);
			for (RNVaultDate lRNVaultDate : lListRNVaultDate) {
				for (RNBarTypeMetalVaultDate lRNBarTypeMetalVaultDate : lRNVaultDate.getpMapKeyToRNBarTypeMetalVaultDate().values()) {
					BKAssetMetal lBKAssetMetal = lRNBarTypeMetalVaultDate.getpBKBarType().getpBKAssetMetal();
					/*
					 * Check error on the number of bars
					 */
					int lNbBarsCompta = lRNBarTypeMetalVaultDate.getpComptaBarsQuantity();
					int lNbBarsVault = lRNBarTypeMetalVaultDate.getpVaultBarsQuantity();
					if (lNbBarsCompta != lNbBarsVault) {
						lErrorMsg += "\n"
								+ "\nVault= " + lRNVaultDate.getpBKEntity().getpName()
								+ "\nDate= " + lRNVaultDate.getpDate()
								+ "\nType bars= " + lRNBarTypeMetalVaultDate.getpBKBarType().getpName()
								+ "\nBKAssetMetal= " + lBKAssetMetal.getpName()
								+ "\nNb bars according to reconciliation vault = " + lNbBarsVault
								+ "\nNb bars according to Compta= " + lNbBarsCompta;
					}
					/*
					 * Check error on OZ
					 */
					double lErrorAcceptable = BKStaticConst.getRECONCILIATION_ERROR_ACCEPTABLE_WEIGHT_PER_BAR()
							* Math.max(lNbBarsCompta, lNbBarsVault);
					double lOzAccordingToVault = lRNBarTypeMetalVaultDate.getpVaultBarsWeightOz();
					double lOzAccordingToCompta = lRNBarTypeMetalVaultDate.getpComptaBarsWeightOz();
					if (Math.abs(lOzAccordingToVault - lOzAccordingToCompta) > lErrorAcceptable) {
						lErrorMsg += "\n"
								+ "\nVault= " + lRNVaultDate.getpBKEntity().getpName()
								+ "\nDate= " + lRNVaultDate.getpDate()
								+ "\nType bars= " + lRNBarTypeMetalVaultDate.getpBKBarType().getpName()
								+ "\nBKAssetMetal= " + lBKAssetMetal.getpName()
								+ "\nOz according to reconciliation vault 	= " + lOzAccordingToVault
								+ "\nOz according to Compta = " + lOzAccordingToCompta
								+ "\nAcceptable error= " + lErrorAcceptable;
					}
				}
			}
			if (!lErrorMsg.equals("")) {
				pRNVault.getpRNWriteFile().writeFile(lDate);
				lErrorMsg = "Error in reconciliating the vaults and compta."
						+ "\nData of 'reconciliation vault' comes from the files given manually for reconciliation."
						+ "\nData of 'Compta' comes from the sum of all BKtransactions"
						+ lErrorMsg
						+ "\n"
						+ "\nCheck the file for debug= '" + pRNVault.getpRNWriteFile().getpDirPlusNameFile() + "'";
				BKCom.error(lErrorMsg);
			}
		}
	}

	/*
	 * Getters & Setters
	 */
	public final RNVault getpRNVault() {
		return pRNVault;
	}


}
