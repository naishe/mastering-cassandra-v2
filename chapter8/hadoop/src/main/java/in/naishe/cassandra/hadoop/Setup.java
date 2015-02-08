package in.naishe.cassandra.hadoop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.Session;

public class Setup {
	private static final String DATA_SRC = "/alice.txt";
	private static Cluster cluster;
	public static final String END_POINT = "localhost";
	public static final String KEY_SPACE = "hadoop_test";
	public static final String SRC_TABLE = "lines";
	public static final String OUT_TABLE = "output";
	public static final String OUT_ROWKEY = "word";
	
	public static final Cluster getConnection(){
		if(cluster == null){
			cluster = Cluster
						.builder()
						.addContactPoint(END_POINT)
						.build();
		}
		return cluster;
	}
	
	public static final void closeConnection(){
		if(cluster != null){
			cluster.close();
		}
	}
	
	private static void loadData() throws IOException{
		
		String insertQuery = "INSERT INTO "
				+ KEY_SPACE + "." + SRC_TABLE
				+ " "
				+ "(id, line) values "
				+ "(?, ?)";
		
		Cluster cluster = getConnection();
		try (
				InputStream is = Setup.class.getResourceAsStream(DATA_SRC);
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				Session session = cluster.connect();
		) {
			PreparedStatement stmt = session.prepare(insertQuery);
			BoundStatement boundStmt = new BoundStatement(stmt);
			String line = null;
			while((line = br.readLine()) != null){
				System.out.println(line);
				session.execute(boundStmt.bind(UUID.randomUUID(), line));
			}
		}
	}

	private static void createSchema() {
		Session session = getConnection().connect();
		String createKeyspace = "CREATE KEYSPACE IF NOT EXISTS "
				+ KEY_SPACE
				+ "  WITH replication ="
				+ " {'class': 'SimpleStrategy', 'replication_factor' : 1}";
		String dropSrcTable = "DROP TABLE IF EXISTS "
				+ KEY_SPACE + "." + SRC_TABLE;
		String createSrcTable =  "CREATE TABLE "
				+ KEY_SPACE + "."  + SRC_TABLE
				+ " ( id uuid,"
				+ "   line text, "
				+ "   PRIMARY KEY (id) ) ";
		
		String dropOutTable = "DROP TABLE IF EXISTS "
				+ KEY_SPACE + "." + OUT_TABLE;
		String createOutTable =  "CREATE TABLE "
				+ KEY_SPACE + "."  + OUT_TABLE
				+ " ( word text,"
				+ "   word_count text, "
				+ "   PRIMARY KEY (word) ) ";
		

		try {
			System.out.println("Creating keyspace.");
			session.execute(createKeyspace);
			System.out.println("Sleeping to make sure the change is persisted across the cluster");
			Thread.sleep(1000);
			System.out.println("Creating the table.");
			session.execute(dropSrcTable);
			session.execute(createSrcTable);
			session.execute(dropOutTable);
			session.execute(createOutTable);
			
		} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException {
		createSchema();
		loadData();
		closeConnection();
	}

}
