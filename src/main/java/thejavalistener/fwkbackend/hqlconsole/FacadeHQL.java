package thejavalistener.fwkbackend.hqlconsole;

import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@Component
public class FacadeHQL
{
	@PersistenceContext
	private EntityManager em;

	@Transactional
	public void persist(Object o)
	{
		em.persist(o);
	}

}
