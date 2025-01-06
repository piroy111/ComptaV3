package step08_output_files.abstracts_with_history;

import staticdata.datas.BKStaticConst;
import step01_objects_from_conf_files.account.BKAccount;
import step01_objects_from_conf_files.account.BKAccountManager;
import step08_output_files.client_nav_v3.BKOutputHistory_ClientNAV_v2;
import step10_launchme.BKLaunchMe;

public class BKOutputHistoryManager {

	public BKOutputHistoryManager(BKLaunchMe _sBKLaunchMe) {
		pBKLaunchMe = _sBKLaunchMe;
	}

	/*
	 * Data
	 */
	private BKLaunchMe pBKLaunchMe;
	
	/**
	 * 
	 */
	public final void run() {
		/*
		 * In order to save time, we do the output files only when all the COMPTA COMPUTORS have finished and we notify it in the conf file (BKStaticConst.getDATE_FREEZE_COMPTA_MIN() >= BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS())
		 */
		if (!BKStaticConst.getIS_SKIP_WRITE_FILES_CLIENTS() && BKStaticConst.getDATE_FREEZE_COMPTA_MIN() >= BKStaticConst.getDATE_STOP_COUNTING_IN_TRANSACTIONS()) {
			for (BKAccount lBKAccount : BKAccountManager.getpListBKAccountExceptBunker()) {
				new BKOutputHistory_ClientNAV_v2(this, lBKAccount).writeFile();
			}
		}		
	}

	/**
	 * 
	 * @return
	 */
	protected final int getpDateFYFrozenToDownload() {
		return pBKLaunchMe.getpBKFrozenManager().getpBKFrozenDateChooser().getpDateFYFrozenToDownload();
	}

	/*
	 * Getters & Setters
	 */
	public final BKLaunchMe getpBKLaunchMe() {
		return pBKLaunchMe;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
