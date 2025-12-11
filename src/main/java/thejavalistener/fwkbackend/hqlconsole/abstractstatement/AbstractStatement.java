package thejavalistener.fwkbackend.hqlconsole.abstractstatement;

public abstract class AbstractStatement<R>
{
	private String hql;
	
	public abstract R process();

	public String getHql()
	{
		return hql;
	}

	public void setHql(String hql)
	{
		this.hql=hql;
	}
}
