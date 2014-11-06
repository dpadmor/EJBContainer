package includeEJB;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import container.EJBContainer;
import exception.BeanException;
import exception.EMException;
import annotations.EJB;
import junit.framework.TestCase;

public class EJBContainerTest extends TestCase {
	
	@EJB
	public MyEJBInterface1 ejb;

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
		classes.add(MyEJBInterface1.class);
		classes.add(MyEJBInterface2.class);
		classes.add(MyEJBSession1.class);
		classes.add(MyEJBSession2.class);
		EJBContainer.setClassLoader(classes);
	}
	
	@Test
	public void testInstance() {
		initClassLoader();
		try {
			EJBContainer.getInstance().inject(this);
			ejb.methode1();
		} catch (EMException | BeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
