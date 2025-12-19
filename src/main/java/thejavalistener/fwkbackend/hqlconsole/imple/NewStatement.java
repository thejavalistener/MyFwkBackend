package thejavalistener.fwkbackend.hqlconsole.imple;

import java.util.List;

import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import thejavalistener.fwkbackend.DaoSupport;
import thejavalistener.fwkbackend.hqlconsole.NewEntityDialog;
import thejavalistener.fwkbackend.hqlconsole.abstractstatement.AbstractUpdateStatement;
import thejavalistener.fwkutils.awt.list.MyComboBox;
import thejavalistener.fwkutils.awt.variuos.MyAwt;
import thejavalistener.fwkutils.string.MyString;

@Component
@Scope("prototype")
public class NewStatement extends AbstractUpdateStatement
{
	@Autowired
	private ApplicationContext ctx;
	
	public NewStatement()
	{
		setUpdateType(INSERT);
	}
	
	@Autowired
	private DaoSupport dao;
	
	private JPanel parent;

	@Override
	public Integer process()
	{
	    NewEntityDialog dlg;

	    String hql = getHql();
	    List<String> words = MyString.wordList(hql);

	    switch (words.size())
	    {
	        case 1:
	            dlg = ctx.getBean(NewEntityDialog.class, parent);
	            break;

	        case 2:
	            String entityName = words.get(1);
	            String fullEntityName = dao.isEntity(entityName);

	            if (fullEntityName == null)
	            {
	                MyAwt.showErrorMessage(
	                    "No existe la entidad: " + entityName + ".",
	                    "Error",
	                    parent
	                );
	                return 0;
	            }

	            dlg = ctx.getBean(NewEntityDialog.class, parent, fullEntityName);
	            break;

	        default:
	            MyAwt.showErrorMessage(
	                "La sintaxis es: new [EntityName].",
	                "Error",
	                parent
	            );
	            return 0;
	    }

	    // abro la ventana recursiva
	    dlg.setOnNewRequested((entity,i) -> 
	    {
	        NewEntityDialog d = ctx.getBean(NewEntityDialog.class, parent, entity);
		    d.setVisible(true);
		    
		    Object nuevo = d.getLastEntityCreated();
		    dlg.setNewManyToOne(nuevo,i);
		    
	    });

	    dlg.setVisible(true);

	    return 0;
	}
	
	
	public JPanel getParent()
	{
		return parent;
	}

	public void setParent(JPanel parent)
	{
		this.parent=parent;
	}

}