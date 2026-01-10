package thejavalistener.fwkbackend.hqlconsole;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import thejavalistener.fwkbackend.hqlconsole.abstractstatement.AbstractStatement;
import thejavalistener.fwkbackend.hqlconsole.abstractstatement.AbstractUpdateStatement;
import thejavalistener.fwkbackend.hqlconsole.imple.ColumnQueryStatement;
import thejavalistener.fwkbackend.hqlconsole.imple.DeleteStatement;
import thejavalistener.fwkbackend.hqlconsole.imple.DescStatement;
import thejavalistener.fwkbackend.hqlconsole.imple.EntityQueryStatement;
import thejavalistener.fwkbackend.hqlconsole.imple.InsertStatement;
import thejavalistener.fwkbackend.hqlconsole.imple.NewStatement;
import thejavalistener.fwkbackend.hqlconsole.imple.ShutdownStatement;
import thejavalistener.fwkbackend.hqlconsole.imple.UpdateStatement;
import thejavalistener.fwkutils.string.MyString;


@Component
public class FactoryStatement
{
	@Autowired
	private ApplicationContext ctx;
	
	private UpdateListener listener;
	
	public AbstractStatement<?> getStatement(String hql)
	{
		AbstractStatement<?> stm = null;
		
		// tomo la primera palabra
		String w = MyString.getWordAt(hql,0).toLowerCase().trim();
		switch(w)
		{
			case "new":
				stm = ctx.getBean(NewStatement.class);
				break;
			case "desc":
				stm = ctx.getBean(DescStatement.class);
				break;
			case "select":
				stm = ctx.getBean(ColumnQueryStatement.class);					
				break;
			case "from":
				stm = ctx.getBean(EntityQueryStatement.class);					
				break;
			case "delete":
				stm = ctx.getBean(DeleteStatement.class);										
				((AbstractUpdateStatement)stm).setUpdateListener(listener);
				break;
			case "update":
				stm = ctx.getBean(UpdateStatement.class); 						
				((AbstractUpdateStatement)stm).setUpdateListener(listener);
				break;
			case "insert":
				stm = ctx.getBean(InsertStatement.class);
				((AbstractUpdateStatement)stm).setUpdateListener(listener);
				break;
			case "shutdown":
				stm = ctx.getBean(ShutdownStatement.class);
				((AbstractUpdateStatement)stm).setUpdateListener(listener);
				break;
			default:
				throw new RuntimeException("La sentencia debe comenzar con SELECT, FROM, UPDATE, DELETE o INSERT");		
		}
				
		stm.setHql(hql);
		return stm;
	}

	public void setUpdateListener(UpdateListener listener)
	{
		this.listener = listener;
	}
}
