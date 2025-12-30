package thejavalistener.fwkbackend.email;

public interface MyEmailDataSource
{
	public void init();
	public String getFrom();
	public String getSubject(int idx);
	public String getText(int idx);
	public String[] getTo(int idx);
	public String[] getCC(int idx);
	public String[] getBCC(int idx);
	public MyEmailAttachment[] getAttachments(int idx);
	public int size();
}
