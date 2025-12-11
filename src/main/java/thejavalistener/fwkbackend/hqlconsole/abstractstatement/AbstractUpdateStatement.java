package thejavalistener.fwkbackend.hqlconsole.abstractstatement;

import java.util.function.Function;

public abstract class AbstractUpdateStatement extends AbstractStatement<Integer>
{
	public Function<Integer,Boolean> executeCommit;

	public Function<Integer,Boolean> getExecuteCommit()
	{
		return executeCommit;
	}

	public void setExecuteCommit(Function<Integer,Boolean> executeCommit)
	{
		this.executeCommit=executeCommit;
	}
}