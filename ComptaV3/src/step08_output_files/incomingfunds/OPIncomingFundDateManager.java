package step08_output_files.incomingfunds;

import java.util.TreeMap;

class OPIncomingFundDateManager {

	protected OPIncomingFundDateManager(BKOutput_IncomingFundsFromClients _sBKOutput_IncomingFundsFromClients) {
		pBKOutput_IncomingFundsFromClients = _sBKOutput_IncomingFundsFromClients;
		/*
		 * 
		 */
		pTreeMapDateToOPIncomingFundDate = new TreeMap<>();
	}

	/*
	 * Data
	 */
	private BKOutput_IncomingFundsFromClients pBKOutput_IncomingFundsFromClients;
	private TreeMap<Integer, OPIncomingFundDate> pTreeMapDateToOPIncomingFundDate;

	/**
	 * Classic get or create
	 * @param _sDate
	 * @return
	 */
	public final OPIncomingFundDate getpOrCreateOPIncomingFunDate(int _sDate) {
		OPIncomingFundDate lOPIncomingFunDate = pTreeMapDateToOPIncomingFundDate.get(_sDate);
		if (lOPIncomingFunDate == null) {
			lOPIncomingFunDate = new OPIncomingFundDate(_sDate, this);
			pTreeMapDateToOPIncomingFundDate.put(_sDate, lOPIncomingFunDate);
		}
		return lOPIncomingFunDate;
	}

	/*
	 * Getters & Setters
	 */
	public final BKOutput_IncomingFundsFromClients getpBKOutput_IncomingFundsFromClients() {
		return pBKOutput_IncomingFundsFromClients;
	}
	public final TreeMap<Integer, OPIncomingFundDate> getpTreeMapDateToOPIncomingFundDate() {
		return pTreeMapDateToOPIncomingFundDate;
	}

}
