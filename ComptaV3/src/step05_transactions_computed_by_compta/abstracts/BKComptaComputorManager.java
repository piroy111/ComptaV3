package step05_transactions_computed_by_compta.abstracts;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import basicmethods.BasicDateInt;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.ReadFile;
import basicmethods.ReadFile.comReadFile;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step04_debug.pull_out_bars_in_error.DGPullOutBarsInError;
import step05_transactions_computed_by_compta.computors.BKComptaComputorCostOfPositionCurrencies;
import step05_transactions_computed_by_compta.computors.BKComptaComputorDrawCommercial;
import step05_transactions_computed_by_compta.computors.BKComptaComputorLoansCostOfBarsFromPRoy;
import step05_transactions_computed_by_compta.computors.BKComptaComputorLoansCostOfCurrenciesFromPRoy;
import step05_transactions_computed_by_compta.computors.BKComptaComputorLoansOfBarsFromProy;
import step05_transactions_computed_by_compta.computors.BKComptaComputorLoansOfBarsReturnToProy;
import step05_transactions_computed_by_compta.computors.BKComptaComputorProvisionsLeasing;
import step05_transactions_computed_by_compta.computors.BKComptaComputorStorageFees;
import step10_launchme.BKLaunchMe;
import tictoc.BasicTicToc;

public class BKComptaComputorManager {

	public BKComptaComputorManager(BKLaunchMe _sBKLaunchMe) {
		pBKLaunchMe = _sBKLaunchMe;
		/*
		 * 
		 */
		pListBKComptaComputorAbstract = new ArrayList<>();
		createBKComptaComputor();
		loadDateFreeze();
	}

	/*
	 * Data
	 */
	private BKLaunchMe pBKLaunchMe;
	private List<BKComptaComputorAbstract> pListBKComptaComputorAbstract;
	private TreeMap<Integer, List<Integer>> pTreeMapDateFileToListDateInFile;
	private BKComptaComputorLoansOfBarsFromProy pBKComptaComputorLoansOfBarsFromProy;
	private BKComptaComputorLoansOfBarsReturnToProy pBKComptaComputorLoansOfBarsReturnToProy;
	private BKComptaComputorStorageFees pBKComptaComputorStorageFees;
	private BKComptaComputorLoansCostOfCurrenciesFromPRoy pBKComptaComputorLoansOfCurrenciesFromPRoy;
	private BKComptaComputorCostOfPositionCurrencies pBKComptaComputorCostOfPositionCurrencies;
	private BKComptaComputorLoansCostOfBarsFromPRoy pBKComptaComputorLoansCostOfBarsFromPRoy;
	private BKComptaComputorProvisionsLeasing pBKComptaComputorProvisionsLeasing;
	private BKComptaComputorDrawCommercial pBKComptaComputorDrawCommercial;

	/**
	 * To be filled by the programmer<br>
	 * The order of instantiation is important<br>
	 */
	private void createBKComptaComputor() {
		/*
		 * First we create or offset loans in order to have the correct ownership of bars
		 */
		pBKComptaComputorLoansOfBarsFromProy = new BKComptaComputorLoansOfBarsFromProy(this);
		pBKComptaComputorLoansOfBarsReturnToProy = new BKComptaComputorLoansOfBarsReturnToProy(this);
		/*
		 * Then we count the cost of storage and the cost of loan of bars from PRoy
		 */
		pBKComptaComputorStorageFees = new BKComptaComputorStorageFees(this);
		pBKComptaComputorLoansCostOfBarsFromPRoy = new BKComptaComputorLoansCostOfBarsFromPRoy(this);
		/*
		 * We compute the amount of currency detained by each account and the associated cost (negative interest rate)<br>
		 */
		pBKComptaComputorCostOfPositionCurrencies = new BKComptaComputorCostOfPositionCurrencies(this);
		/*
		 * Then we deduce the amount of currency borrowed from PRoy to finance Bunker negative balance sheet
		 */
		pBKComptaComputorLoansOfCurrenciesFromPRoy = new BKComptaComputorLoansCostOfCurrenciesFromPRoy(this);
		/*
		 * We count the estimated gain from leasing for Bunker and we switch it with provisions
		 */
		pBKComptaComputorProvisionsLeasing = new BKComptaComputorProvisionsLeasing(this); 
		/*
		 * We transfer the limit of the draw commercial
		 */
		pBKComptaComputorDrawCommercial = new BKComptaComputorDrawCommercial(this);
	}

