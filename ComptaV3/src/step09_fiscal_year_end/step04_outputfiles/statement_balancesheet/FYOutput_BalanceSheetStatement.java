package step09_fiscal_year_end.step04_outputfiles.statement_balancesheet;

import basicmethods.BasicDateInt;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.account.BKAccountManager;
import step01_objects_from_conf_files.income.BKIncome;
import step01_objects_from_conf_files.income.BKIncomeManager;
import step03_partitions.partitions.BKPartitionPerBKIncomeAndBKAccount;
import step09_fiscal_year_end.step01_amortization.per_year.FYAmortizationYear;
import step09_fiscal_year_end.step03_balancesheet.assets.FYEntityAssetGroup;
import step09_fiscal_year_end.step03_balancesheet.liabilities.FYLiabilitiesGroup;
import step09_fiscal_year_end.step04_outputfiles.abstracts.FYOutputFileAbstract;
import step09_fiscal_year_end.step04_outputfiles.abstracts.FYOutputFileManager;

public class FYOutput_BalanceSheetStatement extends FYOutputFileAbstract {

	public FYOutput_BalanceSheetStatement(FYOutputFileManager _sFYOutputFileManager) {
		super(_sFYOutputFileManager);
	}

	/*
	 * Data
	 */
	private static boolean IS_PRINT_ALL_PVL = false;

