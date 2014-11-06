package ejb.stateful;

import annotations.Stateful;

@Stateful
public class EJBSessionStateful implements EJBInterfaceStateful{

	private Integer value = 0;
	
	public EJBSessionStateful()
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
}