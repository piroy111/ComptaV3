package step02_load_transactions.objects.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import basicmethods.BasicPrintMsg;
import staticdata.com.BKCom;
import staticdata.datas.BKStaticConst.type_entity;

public class BKEntityManager {


	/*
	 * Data
	 */
	private static Map<String, BKEntity> mapKeyToBKEntity = new HashMap<>();
	private static List<BKEntity> listBKEntity = new ArrayList<>();
	private static List<BKEntity> listBKEntityPhysical = new ArrayList<>();
	private static Map<String, BKEntity> mapKeyToBKEntityPhysical = new HashMap<>();
	private static BKEntity BKEntityTransfer = getpOrCreateBKEntityTransfer();

	/**
	 * 
	 * @return
	 */
	private static BKEntity getpOrCreateBKEntityTransfer() {
		return getpOrCreateBKEntity(type_entity.TRANSFER.toString(), type_entity.TRANSFER);
	}

	/**
	 * 
	 * @param _sName
	 * @return
	 */
	public static final BKEntity getpOrCreateBKEntity(String _sName, type_entity _sTypeEntity) {
		String lKey = BKEntity.getKey(_sName, _sTypeEntity);
		BKEntity lBKEntity = mapKeyToBKEntity.get(lKey);
		if (lBKEntity == null) {
			lBKEntity = new BKEntity(_sName, _sTypeEntity);
			/*
			 *	Store in Map 
			 */
			mapKeyToBKEntity.put(lKey, lBKEntity);
			mapKeyToBKEntityPhysical.put(lKey, lBKEntity);
			/*
			 * Store in list
			 */
			listBKEntity.add(lBKEntity);
			listBKEntityPhysical.add(lBKEntity);
			Collections.sort(listBKEntity);
		}
		return lBKEntity;
	}

	/**
	 * 
	 * @param _sKey
	 * @param _sSender
	 * @return
	 */
	public static final BKEntity getpAndCheckBKEntityFromName(String _sName, Object _sSender) {
		BKEntity lBKEntity = null;
		for (type_entity lTypeEntity : type_entity.values()) {
			String lKey = BKEntity.getKey(_sName, lTypeEntity);
			if (mapKeyToBKEntity.containsKey(lKey)) {
				if (lBKEntity != null) {
					BKCom.error("There are 2 BKEntities with the same name but with different types"
							+ "\n1st BKEntity= " + lBKEntity
							+ "\n2nd BKEntity= " + mapKeyToBKEntity.get(lKey));
				}
				lBKEntity = mapKeyToBKEntity.get(lKey);
			}
		}
		return lBKEntity;
	}

	/**
	 * 
	 * @param _sKey
	 * @param _sSender
	 * @return
	 */
	public static final BKEntity getpAndCheckBKEntityFromKey(String _sKey, Object _sSender) {
		BKEntity lBKEntity = mapKeyToBKEntity.get(_sKey);
		if (lBKEntity == null) {
			BKCom.error("Unknown BKEntity; _sKey= '" + _sKey + "'"
					+ "\nSender= " + BasicPrintMsg.displaySender(_sSender)); 
		}
		return lBKEntity;
	}

	/**
	 * 
	 * @param _sBKEntity
	 * @return
	 */
	public static boolean getIsPhysical(BKEntity _sBKEntity) {
		return _sBKEntity.getpTypeEntity() == type_entity.PHYSICAL;
	}

	/*
	 * Getters & Setters
	 */
	public static final Map<String, BKEntity> getMapKeyToBKEntity() {
		return mapKeyToBKEntity;
	}
	public static final List<BKEntity> getListBKEntity() {
		return listBKEntity;
	}
	public static final List<BKEntity> getListBKEntityPhysical() {
		return listBKEntityPhysical;
	}
	public static final BKEntity getBKEntityTransfer() {
		return BKEntityTransfer;
	}
	public static final Map<String, BKEntity> getMapKeyToBKEntityPhysical() {
		return mapKeyToBKEntityPhysical;
	}


}
