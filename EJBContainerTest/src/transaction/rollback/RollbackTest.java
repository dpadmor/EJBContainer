package transaction.rollback;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import annotations.EJB;
import container.EJBContainer;
import exception.BeanException;
import exception.EMException;

public class RollbackTest {

	public RollbackTest() {
		// TODO Auto-generated constructor stub
	}
	
	@EJB
	public MyEJBInterface ejb;
	
	public void initClassLoader()
	{
		//pour l'instant, creation en dur du classloader
		List<Class<?> > classes = new ArrayList<Class<?> >();
		classes.add(RollbackTest.class);
		classes.add(MyEJBInterface.class);
		classes.add(MyEJBSession.class);
		EJBContainer.setClassLoader(classes);
	}
	
	@Test
	public void rollbackTest() {
		initClassLoader();	
		try {
			EJBContainer.getInstance().inject(this);
		} catch (EMException | BeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			ejb.maMethode();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
