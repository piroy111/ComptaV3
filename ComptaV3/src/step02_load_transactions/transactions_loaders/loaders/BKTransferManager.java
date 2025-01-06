package step02_load_transactions.transactions_loaders.loaders;

import staticdata.datas.BKStaticConst.type_entity;
import staticdata.datas.BKStaticDir;
import staticdata.datas.BKStaticNameFile;
import step02_load_transactions.objects.direntity.BKDirEntity;
import step02_load_transactions.objects.direntity.BKDirEntityManager;

public class BKTransferManager extends BKDirEntityManager {

	public BKTransferManager() {
		super(BKStaticDir.getLOAD_TRANSACTIONS_TRANSFER(), type_entity.TRANSFER);
	}

	@Override public String getpSubFolderNameTransactions() {
		return BKStaticDir.getTRANSFER_SUBFOLDER_TRANSACTIONS();
	}

	@Override public String getpSubFolderNameDocs() {
		return null;
	}

	@Override public String getpSubFolderNameBalances() {
		return null;
	}

	@Override public String getpSuffixFileNameTransactions() {
		return BKStaticNameFile.getSUFFIX_TRANSACTIONS();
	}

	@Override public String getpSuffixFileNameDocs() {
		return null;
	}

	@Override public String getpSuffixFileNameBalances() {
		return null;
	}

	@Override public boolean getpIsAndTreatSpecialCase(BKDirEntity _sBKDirEntity) {
		return false;
	}
}
