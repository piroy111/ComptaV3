package step09_fiscal_year_end;

import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticDir;
import step09_fiscal_year_end.step00_loaders.FYDateManager;
import step09_fiscal_year_end.step00_loaders.FYPreviousFYIncomeManager;
import step09_fiscal_year_end.step01_amortization.FYAmortizationManager;
import step09_fiscal_year_end.step02_income.FYIncomeManager;
import step09_fiscal_year_end.step03_balancesheet.FYBalanceSheetManager;
import step09_fiscal_year_end.step04_outputfiles.abstracts.FYOutputFileManager;
import step10_launchme.BKLaunchMe;

public class FYManager {

	public static void main(String[] _sArgs) {
		BKStaticDir.detectDIR(new String[] {"G:/My Drive/Compta_bunker_v3/11_Program_to_run/"});
		new FYManager(null).run();
	}

	public FYManager(BKLaunchMe _sBKLaunchMe) {
		pBKLaunchMe = _sBKLaunchMe;
		/*
		 * 
		 */
		pFYAmortizationManager = new FYAmortizationManager(this);
		pFYOutputFileManager = new FYOutputFileManager(this);
		pFYDateManager = new FYDateManager(this);
		pFYIncomeManager = new FYIncomeManager(this);
		pFYBalanceSheetManager = new FYBalanceSheetManager(this);
		pFYPreviousFYIncomeManager = new FYPreviousFYIncomeManager(this);
	}

	/*
	 * Data
	 */
	private BKLaunchMe pBKLaunchMe;
	private FYAmortizationManager pFYAmortizationManager;
	private FYOutputFileManager pFYOutputFileManager;
	private FYDateManager pFYDateManager;
	private FYIncomeManager pFYIncomeManager;
	private FYBalanceSheetManager pFYBalanceSheetManager;
	private FYPreviousFYIncomeManager pFYPreviousFYIncomeManager;

	/**
	 * 
	 */
	public final void run() {
		BasicPrintMsg.displaySuperTitle(this, "Fiscal Year End");
		/*
		 * Load CONF
		 */
		pFYPreviousFYIncomeManager.loadFromFile();
		pFYDateManager.run();		
		/*
		 * Compute
		 */
		pFYAmortizationManager.run();
		pFYIncomeManager.loadFYIncome();
		pFYBalanceSheetManager.compute();
		/*
		 * Write output files
		 */
		pFYOutputFileManager.writeFilesRelatedToIncome();
		pFYOutputFileManager.writeFilesRelatedToBalanceSheet();
	}

	/*
	 * Getters & Setters
	 */
	public final BKLaunchMe getpBKLaunchMe() {
		return pBKLaunchMe;
	}
	public final FYAmortizationManager getpFYAmortizationManager() {
		return pFYAmortizationManager;
	}
	public final FYOutputFileManager getpFYOutputFileManager() {
		return pFYOutputFileManager;
	}
	public final FYDateManager getpFYDateManager() {
		return pFYDateManager;
	}

	public final FYIncomeManager getpFYIncomeManager() {
		return pFYIncomeManager;
	}

	public final FYBalanceSheetManager getpFYBalanceSheetManager() {
		return pFYBalanceSheetManager;
	}

	public final FYPreviousFYIncomeManager getpFYPreviousFYIncomeManager() {
		return pFYPreviousFYIncomeManager;
	}
	
}
