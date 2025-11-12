package thejavalistener.fwkbackend.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;

public class MyEmailDatasourceSender
{
	@Autowired
	private JavaMailSender mailSender;
	
	public void sendEmails(MyEmailDatasource ds)
	{
		sendEmails(ds,new MyEmailControllerBasicImple());
	}
	
	public void sendEmails(MyEmailDatasource ds,MyEmailController controller)
	{
		try
		{
			ds.init();
			controller.onInit(ds);
			
			SimpleMailMessage message = new SimpleMailMessage();
			
	    	StringBuffer attachedContentFilename = new StringBuffer();
	    	
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
			    	
					MimeMessage mime = mailSender.createMimeMessage();
					MimeMessageHelper helper = new MimeMessageHelper(mime,true);

					// from
				    helper.setFrom(ds.getFrom());
					
					// to
					String to = ds.getTo(i);
			    	helper.setTo(to);
			    	message.setTo(to);
			    	
			    	// cc
			    	String[] cc = ds.getCC(i);
			    	if( cc!=null )
			    	{
			    		message.setCc(ds.getCC(i));
			    		helper.setCc(ds.getCC(i));
			    	}
			    	
			    	// bcc
			    	String[] bcc = ds.getBCC(i);
			    	if( bcc!=null )
			    	{
				    	message.setBcc(ds.getBCC(i));
					    helper.setBcc(ds.getBCC(i));
			    	}
			    	
			    	// subject
			    	String subject = ds.getSubject(i);
			    	helper.setSubject(subject);
			    	message.setSubject(subject);
			    	
			    	// body
			    	String body = ds.getBody(i);
			    	helper.setText(body);
			    	message.setText(body);
			    	
			    	// attach?
			    	attachedContentFilename.delete(0,attachedContentFilename.length());
			    	ByteArrayDataSource baDs = ds.getAttachedFile(i,attachedContentFilename);
			    	if( baDs!=null )
			    	{
				    	helper.addAttachment(attachedContentFilename.toString(),baDs);				    		
			    	}
			    	
			    	// lo envio
			    	mailSender.send(mime);	
			    	
			    	sendedEmails++;
			    	
			    	// notifico el trabajo
			    	if( !controller.onJobFinishied(i+1,message) )
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
