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
	
	public UpdateStatement()
	{
		setUpdateType(UPDATE);
	}
		
	@Override
	@Transactional
	public Integer process()
	{
		Query q = em.createQuery(getHql());				
		int updateCount = q.executeUpdate();
		
		// si no hubo cambios => no hay commit ni update
		if( updateCount==0 )
		{
			return 0;
		}
		
		// si hubo cambios => commit?
		if( !getExecuteCommit().apply(updateCount) )
		{
			rollback();
			return -1;
		}
		
		// hubo cambios y commit => notify
		notifyUpdate(getUpdateType(),updateCount);
		
		return updateCount;
	}
}
