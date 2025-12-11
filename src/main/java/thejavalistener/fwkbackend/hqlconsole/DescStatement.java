package thejavalistener.fwkbackend.hqlconsole;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.metamodel.EntityType;
import thejavalistener.fwkbackend.hqlconsole.abstractstatement.AbstractColumnQueryStatement;
import thejavalistener.fwkutils.string.MyString;
import thejavalistener.fwkutils.various.MyCollection;

@Component
public class DescStatement extends AbstractColumnQueryStatement
{
	@PersistenceContext
	private EntityManager em = null;

	private Set<EntityType<?>> entities = null;
	
	@Override
	public List<Object[]> process()
	{
		List<Object[]> ret = new ArrayList<>();
		
		// obtengo todas las entidades definidas en el contexto
		entities = em.getMetamodel().getEntities();

		// spliteo el sql esperando "DESC" o "DESC Alumno"
		List<String> words = MyString.wordList(getHql());
		int w = words.size();
		
		switch(w)
		{
			case 1: // DESC
				_procesarDesc(ret);
				break;
			case 2: // DESC Alumno
				_procesarDescEntity(words.get(1),ret);
				break;
			default:
				throw new RuntimeException("Modo de uso: DESC [AlgunaEntidad]");
		}
		
		return ret;
	}
	
	private long _procesarDesc(List<Object[]> outputRows)
	{
		entities.forEach((b)->
		{
			EntityType<?> et = b;
			MyEntity e = new MyEntity(et.getJavaType());
			outputRows.add(new Object[]{e.getClassName(),e.getTableName().toUpperCase()});
		});
		
		return entities.size();
	}
	
	private long _procesarDescEntity(String entity,List<Object[]> outputRows)
	{
		EntityType<?> x = MyCollection.findElm(entities,(et)->et.getJavaType().getSimpleName().equals(entity));
		if( x==null )
		{
			throw new RuntimeException("La entidad ["+entity+"] no está mappeada");
		}

		MyEntity e = new MyEntity(x.getJavaType());
		List<MyAttribute> atts = e.getAttributes();
		for(MyAttribute att:atts)
		{
			String add = att.isId()?" (*)":att.isFk()?" (fk)":"";
			outputRows.add(new Object[]{att.getName()+add
					            ,att.getType().getSimpleName()
					            ,att.getFieldName().toUpperCase()
			});					
		}
		
		return atts.size();
	}
}
