package step04_debug.standalones.buildbalancesformalca;

import java.util.List;

import basicmethods.BasicFichiersNio;
import staticdata.datas.BKStaticDir;

public class STL_Build_files_balances_for_Malca_SG {

	
	public static void main(String[] _sArgs) {
		BKStaticDir.detectDIR(_sArgs);
		new STL_Build_files_balances_for_Malca_SG().run();
	}
	
	/**
	 * 
	 */
	protected STL_Build_files_balances_for_Malca_SG() {
		
	}
	
	/**
	 * 
	 */
	public final void run() {
		String lDirMain = BKStaticDir.getLOAD_TRANSACTIONS_PHYSICAL();
		List<String> lListSubDir = BasicFichiersNio.getListFilesAndDirectoriesInDirectory(lDirMain);
		for (String lSubDir : lListSubDir) {
			if (
//					lSubDir.startsWith("Refiner") || 
					lSubDir.equals("Vault_Malca_SG")
//					lSubDir.equals("Vault_FCCS")
					) {
				MSFolder lMSFolder = new MSFolder(lDirMain, lSubDir);
				lMSFolder.run();
			}
		}
	}
	
}
