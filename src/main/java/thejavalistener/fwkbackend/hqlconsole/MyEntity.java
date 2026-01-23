package thejavalistener.fwkbackend.hqlconsole;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import thejava.listener.fwkutils.log.MyLogs;
import thejavalistener.fwkutils.string.MyString;

public class MyEntity
{
	private String tableName;
	private String className;
	private String packageName;
	private List<MyAttribute> attributes;
	
	public MyEntity(Object bean)
	{
		this(bean.getClass());
	}
	public MyEntity(Class<?> clazz)
	{
		attributes = new ArrayList<>();

		_introspect(clazz);
	}

	private void _introspect(Class<?> clazz)
	{		
		className = clazz.getSimpleName();
		
		Table annTable = clazz.getAnnotation(Table.class);
		tableName = annTable!=null?annTable.name():className;
		
		// determino si es un supertipo
		MyLogs.get().debug("Acá debería mejorar MyEntity para buscar el supertype recursivamente...");
		if( clazz.getAnnotation(DiscriminatorValue.class)!=null )
		{
			tableName = "ST->"+clazz.getSuperclass().getSimpleName();
		}
		
		packageName = clazz.getPackageName();
		
		for(Field f:clazz.getDeclaredFields())
		{
			Column annColumn = f.getAnnotation(Column.class);
			JoinColumn annJoinColumn = f.getAnnotation(JoinColumn.class);
			Id annId = f.getAnnotation(Id.class);
			

			// es local field
			if( annColumn!=null )
			{
				MyAttribute att = new MyAttribute();
				String fieldName = MyString.ifEmtyOrNull(annColumn.name(),f.getName());
				att.setName(f.getName());
				att.setFieldName(fieldName);
				att.setId(annId!=null);
				att.setFk(false);
				att.setFkTableName(null);
				att.setType(f.getType());
				attributes.add(att);
			}
			else	
			{
				if( annJoinColumn!=null )
				{
					String fieldName = MyString.ifEmtyOrNull(annJoinColumn.name(),f.getName());

					MyAttribute att = new MyAttribute();
					att.setName(f.getName());
					att.setFieldName(fieldName);
					att.setId(false);
					att.setFk(true);
					att.setFkTableName(f.getType().getAnnotation(Table.class).name());
					att.setType(f.getType());
					attributes.add(att);
				}				
			}
		}
	}
	
	public String getPackageName()
	{
		return packageName;
	}

	public String getTableName()
	{
		return tableName;
	}
	public String getClassName()
	{
		return className;
	}

	public List<MyAttribute> getAttributes()
	{
		return attributes;
	}
}
