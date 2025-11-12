package thejavalistener.fwkbackend.email;

import jakarta.mail.util.ByteArrayDataSource;

public interface MyEmailDatasource
{
	public void init();
	public String getTo(int idx);
	public String[] getCC(int idx);
	public String[] getBCC(int idx);
	public String getFrom();
	public String getSubject(int idx);
	public String getBody(int idx);
	public int size();
	public ByteArrayDataSource getAttachedFile(int idx,StringBuffer fileName);
}
