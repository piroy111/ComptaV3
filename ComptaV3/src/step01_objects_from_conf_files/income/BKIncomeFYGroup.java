package step01_objects_from_conf_files.income;

import java.util.TreeMap;

public class BKIncomeFYGroup {

	protected BKIncomeFYGroup(String _sName) {
		pName = _sName;
		/*
		 * 
		 */
		pTreeMapNameToBKIncomeFY = new TreeMap<>();
	}
	
	/*
	 * Data
	 */
	private String pName;
	private TreeMap<String, BKIncomeFY> pTreeMapNameToBKIncomeFY;
	
	/**
	 * 
	 * @param _sName
	 * @return
	 */
	public final BKIncomeFY getpOrCreateBKIncomeFY(String _sName) {
		BKIncomeFY lBKIncomeFY = pTreeMapNameToBKIncomeFY.get(_sName);
		if (lBKIncomeFY == null) {
			lBKIncomeFY = new BKIncomeFY(_sName, this);
			pTreeMapNameToBKIncomeFY.put(_sName, lBKIncomeFY);
		}
		return lBKIncomeFY;
	}

	/*
	 * Getters & Setters
	 */
	public final String getpName() {
		return pName;
	}
	public final TreeMap<String, BKIncomeFY> getpTreeMapNameToBKIncomeFY() {
		return pTreeMapNameToBKIncomeFY;
	}
	
}
