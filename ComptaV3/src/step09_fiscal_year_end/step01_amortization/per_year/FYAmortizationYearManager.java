package step09_fiscal_year_end.step01_amortization.per_year;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import basicmethods.AMNumberTools;
import basicmethods.BasicPrintMsg;
import step09_fiscal_year_end.step01_amortization.FYAmortizationManager;
import step09_fiscal_year_end.step01_amortization.intangible_asset.FYIntangibleAsset;
import step09_fiscal_year_end.step01_amortization.intangible_asset.FYIntangibleAssetYear;

public class FYAmortizationYearManager {

	public FYAmortizationYearManager(FYAmortizationManager _sFYAmortizationManager) {
		pFYAmortizationManager = _sFYAmortizationManager;
		/*
		 * 
		 */
		pTreeMapYearToFYAmortizationYear = new TreeMap<>();
	}

	/*
	 * Data
	 */
	private FYAmortizationManager pFYAmortizationManager;
	private TreeMap<Integer, FYAmortizationYear> pTreeMapYearToFYAmortizationYear;

	/**
	 * 
	 */
	public final void compute() {
		BasicPrintMsg.displayTitle(this, "Compute amortization");
		/*
		 * Initiate
		 */
		List<FYIntangibleAsset> lListFYIntangibleAsset = new ArrayList<>(pFYAmortizationManager
				.getpFYIntangibleAssetManager().getpTreeMapNameToFYIntangibleAsset().values());
		/*
		 * Create the objects FYAmortizationYear for each useful year
		 */
		int lYearMin = Integer.MAX_VALUE;
		int lYearMax = Integer.MIN_VALUE;
		for (FYIntangibleAsset lFYIntangibleAsset : lListFYIntangibleAsset) {
			for (FYIntangibleAssetYear lFYIntangibleAssetYear : lFYIntangibleAsset.getpTreeMapDateToFYIntangibleAssetDate().values()) {
				int lYear = lFYIntangibleAssetYear.getpYearDebit();
				lYearMin = Math.min(lYearMin, lYear);
				lYearMax = Math.max(lYearMax, lYear + lFYIntangibleAssetYear.getpNumberOfYearsSpreading());
			}
		}
		for (int lYear = lYearMin; lYear <= lYearMax; lYear++) {
			getpOrCreateFYAmortizationYear(lYear);
		}
		/*
		 * Compute the amortization for each year + sum of intangible assets for the year
		 */
		for (FYIntangibleAsset lFYIntangibleAsset : lListFYIntangibleAsset) {
			for (FYIntangibleAssetYear lFYIntangibleAssetYear : lFYIntangibleAsset.getpTreeMapDateToFYIntangibleAssetDate().values()) {
				/*
				 * Load
				 */
				int lYear = lFYIntangibleAssetYear.getpYearDebit();
				int lSpread = lFYIntangibleAssetYear.getpNumberOfYearsSpreading();
				double lAmountAmortization = -lFYIntangibleAssetYear.getpAmountUSD() / lSpread;
				/*
				 * Feed amortization
				 */
				FYAmortizationYear lFYAmortizationYear = getpOrCreateFYAmortizationYear(lYear);
				if (!AMNumberTools.isNaNOrZero(lAmountAmortization)) {
					for (int lYearAmortization = lYear + 1; lYearAmortization <= lYear + lSpread; lYearAmortization++) {
						getpOrCreateFYAmortizationYear(lYearAmortization).addNewAmortization(lAmountAmortization);
					}
				}
				/*
				 * Feed intangible asset for the year
				 */
				lFYAmortizationYear.addNewIntangibleAssetForTheYear(lFYIntangibleAssetYear.getpAmountUSD());
			}
		}
		/*
		 * compute and fill the data of FYAmortizationDate
		 */
		double lSumIntangibleAssetPast = 0.;
		double lSumAmortizationPast = 0.;
		for (FYAmortizationYear lFYAmortizationYear : pTreeMapYearToFYAmortizationYear.values()) {
			/*
			 * Compute and store
			 */
			lFYAmortizationYear.setpCarryingIntangibleAssets(lSumIntangibleAssetPast 
					+ lSumAmortizationPast + lFYAmortizationYear.getpSumAmortization());
			lFYAmortizationYear.setpAdjustmentToFYIncome(lFYAmortizationYear.getpSumAmortization()
					+ lFYAmortizationYear.getpSumIntangibleAssetsForTheYear());
			/*
			 * Store as past
			 */
			lSumIntangibleAssetPast += lFYAmortizationYear.getpSumIntangibleAssetsForTheYear();
			lSumAmortizationPast += lFYAmortizationYear.getpSumAmortization();
		}
		/*
		 * Display
		 */
		for (FYAmortizationYear lFYAmortizationYear : pTreeMapYearToFYAmortizationYear.values()) {
			BasicPrintMsg.display(this, lFYAmortizationYear.toString());
		}
	}


	/**
	 * 
	 * @param _sYear
	 * @return
	 */
	public final FYAmortizationYear getpOrCreateFYAmortizationYear(int _sYear) {
		FYAmortizationYear lFYAmortizationYear = pTreeMapYearToFYAmortizationYear.get(_sYear);
		if (lFYAmortizationYear == null) {
			lFYAmortizationYear = new FYAmortizationYear(_sYear, this);
			pTreeMapYearToFYAmortizationYear.put(_sYear, lFYAmortizationYear);
		}
		return lFYAmortizationYear;
	}

	/*
	 * Getters & Setters
	 */
	public final FYAmortizationManager getpFYAmortizationManager() {
		return pFYAmortizationManager;
	}
	public final TreeMap<Integer, FYAmortizationYear> getpTreeMapYearToFYAmortizationYear() {
		return pTreeMapYearToFYAmortizationYear;
	}

}
