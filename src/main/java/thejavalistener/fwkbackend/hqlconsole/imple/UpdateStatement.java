package thejavalistener.fwkbackend.hqlconsole.imple;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import thejavalistener.fwkbackend.hqlconsole.abstractstatement.AbstractUpdateStatement;

@Component
@Primary
public class UpdateStatement extends AbstractUpdateStatement
{
	@PersistenceContext
	private EntityManager em; 
		
	@Override
	@Transactional
	public Long process()
	{
		Query q = em.createQuery(getHql());				
		return q.executeUpdate();
	}
}
