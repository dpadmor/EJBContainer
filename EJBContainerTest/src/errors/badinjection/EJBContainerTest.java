package errors.badinjection;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import annotations.EJB;
import container.EJBContainer;
import exception.BeanException;
import exception.EMException;
import junit.framework.TestCase;

public class EJBContainerTest extends TestCase {

	
	@EJB
	public MyEJBSession ejb;
	
	public EJBContainerTest() {
		// TODO Auto-generated constructor stub
	}

	public EJBContainerTest(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	public void initClassLoader()
	{
		//pour l'instant, creation en dur du classloader
		List<Class<?> > classes = new ArrayList<Class<?> >();
		classes.add(EJBContainerTest.class);
		classes.add(MyEJBInterface.class);
		classes.add(MyEJBSession.class);
		EJBContainer.setClassLoader(classes);
	}
	
	@Test(expected = BeanException.class)
	public void testInstance() {
		initClassLoader();
		
		try {
			EJBContainer.getInstance().inject(this);
		} catch (EMException | BeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
