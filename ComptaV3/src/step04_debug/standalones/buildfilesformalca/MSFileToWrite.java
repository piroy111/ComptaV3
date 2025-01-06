package step04_debug.standalones.buildfilesformalca;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicFichiers;

class MSFileToWrite {

	protected MSFileToWrite(String _sDir, String _sNameFile) {
		pDir = _sDir;
		pNameFile = _sNameFile;
		/*
		 * 
		 */
		pListLineToWrite = new ArrayList<>();
	}
	
	/*
	 * Data
	 */
	private String pDir;
	private String pNameFile;
	private List<String> pListLineToWrite;
	private String pFileOrigin;
	
	/**
	 * 
	 * @param _sLine
	 */
	public final void addNewLine(String _sLine) {
		pListLineToWrite.add(_sLine);
	}
	
	
	/**
	 * 
	 */
	public final void writeFile() {
		if (pListLineToWrite.size() > 0) {
			String lHeader = "Date of the transaction,BKASset (as per names in the conf file 'Prices_histo_assets.csv'),Comment,Quantity,Price (for physical assets write NaN; for paper assets we must have a price executed),BKAccount (email as per the emails in the conf file 'Accounts and currency.csv'),BKIncome (as per names in the conf file 'BKIncome.csv')";
			BasicFichiers.writeFile(this, pDir, pNameFile, lHeader, pListLineToWrite);
		}
	}

	/*
	 * Getters & Setters
	 */
	public final String getpDir() {
		return pDir;
	}
	public final String getpNameFile() {
		return pNameFile;
	}
	public final List<String> getpListLineToWrite() {
		return pListLineToWrite;
	}
	public final String getpFileOrigin() {
		return pFileOrigin;
	}
	public final void setpFileOrigin(String _sPFileOrigin) {
		pFileOrigin = _sPFileOrigin;
	}
	
	
	
}
