package step04_debug.standalones.buildbkrozentracker;

import staticdata.datas.BKStaticDir;

public class STL_Write_file_dates_files_BKTransaction_for_BKFrozenTracker {

	
	public static void main(String _sArgs[]) {
		BKStaticDir.detectDIR(_sArgs);
		new STFRManager().run();
	}
	
	
	
}
