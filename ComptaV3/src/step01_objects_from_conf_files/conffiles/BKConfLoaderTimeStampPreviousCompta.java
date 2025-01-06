package step01_objects_from_conf_files.conffiles;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import basicmethods.BasicFichiers;
import basicmethods.BasicFichiersNio;
import basicmethods.BasicFichiersNioRaw;
import basicmethods.BasicTime;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;

public class BKConfLoaderTimeStampPreviousCompta {

	/*
	 * Data
	 */
	private static boolean IS_LOADED = false;

	/**
	 * 
	 */
	public static void loadConfFile() {
		if (!IS_LOADED) {
			IS_LOADED = true;
			/*
			 * Find the date of COMPTA
			 */
			int lDateCompta = BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS();
			/*
			 * Initiate
			 */
			String lDir = BKStaticDir.getTIME_STAMP_COMPUTATION_COMPTA();
			String lNameFile = lDateCompta + BKStaticNameFile.getSUFFIX_TIME_STAMP_COMPUTATION_COMPTA();
			Path lPath = Paths.get(lDir + lNameFile);
			/*
			 * Case the file does not exist --> we set the time stamp at 0 (= we will take all the files into account)
			 * As long as the COMPTA has not been finished, we re-do all the checks
			 */
			if (!BasicFichiersNioRaw.getIsAlreadyExist(lPath)) {
				BKStaticConst.setTIME_STAMP_FILE_PREVIOUS_COMPTA(0);
			}
			/*
			 * Otherwise we read the time stamp of the file
			 */
			else {
				long lTimeStamp;
				try {
					lTimeStamp = BasicFichiersNio.getLastModifiedTime(lPath);
					BKStaticConst.setTIME_STAMP_FILE_PREVIOUS_COMPTA(lTimeStamp);
				} catch (IOException lException) {
					lException.printStackTrace();
					System.exit(-1);
				}				
			}
		}
	}
	
	/**
	 * 
	 */
	public static void writeFile() {
		String lDir = BKStaticDir.getTIME_STAMP_COMPUTATION_COMPTA();
		String lNameFile = BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS()
				+ BKStaticNameFile.getSUFFIX_TIME_STAMP_COMPUTATION_COMPTA();
		BasicFichiers.writeFile(lDir, lNameFile, "" + BasicTime.getmNow(), null);
	}
	
	
}
