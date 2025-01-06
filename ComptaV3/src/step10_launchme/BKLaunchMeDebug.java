package step10_launchme;

import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticDir;

public class BKLaunchMeDebug {

	public static void main(String[] _sArgs) {
		BKStaticDir.detectDIR(_sArgs);
		/*
		 * True --> we will skip the reconciliation with the platform (allows to see the results if we know the problem of the reconciliation is not a major one)
		 */
		BKStaticConst.setIS_SKIP_RECONCILIATION_PLATORM(true);
		/*
		 * True --> we will not write all the files of the clients (because it takes a lot of time)
		 */
		BKStaticConst.setIS_SKIP_WRITE_FILES_CLIENTS(true);
		/*
		 * 
		 */		
		new BKLaunchMe().run();
	}
	
}
