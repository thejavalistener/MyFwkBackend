package thejavalistener.fwkbackend.hqlconsole;
import java.util.List;
import java.util.function.Function;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Component
@Primary
public class UpdateStatement extends AbstractStatement
{
	@PersistenceContext
	private EntityManager em; 
		
	@Override
	@Transactional
	public long process(String sql, List<Object[]> outputRows, Function<Long,Boolean> commit)
	{
		Query q = em.createQuery(sql);				
		
		long updateCount = q.executeUpdate();
		
		if( updateCount>0 && !commit.apply(updateCount) )
		{
			throw new IllegalStateException("Rolledback");			
		}	
		
		return updateCount;
	}
}
