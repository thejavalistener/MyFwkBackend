package thejavalistener.fwkbackend.hqlconsole.abstractstatement;

import java.util.function.Function;

import org.springframework.transaction.interceptor.TransactionAspectSupport;

import thejavalistener.fwkbackend.hqlconsole.UpdateListener;

public abstract class AbstractUpdateStatement extends AbstractStatement<Integer>
{
	public static final int INSERT=1;
	public static final int UPDATE=2;
	public static final int DELETE=3;
	
	private UpdateListener listener;

	public int updateType;
	public Function<Integer,Boolean> executeCommit;

	public Function<Integer,Boolean> getExecuteCommit()
	{
		return executeCommit;
	}
	
	public String getDescription()
	{
		int t=getUpdateType();
		return t==INSERT?"INSERT":t==DELETE?"DELETE":"UPDATE";
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
	
	public void setUpdateListener(UpdateListener listener)
	{
		this.listener = listener;
	}
	
	protected void notifyUpdate(int updateType,int updateCount)
	{
		if( listener!=null &&updateCount>0 ) listener.onDataChanged(updateType,updateCount);
	}

	protected void rollback()
	{
		TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
	}
}