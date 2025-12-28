package thejavalistener.fwkbackend.email;

import org.springframework.mail.SimpleMailMessage;

public class MyEmailControllerBasicImple implements MyEmailController
{
	@Override
	public void onInit(MyEmailDatasource dataSource)
	{
	}

	@Override
	public boolean onJobStarting(int currentJob)
	{
		return true;
	}

	@Override
	public boolean onJobFinishied(int currentJob, SimpleMailMessage message)
	{
		return true;
	}

	@Override
	public void onDestroy(int sended)
	{
	}
}
