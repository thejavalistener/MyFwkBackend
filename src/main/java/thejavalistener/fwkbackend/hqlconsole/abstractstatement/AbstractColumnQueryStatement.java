package thejavalistener.fwkbackend.hqlconsole.abstractstatement;

import java.util.List;

public abstract class AbstractColumnQueryStatement extends AbstractQueryStatement<List<Object[]>>
{
	public abstract List<Object[]> process();
}
