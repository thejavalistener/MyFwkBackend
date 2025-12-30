package thejavalistener.fwkbackend.email;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import thejavalistener.fwkutils.various.MyException;

public class MyEmail
{
	@Autowired
	private JavaMailSender javaMailSender;

	public void send(String from, String to, String subject, String text)
	{
		send(from,to,subject,text,null,null,null);
	}
	
	public void send(String from, String to, String subject, String text,String attachFullFilename)
	{
		send(from,to,subject,text,null,null,attachFullFilename);
	}

	public void send(String from, String to, String subject, String text, String cc, String bcc)
	{
		send(from,to,subject,text,cc,bcc,null);
	}
	public void send(String from, String to, String subject, String text, String cc, String bcc, String attachFullFilename)
	{
		String[] acc=(cc!=null)?new String[] {cc}:null;
		String[] abcc=(bcc!=null)?new String[] {bcc}:null;
		String[] aAtt=(attachFullFilename!=null)?new String[] {attachFullFilename}:null;

		send(from,new String[] {to},subject,text,acc,abcc,aAtt);
	}

	public void send(String from, String[] to, String subject, String text, String[] cc, String[] bcc, String[] attachFullFilenames)
	{
		try
		{
			MimeMessage mime=javaMailSender.createMimeMessage();
			MimeMessageHelper helper=new MimeMessageHelper(mime,true);

			MyException.throwIf(() -> from==null||from.length()==0,"from no puede ser null ni vacío");
			MyException.throwIf(() -> to == null || to.length == 0,"to no puede ser null ni vacío");

			helper.setFrom(from);
			helper.setTo(to);

			if(cc!=null) helper.setCc(cc);
			if(bcc!=null) helper.setBcc(bcc);

			helper.setSubject(subject);
			helper.setText(text,false);

			// adjuntos
			if(attachFullFilenames!=null)
			{
				for(String filename:attachFullFilenames)
				{
					if(filename==null) continue;

					Path path=Paths.get(filename);

//					ByteArrayDataSource ds=new ByteArrayDataSource(Files.readAllBytes(path),Files.probeContentType(path));
					String contentType = Files.probeContentType(path);
					if (contentType == null)
					{
					    contentType = "application/octet-stream";
					}

					ByteArrayDataSource ds = new ByteArrayDataSource(Files.readAllBytes(path),contentType);

					helper.addAttachment(path.getFileName().toString(),ds);
				}
			}

			javaMailSender.send(mime);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	
	public void send(MyEmailDataSource ds)
	{
		send(ds,new MyEmailControllerBasicImple());
	}
	
	public void send(MyEmailDataSource ds,MyEmailController controller)
	{
		try
		{
			ds.init();
			controller.onInit(ds);
			
	    	
	    	int n=ds.size();
	    	int i=0;
	    	int sendedEmails = 0;
		    for(; i<n; i++)
		    {
				try
				{
			    	if( !controller.onJobStarting(i) )
		    		{
			    		i=n;
			    		continue;
		    		}
			    	
					String ffrom = ds.getFrom();
					String[] to = ds.getTo(i);
			    	String subject = ds.getSubject(i);
			    	String text = ds.getText(i);
			    	String[] cc = ds.getCC(i);
			    	String[] bcc = ds.getBCC(i);
			    	String[] attach = ds.getAttachedFullFilename(i);
			    	
			    	// envío el mail
			    	send(ffrom,to,subject,text,cc,bcc,attach);	
			    	
			    	sendedEmails++;
			    	
			    	// notifico el trabajo
			    	if( !controller.onJobFinishied(i+1) )
			    	{
			    		i = n;
			    	}
				}
				catch(Exception e)
				{
					e.printStackTrace();
					throw new RuntimeException(e);
				}
		    }
		    
		    controller.onDestroy(sendedEmails);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

}
