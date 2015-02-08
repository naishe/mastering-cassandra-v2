package in.naishe.cass2;

import java.nio.ByteBuffer;
import java.util.Collection;

import org.apache.cassandra.db.ColumnFamily;
import org.apache.cassandra.db.Mutation;
import org.apache.cassandra.triggers.ITrigger;

public class AllCapsTrigger implements ITrigger {

	private static final String table = "countword";
	public Collection<Mutation> augment(ByteBuffer key, ColumnFamily cf) {
/*		for(Cell cell: cf){
			if(cell.value().hasRemaining()){
				System.out.println("~~~ *******Value:  " + new String(cell.value().array(), StandardCharsets.UTF_8));
				Mutation mutation = new Mutation("demo_cql", key);
				mutation.add("trctr", SimpleSparseCellNameType, value, timestamp);
			}
		}*/
		
		return null;
	}

}
