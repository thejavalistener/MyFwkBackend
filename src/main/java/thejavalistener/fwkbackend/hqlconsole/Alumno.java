package thejavalistener.fwkbackend.hqlconsole;

import java.sql.Date;
import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Id;

public class Alumno
{
	@Id
	@Column
	private int idAlumno;
	
	@Column
	private String nombre;
	
	@Column
	private Date fechaNacimiento;

	@Column
	private Timestamp ts;

	public int getIdAlumno()
	{
		return idAlumno;
	}

	public void setIdAlumno(int idAlumno)
	{
		this.idAlumno=idAlumno;
	}

	public String getNombre()
	{
		return nombre;
	}

	public void setNombre(String nombre)
	{
		this.nombre=nombre;
	}

	public Date getFechaNacimiento()
	{
		return fechaNacimiento;
	}

	public void setFechaNacimiento(Date fechaNacimiento)
	{
		this.fechaNacimiento=fechaNacimiento;
	}

	public Timestamp getTs()
	{
		return ts;
	}

	public void setTs(Timestamp ts)
	{
		this.ts=ts;
	}


	
}