	@Override public void buildFileContent() {
		/*
		 * Initiate
		 */
		int lFYYearCurrent = pFYManager.getpFYDateManager().getpYearFYCurrent();
		int lFYYearPrevious = pFYManager.getpFYDateManager().getpYearFYPrevious();
		int lFYDateCurrent = pFYManager.getpFYDateManager().getpDateFYCurrent();
		int lFYDatePrevious = pFYManager.getpFYDateManager().getpDateFYPastPrevious();
		String lIndentPrevious = ",,,,,";
		/*
		 * Intangible assets
		 */
		addNewLineToWrite("ASSETS");
		addNewLineToWrite(",Intangible Assets");
		FYAmortizationYear lFYAmortizationYearCurrent = pFYManager.getpFYAmortizationManager().getpFYAmortizationYearManager().getpOrCreateFYAmortizationYear(lFYYearCurrent);
		FYAmortizationYear lFYAmortizationYearPrevious = pFYManager.getpFYAmortizationManager().getpFYAmortizationYearManager().getpOrCreateFYAmortizationYear(lFYYearPrevious);
		addNewLineToWrite(",,Carrying from previous years"
				+ "," + lFYAmortizationYearCurrent.getpCarryingIntangibleAssets()
				+ lIndentPrevious + lFYAmortizationYearPrevious.getpCarryingIntangibleAssets());
		addNewLineToWrite(",,Purchase of intangible asset for this year:"
				+ "," + lFYAmortizationYearCurrent.getpSumIntangibleAssetsForTheYear()
				+ lIndentPrevious + lFYAmortizationYearPrevious.getpSumIntangibleAssetsForTheYear());
		double lTotalIntagibleAssetsCurrent = lFYAmortizationYearCurrent.getpCarryingIntangibleAssets() + lFYAmortizationYearCurrent.getpSumIntangibleAssetsForTheYear();
		double lTotalIntagibleAssetsPrevious = lFYAmortizationYearPrevious.getpCarryingIntangibleAssets() + lFYAmortizationYearPrevious.getpSumIntangibleAssetsForTheYear();
		addNewLineToWrite(",Total Intangible Assets"
				+ ",,," + lTotalIntagibleAssetsCurrent
				+ lIndentPrevious + lTotalIntagibleAssetsPrevious);
		/*
		 * Assets
		 */
		addNewLineToWrite("");
		addNewLineToWrite(",Current Assets");
		double lTotalCurrentAssetsCurrent = 0.;
		double lTotalCurrentAssetsPrevious = 0.;
		for (FYEntityAssetGroup lFYEntityAssetGroup : pFYManager.getpFYBalanceSheetManager()
				.getpFYEntityAssetGroupManager().getpTreeMapNameToFYEntityAssetGroup().values()) {
			double lNAVCurrent = lFYEntityAssetGroup.getpNAVCurrent();
			double lNAVPrevious = lFYEntityAssetGroup.getpNAVPrevious();
			if (getpIsNotZero(lNAVCurrent) || getpIsNotZero(lNAVPrevious)) {
				lTotalCurrentAssetsCurrent += lNAVCurrent;
				lTotalCurrentAssetsPrevious += lNAVPrevious;
				addNewLineToWrite(",," + lFYEntityAssetGroup.getpName()
				+ "," + lNAVCurrent
				+ lIndentPrevious + lNAVPrevious);
			}
		}
		/*
		 * Write total of current assets
		 */
		addNewLineToWrite(",Total Current Assets"
				+ ",,," + lTotalCurrentAssetsCurrent
				+ lIndentPrevious + lTotalCurrentAssetsPrevious);
		/*
		 * Write total of assets
		 */
		addNewLineToWrite("TOTAL ASSETS"
				+ ",,,,," + (lTotalCurrentAssetsCurrent + lTotalIntagibleAssetsCurrent)
				+ lIndentPrevious + (lTotalCurrentAssetsPrevious + lTotalIntagibleAssetsPrevious));
		/*
		 * Liabilities
		 */
		addNewLineToWrite("");
		addNewLineToWrite("LIABILITIES");
		addNewLineToWrite(",Current Liabilities");
		double lTotalLiabilityCurrent = 0.;
		double lTotalLiabilityPrevious = 0.;
		for (FYLiabilitiesGroup lFYLiabilitiesGroup : pFYManager.getpFYBalanceSheetManager()
				.getpFYLiabilitiesGroupManager().getpTreeMapNameToFYLiabilitiesGroup().values()) {
			double lLiabilityCurrent = lFYLiabilitiesGroup.getpNAVCurrent();
			double lLiabilityPrevious = lFYLiabilitiesGroup.getpNAVPrevious();
			if (getpIsNotZero(lLiabilityCurrent) || getpIsNotZero(lLiabilityPrevious)) {
				lTotalLiabilityCurrent += lLiabilityCurrent;
				lTotalLiabilityPrevious += lLiabilityPrevious;
				addNewLineToWrite(",," + lFYLiabilitiesGroup.getpName()
				+ "," + lLiabilityCurrent
				+ lIndentPrevious + lLiabilityPrevious);
			}
		}
		addNewLineToWrite(",Total Current Liabilities"
				+ ",,," + lTotalLiabilityCurrent
				+ lIndentPrevious + lTotalLiabilityPrevious);
		/*
		 * Capital
		 */
		addNewLineToWrite("");
		addNewLineToWrite("CAPITAL and RESERVES");
		BKIncome lBKIncomeCapital = BKIncomeManager.getpAndCheckBKIncome(BKStaticConst.getBKINCOME_CAPITAL(), this);
		String lKey = BKPartitionPerBKIncomeAndBKAccount.getKey(lBKIncomeCapital, BKAccountManager.getpBKAccountBunker());
		double lCapitalCurrent = pFYManager.getpBKLaunchMe().getpBKPartitionManager()
				.getpBKPartitionPerBKIncomeAndBKAccount().getpNAV(lKey, lFYDateCurrent);
		double lCapitalPrevious = pFYManager.getpBKLaunchMe().getpBKPartitionManager()
				.getpBKPartitionPerBKIncomeAndBKAccount().getpNAV(lKey, lFYDatePrevious);
		addNewLineToWrite(",,Issued share capital"
				+ "," + lCapitalCurrent
				+ lIndentPrevious + lCapitalPrevious);
		/*
		 * Past FYIncome
		 */
		double lTotalRetainedEarnings = 0;
		double lTotalRetainedEarningsPrevious = 0;
		if (IS_PRINT_ALL_PVL) {
			for (int lDate : pFYManager.getpFYPreviousFYIncomeManager().getpTreeMapFYDateToFYIncome().keySet()) {
				String lMsg = ",,Retained Earnings FY ending "
						+ BasicDateInt.getmDay(lDate)
						+ " " + BasicDateInt.getmMonthName(BasicDateInt.getmMonth(lDate))
						+ " " + BasicDateInt.getmYear(lDate);
				double lFYIncome = pFYManager.getpFYPreviousFYIncomeManager().getpTreeMapFYDateToFYIncome().get(lDate);
				lMsg += "," + lFYIncome 
						+ lIndentPrevious + lFYIncome;
				lTotalRetainedEarnings += lFYIncome;
				lTotalRetainedEarningsPrevious += lFYIncome;
				addNewLineToWrite(lMsg);
			}
		} else {
			double lFYIncome = pFYManager.getpFYPreviousFYIncomeManager().getpSumPastFYIncome();
			String lMsg = ",,Sum of Retained Earnings (without provision) for prior FYs"
					+ "," + lFYIncome
					+ lIndentPrevious + "NaN";
			lTotalRetainedEarnings += lFYIncome;
			lTotalRetainedEarningsPrevious += lFYIncome;
			addNewLineToWrite(lMsg);
		}
		System.out.println("lTotalRetainedEarningsPrevious= " + lTotalRetainedEarningsPrevious);
		/*
		 * Add the current FYIncome
		 */
		double lFYIncomeThisYearMinusProvision = pFYOutputFileManager.getpFYOutput_IncomeStatement().getpFYIncomeMinusProvision();
		String lMsg = ",,Retained Earnings (without provision) FY ending "
				+ BasicDateInt.getmDay(lFYDateCurrent)
				+ " " + BasicDateInt.getmMonthName(BasicDateInt.getmMonth(lFYDateCurrent))
				+ " " + BasicDateInt.getmYear(lFYDateCurrent)
				+ "," + lFYIncomeThisYearMinusProvision
				+ lIndentPrevious + "NaN";
		lTotalRetainedEarnings += lFYIncomeThisYearMinusProvision;
		addNewLineToWrite(lMsg);
		/*
		 * Past provision
		 */
		double lFYTotalProvisionPrevious = pFYManager.getpFYPreviousFYIncomeManager().getpSumPastProvision();
		addNewLineToWrite(",,Sum of Provisions for prior FYs"
				+ "," + lFYTotalProvisionPrevious
				+ lIndentPrevious + "NaN");
		/*
		 * Current provision
		 */
		double lFYProvisionThisYear = pFYOutputFileManager.getpFYOutput_IncomeStatement().getpFYProvision();
		String lMsgProvision = ",,Provision FY ending "
				+ BasicDateInt.getmDay(lFYDateCurrent)
				+ " " + BasicDateInt.getmMonthName(BasicDateInt.getmMonth(lFYDateCurrent))
				+ " " + BasicDateInt.getmYear(lFYDateCurrent)
				+ "," + lFYProvisionThisYear
				+ lIndentPrevious + "NaN";
		addNewLineToWrite(lMsgProvision);
		/*
		 * Total capital and reserves
		 */
		double lTotalReserve = lCapitalCurrent + lTotalRetainedEarnings + lFYTotalProvisionPrevious + lFYProvisionThisYear; 
		addNewLineToWrite(",Total capital and reserves"
				+ ",,," + (lTotalReserve)
				+ lIndentPrevious + "NaN");
		/*
		 * Total liabilities and equity
		 */
		double lTotalLiabilityAndEquity = lTotalLiabilityCurrent + lTotalReserve;
		addNewLineToWrite("TOTAL LIABILITIES and EQUITY"
				+ ",,,,," + (lTotalLiabilityAndEquity)
				+ lIndentPrevious + "NaN");
		/*
		 * Write the balance
		 */
		double lBalanceCurrent = lTotalCurrentAssetsCurrent + lTotalIntagibleAssetsCurrent
				- lTotalLiabilityCurrent - lTotalReserve;
		addNewLineToWrite("");
		addNewLineToWrite("Assets-Liabilities-Equity (should be zero)"
				+ ",,,,," + lBalanceCurrent
				+ lIndentPrevious + "NaN");
	}

	/*
	 * 
	 */
	private boolean getpIsNotZero(double _sDouble) {
		return Double.isFinite(_sDouble)
				&& Math.abs(_sDouble) > 0.1;
	}

}
