package step07_reconciliation.reconciliators.fiscalyearendpreviousincome;

import java.util.List;
import java.util.TreeMap;

import basicmethods.BasicDateInt;
import basicmethods.BasicPrintMsg;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.income.BKIncome;
import step01_objects_from_conf_files.income.BKIncomeManager;
import step03_partitions.partitions.BKPartitionPerBKAccount;
import step03_partitions.partitions.BKPartitionPerBKIncomeAndBKAccount;
import step07_reconciliation.abstracts.BKReconciliatorAbstract;
import step07_reconciliation.abstracts.BKReconciliatorManager;
import step09_fiscal_year_end.FYManager;
import step09_fiscal_year_end.step01_amortization.per_year.FYAmortizationYear;

public class RNFiscalYearEndSamePastIncome extends BKReconciliatorAbstract {

	public RNFiscalYearEndSamePastIncome(BKReconciliatorManager _sBKReconciliatorManager) {
		super(_sBKReconciliatorManager);
	}

	@Override public String getpDetailsOfChecksPerformed() {
		return "Still gives the same past FYIncome";
	}

	@Override public void computeIsPassTest(List<Integer> _sListDateToReconcile) {
		/*
		 * Initiate
		 */
		BKAccount lBKAccountBunker = BKAccountManager.getpBKAccountBunker();
		BKIncome lBKIncomeCapital = BKIncomeManager.getpAndCheckBKIncome(BKStaticConst.getBKINCOME_CAPITAL(), this);
		String lKey = BKPartitionPerBKIncomeAndBKAccount.getKey(lBKIncomeCapital, lBKAccountBunker);
		String lKeyBunker = BKPartitionPerBKAccount.getKey(lBKAccountBunker);
		FYManager lFYManager = pBKReconciliatorManager.getpBKLaunchMe().getpFYManager();
		TreeMap<Integer, Double> lTreeMapDateToFYIncome = lFYManager.getpFYPreviousFYIncomeManager().getpTreeMapFYDateToFYIncome();
		double lErrorAcceptable = lTreeMapDateToFYIncome.size() * BKStaticConst.getERROR_ACCEPTABLE_FYINCOME_PAST();
		/*
		 * 
		 */
		String lErrorMsg = "";
		for (int lFYDate : lTreeMapDateToFYIncome.keySet()) {
			double lFYIncome = lTreeMapDateToFYIncome.get(lFYDate);
			/*
			 * Retrieve the amortization
			 * The Income computed by COMPTA is the FYIncome without the amortization
			 */
			int lFYYear = BasicDateInt.getmYear(lFYDate);
			FYAmortizationYear lFYAmortizationYear = lFYManager.getpFYAmortizationManager().getpFYAmortizationYearManager().getpOrCreateFYAmortizationYear(lFYYear);
			double lAdjustementAmortization = lFYAmortizationYear.getpAdjustmentToFYIncome();
			/*
			 * Compute the P/L from the COMPTA
			 */
			int lFYDatePrevious = BasicDateInt.getmEndOfMonth(BasicDateInt.getmPlusYear(lFYDate, -1));
			double lNAVBunker = pBKPartitionManager.getpBKPartitionPerBKAccount().getpNAVNoNaN(lKeyBunker, lFYDate);
			double lNAVBunkerPrevious = pBKPartitionManager.getpBKPartitionPerBKAccount().getpNAVNoNaN(lKeyBunker, lFYDatePrevious);
			/*
			 * Find the Capital
			 */
			double lNAVCapital = pBKPartitionManager.getpBKPartitionPerBKIncomeAndBKAccount().getpNAVNoNaN(lKey, lFYDate);
			double lNAVCapitalPrevious = pBKPartitionManager.getpBKPartitionPerBKIncomeAndBKAccount().getpNAVNoNaN(lKey, lFYDatePrevious);
			/*
			 * Compute P/L according to COMPTA minus the capital
			 */
			double lPvLCompta = (lNAVBunker - lNAVCapital) - (lNAVBunkerPrevious - lNAVCapitalPrevious);
			double lPvLComptaFY = lPvLCompta + lAdjustementAmortization;
			/*
			 * Check
			 */			
			double lErrorUSD = Math.abs(lPvLComptaFY - lFYIncome);
			if (Double.isNaN(lErrorUSD) || lErrorUSD > lErrorAcceptable) {
				lErrorMsg += "\n"
						+ "\nFYDate= " + lFYDate
						+ "\n   FYIncome as per the conf file= " + lFYIncome
						+ "\n   Conf file= " + BKStaticDir.getCONF_FY_END() + BKStaticNameFile.getCONF_FYINCOME_FREEZE()
						+ "\n"
						+ "\n   FYIncome recomputed= DeltaNAV of Bunker - DeltaNAV of Capital + Adjustment due to amortization"
						+ "\n   FYIncome recomputed= " + lPvLComptaFY
						+ "\n      NAV Bunker as of " + lFYDate + "= " + (lNAVBunker - lNAVCapital)
						+ "\n      NAV Bunker as of " + lFYDatePrevious + "= " + (lNAVBunkerPrevious - lNAVCapitalPrevious)
						+ "\n      Adjustment due to amortization= " + lAdjustementAmortization
						+ "\n"
						+ "\n   Error= " + lErrorUSD + " US$"
						+ "\n   Error acceptable= " + lErrorAcceptable + " US$";
			} else {
				BasicPrintMsg.display(this, "Match FYIncome!"
						+ " FYDate= " + lFYDate
						+ "; FYIncome from file= " + lFYIncome
						+ "; FYIncome from Compta= " + (lPvLCompta + lAdjustementAmortization));
			}
		}
		if (!lErrorMsg.equals("")) {
			BKCom.error("I dont find the same FYIncome as stored in the conf file when I recompute it"
					+ lErrorMsg);
		}
	}

}
