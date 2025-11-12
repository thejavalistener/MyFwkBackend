package thejavalistener.fwkbackend.hqlconsole;
import java.io.PrintStream;
import java.io.PrintWriter;

public class MyRollbackException extends RuntimeException
{
	public MyRollbackException()
	{
		super();
	}
	
	@Override
	public void printStackTrace()
	{
	}
	@Override
	public void printStackTrace(PrintStream p)
	{
	}
	
	@Override
	public String getMessage()
	{
		return "";
	}
	
	@Override
	public void printStackTrace(PrintWriter s)
	{
	}
	
	@Override
	public String getLocalizedMessage()
	{
		return "";
	}
	
	@Override
	public void setStackTrace(StackTraceElement[] stackTrace)
	{
	}
	
	public static void main(String[] args)
	{
		RuntimeException t = new MyRollbackException();
		throw t;
	}
}
