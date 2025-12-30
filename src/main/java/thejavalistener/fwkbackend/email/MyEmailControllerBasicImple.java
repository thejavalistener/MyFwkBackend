package thejavalistener.fwkbackend.email;

public class MyEmailControllerBasicImple implements MyEmailController
{
	@Override
	public void onInit(MyEmailDataSource dataSource)
	{
	}

	@Override
	public boolean onJobStarting(int currentJob)
	{
		return true;
	}

	@Override
	public boolean onJobFinishied(int currentJob)
	{
		return true;
	}

	@Override
	public void onDestroy(int sended)
	{
	}
}
