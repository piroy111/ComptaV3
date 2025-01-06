package step02_load_transactions.objects.entity;

import basicmethods.AMDebug;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst;
import staticdata.datas.BKStaticConst.type_entity;
import step02_load_transactions.objects.direntity.BKDirEntity;

public class BKEntity implements Comparable<BKEntity> {

	public BKEntity(String _sName, type_entity _sTypeEntity) {
		pName = _sName;
		pTypeEntity = _sTypeEntity;
		/*
		 * 
		 */
		pKey = getKey(pName, pTypeEntity);
		pIsVault = pTypeEntity == type_entity.PHYSICAL
				&& (pName.startsWith(BKStaticConst.getBKENTITY_PREFIX_VAULTS()) 
						|| pName.startsWith(BKStaticConst.getBKENTITY_PREFIX_REFINERS()));
	}
	
	/*
	 * Data
	 */
	private String pName;
	private String pKey;
	private type_entity pTypeEntity;
	private BKDirEntity pBKDirEntity;
	private boolean pIsVault;

	/**
	 * 
	 * @param _sBKDirEntity
	 */
	public final void declareNewBKDirEntityRelated(BKDirEntity _sBKDirEntity) {
		if (pBKDirEntity == null) {
			pBKDirEntity = _sBKDirEntity;
		} else {
			String lErrorMsg = "We cannot have 2 different BKEntity with the same name. You must change one name of a folder"
					+ "\nName of BKEntity= '" + pName + "'"
					+ "\nFirst folder=  '" + pBKDirEntity.getpDirEntity() + "'"
					+ "\nSeconf folder= '" + _sBKDirEntity.getpDirEntity() + "'";
			BKCom.error(lErrorMsg);
		}
	}
	
	/**
	 * 
	 * @param _sName
	 * @param _sTypeEntity
	 * @return
	 */
	public static String getKey(String _sName, type_entity _sTypeEntity) {
		return _sName + ";;" + _sTypeEntity;
	}
	
	@Override public int compareTo(BKEntity _sBKEntity) {
		return pName.compareTo(_sBKEntity.pName);
	}
	
	/**
	 * Classic
	 */
	public String toString() {
		return pName;
	}
	
	/*
	 * Getters & Setters
	 */
	public final String getpName() {
		return pName;
	}
	public final String getpKey() {
		return pKey;
	}
	public final type_entity getpTypeEntity() {
		return pTypeEntity;
	}
	public final BKDirEntity getpBKDirEntity() {
		return pBKDirEntity;
	}
	public final boolean getpIsVault() {
		return pIsVault;
	}





	
}
