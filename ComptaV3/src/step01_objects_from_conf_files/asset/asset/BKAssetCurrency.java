package step01_objects_from_conf_files.asset.asset;

import step01_objects_from_conf_files.asset.assetpaperorphysical.BKAssetPhysical;

public class BKAssetCurrency extends BKAssetPhysical {

	public BKAssetCurrency(String _sName, int _sIdxForSort) {
		super(_sName, _sIdxForSort);
	}

	/*
	 * Data
	 */
	private double pCostOfBorrowToPRoy;
	private double pCostOfShortPosition;
	private double pCostOfLongPosition;
	/*
	 * Getters & Setters
	 */
	/**
	 * > 0
	 */
	public final double getpCostOfBorrowToPRoy() {
		return pCostOfBorrowToPRoy;
	}
	public final void setpCostOfBorrowToPRoy(double _sPCostOfBorrowToPRoy) {
		pCostOfBorrowToPRoy = _sPCostOfBorrowToPRoy;
	}
	/**
	 * > 0
	 */
	public final double getpCostOfShortPosition() {
		return pCostOfShortPosition;
	}
	public final void setpCostOfShortPosition(double _sPCostOfShortPosition) {
		pCostOfShortPosition = _sPCostOfShortPosition;
	}
	/**
	 * > 0
	 */
	public final double getpCostOfLongPosition() {
		return pCostOfLongPosition;
	}
	public final void setpCostOfLongPosition(double _sPCostOfLongPosition) {
		pCostOfLongPosition = _sPCostOfLongPosition;
	}
	
}
