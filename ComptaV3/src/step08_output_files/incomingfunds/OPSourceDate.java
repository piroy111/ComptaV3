package step08_output_files.incomingfunds;

class OPSourceDate extends OPRoot {

	protected OPSourceDate(String _sSource, OPIncomingFundDate _sOPIncomingFundDate) {
		super(_sOPIncomingFundDate.getpDate());
		pSource = _sSource;
		pOPIncomingFundDate = _sOPIncomingFundDate;
	}
	
	/*
	 * Data
	 */
	private OPIncomingFundDate pOPIncomingFundDate;
	private String pSource;
	
	@Override protected OPRoot getpOPRootPrevious() {
		if (pOPIncomingFundDate.getpOPIncomingFundDatePrevious() == null) {
			return null;
		}
		return pOPIncomingFundDate.getpOPIncomingFundDatePrevious().getpOrCreateOPSourceDate(pSource);
	}
	
	/*
	 * Getters & Setters
	 */
	public final String getpSource() {
		return pSource;
	}

	
}
