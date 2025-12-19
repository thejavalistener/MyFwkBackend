package thejavalistener.fwkbackend.hqlconsole.imple; 
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.metamodel.EntityType;
import jakarta.transaction.Transactional;
import thejavalistener.fwkbackend.hqlconsole.abstractstatement.AbstractUpdateStatement;

@Component
@Primary
public class InsertStatement extends AbstractUpdateStatement
{
	@PersistenceContext
	private EntityManager em;

	public InsertStatement()
	{
		setUpdateType(INSERT);
	}
	
	@Override
	@Transactional // (rollbackOn=MyRollbackException.class)
	public Integer process()
	{
		try
		{
			Object x = _procesarInsert(getHql());
			em.persist(x);
			em.flush();	
			
			if( !getExecuteCommit().apply(1) )
			{
				rollback();
				return -1;
			}
			
			
			notifyUpdate(1,getUpdateType());

			return 1;
		}
		catch(Exception e)
		{
			throw new RuntimeException("Error en la sentencia: ["+getHql()+"]",e);
		}
		
	}

	public Object _procesarInsert(String hql)
	{
		try
		{
			hql = hql.trim();
			String hqlLower = hql.toLowerCase();

			if(!hqlLower.startsWith("insert into")) throw new IllegalArgumentException("Sentencia inválida");

			int idxValues = hqlLower.indexOf("values");
			if(idxValues == -1) throw new IllegalArgumentException("Falta cláusula VALUES");

			String cabecera = hql.substring(0, idxValues).trim();
			String valoresRaw = hql.substring(idxValues + 6).trim();

			// Paréntesis opcionales
			String valores;
			if(valoresRaw.startsWith("(") && valoresRaw.endsWith(")"))
			{
				valores = valoresRaw.substring(1, valoresRaw.length() - 1).trim();
			}
			else
			{
				valores = valoresRaw;
			}

			// Extraer clase y alias opcional
			String[] tokens = cabecera.split("\\s+");
			if(tokens.length < 3) throw new IllegalArgumentException("Falta clase.");

			String className = tokens[2];
			String alias = null;

			if(tokens.length >= 4)
			{
				alias = tokens[3];
			}

			// Si no tiene punto, buscar en el Metamodel
			if(!className.contains("."))
			{
				String simpleClassName = className;
				className = null;
				for(EntityType<?> entity : em.getMetamodel().getEntities())
				{
					if(entity.getJavaType().getSimpleName().equalsIgnoreCase(simpleClassName))
					{
						className = entity.getJavaType().getName();
						break;
					}
				}
				if(className == null)
				{
					throw new IllegalArgumentException("Clase no encontrada en el Metamodel: " + simpleClassName);
				}
			}

			// Cargar la clase y crear la instancia
			Class<?> clazz = Class.forName(className);
			Object instancia = clazz.getDeclaredConstructor().newInstance();

			// Separar asignaciones respetando comillas simples
			String[] asignaciones = valores.split(",(?=(?:[^']*'[^']*')*[^']*$)");

			for(String asignacion : asignaciones)
			{
				String[] partesAsignacion = asignacion.split("=");
				if(partesAsignacion.length != 2)
				{
					throw new IllegalArgumentException("Asignación inválida: " + asignacion);
				}

				String campoCompuesto = partesAsignacion[0].trim();
				String valorStr = partesAsignacion[1].trim();

				String path;
				if(alias != null)
				{
					if(!campoCompuesto.startsWith(alias + ".")) continue;
					path = campoCompuesto.substring(alias.length() + 1);
				}
				else
				{
					path = campoCompuesto;
				}

				String[] atributos = path.split("\\.");

				if(atributos.length == 1)
				{
					Field field = clazz.getDeclaredField(atributos[0]);
					field.setAccessible(true);
					Object valor = parseValue(field.getType(), valorStr);
					field.set(instancia, valor);
				}
				else if(atributos.length == 2)
				{
					String relacion = atributos[0];
					String subcampo = atributos[1];

					Field fieldRelacion = clazz.getDeclaredField(relacion);
					fieldRelacion.setAccessible(true);

					Class<?> claseRelacion = fieldRelacion.getType();
					Object objetoRelacionado = claseRelacion.getDeclaredConstructor().newInstance();

					Field fieldSub = claseRelacion.getDeclaredField(subcampo);
					fieldSub.setAccessible(true);
					Object valor = parseValue(fieldSub.getType(), valorStr);
					fieldSub.set(objetoRelacionado, valor);

					fieldRelacion.set(instancia, objetoRelacionado);
				}
				else
				{
					throw new IllegalArgumentException("Profundidad no soportada: " + path);
				}
			}

			return instancia;

		}
		catch(Exception e)
		{
			throw new RuntimeException("Error procesando INSERT: " + hql, e);
		}
	}
	
	
	private static Object parseValue(Class<?> tipo, String valor) throws Exception
	{
		valor = valor.trim();

		if(valor.equalsIgnoreCase("null"))
			return null;

		// STRING
		if(tipo == String.class)
		{
			if(!valor.startsWith("'") || !valor.endsWith("'"))
				throw new IllegalArgumentException("Las cadenas deben ir entre comillas simples: " + valor);

			return valor.substring(1, valor.length() - 1);
		}

		// NUMÉRICOS
		if(tipo == int.class || tipo == Integer.class) return Integer.parseInt(valor);
		if(tipo == long.class || tipo == Long.class) return Long.parseLong(valor);
		if(tipo == boolean.class || tipo == Boolean.class) return Boolean.parseBoolean(valor);
		if(tipo == double.class || tipo == Double.class) return Double.parseDouble(valor);
		if(tipo == float.class || tipo == Float.class) return Float.parseFloat(valor);
		if(tipo == BigDecimal.class) return new BigDecimal(valor);

		// DATE
		if(tipo == java.sql.Date.class)
		{
			if(!valor.startsWith("'") || !valor.endsWith("'"))
				throw new IllegalArgumentException("Date debe ir entre comillas simples: " + valor);

			String v = valor.substring(1, valor.length() - 1);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date d = sdf.parse(v);
			return new java.sql.Date(d.getTime());
		}

		// TIMESTAMP
		if(tipo == java.sql.Timestamp.class)
		{
			if(valor.equalsIgnoreCase("now"))
				return new java.sql.Timestamp(System.currentTimeMillis());

			if(!valor.startsWith("'") || !valor.endsWith("'"))
				throw new IllegalArgumentException("Timestamp debe ir entre comillas simples: " + valor);

			String v = valor.substring(1, valor.length() - 1);

			String pattern;
			if(v.matches("\\d{4}-\\d{2}-\\d{2}_\\d{2}:\\d{2}:\\d{2}"))
				pattern = "yyyy-MM-dd_HH:mm:ss";
			else if(v.matches("\\d{4}-\\d{2}-\\d{2}_\\d{2}:\\d{2}"))
			{
				v += ":00";
				pattern = "yyyy-MM-dd_HH:mm:ss";
			}
			else if(v.matches("\\d{4}-\\d{2}-\\d{2}"))
			{
				v += "_00:00:00";
				pattern = "yyyy-MM-dd_HH:mm:ss";
			}
			else
			{
				throw new IllegalArgumentException("Formato de Timestamp inválido: " + valor);
			}

			SimpleDateFormat sdf = new SimpleDateFormat(pattern);
			java.util.Date d = sdf.parse(v);
			return new java.sql.Timestamp(d.getTime());
		}

		throw new IllegalArgumentException("Tipo no soportado: " + tipo);
	}

}