	/**
	 * 
	 */
	private void loadDateFreeze() {
		/*
		 * Read file
		 */
		String lDir = BKStaticDir.getCONF();
		String lNameFile = BKStaticNameFile.getCONF_DATE_FREEZE();
		ReadFile lReadFile = new ReadFile(lDir, lNameFile, comReadFile.FULL_COM);
		/*
		 * Read file
		 */
		for (List<String> lLineList : lReadFile.getmContentList()) {
			if (lLineList.size() < 2) {
				BKCom.error("The line is too short"
						+ "\nLine in error= '" + lLineList
						+ "\nFile in error= '" + lReadFile.getmDirPlusNameFile() + "'");
			}
			int lIdx = -1;
			String lNameComputor = lLineList.get(++lIdx);
			int lDate = BasicString.getInt(lLineList.get(++lIdx));
			/*
			 * Check error
			 */
			if (lDate < 19000101 || lDate > 30000101) {
				BKCom.error("The file contains a date which is incorrect"
						+ "Date= '" + lDate + "'"
						+ "\nLine in error= '" + lLineList
						+ "\nFile in error= '" + lReadFile.getmDirPlusNameFile() + "'");
			}
			if (lDate != BasicDateInt.getmEndOfMonth(lDate)) {
				BKCom.error("All the dates should be the last date of their month"
						+ "Date= '" + lDate + "'"
						+ "\nLine in error= '" + lLineList
						+ "\nFile in error= '" + lReadFile.getmDirPlusNameFile() + "'");
			}
			/*
			 * Assign date freeze
			 */
			boolean lIsFound = false;
			for (BKComptaComputorAbstract lBKComptaComputorAbstract : pListBKComptaComputorAbstract) {
				if (lNameComputor.equals(lBKComptaComputorAbstract.getpName())) {
					lBKComptaComputorAbstract.setpDateFreeze(lDate);
					lIsFound = true;
					break;
				}
			}
			/*
			 * Case the name is unknown
			 */
			if (!lIsFound) {
				BKCom.error("Unknown computor given from the conf file"
						+ "Computor= '" + lNameComputor + "'"
						+ "\nLine in error= '" + lLineList
						+ "\nFile in error= '" + lReadFile.getmDirPlusNameFile() + "'");
			}
		}
		/*
		 * Check all COMPUTORS have been assigned a freeze date
		 */
		for (BKComptaComputorAbstract lBKComptaComputorAbstract : pListBKComptaComputorAbstract) {
			if (lBKComptaComputorAbstract.getpDateFreeze() == null) {
				BKCom.error("Missing computor in the conf file. you must add a line to the conf file with the computor and its freezing date"
						+ "Computor= '" + lBKComptaComputorAbstract.getpName() + "'"
						+ "\nFile in error= '" + lReadFile.getmDirPlusNameFile() + "'");
			}
		}
		/*
		 * Set the static date freeze of COMPTA
		 */
		int lDateFreezeMin = Integer.MAX_VALUE;
		for (BKComptaComputorAbstract lBKComptaComputorAbstract : pListBKComptaComputorAbstract) {
			lDateFreezeMin = Math.min(lDateFreezeMin, lBKComptaComputorAbstract.getpDateFreeze());
		}
		BKStaticConst.setDATE_FREEZE_COMPTA_MIN(lDateFreezeMin);
		/*
		 * Display
		 */
		for (BKComptaComputorAbstract lBKComptaComputorAbstract : pListBKComptaComputorAbstract) {
			BasicPrintMsg.display(this, lBKComptaComputorAbstract.getpName() + " --> Date freeze= " + lBKComptaComputorAbstract.getpDateFreeze());
		}
	}

	/**
	 * 
	 */
	public final void removeFilesOfTheMonth() {
		pTreeMapDateFileToListDateInFile = new TreeMap<>();
		for (BKComptaComputorAbstract lBKComptaComputorAbstract : pListBKComptaComputorAbstract) {
			/*
			 * 
			 */
			lBKComptaComputorAbstract.removeFileOfTheMonth();
			/*
			 * Build a common TreeMap of all the dates
			 */
			for (int lDateFile : lBKComptaComputorAbstract.getpTreeMapDateFileToListDateInFile().keySet()) {
				List<Integer> lListDateInFile = lBKComptaComputorAbstract.getpTreeMapDateFileToListDateInFile().get(lDateFile);
				List<Integer> lListDate = pTreeMapDateFileToListDateInFile.get(lDateFile);
				if (lListDate == null) {
					lListDate = new ArrayList<>();
					pTreeMapDateFileToListDateInFile.put(lDateFile, lListDate);
				}
				for (int lDate : lListDateInFile) {
					if (!lListDate.contains(lDate)) {
						lListDate.add(lDate);
					}
				}
			}
		}		
	}

	/**
	 * 
	 * @param _sBKComptaComputorAbstract
	 */
	protected final void declareNewBKComptaComputorAbstract(BKComptaComputorAbstract _sBKComptaComputorAbstract) {
		if (!pListBKComptaComputorAbstract.contains(_sBKComptaComputorAbstract)) {
			pListBKComptaComputorAbstract.add(_sBKComptaComputorAbstract);
		}
	}

