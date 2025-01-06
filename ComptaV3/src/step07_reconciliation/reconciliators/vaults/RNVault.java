package step07_reconciliation.reconciliators.vaults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import basicmethods.BasicFichiersNio;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticDir;
import step02_load_transactions.objects.entity.BKEntity;
import step02_load_transactions.objects.entity.BKEntityManager;
import step07_reconciliation.abstracts.BKReconciliatorAbstract;
import step07_reconciliation.abstracts.BKReconciliatorManager;
import step07_reconciliation.reconciliators.vaults.loadcompta.RNVaultLoadComptaManager;
import step07_reconciliation.reconciliators.vaults.loadfilesbalances.RNVaultLoadFileBalancesManager;
import step07_reconciliation.reconciliators.vaults.reconciliation.RNVaultReconciliator;
import step07_reconciliation.reconciliators.vaults.vault.RNVaultDateManager;
import step07_reconciliation.reconciliators.vaults.writefile.RNVaultWriteFile;

public class RNVault extends BKReconciliatorAbstract {

	public RNVault(BKReconciliatorManager _sBKReconciliatorManager) {
		super(_sBKReconciliatorManager);
	}

	/*
	 * Data
	 */
	private RNVaultLoadFileBalancesManager pRNLoadFileBalancesManager;
	private RNVaultDateManager pRNVaultDateManager;
	private RNVaultLoadComptaManager pRNLoadComptaManager;
	private RNVaultWriteFile pRNWriteFile;
	private RNVaultReconciliator pRNVaultReconciliator;
	private List<BKEntity> pListBKEntityToCheck;

	@Override public String getpDetailsOfChecksPerformed() {
		String lVaultStr = "";
		/*
		 * 
		 */
		pListBKEntityToCheck = new ArrayList<>();
		List<String> lListSubDir = BasicFichiersNio.getListFilesAndDirectoriesInDirectory(BKStaticDir.getVAULT_BALANCES());
		for (String lNameBKEntity : lListSubDir) {
			BKEntity lBKEntity = BKEntityManager.getpAndCheckBKEntityFromName(lNameBKEntity, this);
			if (!lBKEntity.getpIsVault()) {
				BKCom.error("The BKEntity is not a physical vault. You must use a vault in the reconciliation file"
						+ "\nBKEntity in error= " + lBKEntity);
			}
			pListBKEntityToCheck.add(lBKEntity);
		}
		Collections.sort(pListBKEntityToCheck);
		/*
		 * 
		 */
		for (BKEntity lBKEntity : pListBKEntityToCheck) {
			if (!lVaultStr.equals("")) {
				lVaultStr += " + ";
			}
			lVaultStr += lBKEntity.getpName();
		}
		/*
		 * 
		 */
		String lMsg = "Vaults= " + lVaultStr
				+ "\nVaults holding == Sum transactions in Compta";
		return lMsg;
	}

	/**
	 * 
	 */
	public void computeIsPassTest(List<Integer> _sListDateToReconcile) {
		/*
		 * Instantiate
		 */
		pRNLoadFileBalancesManager = new RNVaultLoadFileBalancesManager(this);
		pRNVaultDateManager = new RNVaultDateManager(this);
		pRNLoadComptaManager = new RNVaultLoadComptaManager(this);
		pRNWriteFile = new RNVaultWriteFile(this);
		pRNVaultReconciliator = new RNVaultReconciliator(this);
		/*
		 * Compute
		 */
		pRNLoadFileBalancesManager.readAndLoadFiles(_sListDateToReconcile);
		pRNLoadComptaManager.loadCompta();
		pRNVaultReconciliator.checkReconciliation();
		pRNWriteFile.writeFile(BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS());
	}

	/*
	 * Getters & Setters
	 */
	public final RNVaultLoadFileBalancesManager getpRNLoadFileBalancesManager() {
		return pRNLoadFileBalancesManager;
	}
	public final RNVaultDateManager getpRNVaultDateManager() {
		return pRNVaultDateManager;
	}
	public final RNVaultLoadComptaManager getpRNLoadComptaManager() {
		return pRNLoadComptaManager;
	}
	public final RNVaultWriteFile getpRNWriteFile() {
		return pRNWriteFile;
	}
	public final RNVaultReconciliator getpRNVaultReconciliator() {
		return pRNVaultReconciliator;
	}
	public final List<BKEntity> getpListBKEntityToCheck() {
		return pListBKEntityToCheck;
	}

	
	
}
