package step08_output_files.bkbars;

import step01_objects_from_conf_files.asset.bar.BKBar;
import step01_objects_from_conf_files.asset.bar.BKBarType;

public class OTBar {

	protected OTBar(BKBarType _sBKBarType) {
		pBKBarType = _sBKBarType;
	}
	
	/*
	 * Data
	 */
	private BKBarType pBKBarType;
	private int pNumberBars;
	private double pWeightOz;
	
	/**
	 * 
	 * @param _sBKBar
	 */
	public final void declareNewBKBar(BKBar _sBKBar) {
		pNumberBars++;
		pWeightOz += _sBKBar.getpWeightOz();
	}

	/*
	 * Getters & Setters
	 */
	public final BKBarType getpBKBarType() {
		return pBKBarType;
	}
	public final int getpNumberBars() {
		return pNumberBars;
	}
	public final double getpWeightOz() {
		return pWeightOz;
	}
	
	
	
}
