package step02_load_transactions.objects.file_delivery;

import step02_load_transactions.objects.file.BKFileManager;

public class BKDeliveryFileManager extends BKFileManager<BKDeliveryFile, BKDeliveryFileManager> {

	public BKDeliveryFileManager(String _sSource) {
		super(_sSource);
	}
	
	/**
	 * Abstract - Factory
	 */
	@Override public BKDeliveryFile factoryBKFile(String _sKey) {
		return new BKDeliveryFile(_sKey, this);
	}
	
}
