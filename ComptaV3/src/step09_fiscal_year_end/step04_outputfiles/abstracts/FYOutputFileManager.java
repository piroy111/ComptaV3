package step09_fiscal_year_end.step04_outputfiles.abstracts;

import basicmethods.BasicPrintMsg;
import step09_fiscal_year_end.FYManager;
import step09_fiscal_year_end.step04_outputfiles.amortization.FYOutput_Amortization;
import step09_fiscal_year_end.step04_outputfiles.statement_balancesheet.FYOutput_BalanceSheetStatement;
import step09_fiscal_year_end.step04_outputfiles.statement_balancesheet.FYOutput_BalanceSheetStatementAssetsBunker;
import step09_fiscal_year_end.step04_outputfiles.statement_balancesheet.FYOutput_BalanceSheetStatementAssetsDetailed;
import step09_fiscal_year_end.step04_outputfiles.statement_balancesheet.FYOutput_BalanceSheetStatementLiabilitiesDetailed;
import step09_fiscal_year_end.step04_outputfiles.statement_income.FYOutput_IncomeStatement;
import step09_fiscal_year_end.step04_outputfiles.statement_income.FYOutput_IncomeStatementDetailed;

public class FYOutputFileManager {

	public FYOutputFileManager(FYManager _sFYManager) {
		pFYManager = _sFYManager;
	}
	
	/*
	 * Data
	 */
	private FYManager pFYManager;
	private FYOutput_IncomeStatement pFYOutput_IncomeStatement;
	
	
	/**
	 * 
	 */
	public final void writeFilesRelatedToIncome() {
		BasicPrintMsg.displayTitle(this, "Write files output related to income");
		/*
		 * Files for debug
		 */
		new FYOutput_Amortization(this).writeFile();
		new FYOutput_IncomeStatementDetailed(this).writeFile();
		/*
		 * File for report
		 */
		pFYOutput_IncomeStatement = new FYOutput_IncomeStatement(this);
		pFYOutput_IncomeStatement.writeFile();		
	}
	
	/**
	 * 
	 */
	public final void writeFilesRelatedToBalanceSheet() {
		BasicPrintMsg.displayTitle(this, "Write files output related to balance sheet");
		/*
		 * Files for debug
		 */
		new FYOutput_BalanceSheetStatementAssetsBunker(this).writeFile();
		new FYOutput_BalanceSheetStatementAssetsDetailed(this).writeFile();
		new FYOutput_BalanceSheetStatementLiabilitiesDetailed(this).writeFile();
		/*
		 * Files for report
		 */
		new FYOutput_BalanceSheetStatement(this).writeFile();
	}

	/*
	 * Getters & Setters
	 */
	public final FYManager getpFYManager() {
		return pFYManager;
	}


	public final FYOutput_IncomeStatement getpFYOutput_IncomeStatement() {
		return pFYOutput_IncomeStatement;
	}
	
}
