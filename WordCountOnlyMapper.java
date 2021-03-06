package packageDemo;
import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;


public class WordCountOnlyMapper {
	public static class WordCountMapper extends Mapper<Object, Text, Text, IntWritable>
	{
		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();
		@Override
		protected void map(Object key,Text value,Context context) throws IOException, InterruptedException
		{
			StringTokenizer itr = new StringTokenizer(value.toString(), ",");
			while(itr.hasMoreTokens())
			{
				word.set(itr.nextToken());
				context.write(word,one); 
			}
			
			
		}
		
	}
	public static class WordCountReducer extends Reducer<Text, IntWritable, Text, IntWritable>
	{
		IntWritable result = new IntWritable();
		@Override
		protected void reduce(Text key,Iterable<IntWritable> values,Context context) throws IOException, InterruptedException
		{
			int sum=0;
			for(IntWritable val:values)
			{
				sum += val.get();
			}
			result.set(sum);
			context.write(key, result);
		}
		
	}
	public static void main(String[] args) throws IOException, InterruptedException, ClassNotFoundException
	{
		Configuration conf = new Configuration();
		Job job = new Job(conf,"WordCountOnlyMappers");
		
		job.setNumReduceTasks(0);
		job.setJarByClass(WordCountOnlyMapper.class);
		job.setMapperClass(WordCountMapper.class);
		job.setReducerClass(WordCountReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		System.exit(job.waitForCompletion(true)?0:1);	

	}

}
