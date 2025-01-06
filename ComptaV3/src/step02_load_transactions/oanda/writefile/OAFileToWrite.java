package step02_load_transactions.oanda.writefile;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicPrintMsg;
import staticdata.com.fileswritten.BKComOnFilesWritten;
import staticdata.datas.BKStaticConst.com_file_written;

class OAFileToWrite {

	protected OAFileToWrite(OAWriteFileManager _sOAWriteFileManager, String _sNameFile) {
		pOAWriteFileManager = _sOAWriteFileManager;
		pNameFile = _sNameFile;
		/*
		 * 
		 */
		pListLineToWrite = new ArrayList<>();
	}
	
	/*
	 * Data
	 */
	private OAWriteFileManager pOAWriteFileManager;
	private List<String> pListLineToWrite;
	private String pHeader;
	private String pNameFile;
	
	/**
	 * 
	 * @param _sHeaderToAdd
	 */
	public final void addNewHeader(String _sHeaderToAdd) {
		if (pHeader == null) {
			pHeader = _sHeaderToAdd;
		} else {
			pHeader += "," + _sHeaderToAdd;
		}
	}
	
	/**
	 * 
	 * @param _sLine
	 */
	public final void addNewLineToWrite(String _sLine) {
		pListLineToWrite.add(_sLine);
	}

	
	/**
	 * 
	 */
	public final void writeFile() {
		String lDir = pOAWriteFileManager.getpOAManager().getpDirOutput();
		BKComOnFilesWritten.writeFile(com_file_written.TransactionsGeneratedByCompta, lDir, pNameFile, pHeader, pListLineToWrite);
		BasicPrintMsg.display(this, "File written= " + lDir + pNameFile);
	}
	
	
	
}
