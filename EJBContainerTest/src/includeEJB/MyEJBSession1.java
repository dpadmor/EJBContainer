package includeEJB;

import annotations.EJB;
import annotations.Stateful;

@Stateful
public class MyEJBSession1 implements MyEJBInterface1{

	@EJB
	public MyEJBInterface2 ejb;
	
	public MyEJBSession1() {
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void methode1()
	{
		ejb.methode2();
	}
}
