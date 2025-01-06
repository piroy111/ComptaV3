package step08_output_files.client_nav_v2;

import java.util.List;

import basicmethods.BasicString;
import basicmethods.ReadFile;

class BKOutput_ClientNAV_FileManager {

	protected BKOutput_ClientNAV_FileManager(BKOutput_ClientNAV_v2_old _sBKOutput_ClientNAV_v2) {
		pBKOutput_ClientNAV_v2 = _sBKOutput_ClientNAV_v2;
	}
	
	/*
	 * Data
	 */
	private BKOutput_ClientNAV_v2_old pBKOutput_ClientNAV_v2;
	private String pNameFileMostRecent;
	
	/**
	 * 
	 */
	public final void findMostRecentFileAndLoadNAVs() {
		/*
		 * Initiate
		 */
		List<String> lListNameFiles = pBKOutput_ClientNAV_v2.getpListFilesInFolder();
		int lDateMostRecent = -1;
		pNameFileMostRecent = null;
		String lSuffixForFile = pBKOutput_ClientNAV_v2.getpSuffixForFile();
		int lDateToAvoid = pBKOutput_ClientNAV_v2.getpListDateEndOfMonth().get(pBKOutput_ClientNAV_v2.getpListDateEndOfMonth().size() - 1);
		/*
		 * Loop on the files to find the most recent one
		 */
		for (String lNameFile : lListNameFiles) {
			if (lNameFile.endsWith(lSuffixForFile)) {
				int lDate = BasicString.getInt(BasicString.getDate(lNameFile));
				if (lDate > lDateMostRecent && lDate <= lDateToAvoid) {
					lDate = lDateMostRecent;
					pNameFileMostRecent = lNameFile;
				}
			}
		}
		/*
		 * Load NAVs
		 */
		if (pNameFileMostRecent != null) {
			String lDir = pBKOutput_ClientNAV_v2.getpDir();
			ReadFile lReadFile = new ReadFile(lDir, pNameFileMostRecent, ReadFile.comReadFile.COM_ONLY_IF_ERROR);
			for (List<String> lLine : lReadFile.getmContentList()) {
				/*
				 * Load line
				 */
				int lIdx = -1;
				int lDate = BasicString.getInt(lLine.get(++lIdx));
				double lIncomingFund = BasicString.getDouble(lLine.get(++lIdx));
				double lNAV = BasicString.getDouble(lLine.get(++lIdx));
				/*
				 * 
				 */
				BKOutput_OneNAV lBKOutput_OneNAV = pBKOutput_ClientNAV_v2.getpBKOutput_OneNAVManager().getpOrCreateBKOutput_OneNAV(lDate);
				lBKOutput_OneNAV.setpIncomingFunds(lIncomingFund);
				lBKOutput_OneNAV.setpNAV(lNAV);
			}
		}
	}
	
	
	
	
}
