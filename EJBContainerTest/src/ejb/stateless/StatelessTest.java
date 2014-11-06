package ejb.stateless;

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

public class StatelessTest {

	@EJB
	public EJBInterfaceStateless myEjbStateless;
	
	public StatelessTest() {
		// TODO Auto-generated constructor stub
	}
	
	public void initClassLoader()
	{
		//pour l'instant, creation en dur du classloader
		List<Class<?> > classes = new ArrayList<Class<?> >();
		classes.add(StatelessTest.class);
		classes.add(EJBInterfaceStateless.class);
		classes.add(EJBSessionStateless.class);
		EJBContainer.setClassLoader(classes);
	}
	
	@Test
	public void testInstance() {
		initClassLoader();
		try {
			EJBContainer.getInstance().inject(this);
			
			BeanInvocationHandler handler = (BeanInvocationHandler)Proxy.getInvocationHandler((Proxy)myEjbStateless);
			myEjbStateless.maMethode1();
			Object bean1 = handler.getBean();
			Integer value = myEjbStateless.maMethode2();
			Object bean2 = handler.getBean();

			assertFalse(bean1 == bean2);
			assertTrue(value == 1);
		} catch (EMException | BeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
