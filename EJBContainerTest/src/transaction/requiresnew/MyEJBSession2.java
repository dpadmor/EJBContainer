package transaction.requiresnew;

import container.EntityManager;
import annotations.PersistenceContext;
import annotations.Stateless;
import annotations.TransactionAttribute;
import annotations.TransactionAttribute.TransactionAttributeType;

@Stateless
public class MyEJBSession2 implements MyEJBInterface2{
	@PersistenceContext
	public EntityManager em;

	public MyEJBSession2() {
		// TODO Auto-generated constructor stub
	}
	
	@TransactionAttribute(type = TransactionAttributeType.REQUIRESNEW)
	public void maMethode2()
	{
		
	}

}
