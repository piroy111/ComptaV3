package step00_freeze_transactions.step01_check_file_missing_or_for_change;

import java.nio.file.Path;
import java.nio.file.Paths;

import basicmethods.BasicDateInt;
import basicmethods.BasicFichiersNioRaw;
import basicmethods.BasicPrintMsg;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;

public class BKFrozenFileMissingChecker {

	public BKFrozenFileMissingChecker(BKFrozenDateChooser _sBKFrozenDateChooser) {
		pBKFrozenDateChooser = _sBKFrozenDateChooser;
	}

	/*
	 * Data
	 */
	private BKFrozenDateChooser pBKFrozenDateChooser;

	/**
	 * 
	 */
	public final void checkForMissingFYFrozen() {
		BasicPrintMsg.displayTitle(this, "Check if all FY frozen files are here");
		/*
		 * Compute dates start and stop of FY + compute the list of FY frozen which should exist
		 */
		for (int lDate : pBKFrozenDateChooser.getpListDateFYFrozen()) {
			String lMsg = "Frozen FY " + lDate + " --> ";
			String lNameFileMissing = getpIsFileAlreadyHere(lDate);
			if (lNameFileMissing.equals("")) {
				lMsg += "Already here";
			} else {
				lMsg += "Missing " + lNameFileMissing;
				pBKFrozenDateChooser.declareNewDateFYFrozenToDo(lDate);
			}
			BasicPrintMsg.display(this, lMsg);
		}
	}

	/**
	 * 
	 * @param _sDateFY
	 * @return
	 */
	private String getpIsFileAlreadyHere(int _sDateFY) {
		int lDateFile = BasicDateInt.getmPlusDay(_sDateFY, 1);
		String lMsg = "";
		/*
		 * Frozen
		 */
		String lDir = BKStaticDir.getFREEZE_BKTRANSACTIONS();
		String lNameFile = lDateFile + BKStaticNameFile.getSUFFIX_FY_TRANSACTIONS_FREEZE();
		Path lPath = Paths.get(lDir + lNameFile);
		if (!BasicFichiersNioRaw.getIsAlreadyExist(lPath)) {
			lMsg = BasicPrintMsg.addErrorMessage(lMsg, "'" + lPath.getFileName().toString() + "'", true);
		}
		/*
		 * Capital
		 */
		lDir = BKStaticDir.getFREEZE_CAPITAL();
		lNameFile = lDateFile + BKStaticNameFile.getSUFFIX_FREEZE_CAPITAL();
		lPath = Paths.get(lDir + lNameFile);
		if (!BasicFichiersNioRaw.getIsAlreadyExist(lPath)) {
			lMsg = BasicPrintMsg.addErrorMessage(lMsg, "'" + lPath.getFileName().toString() + "'", true);
		}			
		/*
		 * All good
		 */
		return lMsg;
	}

	/*
	 * Getters & Setters
	 */
	public final BKFrozenDateChooser getpBKFrozenDateChooser() {
		return pBKFrozenDateChooser;
	}
	

}

























