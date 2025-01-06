package step04_debug.standalones.buildfilebalances;

import basicmethods.AMNumberTools;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;

class BLBalanceDateAsset {

	protected BLBalanceDateAsset(BLBalanceDate _sBLBalanceDate, BKAsset _sBKAsset) {
		pBLBalanceDate = _sBLBalanceDate;
		pBKAsset = _sBKAsset;
	}
	
	/*
	 * Data
	 */
	private BLBalanceDate pBLBalanceDate;
	private BKAsset pBKAsset;
	private double pHoldingPos;
	private double pPriceExecPos;
	private double pNNNExecPos;
	private double pHoldingNeg;
	private double pPriceExecNeg;
	private double pNNNExecNeg;

	/**
	 * 
	 * @param _sAmountToAdd
	 * @param _sPrice
	 */
	public final void addNewData(double _sAmountToAdd, double _sPrice) {
		if (AMNumberTools.isPositiveStrict(_sAmountToAdd)) {
			pHoldingPos += _sAmountToAdd;
			if (!AMNumberTools.isNaNOrNullOrZero(_sPrice)) {
				pNNNExecPos += _sAmountToAdd * _sPrice;
				pPriceExecPos = pNNNExecPos / pHoldingPos;
			}
		} else if (AMNumberTools.isNegativeStrict(_sAmountToAdd)) {
			pHoldingNeg += _sAmountToAdd;
			if (!AMNumberTools.isNaNOrNullOrZero(_sPrice)) {
				pNNNExecNeg += _sAmountToAdd * _sPrice;
				pPriceExecNeg = pNNNExecNeg / pHoldingNeg;
			}
		}
	}
	
	/*
	 * Getters & Setters
	 */
	public final BLBalanceDate getpBLBalanceDate() {
		return pBLBalanceDate;
	}
	public final BKAsset getpBKAsset() {
		return pBKAsset;
	}
	public final double getpHoldingPos() {
		return pHoldingPos;
	}
	public final double getpPriceExecPos() {
		return pPriceExecPos;
	}
	public final double getpNNNExecPos() {
		return pNNNExecPos;
	}
	public final double getpHoldingNeg() {
		return pHoldingNeg;
	}
	public final double getpPriceExecNeg() {
		return pPriceExecNeg;
	}
	public final double getpNNNExecNeg() {
		return pNNNExecNeg;
	}
}
