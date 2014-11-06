package container;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import exception.BeanException;
import exception.EMException;
import annotations.Singleton;
import annotations.Stateful;
import annotations.Stateless;

// gestionnaire pour l'invocation de methodes via le proxy
public class BeanInvocationHandler implements InvocationHandler {
	private Class<?> classBean;
	private Object bean = null;

	public Object getBean() {
		return bean;
	}
	
	private boolean ajoutBean(boolean isSingleton) throws EMException, BeanException
	{
		boolean ajout = true;

		List<Object> liste = EJBContainer.getInstance().getMapManageInstances().get(classBean);
		if (liste == null)
		{	
			//rechercher les methodes postConstruct et preDestroy
			try {
				EJBContainer.getInstance().rechercheAnnotationsCycleVieEJB(classBean);
			} catch (BeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			liste = new ArrayList<>();
		}
		try {
			if (!isSingleton || isSingleton && liste.size() == 0)
			{
				bean = classBean.newInstance();
				liste.add(bean);
				EJBContainer.getInstance().getMapManageInstances().put(classBean, liste);
			}
			else
			{
				bean = liste.get(0);
				ajout = false;
			}
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (ajout)
		{
			System.out.println("Creation d'une nouvelle instance de l'EJB " + classBean);
			//l'EJB vient d'etre cree, appel des methodes postConstruct
			try {
				EJBContainer.getInstance().appelMethodesPostConstruct(bean);
			} catch (BeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			EJBContainer.getInstance().inject(bean);
		}
		
		return ajout;
	}

	public BeanInvocationHandler(Class<?> classBean) throws EMException, BeanException {
        this.classBean = classBean;
        
        System.out.println("Creation du proxy de l'EJB " + classBean);
        
        //on cree un seul proxy lors de l'injection de dependances pour le cas Stateful
        if (classBean.isAnnotationPresent(Stateful.class))
        {
        	ajoutBean(false);
			EJBContainer.getInstance().injectEntityManager(bean);
        }
    }
	
	@Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable 
    {
		Object resultat = null;
		
		//on cree une instance du proxy Stateless
		if (classBean.isAnnotationPresent(Stateless.class))
		{
			ajoutBean(false);
			EJBContainer.getInstance().injectEntityManager(bean);
		}
        //on cree ou on recupere le singleton du proxy
        else if (classBean.isAnnotationPresent(Singleton.class))
        {
        	if (ajoutBean(true))
				EJBContainer.getInstance().injectEntityManager(bean);
        	else
    			System.out.println("Recuperation de l'instance existante de l'EJB Singleton " + classBean);
        }
				
		boolean isTransactionnel = false;
		List<Object> liste = EJBContainer.getInstance().getMapEntityManager().get(bean);

		if (liste != null)
		{
			for (Object o : liste)
				isTransactionnel = EJBContainer.beginMethodCall((MyEntityManager)o, classBean.getMethod(method.getName(), method.getParameterTypes()));
		}
		
        System.out.println("Debut invocation methode " + method.getName() + " par le proxy");        
        
        try {
        	resultat = method.invoke(bean, args);
        	
        	if (liste != null)
	        {
	        	for (Object o : liste)
	        		EJBContainer.endMethodCall((MyEntityManager)o, method, isTransactionnel);
	        }
        } catch (Exception ex) {
        	if (liste != null)
    		{
    			for (Object o : liste)
    				((MyEntityManager)o).getTransaction().rollback();
    		}
        } finally {        
        
	        System.out.println("Fin invocation methode " + method.getName() + " par le proxy\n");
	        
	        //s'il s'agit d'un EJB stateless, on le supprime pour indiquer qu'il n'y a pas de lien client <-> instance
	         //entre chaque appel
	        if (classBean.isAnnotationPresent(Stateless.class))
	        	EJBContainer.getInstance().supprimerEJB(proxy);
        }
	        
        return resultat;
	}
}
