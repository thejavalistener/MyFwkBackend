package thejavalistener.fwkbackend.hqlconsole;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import thejavalistener.fwkbackend.hqlconsole.abstractstatement.AbstractColumnQueryStatement;
import thejavalistener.fwkbackend.hqlconsole.abstractstatement.AbstractEntityQueryStatement;
import thejavalistener.fwkbackend.hqlconsole.abstractstatement.AbstractStatement;
import thejavalistener.fwkbackend.hqlconsole.abstractstatement.AbstractUpdateStatement;

@Component
public class MyHqlConsole extends MyHqlConsoleBase
{
	@Autowired
	private FactoryStatement factory;
	
	@PersistenceContext
	private EntityManager em;
	
	private boolean hayUpdate = false;

		
	@Override
	protected void executeHql(String hql) 
	{
		// si es null no va...
		if(hql==null){return;}

		// analizo qué tipo de sentencia es y retorno lo que venga
		AbstractStatement<?> astm = factory.getStatement(hql);

		if (astm instanceof AbstractEntityQueryStatement e)
		{
		    List<Object> lst = e.process();   // ✔️ tipado correcto
		    
		}
		else if (astm instanceof AbstractColumnQueryStatement c)
		{
		    List<Object[]> lst = c.process(); // ✔️ tipado correcto
		}
		else if (astm instanceof AbstractUpdateStatement u)
		{
		    Long updateCount = u.process();   // ✔️ tipado correcto
		}
		
	}
	
//	class EscuchaNewEntityCreated implements MyScreenMessageListener
//	{
//		@Override
//		public void onMessageEvent(MyScreenMessageEvent e)
//		{
//		}
//	}
	

	
	

}