package thejavalistener.fwkbackend.email;

import jakarta.mail.util.ByteArrayDataSource;

public class InMemoryEmailAttachment implements MyEmailAttachment
{
    private final String fileName;
    private final byte[] data;
    private final String contentType;

    public InMemoryEmailAttachment(String fileName, byte[] data, String contentType)
    {
        this.fileName = fileName;
        this.data = data;
        this.contentType = contentType;
    }

    @Override
    public String getFileName()
    {
        return fileName;
    }

    @Override
    public ByteArrayDataSource getDataSource()
    {
        return new ByteArrayDataSource(data, contentType);
    }
}
