package step04_debug.standalones.buildfilebalances;

import staticdata.datas.BKStaticDir;

public class STL_Build_missing_files_balance_for_physical_transactions {

	public static void main(String[] _sArgs) {
		BKStaticDir.detectDIR(new String[] {"G:/My Drive/Compta_bunker_v3/11_Program_to_run/"});
		/*
		 * example= 'G:/My Drive/Compta_bunker_v3/01_Load_transactions_physical/Broker_OANDA'
		 */
		new BLManager().run(DirIB);
	}
	
	/*
	 * DIR
	 */
	protected static String DirOANDA = "G:/My Drive/Compta_bunker_v3/01_Load_transactions_physical/Broker_OANDA";
	protected static String DirIB = "G:/My Drive/Compta_bunker_v3/01_Load_transactions_physical/Broker_InteractiveBroker";
	
}
