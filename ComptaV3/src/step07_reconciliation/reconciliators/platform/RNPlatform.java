package step07_reconciliation.reconciliators.platform;

import java.util.List;

import staticdata.datas.BKStaticConst;
import step07_reconciliation.abstracts.BKReconciliatorAbstract;
import step07_reconciliation.abstracts.BKReconciliatorManager;
import step07_reconciliation.reconciliators.platform.bars_outside_platform.RNPlatformOutsideManager;
import step07_reconciliation.reconciliators.platform.compta.RNPlatformLoadCompta;
import step07_reconciliation.reconciliators.platform.loadfiles.RNPlatformCheckFilesExistence;
import step07_reconciliation.reconciliators.platform.loadfiles.RNPlatformLoadFilesBalance;
import step07_reconciliation.reconciliators.platform.loadfiles.RNPlatformLoadFilesTransaction;
import step07_reconciliation.reconciliators.platform.objects.platformdate.RNPlatformDateManager;
import step07_reconciliation.reconciliators.platform.objects.platformtransaction.RNPlatformTransactionManager;
import step07_reconciliation.reconciliators.platform.reconciliator.RNPlatformBalanceReconciliator;
import step07_reconciliation.reconciliators.platform.reconciliator.RNPlatformCheckConsistence;
import step07_reconciliation.reconciliators.platform.writefile.RNPlatformWriteFileBalance;
import step07_reconciliation.reconciliators.platform.writefile.RNPlatformWriteFileTransactions;
import step07_reconciliation.reconciliators.platform.writefile.RNPlatformWriteFileTransactionsBKCompta;

public class RNPlatform extends BKReconciliatorAbstract {

	public RNPlatform(BKReconciliatorManager _sBKReconciliatorManager) {
		super(_sBKReconciliatorManager);
		/*
		 * 
		 */
		pRNPlatformDateManager = new RNPlatformDateManager(this);
		pRNPlatformLoadFilesBalance = new RNPlatformLoadFilesBalance(this);
		pRNPlatformBalanceReconciliator = new RNPlatformBalanceReconciliator(this);
		pRNPlatformWriteFileBalance = new RNPlatformWriteFileBalance(this);
		pRNPlatformLoadCompta = new RNPlatformLoadCompta(this);
		pRNPlatformLoadFilesTransaction = new RNPlatformLoadFilesTransaction(this);
		pRNPlatformTransactionManager = new RNPlatformTransactionManager(this);
		pRNPlatformCheckConsistence = new RNPlatformCheckConsistence(this);
		pRNPlatformWriteFileTransactions = new RNPlatformWriteFileTransactions(this);
		pRNPlatformWriteFileTransactionsBKCompta = new RNPlatformWriteFileTransactionsBKCompta(this);
		pRNPlatformCheckFilesExistence = new RNPlatformCheckFilesExistence();
		pRNPlatformOutsideManager = new RNPlatformOutsideManager();
	}

	/*
	 * Data
	 */
	private RNPlatformDateManager pRNPlatformDateManager;
	private RNPlatformLoadFilesBalance pRNPlatformLoadFilesBalance;
	private RNPlatformBalanceReconciliator pRNPlatformBalanceReconciliator;
	private RNPlatformWriteFileBalance pRNPlatformWriteFileBalance;
	private RNPlatformLoadCompta pRNPlatformLoadCompta;
	private RNPlatformLoadFilesTransaction pRNPlatformLoadFilesTransaction;
	private RNPlatformTransactionManager pRNPlatformTransactionManager;
	private RNPlatformCheckConsistence pRNPlatformCheckConsistence;
	private RNPlatformWriteFileTransactions pRNPlatformWriteFileTransactions;
	private RNPlatformWriteFileTransactionsBKCompta pRNPlatformWriteFileTransactionsBKCompta;
	private RNPlatformCheckFilesExistence pRNPlatformCheckFilesExistence;
	private RNPlatformOutsideManager pRNPlatformOutsideManager;
	
	@Override public String getpDetailsOfChecksPerformed() {
		if (BKStaticConst.getIS_SKIP_RECONCILIATION_PLATORM()) {
			return "Skipped due to debug mode";
		}
		return "Holdings Website = Holdings Compta";
	}

	public void computeIsPassTest(List<Integer> _sListDateToReconcile) {
		if (BKStaticConst.getIS_SKIP_RECONCILIATION_PLATORM()) {
			return;
		}
		/*
		 * Load files & COMPTA
		 */
		pRNPlatformCheckFilesExistence.run();
		pRNPlatformLoadFilesBalance.run();
		pRNPlatformLoadFilesTransaction.run();
		pRNPlatformLoadCompta.run();
		/*
		 * Write file output in any case
		 */
		pRNPlatformWriteFileTransactions.run();
		pRNPlatformWriteFileTransactionsBKCompta.run();
		/*
		 * Reconcile
		 */
//		pRNPlatformCheckConsistence.run();
		pRNPlatformBalanceReconciliator.run();
		/*
		 * Write file output if all passed with success
		 */
		pRNPlatformWriteFileBalance.run(null);
	}

	/*
	 * Getters & Setters
	 */
	public final RNPlatformDateManager getpRNPlatformDateManager() {
		return pRNPlatformDateManager;
	}
	public final RNPlatformLoadFilesBalance getpRNPlatformLoadFilesBalance() {
		return pRNPlatformLoadFilesBalance;
	}
	public final RNPlatformBalanceReconciliator getpRNPlatformBalanceReconciliator() {
		return pRNPlatformBalanceReconciliator;
	}
	public final RNPlatformWriteFileBalance getpRNPlatformWriteFile() {
		return pRNPlatformWriteFileBalance;
	}
	public final RNPlatformLoadCompta getpRNPlatformLoadCompta() {
		return pRNPlatformLoadCompta;
	}
	public final RNPlatformTransactionManager getpRNPlatformTransactionManager() {
		return pRNPlatformTransactionManager;
	}
	public final RNPlatformLoadFilesTransaction getpRNPlatformLoadFilesTransaction() {
		return pRNPlatformLoadFilesTransaction;
	}
	public final RNPlatformWriteFileTransactionsBKCompta getpRNPlatformWriteFileTransactionsBKCompta() {
		return pRNPlatformWriteFileTransactionsBKCompta;
	}
	public final RNPlatformWriteFileBalance getpRNPlatformWriteFileBalance() {
		return pRNPlatformWriteFileBalance;
	}
	public final RNPlatformCheckConsistence getpRNPlatformCheckConsistence() {
		return pRNPlatformCheckConsistence;
	}
	public final RNPlatformWriteFileTransactions getpRNPlatformWriteFileTransactions() {
		return pRNPlatformWriteFileTransactions;
	}
	public final RNPlatformCheckFilesExistence getpRNPlatformCheckFilesExistence() {
		return pRNPlatformCheckFilesExistence;
	}
	public final RNPlatformOutsideManager getpRNPlatformOutsideManager() {
		return pRNPlatformOutsideManager;
	}

	
	
	
	
}
