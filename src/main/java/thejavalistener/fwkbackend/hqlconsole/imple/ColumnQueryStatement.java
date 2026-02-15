package thejavalistener.fwkbackend.hqlconsole.imple;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import thejavalistener.fwkbackend.hqlconsole.abstractstatement.AbstractColumnQueryStatement;
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

			Query q = em.createQuery(hql);
			if( hasLimit() )
			{
				q.setMaxResults(getLimit());
			}
			
			@SuppressWarnings("unchecked")
			List<?> result = q.getResultList();
			for(Object o:result)
			{
				if( o instanceof Object[] oa )
				{
					ret.add(oa);
				}
				else
				{
					ret.add(new Object[]{o});
				}
			}
			
			return ret;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
