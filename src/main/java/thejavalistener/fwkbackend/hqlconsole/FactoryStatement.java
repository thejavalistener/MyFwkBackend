package thejavalistener.fwkbackend.hqlconsole;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import thejavalistener.fwkutils.string.MyString;


@Component
public class FactoryStatement
{
	@Autowired
	private ApplicationContext ctx;
	
	public AbstractStatement getStatement(String sql)
	{
		AbstractStatement stm = null;
		
		// tomo la primera palabra
		String w = MyString.getWordAt(sql,0).toLowerCase().trim();

		switch(w.trim().toLowerCase())
		{
			case "new":
//				stm = ctx.getBean(HQLNewObjectStatement.class);
				break;
			case "desc":
				stm = ctx.getBean(DescStatement.class);
				break;
			case "select":
			case "from":
				stm = ctx.getBean(QueryStatement.class);					
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
				
		return stm;
	}
}
