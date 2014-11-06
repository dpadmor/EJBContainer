package ejb.singleton;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import annotations.Singleton;


@Singleton
public class MyEJBSessionSingleton implements MyEJBInterfaceSingleton{
	public MyEJBSessionSingleton()
	{		
	}
	
	public void maMethode1()
	{
		System.out.println("Appel de la méthode 1 de l'EJB");
	}
	
	public void maMethode2()
	{
		System.out.println("Appel de la méthode 2 de l'EJB");
	}
	
	@PreDestroy
	public void liberer()
	{
		System.out.println("Destruction de l'EJB MyEJBInterfaceSingleton - PreDestroy");
	}
	
	@PostConstruct
	public void initBean()
	{
		System.out.println("Initialisation de l'EJB MyEJBInterfaceSingleton - PostConstruct");
		System.out.println();
	}
}
