package step02_load_transactions.objects.file;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicFichiersNio;
import basicmethods.BasicFichiersNioRaw;
import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticNameFile;

public abstract class BKFileManager<F extends BKFile<F, M>, M extends BKFileManager<F, M>> {

	public BKFileManager(String _sSource) {
		pSource = _sSource;
		pMapKeyToBKFile = new HashMap<>();
		/*
		 * 
		 */
		String lNameClass = this.getClass().getSimpleName();
		pNameEvent = lNameClass.substring(0, (lNameClass.length() - "FileManager".length()));
		pNameReadFileTracker = pNameEvent + BKStaticNameFile.getSUFFIX_TRACK_FILE();
		pBKTrackerReader = new BKTrackerReader<F, M>(this);
	}

	/*
	 * Abstract
	 */
	public abstract F factoryBKFile(String _sKey);
	/*
	 * Data
	 */
	private String pSource;
	private String pNameEvent;
	private Map<String, F> pMapKeyToBKFile;
	private String pNameReadFileTracker;
	private BKTrackerReader<F, M> pBKTrackerReader;

	/**
	 * 
	 * @param _sPath
	 * @param _sSuffix
	 * @return
	 */
	public final List<F> createAllBKFileInDirAndSubDir(Path _sPath, String _sSuffix) {
		List<F> lListBKFile = new ArrayList<>();
		if (!BasicFichiersNioRaw.getIsDirectory(_sPath)) {
			BasicPrintMsg.errorCodeLogic();
		}
		List<Path> lListPathFiles = BasicFichiersNio.getListFilesAndSubFiles(_sPath);
		for (Path lPathFile : lListPathFiles) {
			if (_sSuffix == null || lPathFile.getFileName().toString().endsWith(_sSuffix)) {
				lListBKFile.add(getpOrCreateBKFile(lPathFile));
			}
		}
		return lListBKFile;
	}
	
	/**
	 * 
	 * @param _sPath
	 * @return
	 */
	public final F getpOrCreateBKFile(Path _sPath) {
		String lKey = BKFile.getKey(_sPath);
		F lBKFile = pMapKeyToBKFile.get(lKey);
		if (lBKFile == null) {
			lBKFile = factoryBKFile(lKey);
			pMapKeyToBKFile.put(lKey, lBKFile);
			/*
			 * 
			 */
			lBKFile.initiateData(_sPath);
		}
		return lBKFile;
	}
	
	/**
	 * 
	 * @param _sPath
	 * @return
	 */
	public final F getpOrCreateBKFile(String _sKey) {
		F lBKFile = pMapKeyToBKFile.get(_sKey);
		if (lBKFile == null) {
			lBKFile = factoryBKFile(_sKey);
			pMapKeyToBKFile.put(_sKey, lBKFile);
		}
		return lBKFile;
	}
	
	/**
	 * 
	 * @param _sPath
	 * @return
	 */
	public final F getpAndCheckBKFile(Path _sPath) {
		String lKey = BKFile.getKey(_sPath);
		if (!pMapKeyToBKFile.containsKey(lKey)) {
			BasicPrintMsg.error("The BKFile should have been created earlier because we list of the existing files earlier. This case should not happen");
			return null;
		} else {
			return pMapKeyToBKFile.get(lKey);
		}
	}
	
	/**
	 * 
	 */
	public final void readFileTracker() {
		BasicPrintMsg.displayTitle(this, "Reading " + pNameReadFileTracker);
		pBKTrackerReader.read();
	}


	/*
	 * Getters & Setters
	 */
	public final Map<String, F> getpMapKeyToBKFile() {
		return pMapKeyToBKFile;
	}
	public final String getpNameReadFileTracker() {
		return pNameReadFileTracker;
	}
	public final BKTrackerReader<F, M> getpBKTrackerReader() {
		return pBKTrackerReader;
	}
	public final String getpNameEvent() {
		return pNameEvent;
	}
	public final String getpSource() {
		return pSource;
	}
	

	
}
