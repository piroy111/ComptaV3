package staticdata.dateloaders;

import java.util.List;

import basicmethods.BasicString;
import basicmethods.ReadFile;
import staticdata.datas.BKStaticConst;

public class BKStaticDateEarliestTransactionLoader {

		
	
	/**
	 * Set the date of earliest transaction as the minimum date between the dates of the file and the current date of earliest transactions
	 * @param _sReadFile
	 */
	public final static void readFileAndUpdateDate(ReadFile _sReadFile) {
		if (_sReadFile.getmHeader().startsWith("Date")) {
			for (List<String> lLine : _sReadFile.getmContentList()) {
				int lDate = BasicString.getInt(lLine.get(0));
				updateEarliestDate(lDate);
			}			
		}		
	}
	
	/**
	 * 
	 * @param _sNewDate
	 */
	public final static void updateEarliestDate(int _sNewDate, Long _sTimeStamp) {
		if ((_sTimeStamp == null || _sTimeStamp >= BKStaticConst.getTIME_STAMP_FILE_PREVIOUS_COMPTA())) {
			updateEarliestDate(_sNewDate);
		}
	}
	
	/**
	 * 
	 * @param _sNewDate
	 */
	public final static void updateEarliestDate(int _sNewDate) {
		if (_sNewDate < BKStaticConst.getDATE_FIRST_CHANGED_TRANSACTIONS()) {
			BKStaticConst.setDATE_FIRST_CHANGED_TRANSACTIONS(_sNewDate);
		}
	}
	
}
