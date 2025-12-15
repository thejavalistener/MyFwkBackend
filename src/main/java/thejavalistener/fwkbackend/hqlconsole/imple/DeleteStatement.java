package thejavalistener.fwkbackend.hqlconsole.imple;
import org.springframework.stereotype.Component;

@Component
public class DeleteStatement extends UpdateStatement
{
	public DeleteStatement()
	{
		setUpdateType(DELETE);
	}
}
