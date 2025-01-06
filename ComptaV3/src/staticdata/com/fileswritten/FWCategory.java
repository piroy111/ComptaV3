package staticdata.com.fileswritten;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class FWCategory {

	protected FWCategory(String _sName) {
		pName = _sName;
		/*
		 * 
		 */
		pMapKeyToFWFile = new HashMap<>();
		pListFWFile = new ArrayList<>();
	}
	
	/*
	 * Data
	 */
	private String pName;
	private Map<String, FWFile> pMapKeyToFWFile;
	private List<FWFile> pListFWFile;
	
	/**
	 * 
	 * @return
	 */
	protected final FWFile getpOrCreateFWFile(String _sDir, String _sName) {
		String lKey = FWFile.getKey(_sDir, _sName);
		FWFile lFWFile = pMapKeyToFWFile.get(lKey);
		if (lFWFile == null) {
			lFWFile = new FWFile(this, _sDir, _sName);
			pMapKeyToFWFile.put(lKey, lFWFile);
			pListFWFile.add(lFWFile);
		}
		return lFWFile;
	}

	/*
	 * Getters & Setters
	 */
	public final String getpName() {
		return pName;
	}
	public final List<FWFile> getpListFWFile() {
		return pListFWFile;
	}
	public final Map<String, FWFile> getpMapKeyToFWFile() {
		return pMapKeyToFWFile;
	}
}
