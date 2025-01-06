package step08_output_files.incomingfunds;

import java.util.HashMap;
import java.util.Map;

import basicmethods.AMNumberTools;
import step01_objects_from_conf_files.asset.asset.BKAssetCurrency;

abstract class OPRoot {

	protected OPRoot(int _sDate) {
		pDate = _sDate;
		/*
		 * 
		 */
		pMapBKAssetCurrencyToStockIncomingFunds = new HashMap<>();
		pMapBKAssetCurrencyToFlowIncomingFunds = new HashMap<>();
	}

	/*
	 * Abstract
	 */
	protected abstract OPRoot getpOPRootPrevious();
	/*
	 * Data
	 */
	protected int pDate;
	protected double pStockIncomingFundUSD;
	protected double pFlowIncomingFundUSD;
	protected Map<BKAssetCurrency, Double> pMapBKAssetCurrencyToStockIncomingFunds;
	protected Map<BKAssetCurrency, Double> pMapBKAssetCurrencyToFlowIncomingFunds;


	/**
	 * Stock & Flow
	 * @param _sPStockIncomingFunds
	 */
	public final void addpIncomingFunds(double _sPStockIncomingFunds, 
			BKAssetCurrency _sBKAssetCurrency) {
		if (AMNumberTools.isNaNOrNullOrZero(_sPStockIncomingFunds)) {
			return;
		}
		/*
		 * Store the stock of incoming funds in the local currency of the incoming fund
		 */
		Double lStockIncomingFund = pMapBKAssetCurrencyToStockIncomingFunds.get(_sBKAssetCurrency);
		if (lStockIncomingFund == null) {
			lStockIncomingFund = 0.;
		}
		lStockIncomingFund += _sPStockIncomingFunds;
		pMapBKAssetCurrencyToStockIncomingFunds.put(_sBKAssetCurrency, lStockIncomingFund);
		/*
		 * initiate the flow of the incoming funds in the local currency as the opposite of the previous stock
		 */
		double lFlowIncomingFund = lStockIncomingFund;
		OPRoot lOPRootPrevious = getpOPRootPrevious();
		if (lOPRootPrevious != null) {
			if (lOPRootPrevious.pMapBKAssetCurrencyToStockIncomingFunds.containsKey(_sBKAssetCurrency)) {
				lFlowIncomingFund += -lOPRootPrevious.pMapBKAssetCurrencyToStockIncomingFunds.get(_sBKAssetCurrency);
			}
		}
		pMapBKAssetCurrencyToFlowIncomingFunds.put(_sBKAssetCurrency, lFlowIncomingFund);
		/*
		 * Compute the flow as the difference of the previous stock in local currency (stored in map above) and the current stock
		 */
		pFlowIncomingFundUSD = 0.;
		for (BKAssetCurrency lBKAssetCurrency : pMapBKAssetCurrencyToFlowIncomingFunds.keySet()) { 
			double lPriceUSD = lBKAssetCurrency.getpPriceUSD(pDate);
			pFlowIncomingFundUSD += pMapBKAssetCurrencyToFlowIncomingFunds.get(lBKAssetCurrency) * lPriceUSD;		
		}
		/*
		 * Compute the stock in USD as the stock of the previous OPRoot + the flow in USD of now
		 * This way, we will use always the same FOREX for the past incoming funds 
		 */
		pStockIncomingFundUSD = 0.;
		if (lOPRootPrevious != null) {
			pStockIncomingFundUSD = lOPRootPrevious.getpStockIncomingFundUSD();
		}
		pStockIncomingFundUSD += pFlowIncomingFundUSD;
	}

	/*
	 * Getters & Setters
	 */
	public final double getpStockIncomingFundUSD() {
		return pStockIncomingFundUSD;
	}
	public final double getpFlowIncomingFundUSD() {
		return pFlowIncomingFundUSD;
	}
	public final Map<BKAssetCurrency, Double> getpMapBKAssetCurrencyToStockIncomingFunds() {
		return pMapBKAssetCurrencyToStockIncomingFunds;
	}


}
