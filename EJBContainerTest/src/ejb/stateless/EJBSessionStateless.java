package ejb.stateless;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import annotations.Stateless;

@Stateless
public class EJBSessionStateless implements EJBInterfaceStateless{

	private Integer value = 0;
	
	public EJBSessionStateless()
	{
	}
	
	public void maMethode1()
	{
		++value;
	}
	
	public Integer maMethode2()
	{
		++value;
		return value;
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
	}
		
}