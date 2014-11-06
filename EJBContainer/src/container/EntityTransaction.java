package container;

import exception.BeanException;

public class EntityTransaction {

	public EntityTransaction() {
		// TODO Auto-generated constructor stub
	}
	
	public void begin()
	{
		try {
			EJBContainer.getInstance().setTransactionEnCours(true);
		} catch (BeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Debut de la transaction");
	}
	
	public void commit()
	{
		try {
			EJBContainer.getInstance().setTransactionEnCours(false);
		} catch (BeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Commit de la transaction");
	}
	
	public void rollback()
	{
		try {
			EJBContainer.getInstance().setTransactionEnCours(false);
		} catch (BeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Rollback de a transaction");
	}

}
