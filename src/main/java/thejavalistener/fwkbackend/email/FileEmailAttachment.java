package thejavalistener.fwkbackend.email;

import java.nio.file.Files;
import java.nio.file.Path;

import jakarta.mail.util.ByteArrayDataSource;

public class FileEmailAttachment implements MyEmailAttachment
{
    private final Path path;

    public FileEmailAttachment(Path path)
    {
        this.path = path;
    }

    @Override
    public String getFileName()
    {
        return path.getFileName().toString();
    }

    @Override
    public ByteArrayDataSource getDataSource() throws Exception
    {
        String contentType = Files.probeContentType(path);
        if (contentType == null)
            contentType = "application/octet-stream";

        return new ByteArrayDataSource(
            Files.readAllBytes(path),
            contentType
        );
    }
}
