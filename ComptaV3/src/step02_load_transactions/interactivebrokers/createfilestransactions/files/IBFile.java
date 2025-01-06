package step02_load_transactions.interactivebrokers.createfilestransactions.files;

import java.nio.file.Path;

import basicmethods.ReadFile;
import basicmethods.ReadFile.comReadFile;

public class IBFile implements Comparable<IBFile> {

	protected IBFile(int _sDateStart, int _sDateStop, Path _sPath, IBFileManager _sIBFileManager) {
		pDateStart = _sDateStart;
		pDateStop = _sDateStop;
		pPath = _sPath;
		pIBFileManager = _sIBFileManager;
	}
	
	/*
	 * Data
	 */
	private int pDateStart;
	private int pDateStop;
	private Path pPath;
	private IBFileManager pIBFileManager;
	private ReadFile pReadFile;
	
	/**
	 * 
	 */
	private void readFile() {
		if (pReadFile == null) {
			pReadFile = new ReadFile(pPath, comReadFile.FULL_COM);
		}
	}

	@Override public int compareTo(IBFile _sIBFile) {
		return Integer.compare(pDateStop, _sIBFile.getpDateStop());
	}
	
	/*
	 * Getters & Setters
	 */
	public final int getpDateStart() {
		return pDateStart;
	}
	public final int getpDateStop() {
		return pDateStop;
	}
	public final Path getpPath() {
		return pPath;
	}
	public final IBFileManager getpIBFileManager() {
		return pIBFileManager;
	}
	public final ReadFile getpReadFile() {
		readFile();
		return pReadFile;
	}

	
	
	
}
