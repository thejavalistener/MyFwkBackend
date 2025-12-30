package thejavalistener.fwkbackend.email;

public interface MyEmailController
{
	public void onInit(MyEmailDataSource dataSource);
	public boolean onJobStarting(int currentJob);
	public boolean onJobFinishied(int currentJob);
	public void onDestroy(int emailsSended);
}
