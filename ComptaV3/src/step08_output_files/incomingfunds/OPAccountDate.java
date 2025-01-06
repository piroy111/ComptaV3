package step08_output_files.incomingfunds;

import step01_objects_from_conf_files.account.BKAccount;

class OPAccountDate extends OPRoot implements Comparable<OPAccountDate> {

	protected OPAccountDate(OPIncomingFundDate _sOPIncomingFundDate, BKAccount _sBKAccount) {
		super(_sOPIncomingFundDate.getpDate());
		pOPIncomingFundDate = _sOPIncomingFundDate;
		pBKAccount = _sBKAccount;
	}
	
	/*
	 * Data
	 */
	private OPIncomingFundDate pOPIncomingFundDate;
	private BKAccount pBKAccount;
	
	@Override protected OPRoot getpOPRootPrevious() {
		if (pOPIncomingFundDate.getpOPIncomingFundDatePrevious() == null) {
			return null;
		}
		return pOPIncomingFundDate.getpOPIncomingFundDatePrevious().getpOrCreateOPAccountDate(pBKAccount);
	}
	
	@Override public int compareTo(OPAccountDate _sOPAccountDate) {
		return -Double.compare(pFlowIncomingFundUSD, _sOPAccountDate.pFlowIncomingFundUSD);
	}
	
	/*
	 * Getters & Setters
	 */
	public final BKAccount getpBKAccount() {
		return pBKAccount;
	}

	
	
	
	
	
	
	
	
}
