package thejavalistener.fwkbackend.hqlconsole;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import thejavalistener.fwkbackend.hqlconsole.abstractstatement.AbstractUpdateStatement;
import thejavalistener.fwkbackend.hqlconsole.imple.ColumnQueryStatement;
import thejavalistener.fwkbackend.hqlconsole.imple.UpdateStatement;
import thejavalistener.fwkutils.string.MyString;


@Component
public class FactoryStatement
{
	@Autowired
	private ApplicationContext ctx;
	
	public AbstractUpdateStatement getStatement(String hql)
	{
		AbstractUpdateStatement stm = null;
		
		// tomo la primera palabra
		String w = MyString.getWordAt(hql,0).toLowerCase().trim();
		switch(w)
		{
			case "new":
//				stm = ctx.getBean(HQLNewObjectStatement.class);
				break;
			case "desc":
				stm = ctx.getBean(DescStatement.class);
				break;
			case "select":
			case "from":
				stm = ctx.getBean(ColumnQueryStatement.class);					
				break;
			case "delete":
			case "update":
				stm = ctx.getBean(UpdateStatement.class);										
				break;
			case "insert":
				stm = ctx.getBean(InsertStatement.class);
				break;
			default:
				throw new RuntimeException("La sentencia debe comenzar con SELECT, FROM, UPDATE, DELETE o INSERT");		
		}
				
		stm.setHql(hql);
		return stm;
	}
}
