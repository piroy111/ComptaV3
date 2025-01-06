package step07_reconciliation.abstracts;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicDateInt;
import basicmethods.BasicFichiers;
import basicmethods.BasicPrintMsg;
import staticdata.com.fileswritten.BKComOnFilesWritten;
import staticdata.datas.BKStaticConst.com_file_written;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step03_partitions.abstracts.objects.BKPartitionManager;
import step07_reconciliation.reconciliators.bars.RNBKBars;
import step07_reconciliation.reconciliators.entity.RNBKEntity;
import step07_reconciliation.reconciliators.entity.RNBKEntityBKBar;
import step07_reconciliation.reconciliators.entitytransfer.RNBKEntityTransfer;
import step07_reconciliation.reconciliators.filesarepresentforbrokers.RNFilesFromOndaAndIbArePresent;
import step07_reconciliation.reconciliators.filesarepresentforphysicalopened.RNFilesPhysicalOpenedArePresent;
import step07_reconciliation.reconciliators.filesfromwebsitearepresent.RNFilesFromWebSiteArePresent;
import step07_reconciliation.reconciliators.loans.RNBKLoans;
import step07_reconciliation.reconciliators.platform.RNPlatform;
import step07_reconciliation.reconciliators.priceshistoareupdated.RNPricesHistoUpdated;
import step07_reconciliation.reconciliators.vaults.RNVault;
import step10_launchme.BKLaunchMe;

public class BKReconciliatorManager {

	public BKReconciliatorManager(BKLaunchMe _sBKLaunchMe) {
		pBKLaunchMe = _sBKLaunchMe;
		/*
		 * 
		 */
		pBKPartitionManager = pBKLaunchMe.getpBKPartitionManager();
		pListBKReconciliatorAbstract = new ArrayList<>();
		declareReconciliator();
	}
	
	/*
	 * Data
	 */
	private BKLaunchMe pBKLaunchMe;
	private BKPartitionManager pBKPartitionManager;
	private List<BKReconciliatorAbstract> pListBKReconciliatorAbstract;
	
	/**
	 * 
	 */
	private void declareReconciliator() {
		new RNFilesFromWebSiteArePresent(this);
		new RNFilesFromOndaAndIbArePresent(this);
		new RNFilesPhysicalOpenedArePresent(this);
		new RNPricesHistoUpdated(this);
//		new RNFiscalYearEndSamePastIncome(this);
		new RNBKEntityTransfer(this);
		new RNBKBars(this);
		new RNBKEntityBKBar(this);
		new RNBKEntity(this);
		new RNBKLoans(this);
		new RNVault(this);
		new RNPlatform(this);
	}

	/**
	 * 
	 */
	public final void run() {
		BasicPrintMsg.displaySuperTitle(this, "CHECKS and RECONCILIATIONS");
		/*
		 * Prepare file to write
		 */
		String lDir = BKStaticDir.getOUTPUT_RECONCILIATION_MAIN();
		BasicFichiers.getOrCreateDirectory(lDir);
		String lNameFile = BasicDateInt.getmToday() + BKStaticNameFile.getSUFFIX_RECONCILIATOR();
		String lHeader = "Check performed,Result of the test";
		List<String> lListLineToWrite = new ArrayList<>();
		/*
		 * Test and write file output each time a test is finished (so we can see the result even if there is a crash)
		 */
		for (BKReconciliatorAbstract lBKReconciliatorAbstract : pListBKReconciliatorAbstract) {
			String lNameClass = lBKReconciliatorAbstract.getClass().getSimpleName();
			/*
			 * Conduct tests
			 */
			BasicPrintMsg.displayTitle(lBKReconciliatorAbstract, lBKReconciliatorAbstract.toString());
			BasicPrintMsg.display(lBKReconciliatorAbstract, lBKReconciliatorAbstract.getpDetailsOfChecksPerformed());
			lBKReconciliatorAbstract.computeIsPassTest();
			/*
			 * 
			 */
			String lTitle = lBKReconciliatorAbstract.getpDetailsOfChecksPerformed();
			String[] lArrayMsg = lTitle.split("\n", -1);
			for (String lMsg : lArrayMsg) {
				BasicPrintMsg.display(this, lMsg + " --> Ok");
			}
			/*
			 * Write file output
			 */
			String lTitleClass = lNameClass.substring(2);
			for (String lMsg : lArrayMsg) {
				lListLineToWrite.add(lTitleClass + "," + lMsg + "," + "Ok");
			}
			BKComOnFilesWritten.writeFile(com_file_written.Reconciliator, lDir, lNameFile, lHeader, lListLineToWrite);
		}
	}

	/**
	 * 
	 * @param _sBKReconciliatorAbstract
	 */
	protected final void declareNewBKReconciliatorAbstract(BKReconciliatorAbstract _sBKReconciliatorAbstract) {
		pListBKReconciliatorAbstract.add(_sBKReconciliatorAbstract);
	}
	
	/*
	 * Getters & Setters
	 */
	public final BKPartitionManager getpBKPartitionManager() {
		return pBKPartitionManager;
	}
	public final BKLaunchMe getpBKLaunchMe() {
		return pBKLaunchMe;
	}
	
	
	
	
}
