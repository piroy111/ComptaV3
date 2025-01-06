package step09_fiscal_year_end.step04_outputfiles.statement_income;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import staticdata.datas.BKStaticConst;
import step09_fiscal_year_end.step01_amortization.per_year.FYAmortizationYear;
import step09_fiscal_year_end.step02_income.objects.FYIncomeFY;
import step09_fiscal_year_end.step02_income.objects.FYIncomeFYGroup;
import step09_fiscal_year_end.step04_outputfiles.abstracts.FYOutputFileAbstract;
import step09_fiscal_year_end.step04_outputfiles.abstracts.FYOutputFileManager;

public class FYOutput_IncomeStatement extends FYOutputFileAbstract {

	public FYOutput_IncomeStatement(FYOutputFileManager _sFYOutputFileManager) {
		super(_sFYOutputFileManager);
	}

	/*
	 * Data
	 */
	private double pFYIncome;
	private double pFYProvision;
	
	
	@Override public void buildFileContent() {
		/*
		 * Initiate
		 */
		pFYIncome = 0.;
		pFYProvision = 0.;
		double lFYProvisionPrevious = 0.;
		double lIncomePrevious = 0.;
		int lYearCurrent = pFYManager.getpFYDateManager().getpYearFYCurrent();
		int lYearPrevious = pFYManager.getpFYDateManager().getpYearFYPrevious();
		String lIndentPrevious = ",,,,";
		/*
		 * Header
		 */
		addNewHeader("BKIncomeFYGroup,BKIncomeFY,BKIncome");
		addNewHeader(",FY " + lYearCurrent);
		addNewHeader(lIndentPrevious + "FY " + lYearPrevious);
		/*
		 * Sort in reverse order to have revenue first and cost after
		 */
		List<FYIncomeFYGroup> lListFYIncomeFYGroup = new ArrayList<>(pFYManager.getpFYIncomeManager().getpTreeMapNameToFYIncomeFYGroup().values());
		Collections.reverse(lListFYIncomeFYGroup);
		/*
		 * Write revenue + cost + expenses
		 */
		for (FYIncomeFYGroup lFYIncomeFYGroup : lListFYIncomeFYGroup) {
			if (lFYIncomeFYGroup.getpName().toUpperCase().contains("CAPITAL")) {
				continue;
			}
			if (lFYIncomeFYGroup.getpName().equals(BKStaticConst.getFYE_PROVISION_GROUP())) {
				pFYProvision += lFYIncomeFYGroup.getpIncomeCurrent();
				lFYProvisionPrevious += lFYIncomeFYGroup.getpIncomePrevious();
//				continue;
			}
			addNewLineToWrite("");
			addNewLineToWrite(lFYIncomeFYGroup.getpName());
			for (FYIncomeFY lFYIncomeFY : lFYIncomeFYGroup.getpTreeMapNameToFYIncomeFY().values()) {
				addNewLineToWrite("," + lFYIncomeFY.getpName()
				+ ",," + lFYIncomeFY.getpIncomeCurrent()
				+ lIndentPrevious + lFYIncomeFY.getpIncomePrevious());
			}
			addNewLineToWrite(lFYIncomeFYGroup.getpName()
					+ ",,,," + lFYIncomeFYGroup.getpIncomeCurrent()
					+ lIndentPrevious + lFYIncomeFYGroup.getpIncomePrevious());
			pFYIncome += lFYIncomeFYGroup.getpIncomeCurrent();
			lIncomePrevious += lFYIncomeFYGroup.getpIncomePrevious();
		}
		/*
		 * Write Amortization
		 */
		FYAmortizationYear lFYAmortizationYearCurrent = pFYManager.getpFYAmortizationManager().getpFYAmortizationYearManager().getpOrCreateFYAmortizationYear(lYearCurrent);
		FYAmortizationYear lFYAmortizationYearPrevious = pFYManager.getpFYAmortizationManager().getpFYAmortizationYearManager().getpOrCreateFYAmortizationYear(lYearPrevious);
		addNewLineToWrite("");
		addNewLineToWrite("Adjustement due to amortization"
				+ ",,,," + lFYAmortizationYearCurrent.getpAdjustmentToFYIncome()
				+ lIndentPrevious + lFYAmortizationYearPrevious.getpAdjustmentToFYIncome());
		pFYIncome += lFYAmortizationYearCurrent.getpAdjustmentToFYIncome();
		lIncomePrevious += lFYAmortizationYearPrevious.getpAdjustmentToFYIncome();
		/*
		 * Write total
		 */
		addNewLineToWrite("");
		addNewLineToWrite("Grand total - P/L"
				+ ",,,,," + pFYIncome
				+ lIndentPrevious + lIncomePrevious);
		addNewLineToWrite("Grand total - Provisions"
				+ ",,,,," + pFYProvision
				+ lIndentPrevious + lFYProvisionPrevious);
	}
	/*
	 * Getters & Setters
	 */
	public final double getpFYIncome() {
		return pFYIncome;
	}
	public final double getpFYProvision() {
		return pFYProvision;
	}
	public final double getpFYIncomeMinusProvision() {
		return pFYIncome - pFYProvision;
	}

	
	
	
	
	
	
	
}
