package container;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class EMInvocationHandler  implements InvocationHandler{
	private Object em;
	
	public EMInvocationHandler(Object em) {
		this.em = em;
		System.out.println("Creation du proxy de l'EntityManager");
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable 
			
	{
		System.out.println("Debut invocation methode de l'EntityManager " + method.getName() + " par le proxy");
		method.invoke(em, args);
		System.out.println("Fin invocation methode de l'EntityManager " + method.getName() + " par le proxy");
		
		return null;
	}

	public Object getEm() {
		return em;
	}

}
