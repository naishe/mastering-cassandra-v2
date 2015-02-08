package in.naishe.simpleBlog;

import java.io.Closeable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;
import com.datastax.driver.mapping.Mapper;
import com.datastax.driver.mapping.MappingManager;

public class CassandraConnection {
	private static final Logger log = Logger.getLogger(CassandraConnection.class);
	private static Cluster cluster = getCluster();
	
	public static final Session getSession(){
		if ( cluster == null ){
			cluster = getCluster();
		}
		
		return cluster.connect();
	}
	
	private static Cluster getCluster(){
		Cluster clust = Cluster
						.builder()
						.addContactPoint(Constants.HOST)
						.build();
		return clust;
	}
	
	public static final void closeClusterConnection(){
		if ( cluster != null && !cluster.isClosed()){
			try{
				cluster.close();
			}catch(Exception e){
				/*ignore*/
				log.warn("Error closing cluster connection", e);
			}
		}
	}
	
	/**
	 * This is just an utility class to make sure that I do not create
	 * Mapper again and again (which recreates same PreparedStatement)
	 * causing the driver library to throw ugly warning about inefficiency
	 * in recreating statement.
	 * 
	 * This class makes sure for a given session and class, you create mappers
	 * just once.
	 * @author naishe
	 *
	 */
	public static class SessionWrapper implements Closeable{
		private Session session;
		private Map<Class<?>, Mapper<? extends AbstractVO<?>>> mapperMap = new ConcurrentHashMap<>();
		private AllQueries allQueries;
		
		public SessionWrapper(){
			this.session = CassandraConnection.getSession();
		}
		
		@SuppressWarnings("unchecked")
		public <T extends AbstractVO<T>> Mapper<T> getMapper(Class<T> klass){
			if( !mapperMap.containsKey(klass) ){
				mapperMap.put(klass, new MappingManager(session).mapper(klass));
			}
			
			return (Mapper<T>) mapperMap.get(klass);
		}
		
		public boolean isClosed(){
			return session.isClosed();
		}
		
		@Override
		public void close() {
			session.close();
		}
		
		public Session getSession(){
			return this.session;
		}

		public AllQueries getAllQueries() {
			if(allQueries == null){
				allQueries = new MappingManager(getSession()).createAccessor(AllQueries.class);
			}
			return allQueries;
		}
	}
}
