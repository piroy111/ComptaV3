package staticdata.dateloaders;

import basicmethods.BasicDateInt;
import staticdata.datas.BKStaticConst;

public class BKStaticDateComptaLoader {

	private static boolean IS_LOADED = false;
	
	
	/**
	 * Choose the last date of the month
	 */
	public static void loadDateStopCountingInTransactions() {
		if (!IS_LOADED) {
			IS_LOADED = true;
			int lToday = BasicDateInt.getmToday();
			if (lToday == BasicDateInt.getmEndOfMonth(lToday)) {
				BKStaticConst.setDATE_STOP_COUNTING_IN_TRANSACTIONS(lToday);
			} else {
				BKStaticConst.setDATE_STOP_COUNTING_IN_TRANSACTIONS(BasicDateInt.getmEndOfMonth(BasicDateInt.getmPlusMonth(lToday, -1)));
			}
		}
	}


	
}
