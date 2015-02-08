package in.naishe.cassandra.hadoop;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.cassandra.cli.CliParser.newColumnFamily_return;
import org.apache.cassandra.hadoop.ConfigHelper;
import org.apache.cassandra.hadoop.cql3.CqlConfigHelper;
import org.apache.cassandra.hadoop.cql3.CqlInputFormat;
import org.apache.cassandra.hadoop.cql3.CqlOutputFormat;
import org.apache.cassandra.utils.ByteBufferUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import com.datastax.driver.core.Row;
import com.google.common.base.Strings;

public class WordCount extends Configured implements Tool {
	private static final boolean isCassandra = true;

	public static void main(String[] args) throws Exception{
		ToolRunner.run(new Configuration(), new WordCount(), args);
	}

	@Override
	public int run(String[] arg0) throws Exception {
		Job job = Job.getInstance(getConf(), "wordcountjob");
		job.setJarByClass(getClass());

		// Cassandra configuration
		ConfigHelper.setInputInitialAddress(job.getConfiguration(), Setup.END_POINT);
		ConfigHelper.setInputColumnFamily(job.getConfiguration(), Setup.KEY_SPACE, Setup.SRC_TABLE);
		ConfigHelper.setInputPartitioner(job.getConfiguration(), "Murmur3Partitioner");

		// Mapper config
		job.setMapperClass(StringTokenizerMapper.class);
		job.setInputFormatClass(CqlInputFormat.class);
		CqlConfigHelper.setInputCql(job.getConfiguration(), 
				"select * from " + Setup.KEY_SPACE + "." + Setup.SRC_TABLE + 
				" where token(id) > ? and token(id) <= ? allow filtering");
		CqlConfigHelper.setInputCQLPageRowSize(job.getConfiguration(), "100");

		// Reducer config
		if(isCassandra) {
			job.setCombinerClass(CassandraReducer.class);
			job.setReducerClass(CassandraReducer.class);
			job.setMapOutputKeyClass(Text.class);
			job.setMapOutputValueClass(IntWritable.class);
			job.setOutputKeyClass(Map.class);
			job.setOutputValueClass(List.class);
			job.setOutputFormatClass(CqlOutputFormat.class);

			ConfigHelper.setOutputColumnFamily(job.getConfiguration(), Setup.KEY_SPACE, Setup.OUT_TABLE);
			//job.getConfiguration().set("row_key", Setup.OUT_ROWKEY);
			String cql = "update " + Setup.KEY_SPACE + "." + Setup.OUT_TABLE + " "
					+ "set word_count = ? ";
			CqlConfigHelper.setOutputCql(job.getConfiguration(), cql);
			ConfigHelper.setOutputInitialAddress(job.getConfiguration(), Setup.END_POINT);
			ConfigHelper.setOutputPartitioner(job.getConfiguration(), "Murmur3Partitioner");	
		} else {
			job.setCombinerClass(FileReducer.class);
			job.setReducerClass(FileReducer.class);
			job.setOutputKeyClass(Text.class);
			job.setOutputValueClass(IntWritable.class);

			FileOutputFormat.setOutputPath(job, new Path("/tmp/res"));
		}
		job.waitForCompletion(true);

		return 0;
	}

	public static class StringTokenizerMapper extends Mapper<Long, Row, Text, IntWritable> {
		@Override
		protected void map(Long key, Row value,
				Mapper<Long, Row, Text, IntWritable>.Context context)
						throws IOException, InterruptedException {

			String line = value.getString("line");
			if(!Strings.isNullOrEmpty(line)){
				String[] frags = line.replaceAll("[^A-Za-z\\d\\s]", "").trim().split("\\s+");
				System.out.println(Arrays.asList(frags).toString());
				for(String frag: frags){
					context.write(new Text(frag.toLowerCase()), new IntWritable(1));
				}
			}
		}
	}

	public static class FileReducer extends Reducer<Text, IntWritable, Text, IntWritable> {
		@Override
		protected void reduce(Text key, Iterable<IntWritable> vals,
				Reducer<Text, IntWritable, Text, IntWritable>.Context ctx)
						throws IOException, InterruptedException {
			int total = 0;
			for (IntWritable value: vals){
				total = total + value.get();
			}
			ctx.write(key, new IntWritable(total));
			System.out.println(key + ": " + total);
		}
	}

	public static class CassandraReducer extends Reducer<Text, IntWritable, Map<String, ByteBuffer>, List<ByteBuffer>> {
		private Map<String, ByteBuffer> keys;
		@Override
		protected void setup(
				Reducer<Text, IntWritable, Map<String, ByteBuffer>, List<ByteBuffer>>.Context context)
						throws IOException, InterruptedException {
			keys = new LinkedHashMap<>();
		}

		@Override
		protected void reduce(
				Text key,
				Iterable<IntWritable> vals,
				Reducer<Text, IntWritable, Map<String, ByteBuffer>, List<ByteBuffer>>.Context ctx)
						throws IOException, InterruptedException {
			int total = 0;
			for(IntWritable val: vals){
				total = total + val.get();
			}

			System.out.println(key.toString() + ": " + total);
			
			keys.put(Setup.OUT_ROWKEY, ByteBufferUtil.bytes(key.toString()));
			List<ByteBuffer> sum = new ArrayList<>();
			sum.add(ByteBufferUtil.bytes(total));
			ctx.write(keys, sum);
		}
	}


}
