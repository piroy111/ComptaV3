package step08_output_files.leasing;

 abstract class Item implements Comparable<Item> {

	protected Item(Object _sObjectRoot) {
		if (_sObjectRoot == null) {
			pName = "Total " + this.getClass().getSimpleName();
		} else {
			pName = _sObjectRoot.toString();
		}
	}
	
	/*
	 * Data
	 */
	private String pName;
	private double pKeySort;
	
	/**
	 * 
	 * @param _sAccountEntityAssetLeasing
	 */
	public final void setAmountUSDForSort(AccountEntityAssetLeasing _sAccountEntityAssetLeasing) {
		pKeySort = _sAccountEntityAssetLeasing.getpAmountInUSD();
	}
	
	/**
	 * 
	 */
	@Override public int compareTo(Item _sItem) {
		return -Double.compare(pKeySort, _sItem.pKeySort);
	}
	
	/*
	 * Getters & Setters
	 */
	public final String getpName() {
		return pName;
	}





	
	
	
}
