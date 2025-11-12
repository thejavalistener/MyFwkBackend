package thejavalistener.fwkbackend.hqlconsole;

public class MyAttribute
{
	private String name;
	private String fieldName;
	private Class<?> type;
	private Boolean id;
	private Boolean fk;
	private String fkTableName;
	
	public String getFkTableName()
	{
		return fkTableName;
	}
	public void setFkTableName(String fkTableName)
	{
		this.fkTableName=fkTableName;
	}
	public String getName()
	{
		return name;
	}
	public void setName(String name)
	{
		this.name=name;
	}
	public String getFieldName()
	{
		return fieldName;
	}
	public void setFieldName(String fieldName)
	{
		this.fieldName=fieldName;
	}
	public Class<?> getType()
	{
		return type;
	}
	public void setType(Class<?> type)
	{
		this.type=type;
	}
	public Boolean isId()
	{
		return id;
	}
	public void setId(Boolean id)
	{
		this.id=id;
	}
	public Boolean isFk()
	{
		return fk;
	}
	public void setFk(Boolean fk)
	{
		this.fk=fk;
	}
}
