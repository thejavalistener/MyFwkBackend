package thejavalistener.fwkbackend.hqlconsole;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import javax.swing.JButton;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.metamodel.EntityType;
import thejavalistener.fwkbackend.hqlconsole.abstractstatement.AbstractStatement;
import thejavalistener.fwkbackend.hqlconsole.imple.InsertStatement;
import thejavalistener.fwkutils.awt.link.MyLink;
import thejavalistener.fwkutils.awt.list.MyComboBox;
import thejavalistener.fwkutils.awt.text.MyTextField;
import thejavalistener.fwkutils.awt.variuos.MyAwt;
import thejavalistener.fwkutils.awt.variuos.MyComponent;
import thejavalistener.fwkutils.string.MyString;
import thejavalistener.fwkutils.various.MyReflection;
import thejavalistener.fwkutils.various.UDate;

@Component
@Scope("prototype")
public class CreateNewEntityDialog extends CreateNewEntityDialogBase
{
	@Autowired
	private FactoryStatement factory;
	
	// combo con entities
	protected Class<?> entityClass;
	
	// componentes del from
	protected List<String> labels = new ArrayList<>();	
	protected List<MyComponent> myComponents = new ArrayList<>();	
	
	// acetar o cancelar en el form
	protected JButton bInsert;
	protected JButton bCancel;

	public CreateNewEntityDialog(JPanel parent)
	{
		this(parent,null);
	}
	
	public CreateNewEntityDialog(JPanel parent,String entityClassname)
	{	
		this.entityClassname = entityClassname;
		this.parent = MyAwt.getMainWindow(parent);

		// creo la parte estática de la interface gráfica
		_createStaticGUI();
	}
	
	@PostConstruct
	public void init()
	{
		// cargo el combo con todas las clases o sólo con 1 
		if( entityClassname==null )
		{
			// itero las entidades y cargo todas
			for(EntityType<?> entityType:getMetamodel().getEntities())
			{
				cbEntities.addItem(entityType.getJavaType());
			}
			cbEntities.setSelectedItem(0);
		}
		else
		{
			// sólo la que me indicaron en línea de comandos
			entityClass = MyReflection.clasz.forName(entityClassname);
			cbEntities.addItem(entityClass);
			cbEntities.setSelectedItem(t->t.equals(entityClass));
			cbEntities.setEnabled(false);
		}
		
		// genera los fields dinámicos en el listener
		// llama a _crearFormDinamico()
		cbEntities.forceItemEvent();		
	}
	
	@Override
	protected void _crearFormDinamico()
	{
		double wL = .3;
		double wC = .7;

		// reseteo el form
		labels.clear();
		myComponents.clear();
		form.reset();

		// primero el combobox en entities
		form.addRow().add(cbEntities.c());
		
		// separador
		form.addSeparator();
		
		// trate todos los campos anotados con @Column o con @ManyToOne omitiendo los finales y estáticos
		Function<Field,Boolean> func = f->{
			boolean esFinalOEstatico = Modifier.isFinal(f.getModifiers())||Modifier.isStatic(f.getModifiers());
			boolean tieneColumn = f.getAnnotation(Column.class)!=null;
			boolean tieneManyToOne = f.getAnnotation(ManyToOne.class)!=null;
			return !esFinalOEstatico && (tieneColumn || tieneManyToOne);
		};
		
		entityClass = cbEntities.getSelectedItem();
		
		// itero los fields de la clase y genero el form
		Field[] fields = MyReflection.clasz.getDeclaredFields(entityClass,func);
		for(int i=0; i<fields.length; i++)
		{
			Field field = fields[i];
			Class<?> typeClass = field.getType();
			String label = field.getName();
			
			JButton btnNew = null;

			MyComponent myComponent;
			
			if(field.getAnnotation(Column.class)!=null) //local
			{
				MyTextField textField = new MyTextField();
				_agregarMascaraAlTF(label,textField,typeClass);
				myComponent = textField;
				
				boolean esId = field.getAnnotation(Id.class)!=null;
				boolean esAutoIncremental = field.getAnnotation(GeneratedValue.class)!=null;
				if(  esId && esAutoIncremental )
				{
					textField.setEnabled(false);
				}
			}
			else // foraneo: comboBox
			{
				List<Object> items = dao.queryMultipleRows("FROM "+typeClass.getName());
				MyComboBox<Object> comboBox = new MyComboBox<>();
				comboBox.setSpecialItem(" ");
				comboBox.setItems(items);
				myComponent = comboBox;
				btnNew = new JButton("New");

				btnNew.addActionListener(new EscuchaNew(label,typeClass));
			}
			
			labels.add(label);
			myComponents.add(myComponent);
			
			MyLink lbl = new MyLink(label);
//			MyAwt.setPreferredWidth(80,lbl);
			
			if(btnNew!=null)
			{
				double w1 = wC*0.7;
				double w2 = wC*0.3;
//				form.addRow().add(lbl.c(),wL).add(myComponent.c(),w1).add(btnNew,w2);
				form.addRow().add(lbl.c()).add(myComponent.c()).add(btnNew).layout(wL,w1,w2);
			}
			else
			{
//				form.addRow().add(lbl.c(),wL).add(myComponent.c(),wC);				
				form.addRow().add(lbl.c()).add(myComponent.c()).layout(wL,wC);				
			}
		}
		
		form.addSeparator();
		
		// botones cancelar/insertar
		bCancel = new JButton("Cancel");
		bCancel.addActionListener(new EscuchaCancel());
		bInsert = new JButton("Insert");
		bInsert.addActionListener(new EscuchaInsert());
		
		form.addRow().add(bCancel).add(bInsert);
		
		form.makeForm();
	}
	
