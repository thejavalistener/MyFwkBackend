package thejavalistener.fwkbackend.hqlconsole;

import java.util.List;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import thejavalistener.fwkbackend.hqlconsole.abstractstatement.AbstractColumnQueryStatement;
import thejavalistener.fwkbackend.hqlconsole.abstractstatement.AbstractEntityQueryStatement;
import thejavalistener.fwkbackend.hqlconsole.abstractstatement.AbstractStatement;
import thejavalistener.fwkbackend.hqlconsole.abstractstatement.AbstractUpdateStatement;
import thejavalistener.fwkbackend.hqlconsole.imple.NewStatement;
import thejavalistener.fwkutils.awt.variuos.MyAwt;
import thejavalistener.fwkutils.various.MyCollection;
import thejavalistener.fwkutils.various.MyReflection;

@Component
@Scope("prototype")
public class MyHqlConsole extends MyHqlConsoleBase
{
	@Autowired
	private FactoryStatement factory;
	
	private UpdateListener listener;
	
	@Override
	protected void executeHql(String hql) 
	{
		// si es null no va...
		if(hql==null){return;}

		// analizo qué tipo de sentencia es y retorno lo que venga
		AbstractStatement<?> astm = factory.getStatement(hql);

		// Retorna entities. Por ejemplo: FROM Alumno
		if (astm instanceof AbstractEntityQueryStatement e)
		{
		    List<Object> lst = e.process();
		    
		    if( lst.size()==0)
	    	{
		    	MyAwt.showInformationMessage("No se encontraron datos","Sin Datos",contentPane);				
		    	return;
	    	}
		    
		    List<String> sHeaders = MyReflection.clasz.getAttributes(e.getEntityClass());
		    List<Object[]> rows = MyCollection.extract(lst,o->MyReflection.object.getValues(o).toArray());
		    addResult(hql,rows,sHeaders.toArray());
		    
		} // Retorna campos sueltos. Por ejemplo: SELECT a.legajo, a.nombre FROM Alumno a
		else if (astm instanceof AbstractColumnQueryStatement c)
		{
		    List<Object[]> lst = c.process(); 
		    if( lst.size()==0)
	    	{
		    	MyAwt.showInformationMessage("No se encontraron datos","Sin Datos",contentPane);				
		    	return;
	    	}
		    addResult(hql,lst);
		}
		else if (astm instanceof NewStatement ns)
		{
			ns.setParent(contentPane);
			ns.setUpdateListener(listener);
			ns.process();
		}
		else if (astm instanceof AbstractUpdateStatement u)
		{
			Function<Integer,Boolean> f = (uc)->MyAwt.showConfirmYES_NO("updateCount="+uc+". Confirma la operacion?","COMMIT",contentPane)==0;
			u.setUpdateListener(listener);
			u.setExecuteCommit(f);
			int rtdo = u.process();
			
			if( rtdo<=0 )
			{
				String rolledback = rtdo<0?"ROLLEDBACK! ":"";
				MyAwt.showInformationMessage(rolledback+"Ninguna fila resultó afectada","Sin Cambios",contentPane);
			}
			else
			{
				MyAwt.showInformationMessage(rtdo+" filas resultaron afectadas",u.getDescription(),contentPane);				
			}
		}
	}

	public void setUpdateListener(UpdateListener listener)
	{
		factory.setUpdateListener(listener);
		this.listener=listener;
	}	
}