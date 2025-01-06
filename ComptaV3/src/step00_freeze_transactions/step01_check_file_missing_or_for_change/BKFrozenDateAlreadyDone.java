package step00_freeze_transactions.step01_check_file_missing_or_for_change;

import java.util.ArrayList;
import java.util.List;

import basicmethods.BasicString;
import basicmethods.ReadFile;
import basicmethods.ReadFile.comReadFile;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;

class BKFrozenDateAlreadyDone {

	protected BKFrozenDateAlreadyDone(BKFrozenDateChooser _sBKFrozenDateChooser) {
		pBKFrozenDateChooser = _sBKFrozenDateChooser;
	}

	/*
	 * Data
	 */
	private BKFrozenDateChooser pBKFrozenDateChooser;
	private List<Integer> pListFYFrozenForbiddenToDo; 





	/**
	 * 
	 */
	public final void loadConfFile() {
		pListFYFrozenForbiddenToDo = new ArrayList<>();
		String lDir = BKStaticDir.getCONF_FY_END();
		String lNameFile = BKStaticNameFile.getCONF_FYFROZEN_FREEZE();
		ReadFile lReadFile = new ReadFile(lDir, lNameFile, comReadFile.FULL_COM);
		for (String lFYStr : lReadFile.getmContent()) {
			int lFY = BasicString.getInt(lFYStr);
			pListFYFrozenForbiddenToDo.add(lFY);
		}
	}


	/**
	 * 
	 */
	public final void check() {
		int lDateTODo = pBKFrozenDateChooser.getpDateFYFrozenToDo();
		if (pListFYFrozenForbiddenToDo.contains(lDateTODo)) {
			String lMsg = "You asked me to crash if I had to re-do the FYFrozen " + lDateTODo
					+ "\nYou can see in the messages above what triggered the re-computation of the FYFrozen"
					+ "\nEither you check and change what triggered the re-computation"
					+ "\nOr you change the conf file so that I can recompute the FYFrozen. You need to remove the date " + lDateTODo + " from the conf file"
					+ "\nConf file= '" + BKStaticDir.getCONF_FY_END() + BKStaticNameFile.getCONF_FYFROZEN_FREEZE();
			BKCom.error(lMsg);
		}
	}



}
