package thejavalistener.fwkbackend.hqlconsole.abstractstatement;

import java.util.List;

public abstract class AbstractEntityQueryStatement extends AbstractStatement<List<Object>>
{
	public abstract List<Object> process();
}
