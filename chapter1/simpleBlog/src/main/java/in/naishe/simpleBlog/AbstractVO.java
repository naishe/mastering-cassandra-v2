package in.naishe.simpleBlog;

import in.naishe.simpleBlog.CassandraConnection.SessionWrapper;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.Result;

public abstract class AbstractVO<T extends AbstractVO<T>> {
	
	abstract protected T getInstance();
	abstract protected Class<T> getType();
	
	protected Mapper<T> getMapper(SessionWrapper sessionWrapper){
		return sessionWrapper.getMapper(getType());
	}
	
	public void save(SessionWrapper sessionWrapper){
		getMapper(sessionWrapper).save(getInstance());
	}
	
	public void delete(SessionWrapper sessionWrapper){
		getMapper(sessionWrapper).delete(getInstance());
	}
	
	public T get(SessionWrapper sessionWrapper, Object... primaryKeyComponents ){
		return getMapper(sessionWrapper).get(primaryKeyComponents);
	}
	
	public Result<T> map(SessionWrapper sessionWrapper, ResultSet resultSet){
		return getMapper(sessionWrapper).map(resultSet);
	}
}
