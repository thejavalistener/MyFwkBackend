package thejavalistener.fwkbackend.hqlconsole.abstractstatement;

import java.util.List;

public abstract class AbstractEntityQueryStatement extends AbstractQueryStatement<List<Object>>
{
	private Class<?> entityClass;
	
	public abstract List<Object> process();

	public Class<?> getEntityClass()
	{
		return entityClass;
	}

	public void setEntityClass(Class<?> entityClass)
	{
		this.entityClass=entityClass;
	}
}
