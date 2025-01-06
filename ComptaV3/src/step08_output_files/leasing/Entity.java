package step08_output_files.leasing;

import step02_load_transactions.objects.entity.BKEntity;

class Entity extends Item {

	protected Entity(BKEntity _sBKEntity) {
		super(_sBKEntity);
	}

	/*
	 * Data
	 */
	private BKEntity pBKEntity;

	/*
	 * Getters & Setters
	 */
	public final BKEntity getpBKEntity() {
		return pBKEntity;
	}

}
