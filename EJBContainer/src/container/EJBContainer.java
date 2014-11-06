package container;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import exception.BeanException;
import exception.EMException;
import exception.TransactionException;
import annotations.EJB;
import annotations.Local;
import annotations.PersistenceContext;
import annotations.Singleton;
import annotations.Stateful;
import annotations.Stateless;
import annotations.TransactionAttribute;

public class EJBContainer {
	//instance unique du conteneur
	private static EJBContainer INSTANCE = null;

	//map interface->implementation
	private Map<Class<?>, Class<?> > mapEJB = new HashMap<> ();
	
	//map implementation->[liste PostContruct]
	private Map<Class<?>, List<Method> > mapPostConstruct = new HashMap<>();
	
	//map implementation->[liste PreDestroy]
	private Map<Class<?>, List<Method> > mapPreDestroy = new HashMap<>();
	
	//map implementation->[List de ses instances creees par le conteneur]
	private Map<Class<?>, List<Object> > mapManageInstances = new HashMap<>();
	
	private boolean isTransactionEnCours = false;
	
	public boolean isTransactionEnCours() {
		return isTransactionEnCours;
	}	
	
	public void setTransactionEnCours(boolean isTransactionEnCours) {
		this.isTransactionEnCours = isTransactionEnCours;
	}


	public Map<Class<?>, List<Object>> getMapManageInstances() {
		return mapManageInstances;
	}
	
	//map instance d'EJB->[List des EntityManager pour la gestion des transactions]
	private Map<Object, List<Object> > mapEntityManager = new HashMap<>();
	public Map<Object, List<Object> > getMapEntityManager() {
		return mapEntityManager;
	}
	
	// propriete du classloader
	private static List<Class<?> > classLoader = new ArrayList<Class<?> >();
	public static List<Class<?> > getClassLoader() {
		return classLoader;
	}
	public static void setClassLoader(List<Class<?>> classLoader) {
		EJBContainer.classLoader = classLoader;
	}
	
	/*
	 * Role : obtenir l'instance du conteneur = singleton
	 */
	public static EJBContainer getInstance() throws BeanException
	{
		if (INSTANCE == null)
		{
			INSTANCE = new EJBContainer();
			INSTANCE.bootstrapInit();
		}
		return INSTANCE;
	}
	

	/*
	 * Role : initialiser le conteneur en chargeant le classloader + en mappant interface->implementation
	 */
	public void bootstrapInit() throws BeanException{
		//List<Class<?> > classes = scanClassLoader();
		remplirMapEJB();
	}

