package step02_load_transactions.objects.holdings;

import basicmethods.AMNumberTools;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;

public class BKHolding implements Comparable<BKHolding> {

	public BKHolding(BKAsset _sBKAsset) {
		pBKAsset = _sBKAsset;
		/*
		 * 
		 */
		pIsAtLeastOneData = false;
	}
	
	/*
	 * Data
	 */
	private BKAsset pBKAsset;
	private double pQtyPos;
	private double pQtyNeg;
	private double pPriceExecPos;
	private double pPriceExecNeg;
	private double pNNNExecPos;
	private double pNNNExecNeg;
	private boolean pIsAtLeastOneData;
	private double pQty;
	private double pPriceExec;
	private double pNNNExec;
	private double pNNNCurrentUSD;
	
	/**
	 * 
	 */
	public final void reset() {
		pQtyPos = 0.;
		pQtyNeg = 0.;
		pPriceExecPos = 0.;
		pPriceExecNeg = 0.;
		pNNNExecPos = 0.;
		pNNNExecNeg = 0.;
		pIsAtLeastOneData = false;
		pQty = 0.;
		pNNNCurrentUSD = 0.;
		pPriceExec = 0.;
		pNNNExec = 0.;
	}
	
	/**
	 * 
	 * @param _sQtyExec
	 * @param _sPriceExec
	 */
	public final void addNewData(double _sQtyExec, double _sPriceExec, String _sFileOrigin) {
		/*
		 * Error cases
		 */
		String lErrorMsg = "";
		if (!Double.isFinite(_sQtyExec)) {
			lErrorMsg = "Qty exec is not a finite number";
		}
		if (pBKAsset.getpIsPaper() && !Double.isFinite(_sPriceExec)) {
			lErrorMsg = "The price must be a finite number because the nature of the asset is paper";
		}
		if (!lErrorMsg.equals("")) {
			BKCom.error(lErrorMsg
					+ "\nBKAsset= " + pBKAsset
					+ "\n_sQtyExec= " + _sQtyExec
					+ "\n_sPriceExec= " + _sPriceExec
					+ "\n_sFileOrigin= '" + _sFileOrigin + "'");
		}
		/*
		 * Normal cases
		 */
		if (AMNumberTools.isPositiveStrict(_sQtyExec)) {
			pQtyPos += _sQtyExec;
			pNNNExecPos += _sQtyExec * _sPriceExec;
			pPriceExecPos = pNNNExecPos / pQtyPos;
			pIsAtLeastOneData = true;
		} else if (AMNumberTools.isNegativeStrict(_sQtyExec)) {
			pQtyNeg += _sQtyExec;
			pNNNExecNeg += _sQtyExec * _sPriceExec;
			pPriceExecNeg = pNNNExecNeg / pQtyNeg;
			pIsAtLeastOneData = true;
		}
		pQty = pQtyPos + pQtyNeg;
		pNNNExec = pNNNExecPos + pNNNExecNeg;
		pPriceExec = pNNNExec / pQty;
		pNNNCurrentUSD = pQty * pBKAsset.getpPriceUSD();
	}

	@Override public int compareTo(BKHolding _sBKHolding) {
		return Double.compare(pNNNCurrentUSD, _sBKHolding.getpNNNCurrentUSD());
	}
	
	/**
	 * 
	 * @param _sBKHolding
	 * @return
	 */
	public final boolean getpIsEquals(BKHolding _sBKHolding) {
		if (!pBKAsset.equals(_sBKHolding.getpBKAsset())) {
			return false;
		}
		if (!getpIsEquals(pQty, _sBKHolding.getpQty())) {
			return false;
		}
		if (pBKAsset.getpIsPaper()) {
			return getpIsEquals(pNNNExec, _sBKHolding.getpNNNExec());
		}
		return true;
	}
	
	/**
	 * NaN is considered same as 0
	 * @param _sDouble1
	 * @param _sDouble2
	 * @return
	 */
	public static boolean getpIsEquals(Double _sDouble1, Double _sDouble2) {
		if (getpIsNaNOrZero(_sDouble1)) {
			return getpIsNaNOrZero(_sDouble2);
		} else if (getpIsNaNOrZero(_sDouble2)) {
			return false;
		} else {
			return getpIsZero(_sDouble1 - _sDouble2);
		}
	}
	
	/**
	 * 
	 * @param _sDouble
	 * @return
	 */
	public static boolean getpIsNaNOrZero(Double _sDouble) {
		return _sDouble == null || !Double.isFinite(_sDouble) || Double.isNaN(_sDouble) || getpIsZero(_sDouble);
	}
	
	/**
	 * 
	 * @param _sDouble
	 * @return
	 */
	public static  boolean getpIsZero(Double _sDouble) {
		return Double.isFinite(_sDouble)
				&& -BKStaticConst.getERROR_ACCEPTABLE_COMPTA() < _sDouble && _sDouble < BKStaticConst.getERROR_ACCEPTABLE_COMPTA();
	}
	
	/**
	 * Classic toString
	 */
	public String toString() {
		return "Qty= " + pQty + "; Price= " + pPriceExec;
	}
	
	/*
	 * Getters & Setters
	 */
	public final double getpQtyPos() {
		return pQtyPos;
	}
	public final double getpQtyNeg() {
		return pQtyNeg;
	}
	public final double getpPriceExecPos() {
		return pPriceExecPos;
	}
	public final double getpPriceExecNeg() {
		return pPriceExecNeg;
	}
	public final BKAsset getpBKAsset() {
		return pBKAsset;
	}
	public final boolean getpIsAtLeastOneData() {
		return pIsAtLeastOneData;
	}
	public final double getpQty() {
		return pQty;
	}
	public final double getpNNNCurrentUSD() {
		return pNNNCurrentUSD;
	}
	public final double getpNNNExecPos() {
		return pNNNExecPos;
	}
	public final double getpNNNExecNeg() {
		return pNNNExecNeg;
	}
	public final double getpPriceExec() {
		return pPriceExec;
	}
	public final double getpNNNExec() {
		return pNNNExec;
	}

	



	
	
}
