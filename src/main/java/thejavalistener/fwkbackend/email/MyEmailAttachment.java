package thejavalistener.fwkbackend.email;

import jakarta.mail.util.ByteArrayDataSource;

public interface MyEmailAttachment
{
    String getFileName();
    ByteArrayDataSource getDataSource() throws Exception;
}
