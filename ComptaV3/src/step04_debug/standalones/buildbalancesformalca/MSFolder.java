package step04_debug.standalones.buildbalancesformalca;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import basicmethods.BasicFichiers;
import basicmethods.BasicFichiersNio;
import basicmethods.BasicPrintMsg;
import basicmethods.BasicString;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step01_objects_from_conf_files.asset.assetabstract.BKAsset;

class MSFolder {

	public MSFolder(String _sDirMain, String _sNameSubDir) {
		pDirMain = _sDirMain;
		pNameSubDir = _sNameSubDir;
		/*
		 * 
		 */
		pTreeMapDateToMapBKAssetToFlow = new TreeMap<>();
		pTreeMapDateToMSFileDate = new TreeMap<>();
	}

	/*
	 * Data
	 */
	private String pDirMain;
	private String pNameSubDir;
	private TreeMap<Integer, MSFileDate> pTreeMapDateToMSFileDate;
	private TreeMap<Integer, Map<BKAsset, Double>> pTreeMapDateToMapBKAssetToFlow;

	/**
	 * 
	 */
	public final void run() {
		/*
		 * Create all the MSFile from the list of the files transactions
		 */
		BasicPrintMsg.displayTitle(this, "Read files and load transactions");
		pTreeMapDateToMapBKAssetToFlow = new TreeMap<>();
		String lDirTransaction = pDirMain + pNameSubDir + "/" + BKStaticDir.getPHYSICAL_SUBFOLDER_TRANSACTIONS() + "/";
		List<String> lListNameFile = BasicFichiersNio.getListFilesAndDirectoriesInDirectory(lDirTransaction);
		for (String lNameFile : lListNameFile) {
			int lDate = BasicString.getInt(lNameFile.substring(0, 8));
			MSFileDate lMSFileDate = getpOrCreateMSFileDate(lDate);
			lMSFileDate.setpNameFile(lNameFile);
			lMSFileDate.setpDir(lDirTransaction);
			/*
			 * Load the file and compute balances of the file
			 */
			lMSFileDate.run();
		}
		/*
		 * Compute balance of the folder in chronological order
		 */
		BasicPrintMsg.displayTitle(this, "Write files balances and proof");
		for (int lDateFile : pTreeMapDateToMSFileDate.keySet()) {
			/*
			 * Compute Balance
			 */
			Map<BKAsset, Double> lMapBKAssetToBalance = new HashMap<>();
			for (int lDate : pTreeMapDateToMapBKAssetToFlow.keySet()) {
				if (lDate > lDateFile) {
					break;
				}
				Map<BKAsset, Double> lMapBKAssetToFlow = pTreeMapDateToMapBKAssetToFlow.get(lDate);
				for (BKAsset lBKAsset : lMapBKAssetToFlow.keySet()) {
					double lFlow = lMapBKAssetToFlow.get(lBKAsset);
					Double lBalance = lMapBKAssetToBalance.get(lBKAsset);
					if (lBalance == null) {
						lBalance = 0.;
					}
					lBalance += lFlow;
					lMapBKAssetToBalance.put(lBKAsset, lBalance);
				}
			}
			/*
			 * Write file of balance
			 */
			List<String> lListLineToWriteBalance = new ArrayList<>();
			for (BKAsset lBKAsset : lMapBKAssetToBalance.keySet()) {
				String lLine = lBKAsset.getpName()
						+ "," + lMapBKAssetToBalance.get(lBKAsset);
				lListLineToWriteBalance.add(lLine);
			}
			String lHeaderBalance = "BKASset (as per names in the conf file 'Prices_histo_assets.csv'),Holding";
			String lDirBalance = pDirMain + pNameSubDir + "/" + BKStaticDir.getPHYSICAL_SUBFOLDER_BALANCES();
			String lNameFileBalance = lDateFile + "_" + pNameSubDir + "_" + "Physical" + BKStaticNameFile.getSUFFIX_BALANCES();
			BasicFichiers.writeFile(this, lDirBalance, lNameFileBalance, lHeaderBalance, lListLineToWriteBalance);
			/*
			 * Write file proof
			 */
			String lDirProof = pDirMain + pNameSubDir + "/" + BKStaticDir.getPHYSICAL_SUBFOLDER_DOCS();
			String lNameFileProof = lDateFile + "_" + pNameSubDir + "_" + "Physical" + BKStaticNameFile.getSUFFIX_DOCS() + "Empty.csv";
			BasicFichiers.writeFile(this, lDirProof, lNameFileProof, null, new ArrayList<>());
		}
	}

	/**
	 * 
	 * @param _sDate
	 * @param _sNameFile
	 * @return
	 */
	public final MSFileDate getpOrCreateMSFileDate(int _sDate) {
		MSFileDate lMSFileDate = pTreeMapDateToMSFileDate.get(_sDate);
		if (lMSFileDate == null) {
			lMSFileDate = new MSFileDate(_sDate, this);
			pTreeMapDateToMSFileDate.put(_sDate, lMSFileDate);
		}
		return lMSFileDate;
	}

	/**
	 * 
	 * @param _sBKAsset
	 * @param _sFlowToAdd
	 */
	public final void addFlow(int _sDate, BKAsset _sBKAsset, double _sFlowToAdd) {
		Map<BKAsset, Double> lMapBKAssetToFlow = pTreeMapDateToMapBKAssetToFlow.get(_sDate);
		if (lMapBKAssetToFlow == null) {
			lMapBKAssetToFlow = new HashMap<>();
			pTreeMapDateToMapBKAssetToFlow.put(_sDate, lMapBKAssetToFlow);
		}
		Double lBalance = lMapBKAssetToFlow.get(_sBKAsset);
		if (lBalance == null) {
			lBalance = 0.;
		}
		lBalance += _sFlowToAdd;
		lMapBKAssetToFlow.put(_sBKAsset, lBalance);
	}




}
