//package thejavalistener.fwkbackend.hqlconsole.abstractstatement;
//
//import org.springframework.beans.factory.annotation.Autowired;
//
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.EntityManagerFactory;
//import jakarta.persistence.EntityTransaction;
//import jakarta.persistence.Query;
//
//public class AbstractUpdateStatement extends AbstractStatement<Integer>
//{
//    @Autowired
//    private EntityManagerFactory emf;
//
//    protected EntityManager em;
//    protected EntityTransaction tx;
//
//    @Override
//    public Integer process()   // nadie hace commit acá adentro
//    {
//        em = emf.createEntityManager();
//        tx = em.getTransaction();
//        tx.begin();
//
//        Query q = em.createQuery(getHql());
//        return q.executeUpdate();   
//    }
//
//
//    public void commit()
//    {
//        if (tx != null && tx.isActive())
//            tx.commit();
//
//        if (em != null && em.isOpen())
//            em.close();
//    }
//
//    public void rollback()
//    {
//        if (tx != null && tx.isActive())
//            tx.rollback();
//
//        if (em != null && em.isOpen())
//            em.close();
//    }
//}