	private Object _generarObject()
	{
		try
		{
			Class<?> entityClass = cbEntities.getSelectedItem();
			
			Object ret = entityClass.getConstructor().newInstance();
			Field[] fields = MyReflection.clasz.getDeclaredFields(entityClass,f->f.getAnnotation(Column.class)!=null||f.getAnnotation(ManyToOne.class)!=null);
			for(Field field:fields)
			{
				int idx = labels.indexOf(field.getName());
				Object value = myComponents.get(idx).getValue();

				if( MyReflection.clasz.isFinalClass(field.getType()))
				{
					String sValue = (String)value;
					if( sValue==null ) continue;
					
					value = MyString.parseTo(sValue,field.getType());
				}
				
				MyReflection.object.invokeSetter(ret,field.getName(),value);					

			}
			
			return ret;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
		
	class EscuchaNew implements ActionListener
	{
		private String label;
		private Class<?> typeClass;
		public EscuchaNew(String lbl,Class<?> typeClass)
		{
			this.label = lbl;
			this.typeClass = typeClass;
		}
		
		@Override
		public void actionPerformed(ActionEvent e)
		{
//			Object newObject = getMyApp().showScreen(HQLCreateNewEntity.class).pack().apply();
			
			List<Object> items = dao.queryMultipleRows("FROM "+typeClass.getName());
			int idx = labels.indexOf(label);
			MyComboBox<Object> cb = (MyComboBox)myComponents.get(idx);
			cb.setItems(items);
			
//			cb.setSelectedItem(t->_equalsById(t,newObject));
		}

		private boolean _equalsById(Object a, Object b)
		{
			if(a==null&&b==null) return true;
			if(a==null&&b!=null||a!=null&&b==null) return false;
	
			Field aId=MyReflection.clasz.getDeclaredField(a.getClass(),Id.class);
			Field bId=MyReflection.clasz.getDeclaredField(b.getClass(),Id.class);
	
			Object aV=MyReflection.object.invokeGetter(a,aId.getName());
			Object bV=MyReflection.object.invokeGetter(b,bId.getName());
	
			return aV.equals(bV);
		}

	}

	class EscuchaCancel implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			dialog.setVisible(false);
		}
	}
	
	private void _resetTextFields()
	{
		myComponents.forEach((c)->c.resetValue());
		myComponents.get(0).requestFocus();		
	}

