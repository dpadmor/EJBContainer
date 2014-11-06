package transaction.rollback;

import container.EntityManager;
import annotations.PersistenceContext;
import annotations.Stateless;

@Stateless
public class MyEJBSession implements MyEJBInterface {

	@PersistenceContext
	public EntityManager em;

	public MyEJBSession() {
		// TODO Auto-generated constructor stub
	}
	
	public void maMethode() throws Exception {
		throw new Exception("un rollback est neccessaire");
	}

}
