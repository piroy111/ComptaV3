package step09_fiscal_year_end.step04_outputfiles.amortization;

import java.util.ArrayList;
import java.util.List;

import step09_fiscal_year_end.step01_amortization.intangible_asset.FYIntangibleAsset;
import step09_fiscal_year_end.step01_amortization.intangible_asset.FYIntangibleAssetYear;
import step09_fiscal_year_end.step01_amortization.per_year.FYAmortizationYear;
import step09_fiscal_year_end.step04_outputfiles.abstracts.FYOutputFileAbstract;
import step09_fiscal_year_end.step04_outputfiles.abstracts.FYOutputFileManager;

public class FYOutput_Amortization extends FYOutputFileAbstract {

	public FYOutput_Amortization(FYOutputFileManager _sFYOutputFileManager) {
		super(_sFYOutputFileManager);
	}

	@Override public void buildFileContent() {
		/*
		 * Header
		 */
		addNewHeader("Item/FiscalYear");
		List<Integer> lListFiscalYear = new ArrayList<>(pFYOutputFileManager.getpFYManager()
				.getpFYAmortizationManager().getpFYAmortizationYearManager().getpTreeMapYearToFYAmortizationYear().keySet());
		for (int lFiscalYear : lListFiscalYear) {
			addNewHeader(lFiscalYear + "");
		}
		/*
		 * 
		 */
		String lLineAm = "Total amortization for the year";
		String lLineIA = "Intangible asset for the year";
		String lLineCA = "Carrying intangible asset";
		String lLineAd = "Adjustment to add to FY income";
		for (FYAmortizationYear lFYAmortizationYear : pFYOutputFileManager.getpFYManager()
				.getpFYAmortizationManager().getpFYAmortizationYearManager().getpTreeMapYearToFYAmortizationYear().values()) {
			lLineAm += "," + lFYAmortizationYear.getpSumAmortization();
			lLineIA += "," + lFYAmortizationYear.getpSumIntangibleAssetsForTheYear();
			lLineCA += "," + lFYAmortizationYear.getpCarryingIntangibleAssets();
			lLineAd += "," + lFYAmortizationYear.getpAdjustmentToFYIncome();
		}
		addNewLineToWrite(lLineAm);
		addNewLineToWrite(lLineIA);
		addNewLineToWrite(lLineCA);
		addNewLineToWrite(lLineAd);
		addNewLineToWrite("#");
		/*
		 * Write Lines for the 
		 */
		for (FYIntangibleAsset lFYIntangibleAsset : pFYOutputFileManager.getpFYManager().getpFYAmortizationManager().getpFYIntangibleAssetManager().getpTreeMapNameToFYIntangibleAsset().values()) {
			String lLine = lFYIntangibleAsset.getpName();
			for (int lFiscalYear : lListFiscalYear) {
				FYIntangibleAssetYear lFYIntangibleAssetYear = lFYIntangibleAsset.getpTreeMapDateToFYIntangibleAssetDate().get(lFiscalYear);
				if (lFYIntangibleAssetYear == null) {
					lLine += ",0";
				} else {
					lLine += "," + lFYIntangibleAssetYear.getpAmountUSD();						
				}
			}
			addNewLineToWrite(lLine);
		}
	}

	
	 
	
}
