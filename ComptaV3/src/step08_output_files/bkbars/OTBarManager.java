package step08_output_files.bkbars;

import java.util.HashMap;
import java.util.Map;

import step01_objects_from_conf_files.asset.bar.BKBarType;

public class OTBarManager {

	public OTBarManager() {
		pMapBKBarTypeToOTBar = new HashMap<>();
	}
	
	
	/*
	 * Data
	 */
	private Map<BKBarType, OTBar> pMapBKBarTypeToOTBar;
	
	/**
	 * 
	 * @param _sBKBarType
	 * @return
	 */
	public final OTBar getpOrCreateOTBar(BKBarType _sBKBarType) {
		OTBar lOTBar = pMapBKBarTypeToOTBar.get(_sBKBarType);
		if (lOTBar == null) {
			lOTBar = new OTBar(_sBKBarType);
			pMapBKBarTypeToOTBar.put(_sBKBarType, lOTBar);
		}
		return lOTBar;
	}

	/*
	 * Getters & Setters
	 */
	public final Map<BKBarType, OTBar> getpMapBKBarTypeToOTBar() {
		return pMapBKBarTypeToOTBar;
	}
	
}