	class EscuchaInsert implements ActionListener
	{
		@Override
		public void actionPerformed(ActionEvent e)
		{
			try
			{
				for(MyComponent cmp:myComponents)
				{
					if( cmp.getClass().isAssignableFrom(MyTextField.class) )
					{
						MyTextField tf = (MyTextField)cmp;
						tf.runValidations();
					}
				}
				
				// --------------------- ACAAAAAAAAAAAA -----------------
				Object obj = _generarObject();
				String hql = _objectToHQLInsert(obj);
				InsertStatement stm = (InsertStatement)factory.getStatement(hql); 
				stm.setExecuteCommit(f->true);
				stm.process();
						
				//facade.persist(obj);
				
				Field fId = MyReflection.clasz.getDeclaredField(obj.getClass(),Id.class);
				Object oId = MyReflection.object.invokeGetter(obj,fId.getName());
				String sClass = obj.getClass().getSimpleName();
				String msg = "Se creó una instancia de "+sClass+" con "+fId.getName()+"="+oId.toString();
				MyAwt.showInformationMessage(msg,"Se creó un nuevo objeto",contentPane);
				
				_resetTextFields();				
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
//	public static String _objectToHQLInsert(Object o)
//	{
//		String entityName = o.getClass().getSimpleName();
//		List<Field> fields = MyReflection.clasz.getFields(o.getClass(),f->f.getAnnotation(Column.class)!=null);
//		
//		List<Object> values = MyReflection.object.getValues(o);
//		String hql="INSERT INTO "+entityName+" VALUES ";
//
//		for(int i=0; i<fields.size();i++)
//		{
//			hql+=fields.get(i).getName()+"="+values.get(i);
//			hql+=i<fields.size()-1?", ":"";
//		}
//		
//		return hql;
//	}

//	public static String _objectToHQLInsert(Object o)
//	{
//		String entityName = o.getClass().getSimpleName();
//		List<Field> fields = MyReflection.clasz.getFields(
//			o.getClass(),
//			f -> f.getAnnotation(Column.class) != null
//		);
//
//		StringBuilder hql = new StringBuilder();
//		hql.append("INSERT INTO ").append(entityName).append(" VALUES ");
//
//		for(int i = 0; i < fields.size(); i++)
//		{
//			Field f = fields.get(i);
//			f.setAccessible(true);
//
//			Object value;
//			try
//			{
//				value = f.get(o);
//			}
//			catch(Exception e)
//			{
//				throw new RuntimeException(e);
//			}
//
//			hql.append(f.getName()).append("=");
//
//			if(value == null)
//			{
//				hql.append("null");
//			}
//			else if(value instanceof String)
//			{
//				hql.append("'").append(value).append("'");
//			}
//			else if(value instanceof java.sql.Date)
//			{
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//				hql.append(sdf.format((java.sql.Date)value));
//			}
//			else if(value instanceof java.sql.Timestamp)
//			{
//				// degradar a Date para que parseValue lo acepte
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//				hql.append(sdf.format((java.sql.Timestamp)value));
//			}
//			else
//			{
//				hql.append(value);
//			}
//
//			if(i < fields.size() - 1)
//				hql.append(", ");
//		}
//
//		return hql.toString();
//	}
	
//	public static String _objectToHQLInsert(Object o)
//	{
//		String entityName = o.getClass().getSimpleName();
//		List<Field> fields = MyReflection.clasz.getFields(
//			o.getClass(),
//			f -> f.getAnnotation(Column.class) != null
//		);
//
//		StringBuilder hql = new StringBuilder();
//		hql.append("INSERT INTO ").append(entityName).append(" VALUES ");
//
//		for(int i = 0; i < fields.size(); i++)
//		{
//			Field f = fields.get(i);
//			f.setAccessible(true);
//
//			Object value;
//			try
//			{
//				value = f.get(o);
//			}
//			catch(Exception e)
//			{
//				throw new RuntimeException(e);
//			}
//
//			hql.append(f.getName()).append("=");
//
//			if(value == null)
//			{
//				continue;
////				hql.append("null");
//			}
//			else if(value instanceof String)
//			{
//				hql.append("'").append(value).append("'");
//			}
//			else if(value instanceof java.sql.Date)
//			{
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//				hql.append("'").append(sdf.format((java.sql.Date)value)).append("'");
//			}
//			else if(value instanceof java.sql.Timestamp)
//			{
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
//				hql.append("'").append(sdf.format((java.sql.Timestamp)value)).append("'");
//			}
//			else
//			{
//				hql.append(value);
//			}
//
//			if(i < fields.size() - 1)
//				hql.append(", ");
//		}
//
//		return hql.toString();
//	}

	public static String _objectToHQLInsert(Object o)
	{
		String entityName = o.getClass().getSimpleName();
		List<Field> fields = MyReflection.clasz.getFields(
			o.getClass(),
			f -> f.getAnnotation(Column.class) != null
		);

		StringBuilder hql = new StringBuilder();
		hql.append("INSERT INTO ").append(entityName).append(" VALUES ");

		boolean first = true;

		for(Field f : fields)
		{
			f.setAccessible(true);

			Object value;
			try
			{
				value = f.get(o);
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}

			if(value == null)
				continue;

			if(!first)
				hql.append(", ");
			first = false;

			hql.append(f.getName()).append("=");

			if(value instanceof String)
			{
				hql.append("'").append(value).append("'");
			}
			else if(value instanceof java.sql.Date)
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				hql.append("'").append(sdf.format((java.sql.Date)value)).append("'");
			}
			else if(value instanceof java.sql.Timestamp)
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
				hql.append("'").append(sdf.format((java.sql.Timestamp)value)).append("'");
			}
			else
			{
				hql.append(value);
			}
		}

		return hql.toString();
	}
	
	
	public static void main(String[] args)
	{
		Alumno a = new Alumno();
		a.setIdAlumno(3);
		a.setNombre("Pablito");
		a.setFechaNacimiento(new UDate().toSqlDate());
		
		String hql = _objectToHQLInsert(a);
		System.out.println(hql);
	}
	
}