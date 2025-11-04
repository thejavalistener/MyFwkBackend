package thejavalistener.fwkbackend.email;

import org.springframework.mail.SimpleMailMessage;

public interface MyEmailController
{
	public void onInit(MyEmailDatasource dataSource);
	public boolean onJobStarting(int currentJob);
	public boolean onJobFinishied(int currentJob,SimpleMailMessage message);
	public void onDestroy(int emailsSended);
}
