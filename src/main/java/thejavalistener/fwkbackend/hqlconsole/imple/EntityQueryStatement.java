package thejavalistener.fwkbackend.hqlconsole.imple;
import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import thejavalistener.fwkbackend.hqlconsole.abstractstatement.AbstractEntityQueryStatement;
import thejavalistener.fwkutils.string.MyString;

@Component
@Primary
public class EntityQueryStatement extends AbstractEntityQueryStatement
{
	@PersistenceContext
	private EntityManager em; 
		
	@SuppressWarnings("unchecked")
	@Override
	public List<Object> process()
	{
		try
		{
			String hql = getHql();
			Query q = em.createQuery(hql);
			if( hasLimit() )
			{
				q.setMaxResults(getLimit());
			}
			
			List<Object> ret = q.getResultList();
			if( ret.size()>0 )
			{
				setEntityClass(ret.get(0).getClass());
			}
			
			return ret;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}	
}
