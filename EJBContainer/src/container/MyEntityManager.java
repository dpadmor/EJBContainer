package container;

public class MyEntityManager implements EntityManager{
	
	private EntityTransaction transaction;
	public EntityTransaction getTransaction() {
		return transaction;
	}

	public MyEntityManager() {
		transaction = new EntityTransaction();
	}

	@Override
	public void persist(Object o) {
		System.out.println("Persistance de l'objet " + o + " par l'EntityManager");
		
	}

	@Override
	public void remove(Object o) {
		System.out.println("Suppression de l'objet " + o + " par l'EntityManager");
		
	}

	@Override
	public void merge(Object o) {
		System.out.println("Fusion de l'objet " + o + " par l'EntityManager");
		
	}

	@Override
	public void update(Object o) {
		System.out.println("Mise a jour de l'objet " + o + " par l'EntityManager");
		
	}

}