	/**
	 * 
	 */
	public final void computeAndWriteFileOfMonth() {
		/*
		 * Debug in case we want to trace the BKBars in error before computing the transactions of COMPTA
		 */
		new DGPullOutBarsInError(pBKLaunchMe).run();
		/*
		 * Communication
		 */
		BasicPrintMsg.displaySuperTitle(this, "Compute the transactions generated by the COMPTA + writes the corresponding files");
		BasicTicToc.Start(this);
		/*
		 * Communication
		 */
		for (BKComptaComputorAbstract lBKComptaComputorAbstract : pListBKComptaComputorAbstract) {
			lBKComptaComputorAbstract.initiateGlobal();
		}
		/*
		 * Create and write one file per month
		 */
		for (int lDateFile : pTreeMapDateFileToListDateInFile.keySet()) {
			/*
			 * Initiate: load object once for the whole month
			 */
			List<Integer> lListDateInFile = pTreeMapDateFileToListDateInFile.get(lDateFile);
			for (BKComptaComputorAbstract lBKComptaComputorAbstract : pListBKComptaComputorAbstract) {
				if (lBKComptaComputorAbstract.getpIsFrozen(lDateFile)) {
					continue;
				}
				lBKComptaComputorAbstract.resetpListBKTransactionNew();
				lBKComptaComputorAbstract.initiateMonth();
			}
			/*
			 * Loop on all the dates inside one file and create new BKTransactions (loans of bars, return of loans of bars)
			 */
			for (int lDate : lListDateInFile) {
				for (BKComptaComputorAbstract lBKComptaComputorAbstract : pListBKComptaComputorAbstract) {
					if (lBKComptaComputorAbstract.getpIsFrozen(lDateFile)) {
						continue;
					}
					lBKComptaComputorAbstract.computeNewTransactionsDaily(lDate);
				}
			}
			/*
			 * Create the BKTransactions end of month  (storage, loans of currency, etc.)
			 */
			for (BKComptaComputorAbstract lBKComptaComputorAbstract : pListBKComptaComputorAbstract) {
				if (lBKComptaComputorAbstract.getpIsFrozen(lDateFile)) {
					continue;
				}
				lBKComptaComputorAbstract.computeNewTransactionsMonthly(lDateFile);
			}
			/*
			 * Check errors and write file with all the new BKTransactions of this month
			 */
			for (BKComptaComputorAbstract lBKComptaComputorAbstract : pListBKComptaComputorAbstract) {
				if (lBKComptaComputorAbstract.getpIsFrozen(lDateFile)) {
					continue;
				}
				lBKComptaComputorAbstract.checkErrorsAndWriteFile(lDateFile);
			}
		}
		/*
		 * End -> we forbid the creation of new BKTransaction
		 */
		BasicTicToc.Stop(this);
	}

	/*
	 * Getters & Setters
	 */
	public final BKLaunchMe getpBKLaunchMe() {
		return pBKLaunchMe;
	}
	public final List<BKComptaComputorAbstract> getpListBKComptaComputorAbstract() {
		return pListBKComptaComputorAbstract;
	}
	public final BKComptaComputorLoansOfBarsFromProy getpBKComptaComputorLoansOfBarsFromProy() {
		return pBKComptaComputorLoansOfBarsFromProy;
	}
	public final BKComptaComputorLoansOfBarsReturnToProy getpBKComptaComputorLoansOfBarsReturnToProy() {
		return pBKComptaComputorLoansOfBarsReturnToProy;
	}
	public final BKComptaComputorStorageFees getpBKComptaComputorStorageFees() {
		return pBKComptaComputorStorageFees;
	}
	public final BKComptaComputorLoansCostOfCurrenciesFromPRoy getpBKComptaComputorLoansOfCurrenciesFromPRoy() {
		return pBKComptaComputorLoansOfCurrenciesFromPRoy;
	}
	public final BKComptaComputorCostOfPositionCurrencies getpBKComptaComputorCostOfPositionCurrencies() {
		return pBKComptaComputorCostOfPositionCurrencies;
	}
	public final BKComptaComputorLoansCostOfBarsFromPRoy getpBKComptaComputorLoansCostOfBarsFromPRoy() {
		return pBKComptaComputorLoansCostOfBarsFromPRoy;
	}
	public BKComptaComputorProvisionsLeasing getpBKComptaComputorProvisionsLeasing() {
		return pBKComptaComputorProvisionsLeasing;
	}
	public final BKComptaComputorDrawCommercial getpBKComptaComputorDrawCommercial() {
		return pBKComptaComputorDrawCommercial;
	}

}