	/*
	 * Role : retourner la liste des classes chargees dans le classloader
	 */
	@SuppressWarnings("unchecked")
	public List<Class<?> > scanClassLoader()
	{
		ClassLoader classloader = ClassLoader.getSystemClassLoader();
		Class<ClassLoader> metaClass = ClassLoader.class;
		List<Class<?> > classes = null;

		
		Field fields;
		try {
			fields = metaClass.getDeclaredField("classes");
			fields.setAccessible(true);
			classes = new ArrayList<Class<?> >((Vector<Class<?> >) fields.get(classloader));
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		
		return classes;
	}
	
	
	/*
	 * Role : remplir la map interface->implementation 
	 */
	public void remplirMapEJB() throws BeanException
	{
		for (Class<?> classe : classLoader)
		{
			//recherche des annotations de la classe
			Annotation[] annotationsClasse = classe.getAnnotations();
			for(Annotation annotationClasse : annotationsClasse)
			{
				if(annotationClasse instanceof Stateless || annotationClasse instanceof Stateful || annotationClasse instanceof Singleton)
				{
					//recherche de l'interface de l'EJB session
					Class<?> [] listeInterfaces = classe.getInterfaces();					
					for (Class<?> interfaceEJB : listeInterfaces)
					{
						Annotation[] annotationsInterface = interfaceEJB.getAnnotations();
						for(Annotation annotationInterface : annotationsInterface)
						{
							if (annotationInterface instanceof Local)
							{
								Class<?> implementation = mapEJB.get(interfaceEJB);

								if (implementation == null)
									mapEJB.put(interfaceEJB, classe);
								else
								{
									if (implementation == classe)
										throw new BeanException ("L'EJB " + interfaceEJB.getName() + " ne peut pas etre injecte. Un seul type (Stateless, Stateful, Singleton) est requis au niveau de l'implementation");
									else
										throw new BeanException("Plusieurs EJB implementent l'interface " + interfaceEJB.getName());
								}
							}
						}
					}
				}
			}
		}
	}
	
	/*
	 * Role : lister les champs annotes @EJB
	 * 
	 * Entree :
	 * 	classe : classe dont on cherche par reflexion les champs @EJB
	 * 
	 * Resultat :
	 * 	liste des champs annotés @EJB
	 */
	public List<Field> getListAnnotationEJB(Class<?> classe)
	{
		//recherche de tous les champs annotees @EJB
		List<Field> fields = new ArrayList<Field>();
		
		Field[] fieldsClasse = classe.getFields();

		for(Field field : fieldsClasse)
		{
			Annotation[] annotations =  field.getAnnotations();
			for(Annotation annotation : annotations)
			{
				if(annotation instanceof EJB)
					fields.add(field);
			}
		}
		
		return fields;
	}
	
	
	/*
	 * Role : injecter un EJB
	 * 
	 * o : instance d'un objet managé
	 */
	public void inject (Object o) throws EMException, BeanException
	{
		//liste des champs @EJB dans o
		List<Field> ejbs = getListAnnotationEJB(o.getClass());

		for (Field ejb  : ejbs)
		{
			try {
				if (!ejb.getType().isInterface())
					throw new BeanException("L'injection d'EJB se fait a partir d'une interface. " + ejb.getType().getName() + " n'est pas une interface");
				else 
					ejb.set(o, createBean(ejb.getType()));
				
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}				
		}
	}
	
	/*
	 * Role : creer un proxy sur le bean
	 * 
	 * interfaz : interface que le proxy doit implementer
	 */
	@SuppressWarnings("unchecked")
	public <T> T createBean(Class<T> interfaz) throws EMException, BeanException {
		Class<? extends T> implementation = (Class<? extends T>) mapEJB.get(interfaz);

		if (implementation == null)
			throw new BeanException("l'interface " + interfaz.getName() + " n'a pas d'implementation");
		else {
			BeanInvocationHandler handler = new BeanInvocationHandler(implementation);
	        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] {interfaz}, handler);
		}
	}
	
	/*
	 * Role : rechercher liste des methodes annotees @PreDestroy et @PostConstruct
	 * 
	 * Entrees :
	 *   c : classe de l'EJB sur lequel s'effectue la recherche
	 */
	
	public void rechercheAnnotationsCycleVieEJB (Class<?> c)
	{
		List<Method> listeMethodesPreDestroy = mapPreDestroy.get(c);
		List<Method> listeMethodesPostConstruct = mapPostConstruct.get(c);
		Method[] listeMethodesClasse = c.getMethods();
		
		for (Method methode : listeMethodesClasse)
		{
			Annotation annotation = methode.getAnnotation(PreDestroy.class);
			if (annotation != null)
			{
				if (listeMethodesPreDestroy == null)
					listeMethodesPreDestroy = new ArrayList<Method>();
				listeMethodesPreDestroy.add(methode);
				mapPreDestroy.put(c, listeMethodesPreDestroy);					
			}
			
			annotation = methode.getAnnotation(PostConstruct.class);
			if (annotation != null)
			{
				if (listeMethodesPostConstruct == null)
					listeMethodesPostConstruct = new ArrayList<Method>();
				listeMethodesPostConstruct.add(methode);
				mapPostConstruct.put(c, listeMethodesPostConstruct);					
			}
		}
	}
	
	/*
	 * Role : appel de toutes les methodes annotees preConstruct pour l'EJB
	 * 
	 * Entree : l'EJB
	 */
	public void appelMethodesPostConstruct(Object o) {
		appelMethodesCycleVie(o, mapPostConstruct);
	}
	
	/*
	 * Role : appel de toutes les methodes annotees preDestroy pour l'EJB
	 * 
	 * Entree : l'EJB
	 */
	public void appelMethodesPreDestroy(Object o) {
		appelMethodesCycleVie(o, mapPreDestroy);
	}
	
	public void appelMethodesCycleVie(Object o, Map<Class<?>, List<Method> > map)
	{
		List<Method> listeMethodes = map.get(o.getClass());
		
		if (listeMethodes != null)
		{
			for (Method methode : listeMethodes)
			{
				try {
					methode.invoke(o, (Object[])null);
				} catch (IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/*
	 * Role : supprimer l'EJB bean
	 */
	public void supprimerEJB(Object proxy)
	{
		BeanInvocationHandler handler = (BeanInvocationHandler)Proxy.getInvocationHandler((Proxy)proxy);
		appelMethodesPreDestroy(handler.getBean());
		
		System.out.println("Destruction du proxy par le conteneur");
		System.out.println("\n---------------------------------------\n");
	}


	public void injectEntityManager(Object o) throws EMException
	{
		List<Object> listeEMInstance = mapEntityManager.get(o);
		if(listeEMInstance == null)
			listeEMInstance = new ArrayList<>();
		
		//liste des champs @PersistenceContext dans o
		List<Field> EMs = getListAnnotationEntityManager(o.getClass());

		for (Field em  : EMs)
		{
			try {
				if (!em.getType().isInterface())
					throw new EMException("L'injection d'EJB se fait a partir d'une interface. " + em.getType().getName() + " n'est pas une interface");
				else 
				{
					//injection du proxy
					em.set(o, createEM(em.getType()));
					
					//recuperer l'instance de l'EM
					Proxy p = (Proxy)em.get(o);
					EMInvocationHandler handler = (EMInvocationHandler)Proxy.getInvocationHandler(p);
					listeEMInstance.add(handler.getEm());
				}
			} catch (IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}				
		}

		if (listeEMInstance.size() > 0)
			mapEntityManager.put(o, listeEMInstance);
	}
	
	
	
	/*
	 * Role : creer un proxy sur le bean
	 * 
	 * interfaz : interface que le proxy doit implementer
	 */
	@SuppressWarnings("unchecked")
	public <T> T createEM(Class<T> interfaz) throws EMException {
		
		if (interfaz == EntityManager.class)
		{
			Class<? extends T> implementation = (Class<? extends T>) MyEntityManager.class;
			EMInvocationHandler handler = null;
			try {
				handler = new EMInvocationHandler(implementation.newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
	        
			return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] {interfaz}, handler);
		}
		else
			throw new EMException("L'interface " + interfaz + " ne permet pas d'injecter l'entity manager");
	}
	
	
	/*
	 * Role : lister les champs annotes @PersistenceContext
	 * 
	 * Entree :
	 * 	classe : classe dont on cherche par reflexion les champs @PersistenceContext
	 * 
	 * Resultat :
	 * 	liste des champs annotés @PersistnecContext
	 */
	public List<Field> getListAnnotationEntityManager(Class<?> classe)
	{
		//recherche de tous les champs annotees @PersistenceContext
		List<Field> fields = new ArrayList<Field>();
		
		Field[] fieldsClasse = classe.getFields();
		for(Field field : fieldsClasse)
		{
			Annotation[] annotations =  field.getAnnotations();
			for(Annotation annotation : annotations)
			{
				if(annotation instanceof PersistenceContext)
					fields.add(field);
			}
		}
		
		return fields;
	}
	
	
	public static boolean beginMethodCall(MyEntityManager em, Method method) throws TransactionException
	{
		boolean isTransactionnel = false;
		Annotation annotation = method.getAnnotation(TransactionAttribute.class);
		boolean required = true;

		if (annotation != null)
		{
			switch (((TransactionAttribute) annotation).type()) {
			case REQUIRESNEW: 
				isTransactionnel = true;
				em.getTransaction().begin();
				required = false;
				break;
			case NONE:
				try {
					if (EJBContainer.getInstance().isTransactionEnCours())
						throw new TransactionException("Une transaction est en cours lors de l'appel de la methode " + method.getName() + " annotee NONE");
					else
						System.out.println("Aucune transaction n'est creee");
				} catch (BeanException e1) {
					e1.printStackTrace();
				}
				required = false;
				break;
			default:
				break;					
			}
		}
		
		if (required) {
			try {
				if (EJBContainer.getInstance().isTransactionEnCours())
					System.out.println("Utilisation de la transaction en cours pour la methode " + method.getName());
				else
				{
					isTransactionnel = true;
					em.getTransaction().begin();
				}
			
			} catch (BeanException e) {
				e.printStackTrace();
			}
		}

		return isTransactionnel;
	}
	
	public static void endMethodCall(MyEntityManager em, Method method, boolean isTransactionnel)
	{
		if (isTransactionnel)
			em.getTransaction().commit();
	}
}
