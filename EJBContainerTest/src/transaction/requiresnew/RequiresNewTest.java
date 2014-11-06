package transaction.requiresnew;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import annotations.EJB;
import container.EJBContainer;
import exception.BeanException;
import exception.EMException;

public class RequiresNewTest {

	public RequiresNewTest() {
		// TODO Auto-generated constructor stub
	}
	
	@EJB
	public MyEJBInterface1 ejb;
	
	public void initClassLoader()
	{
		//pour l'instant, creation en dur du classloader
		List<Class<?> > classes = new ArrayList<Class<?> >();
		classes.add(RequiresNewTest.class);
		classes.add(MyEJBInterface1.class);
		classes.add(MyEJBSession1.class);
		classes.add(MyEJBInterface2.class);
		classes.add(MyEJBSession2.class);
		EJBContainer.setClassLoader(classes);
	}
	
	@Test
	public void requiresNewTest() {
		initClassLoader();	
		try {
			EJBContainer.getInstance().inject(this);
			ejb.maMethode1();
		} catch (EMException | BeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
