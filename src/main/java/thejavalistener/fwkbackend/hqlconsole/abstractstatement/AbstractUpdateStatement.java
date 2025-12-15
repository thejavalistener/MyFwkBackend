package thejavalistener.fwkbackend.hqlconsole.abstractstatement;

import java.util.function.Function;

public abstract class AbstractUpdateStatement extends AbstractStatement<Integer>
{
	public static final int INSERT=1;
	public static final int UPDATE=2;
	public static final int DELETE=3;

	public int updateType;
	public Function<Integer,Boolean> executeCommit;

	public Function<Integer,Boolean> getExecuteCommit()
	{
		return executeCommit;
	}

	public void setExecuteCommit(Function<Integer,Boolean> executeCommit)
	{
		this.executeCommit=executeCommit;
	}

	public int getUpdateType()
	{
		return updateType;
	}

	public void setUpdateType(int updateType)
	{
		this.updateType=updateType;
	}
	
	
}