package thejavalistener.fwkbackend.hqlconsole.imple;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Component
public class ShutdownStatement extends UpdateStatement
{
	@PersistenceContext
	private EntityManager em = null;

	@Override
	@Transactional
	public Integer process()
	{
		Query q = em.createNativeQuery("SHUTDOWN");
		return q.executeUpdate();
	}
}
