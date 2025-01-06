package step09_fiscal_year_end.step04_outputfiles.statement_income;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import step09_fiscal_year_end.step02_income.objects.FYIncome;
import step09_fiscal_year_end.step02_income.objects.FYIncomeFY;
import step09_fiscal_year_end.step02_income.objects.FYIncomeFYGroup;
import step09_fiscal_year_end.step04_outputfiles.abstracts.FYOutputFileAbstract;
import step09_fiscal_year_end.step04_outputfiles.abstracts.FYOutputFileManager;

public class FYOutput_IncomeStatementDetailed extends FYOutputFileAbstract {

	public FYOutput_IncomeStatementDetailed(FYOutputFileManager _sFYOutputFileManager) {
		super(_sFYOutputFileManager);
	}

	@Override public void buildFileContent() {
		/*
		 * Header
		 */
		addNewHeader("BKIncomeFYGroup,BKIncomeFY,BKIncome");
		addNewHeader(",FY " + pFYManager.getpFYDateManager().getpYearFYCurrent());
		addNewHeader(",,,FY " + pFYManager.getpFYDateManager().getpYearFYPrevious());
		/*
		 * Sort in reverse order to have revenue first and cost after
		 */
		List<FYIncomeFYGroup> lListFYIncomeFYGroup = new ArrayList<>(pFYManager.getpFYIncomeManager().getpTreeMapNameToFYIncomeFYGroup().values());
		Collections.reverse(lListFYIncomeFYGroup);
		/*
		 * 
		 */
		for (FYIncomeFYGroup lFYIncomeFYGroup : lListFYIncomeFYGroup) {
			if (lFYIncomeFYGroup.getpName().toUpperCase().contains("CAPITAL")) {
				continue;
			}
			addNewLineToWrite("");
			addNewLineToWrite(lFYIncomeFYGroup.getpName());
			for (FYIncomeFY lFYIncomeFY : lFYIncomeFYGroup.getpTreeMapNameToFYIncomeFY().values()) {
				addNewLineToWrite("," + lFYIncomeFY.getpName());
				for (FYIncome lFYIncome : lFYIncomeFY.getpTreeMapNameToFYIncome().values()) {
					addNewLineToWrite(",," + lFYIncome.getpName()
					+ "," + lFYIncome.getpIncomeCurrent()
					+ ",,,," + lFYIncome.getpIncomePrevious());
				}
				addNewLineToWrite("," + lFYIncomeFY.getpName()
				+ ",,," + lFYIncomeFY.getpIncomeCurrent()
				+ ",,,," + lFYIncomeFY.getpIncomePrevious());
			}
			addNewLineToWrite(lFYIncomeFYGroup.getpName()
					+ ",,,,," + lFYIncomeFYGroup.getpIncomeCurrent()
					+ ",,,," + lFYIncomeFYGroup.getpIncomePrevious());
		}
	}

}
