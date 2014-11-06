package transaction.required;

import annotations.EJB;
import annotations.PersistenceContext;
import annotations.Stateless;
import container.EntityManager;

@Stateless
public class MyEJBSession1 implements MyEJBInterface1 {

	@PersistenceContext
	public EntityManager em;

	@EJB
	public MyEJBInterface2 ejb;

	public MyEJBSession1() {
		// TODO Auto-generated constructor stub
	}

	public void maMethode1()  {
		ejb.maMethode2();
	}

}