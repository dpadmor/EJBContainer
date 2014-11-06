package ejb.singleton;

import static org.junit.Assert.*;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import annotations.EJB;
import container.*;
import exception.BeanException;
import exception.EMException;



public class EJBContainerTest {	
	@EJB
	public MyEJBInterfaceSingleton myEJBSingleton1;
	
	@EJB
	public MyEJBInterfaceSingleton myEJBSingleton2;
	
	public void initClassLoader()
	{
		//pour l'instant, creation en dur du classloader
		List<Class<?> > classes = new ArrayList<Class<?> >();
		classes.add(EJBContainerTest.class);
		classes.add(MyEJBInterfaceSingleton.class);
		classes.add(MyEJBSessionSingleton.class);
		EJBContainer.setClassLoader(classes);
	}
	
	@Test
	public void testSingleton() {

		System.out.println("*******************************************************");
		System.out.println("***************** Test Singleton **********************");
		initClassLoader();
		
		try {
			EJBContainer.getInstance().inject(this);
			
			myEJBSingleton1.maMethode1();
			myEJBSingleton2.maMethode2();

			BeanInvocationHandler handler1 = (BeanInvocationHandler)Proxy.getInvocationHandler((Proxy)myEJBSingleton1);
			BeanInvocationHandler handler2 = (BeanInvocationHandler)Proxy.getInvocationHandler((Proxy)myEJBSingleton2);
			
			assertEquals(handler1.getBean(), handler2.getBean());
			
			EJBContainer.getInstance().supprimerEJB(myEJBSingleton1);
			
		} catch (BeanException | EMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("******************************************************");
		System.out.println();
	}
}
