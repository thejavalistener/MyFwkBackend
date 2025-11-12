package thejavalistener.fwkbackend.hqlconsole;
import java.util.List;
import java.util.function.Function;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import thejavalistener.fwkutils.string.MyString;
import thejavalistener.fwkutils.various.MyReflection;

@Component
@Primary
public class QueryStatement extends AbstractStatement
{
	@PersistenceContext
	private EntityManager em; 
		
	@Override
	public long process(String sql, List<Object[]> outputRows, Function<Long,Boolean> commit)
	{
		try
		{
			// Proceso la lapalbra LIMIT 
			int limit = _procesarLIMIT(sql);
			if( limit>=0 )
			{
				sql = _removerLIMIT(sql);
			}
		
			// Creo el Query
			Query q = em.createQuery(sql);
			if( limit>0 )
			{
				q.setMaxResults(limit);
			}
			
			List<?> result = q.getResultList();

			Object[] rows;
			for(Object o:result)
			{
				if( o instanceof Object[] )
				{
					rows = (Object[])o;
				}
				else
				{
					rows = MyReflection.object.getValues(o).toArray();
				}

				outputRows.add(rows);
			}
			
			return result.size();
			
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private int _procesarLIMIT(String sql)
	{
		String aux = sql.toLowerCase().replace('\n',' ').trim();
		List<String> words = MyString.wordList(aux);
		int n = words.size();
		if( n>1 && words.get(n-2).equalsIgnoreCase("limit") )
		{
			return Integer.parseInt(words.get(n-1));
		}
		
		return -1;
	}
	
	public String _removerLIMIT(String sql)
	{
		int p = sql.toLowerCase().lastIndexOf("limit");
		return sql.substring(0,p);
	}
	
}
