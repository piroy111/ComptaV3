package step08_output_files.client_nav_v2;

import java.util.TreeMap;

class BKOutput_OneNAVManager {

	
	protected BKOutput_OneNAVManager() {
		pTreeMapDateToBKOutput_OneNAV = new TreeMap<>();
	}
	
	/*
	 * Data
	 */
	private TreeMap<Integer, BKOutput_OneNAV> pTreeMapDateToBKOutput_OneNAV;
	
	/**
	 * classic get or create
	 * @param _sDate
	 * @return
	 */
	public final BKOutput_OneNAV getpOrCreateBKOutput_OneNAV(int _sDate) {
		BKOutput_OneNAV lBKOutput_OneNAV = pTreeMapDateToBKOutput_OneNAV.get(_sDate);
		if (lBKOutput_OneNAV == null) {
			lBKOutput_OneNAV = new BKOutput_OneNAV(_sDate);
			pTreeMapDateToBKOutput_OneNAV.put(_sDate, lBKOutput_OneNAV);
		}
		return lBKOutput_OneNAV;
	}

	/*
	 * Getters & Setters
	 */
	public final TreeMap<Integer, BKOutput_OneNAV> getpTreeMapDateToBKOutput_OneNAV() {
		return pTreeMapDateToBKOutput_OneNAV;
	}
	
}
