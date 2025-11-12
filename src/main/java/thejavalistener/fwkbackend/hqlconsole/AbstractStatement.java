package thejavalistener.fwkbackend.hqlconsole;

import java.util.List;
import java.util.function.Function;

import org.springframework.stereotype.Component;

@Component
public abstract class AbstractStatement
{
	/** Retorna la cantidad de filas que devuelve el query o la cantidad de filas afectadas por el update */
	public abstract long process(String hql,List<Object[]> outputRows,Function<Long,Boolean> commit);
}
