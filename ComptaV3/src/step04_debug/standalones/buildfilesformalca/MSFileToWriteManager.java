package step04_debug.standalones.buildfilesformalca;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicFichiersNioRaw;
import basicmethods.BasicPrintMsg;

class MSFileToWriteManager {

	protected MSFileToWriteManager() {
		pListMSFileToWrite = new ArrayList<>();
	}

	/*
	 * Data
	 */
	private List<MSFileToWrite> pListMSFileToWrite;

	/**
	 * 
	 * @param _sDir
	 * @param _sNameFile
	 * @return
	 */
	public final MSFileToWrite createNewFile(String _sDir, String _sNameFile) {
		/*
		 * Look for the next available name of file
		 */
		String lDir = _sDir;
		String lNameFile = _sNameFile;
		int lIdx = 00;
		while (BasicFichiersNioRaw.getIsAlreadyExist(Paths.get(lDir + lNameFile))) {
			lNameFile = _sNameFile + "_" + (++lIdx);
		}
		/*
		 * Create file object and return it
		 */
		MSFileToWrite lMSFileToWrite = new MSFileToWrite(lDir, lNameFile);
		pListMSFileToWrite.add(lMSFileToWrite);
		return lMSFileToWrite;
	}

	/**
	 * 
	 */
	public final void writeFiles() {
		BasicPrintMsg.displaySuperTitle(this, "Write all files");
		for (MSFileToWrite lMSFileToWrite : pListMSFileToWrite) {
			if (lMSFileToWrite.getpListLineToWrite().size() > 0) {
				BasicPrintMsg.display(this, 
						BasicPrintMsg.getJustifiedText("Write file '" + lMSFileToWrite.getpNameFile() + "'", 75)
						+ BasicPrintMsg.getJustifiedText(" --> " + lMSFileToWrite.getpListLineToWrite().size() + " lines", 15)
						+ BasicPrintMsg.getJustifiedText(" --> Origin= '" + lMSFileToWrite.getpFileOrigin() + "'", 75));
							lMSFileToWrite.writeFile();
			}
		}
	}







}
