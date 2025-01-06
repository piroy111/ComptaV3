package staticdata.com;

import basicmethods.BasicPrintMsg;
import step04_debug.abstracts.BKDebugManager;

public class BKCom {

	/**
	 * 
	 * @param _sErrorMessage
	 */
	public static void error(String _sErrorMessage) {
		String lErrorMsg = _sErrorMessage;
		lErrorMsg += BKDebugManager.flush();
		BasicPrintMsg.error(lErrorMsg);
	}
	
	/**
	 * 
	 */
	public static void errorCodeLogic() {
		BKDebugManager.flush();
		BasicPrintMsg.errorCodeLogic();
	}
	
}
