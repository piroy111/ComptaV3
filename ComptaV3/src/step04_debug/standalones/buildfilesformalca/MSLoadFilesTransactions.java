package step04_debug.standalones.buildfilesformalca;

import java.util.List;

import basicmethods.BasicFichiersNio;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import basicmethods.ReadFile;
import basicmethods.ReadFile.comReadFile;
import staticdata.datas.BKStaticDir;
import step01_objects_from_conf_files.asset.asset.BKAssetMetal;
import step01_objects_from_conf_files.asset.asset.BKAssetPaperMetal;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;
import step01_objects_from_conf_files.asset.assetabstract.BKAssetManager;

class MSLoadFilesTransactions {

	protected MSLoadFilesTransactions(STL_Files_builder_for_Malca_SG _sSTL_Files_builder_for_Malca_SG) {
		pSTL_Files_builder_for_Malca_SG = _sSTL_Files_builder_for_Malca_SG;
	}
	
	/*
	 * Data
	 */
	private STL_Files_builder_for_Malca_SG pSTL_Files_builder_for_Malca_SG;
	
	/**
	 * 
	 */	
	public final void run() {
		BasicPrintMsg.displaySuperTitle(this, "Read files transactions and create BKTransactions");
		String lDirMain = BKStaticDir.getLOAD_TRANSACTIONS_PHYSICAL();
		List<String> lListSubDir = BasicFichiersNio.getListFilesAndDirectoriesInDirectory(lDirMain);
		for (String lSubDir : lListSubDir) {
			if (lSubDir.startsWith(STL_Files_builder_for_Malca_SG.getDIR_PREFIX_INPUT())) {
				/*
				 * Read files in the SUBDIR
				 */
				BasicPrintMsg.display(this, "Reading lSubDir= '" + lSubDir + "'");
				String lDir = lDirMain + lSubDir + "/" + "Physical transactions in compta format" + "/";
				List<String> lListNameFile = BasicFichiersNio.getListFilesAndDirectoriesInDirectory(lDir);
				for (String lNameFile : lListNameFile) {
					/*
					 * Create the output files
					 */
					int lDate = BasicString.getInt(lNameFile.substring(0, 8));
					MSFileToWrite lMSFileToWriteTransactions = pSTL_Files_builder_for_Malca_SG.getpMSFileToWriteManager().createNewFile(
							STL_Files_builder_for_Malca_SG.getDIR_OUTPUT_TRANSACTIONS(),
							lDate + "_Vault_Malca_SG" + "_Physical_Transactions.csv");
					lMSFileToWriteTransactions.setpFileOrigin(lNameFile);
					MSFileToWrite lMSFileToWriteHedge = pSTL_Files_builder_for_Malca_SG.getpMSFileToWriteManager().createNewFile(
							STL_Files_builder_for_Malca_SG.getDIR_OUTPUT_HEDGE(),
							lDate + "_Hedge_refiners" + "_Transfer_Transactions.csv");
					lMSFileToWriteHedge.setpFileOrigin(lNameFile);
					MSFileToWrite lMSFileToWriteSelf = pSTL_Files_builder_for_Malca_SG.getpMSFileToWriteManager().createNewFile(
							lDir, lNameFile + "_Self");
					lMSFileToWriteSelf.setpFileOrigin(lNameFile);
					/*
					 * Read file input
					 */
					ReadFile lReadFile = new ReadFile(lDir, lNameFile, comReadFile.FULL_COM);
					for (int lIdxLine = 0; lIdxLine < lReadFile.getmContent().size(); lIdxLine++) {
						String lLine = lReadFile.getmContent().get(lIdxLine);
						List<String> lLineList = lReadFile.getmContentList().get(lIdxLine);
						/*
						 * Load line
						 */
						int lIdx = -1;
						lIdx++;
						BKAsset lBKAsset = BKAssetManager.getpAndCheckBKAsset(lLineList.get(++lIdx), 
								lReadFile.getmDirPlusNameFile());
						String lComment = lLineList.get(++lIdx);
						/*
						 * Choose in which file we should input the line
						 */
						if (lBKAsset instanceof BKAssetMetal) {
							lComment = lComment.toUpperCase();
							if (lComment.contains("FUTURE") || lComment.contains("FORWARD") || lComment.contains("FAKE")) {
								lMSFileToWriteSelf.addNewLine(lLine);
							} else {
								lMSFileToWriteTransactions.addNewLine(lLine);
							}
						} else if (lBKAsset instanceof BKAssetPaperMetal) {
							lMSFileToWriteHedge.addNewLine(lLine);
						} else {
							lMSFileToWriteSelf.addNewLine(lLine);
						}
					}
				}
			}
		}
	}
	
	
	
	
	
	
	
	
	
}
