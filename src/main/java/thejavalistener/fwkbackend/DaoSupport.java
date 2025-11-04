package thejavalistener.fwkbackend;
import java.lang.reflect.Field;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Id;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;

import thejavalistener.fwkutils.string.MyString;
import thejavalistener.fwkutils.various.MyReflection;

@Component
public class DaoSupport
{
	@PersistenceContext
	protected EntityManager em;

	public long count(Class<?> entityClass)
	{
		return querySingleRow("SELECT COUNT(*) FROM "+entityClass.getName());
	}
	
	public <T> T querySingleRow(String hql,Object ...params)
	{
		List<T> lst = queryMultipleRows(hql,params);
		int n = lst.size();
		
		if( n>1 )
		{
			String mssg = "Se esperaba 1 o ninguna fila, pero se encontraron: "+n;
			throw new RuntimeException(mssg);
		}
		
		return n==1?lst.get(0):null;		
	}
	 
	
	public <T> T querySingleRowOrFirst(String hql,Object ...params)
	{
		List<T> lst = queryMultipleRows(hql,params);
		return lst.size()>0?lst.get(0):null;		
	}
	
	/** Considera que la lista de parametros esta indexada (WHERE p1=? AND p2=?...) */
	public <T> T querySingleRowIP(String hql,Object ...params)
	{
		List<T> lst = queryMultipleRowsIP(hql,params);
		int n = lst.size();
		if( n>1 )
		{
			String mssg = "Se esperaba 1 o ninguna fila, pero se encontraron: "+n;
			throw new RuntimeException(mssg);
		}
		
		return n==1?lst.get(0):null;
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> queryMultipleRows(String hql,Object ...params)
	{
		Query q = em.createQuery(hql);
		for(int i=0; i<params.length; i+=2)
		{
			q.setParameter(params[i].toString(),params[i+1]);
		}
		return q.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> queryMultipleRows(String hql,int fetchSize,Object ...params)
	{
		Query q = em.createQuery(hql);
		q.setMaxResults(fetchSize);
		for(int i=0; i<params.length; i+=2)
		{
			q.setParameter(params[i].toString(),params[i+1]);
		}
		return q.getResultList();
	}
	
	
	@SuppressWarnings("unchecked")
	/** Considera que la lista de parametros esta indexada (WHERE p1=? AND p2=?...) */
	public <T> List<T> queryMultipleRowsIP(String hql,Object ...params)
	{
		Query q = em.createQuery(hql);
		for(int i=0; i<params.length; i++)
		{
			q.setParameter(i,params[i]);
		}
		
		return q.getResultList();
	}
	
	/** Busca por Id */
	public <T,K> T find(Class<T> clazz,K k)
	{
		return em.find(clazz,k);
	}
	
	public void insert(Object t)
	{
		em.persist(t);
	}	
	
	public void delete(Object t)
	{
		em.remove(t);
	}
	
	public int update(String hql,Object ...params)
	{
		Query q = em.createQuery(hql);
		for(int i=0; i<params.length; i+=2)
		{
			q.setParameter(params[i].toString(),params[i+1]);
		}
		return q.executeUpdate();
	}
	
	public int nativeUpdate(String sql,Object ...params)
	{
		Query q = em.createNativeQuery(sql);
		for(int i=0; i<params.length; i+=2)
		{
			q.setParameter(params[i].toString(),params[i+1]);
		}
		return q.executeUpdate();		
	}
	
	public <T> T nativeQuerySingleRow(String sql,Object ...params)
	{
		List<T> lst = nativeQueryMultipleRows(sql,params);
		int n = lst.size();
		if( n>1 )
		{
			String mssg = "Se esperaba 1 o ninguna fila, pero se encontraron: "+n;
			throw new RuntimeException(mssg);
		}
		
		return n==1?lst.get(0):null;
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> nativeQueryMultipleRows(String sql,Object ...params)
	{
		Query q = em.createNativeQuery(sql);
		for(int i=0; i<params.length; i+=2)
		{
			q.setParameter(params[i].toString(),params[i+1]);
		}
		return q.getResultList();
	}


	/** Inserta o modifica segun exista o no una fila con el mismo id */
	public boolean insertOrUpdate(Object t)
	{
		Class<?> clazz = t.getClass();

		// obtengo el id
		List<Field> lst = MyReflection.clasz.getFields(clazz,(f)->f.getAnnotation(Id.class)!=null);
		return insertOrUpdate(t,new String[]{lst.get(0).getName()});
	}
	
	/** Retorna los objetos que cuyos valores coincidan con $fieldsAndValues */
	public <T> List<T> findBy(Class<T> clazz,Object[] ...fieldsAndValues)
	{
		String hql="";
		hql+="FROM "+clazz.getName()+" x ";
		hql+="WHERE ";
		
		int i=0;
		for(Object[] f:fieldsAndValues)
		{
			String pname="p"+i;
			hql+="x."+f[0].toString()+"=:"+pname+" AND ";
		}

		hql = MyString.substringBeforeLast(hql,"AND");

		i=0;
		TypedQuery<T> q = em.createQuery(hql,clazz);
		for(Object[] x:fieldsAndValues)
		{
			String pname="p"+i;
			q.setParameter(pname,x[1]);
		}
		
		List<T> lst = q.getResultList();
		return lst;
	}
	
	
	
	
	
	/** Inserta o modifica segun exista o no una fila con identicos valores en $fields,
	 * y retorna true si inserta o false si modifica */
	public boolean insertOrUpdate(Object t,String ...fields)
	{
//		Class<?> clazz = t.getClass();
//
//		String hql="";
//		hql+="FROM "+clazz.getName()+" x ";
//		hql+="WHERE ";
//		for(String f:fields)
//		{
//			hql+="x."+f+"=:"+f+" AND ";
//		}
//
//		hql = MyString.substringBeforeLast(hql,"AND");
//
//		Query q = em.createQuery(hql);
//		for(String x:fields)
//		{
//			q.setParameter(x,MyObject.getFieldValue(t,x));
//		}
//		
//		List<?> lst = q.getResultList();
//		
//		if( lst.size()==0 )
//		{
//			em.persist(t);
//			return true;
//		}
//		else
//		{
//			Function<Field,Boolean> func = (f)->f.getAnnotation(Id.class)==null&&(f.getAnnotation(Column.class)!=null||f.getAnnotation(JoinColumn.class)!=null);
//			List<Field> xfields = MyClass.getFields(Perfil.class,func);
//			
//			// update
//			Object p = lst.get(0);
//			MyObject.clone(t,p,xfields);
//			return false;
//		}
		
		return true;
	}
}
