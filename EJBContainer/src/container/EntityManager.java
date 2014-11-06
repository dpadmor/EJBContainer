package container;

public interface EntityManager {

	public void persist(Object o);
	public void remove(Object o);
	public void merge(Object o);
	public void update(Object o);	

}
