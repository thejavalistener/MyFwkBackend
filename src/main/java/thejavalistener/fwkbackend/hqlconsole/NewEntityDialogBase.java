package thejavalistener.fwkbackend.hqlconsole;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.util.Iterator;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.springframework.beans.factory.annotation.Autowired;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.Metamodel;
import thejavalistener.fwkbackend.DaoSupport;
import thejavalistener.fwkutils.awt.form.MyForm;
import thejavalistener.fwkutils.awt.list.MyComboBox;
import thejavalistener.fwkutils.awt.list.MyComboBoxAdapter;
import thejavalistener.fwkutils.awt.text.MyTextField;
import thejavalistener.fwkutils.awt.variuos.MyAwt;

public abstract class NewEntityDialogBase 
{
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	protected DaoSupport dao;

	protected Window parent;

	protected JDialog dialog;
	protected JPanel contentPane;

	protected MyForm form;
	protected String entityClassname;
	
	protected MyComboBox<Class<?>> cbEntities;
	
	protected abstract void _crearFormDinamico();
	
	
	public Metamodel getMetamodel()
	{
		return em.getMetamodel();
	}
	
	public boolean isEntity(String entityName)
	{
		return getFullEntityName(entityName)!=null;
	}
	
	public String getFullEntityName(String entityName)
	{
		Metamodel  mm = getMetamodel();
		for(Iterator<EntityType<?>> it=mm.getEntities().iterator();it.hasNext();)
		{
			EntityType<?> et = it.next();
			if(et.getName().endsWith(entityName))
			{
				return et.getJavaType().getName();
			}
		}
		
		return null;		
	}
	
	protected void _createStaticGUI()
	{
		form = new MyForm(300);		
		JPanel pp = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pp.setBorder(null);
		pp.add(form.c());
		JScrollPane scrollForm = new JScrollPane();
		scrollForm.setBorder(null);
		scrollForm.setViewportView(pp);
		contentPane = new JPanel(new BorderLayout());
		contentPane.add(scrollForm,BorderLayout.WEST);

		// combobox entities
		cbEntities = new MyComboBox<>();
		cbEntities.c().setPreferredSize(new Dimension(300,cbEntities.c().getPreferredSize().height));
		cbEntities.setComboBoxListener(new EscuchaComboEntities());
		cbEntities.setTToString(clazz->clazz.getSimpleName());

		dialog = new JDialog(this.parent);
		dialog.setModal(true);
		dialog.setResizable(false);
		dialog.add(contentPane,BorderLayout.CENTER);
	}

	
	class EscuchaComboEntities extends MyComboBoxAdapter
	{
		@Override
		public void itemStateChanged(ItemEvent e)
		{
			_crearFormDinamico();

			dialog.pack();
			MyAwt.center(dialog,MyAwt.getMainWindow(parent));
		}
	}	
	
	protected void _agregarMascaraAlTF(String label,MyTextField tf,Class<?> clazz)
	{
		String titulo = "Error";
		String mensaje = "El campo "+label+" debe ser: ";
		
		switch(clazz.getCanonicalName())
		{
			case "char","java.lang.Character":
				tf.addMask(MyTextField.MASK_CHAR);
				tf.addValidation(MyTextField.VALID_CHARACTER,mensaje+"char",titulo);
				
				break;
			case "short","java.lang.Short":
				tf.addMask(MyTextField.MASK_INTEGER);
				tf.addValidation(MyTextField.VALID_SHORT,mensaje+"short",titulo);
				break;
			case "int","java.lang.Integer":
				tf.addMask(MyTextField.MASK_INTEGER);
				tf.addValidation(MyTextField.VALID_INTEGER_OR_ENTPY,mensaje+"int",titulo);
				break;
			case "long","java.lang.Long":
				tf.addMask(MyTextField.MASK_INTEGER);
				tf.addValidation(MyTextField.VALID_LONG,mensaje+"long",titulo);
				break;
			case "float","java.lang.Float":
				tf.addMask(MyTextField.MASK_DOUBLE);
				tf.addValidation(MyTextField.VALID_FLOAT,mensaje+"float",titulo);
				break;
			case "double","java.lang.Double":
				tf.addMask(MyTextField.MASK_DOUBLE);
				tf.addValidation(MyTextField.VALID_DOUBLE_OR_EMPTY,mensaje+"double",titulo);
				break;					
			case "boolean","java.lang.Boolean":
				tf.addMask(MyTextField.MASK_BOOLEAN);
				tf.addValidation(MyTextField.VALID_BOOLEAN,mensaje+"true o false",titulo);
				break;					
			case "java.lang.String":
				break;
			case "java.sql.Date":
			case "java.sql.Timestamp":
				tf.addValidation(MyTextField.VALID_DATE_OR_EMPTY,mensaje+"yyyy-mm-dd, NOW, TODAY o SYSDATE",titulo);
				break;
			case "java.sql.Time":
				break;
		}	
	}


}