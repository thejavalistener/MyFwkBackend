package thejavalistener.fwkbackend.hqlconsole.abstractstatement;

import java.util.List;

import thejavalistener.fwkutils.string.MyString;

public abstract class AbstractQueryStatement<R> extends AbstractStatement<R>
{
	private int limit = 0;

	@Override
	public String getHql()
	{
		String hql = super.getHql();

		// Proceso la lapalbra LIMIT 
		int limit = _procesarLIMIT(hql);
		if( limit>=0 )
		{
			this.limit = limit;
			hql = _removerLIMIT(hql);
		}
		
		return hql;
	}
	
	private int _procesarLIMIT(String sql)
	{
		String aux = sql.toLowerCase().replace('\n',' ').trim();
		List<String> words = MyString.wordList(aux);
		int n = words.size();
		if( n>1 && words.get(n-2).equalsIgnoreCase("limit") )
		{
			return Integer.parseInt(words.get(n-1));
		}
		
		return -1;
	}
	
	public String _removerLIMIT(String sql)
	{
		int p = sql.toLowerCase().lastIndexOf("limit");
		return sql.substring(0,p);
	}	

	public int getLimit()
	{
		return limit;
	}
	
	public boolean hasLimit()
	{
		return limit>0;
	}
}
