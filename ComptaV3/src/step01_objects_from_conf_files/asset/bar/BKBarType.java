package step01_objects_from_conf_files.asset.bar;

import java.util.ArrayList;
import java.util.List;

import step01_objects_from_conf_files.asset.asset.BKAssetMetal;

public class BKBarType implements Comparable<BKBarType> {

	protected BKBarType(String _sName, double _sWeightOz, double _sWeightGram, String _sNaturalUnit, double _sNaturalWeight, BKAssetMetal _sBKAssetMetal) {
		pName = _sName;
		pWeightOz = _sWeightOz;
		pWeightGram = _sWeightGram;
		pNaturalUnit = _sNaturalUnit;
		pNaturalWeight = _sNaturalWeight;
		pBKAssetMetal = _sBKAssetMetal;
		/*
		 * 
		 */
		pNaturalWeightStr = Math.round(pNaturalWeight) + " " + pNaturalUnit;
		pListBKBar = new ArrayList<>();
	}

	/*
	 * Data
	 */
	private String pName;
	private double pWeightOz;
	private double pWeightGram;
	private String pNaturalUnit;
	private BKAssetMetal pBKAssetMetal;
	private List<BKBar> pListBKBar;
	private double pNaturalWeight;
	private String pNaturalWeightStr;

	/**
	 * ToString classic
	 */
	public final String toString() {
		return pName;
	}
	
	/**
	 * Sort metal first, then weight
	 */
	@Override public int compareTo(BKBarType _sBKBarType) {
		int lCompareMetal = pBKAssetMetal.compareTo(_sBKBarType.getpBKAssetMetal());
		if (lCompareMetal != 0) {
			return lCompareMetal;
		} else {
			return Double.compare(pWeightOz, _sBKBarType.getpWeightOz());
		}
	}
	
	/**
	 * 
	 * @param _sBKBar
	 */
	public final void declareNewBKBar(BKBar _sBKBar) {
		pListBKBar.add(_sBKBar);
	}
	
	/*
	 * Getters & Setters
	 */
	public final String getpName() {
		return pName;
	}
	public final double getpWeightOz() {
		return pWeightOz;
	}
	public final double getpWeightGram() {
		return pWeightGram;
	}
	public final String getpNaturalUnit() {
		return pNaturalUnit;
	}
	public final BKAssetMetal getpBKAssetMetal() {
		return pBKAssetMetal;
	}
	public final double getpNaturalWeight() {
		return pNaturalWeight;
	}
	public final String getpNaturalWeightStr() {
		return pNaturalWeightStr;
	}
	public final List<BKBar> getpListBKBar() {
		return pListBKBar;
	}
	
}
