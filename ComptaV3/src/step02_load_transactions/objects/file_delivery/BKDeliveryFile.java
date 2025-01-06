package step02_load_transactions.objects.file_delivery;

import step02_load_transactions.objects.file.BKFile;

public class BKDeliveryFile extends BKFile<BKDeliveryFile, BKDeliveryFileManager> {

	
	protected BKDeliveryFile(String _sKey, BKDeliveryFileManager _sBKDeliveryFileManager) {
		super(_sKey, _sBKDeliveryFileManager);
	}

}
