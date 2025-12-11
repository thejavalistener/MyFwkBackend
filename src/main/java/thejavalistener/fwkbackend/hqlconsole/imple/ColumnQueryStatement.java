package thejavalistener.fwkbackend.hqlconsole.imple;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import thejavalistener.fwkbackend.hqlconsole.abstractstatement.AbstractColumnQueryStatement;
import thejavalistener.fwkutils.string.MyString;
import thejavalistener.fwkutils.various.MyReflection;

@Component
@Primary
public class ColumnQueryStatement extends AbstractColumnQueryStatement
{
	@PersistenceContext
	private EntityManager em; 
		
	@Override
	public List<Object[]> process()
	{
		try
		{
			List<Object[]> ret = new ArrayList<>();

			String hql = getHql();

			// Proceso la lapalbra LIMIT 
			int limit = _procesarLIMIT(hql);
			if( limit>=0 )
			{
				hql = _removerLIMIT(hql);
			}
		
			// Creo el Query
			Query q = em.createQuery(hql);
			if( limit>0 )
			{
				q.setMaxResults(limit);
			}
			
			List<?> result = q.getResultList();

			Object[] rows;
			for(Object o:result)
			{	
				rows = (Object[])o;
				rows = MyReflection.object.getValues(o).toArray();
				ret.add(rows);
			}
			
			return ret;
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
