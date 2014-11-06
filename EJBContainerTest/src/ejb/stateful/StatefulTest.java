package ejb.stateful;

import static org.junit.Assert.*;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import container.BeanInvocationHandler;
import container.EJBContainer;
import exception.BeanException;
import exception.EMException;
import annotations.EJB;

public class StatefulTest {

	@EJB
	public EJBInterfaceStateful myEjbStateful;
	
	public StatefulTest() {
		// TODO Auto-generated constructor stub
	}
	
	public void initClassLoader()
	{
		//pour l'instant, creation en dur du classloader
		List<Class<?> > classes = new ArrayList<Class<?> >();
		classes.add(StatefulTest.class);
		classes.add(EJBInterfaceStateful.class);
		classes.add(EJBSessionStateful.class);
		EJBContainer.setClassLoader(classes);
	}
	
	@Test
	public void testInstance() {
		initClassLoader();
		try {
			EJBContainer.getInstance().inject(this);

			BeanInvocationHandler handler = (BeanInvocationHandler)Proxy.getInvocationHandler((Proxy)myEjbStateful);
			myEjbStateful.maMethode1();
			Object bean1 = handler.getBean();
			Integer value = myEjbStateful.maMethode2();
			Object bean2 = handler.getBean();

			assertTrue(bean1 == bean2);
			assertTrue(value == 2);
		} catch (EMException | BeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
