package edu.gatech.cse6242;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.*;
import org.apache.hadoop.util.*;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.util.StringTokenizer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class Q1 {
	

public static class ProjectionMapper extends Mapper<LongWritable, Text, Text, LongWritable> {
  private Text word = new Text();
  private LongWritable count = new LongWritable();
 
  @Override
  protected void map(LongWritable key, Text value, Context context)
      throws IOException, InterruptedException {
    // value is tab separated values: word, year, occurrences, #books, #pages
    // we project out (word, occurrences) so we can sum over all years
    String[] split = value.toString().split("\t+");
    word.set(split[1]);
    if (split.length > 0) {
      try {
        count.set(Long.parseLong(split[2]));
        context.write(word, count);
      } catch (NumberFormatException e) {
        // cannot parse - ignore
      }
    }
  }
}
public static class LongSumReducer<KEY> extends Reducer<KEY, LongWritable,
                                                 KEY,LongWritable> {
 
  private LongWritable result = new LongWritable();
 
  public void reduce(KEY key, Iterable<LongWritable> values,
                     Context context) throws IOException, InterruptedException {
    long sum = 0;
    for (LongWritable val : values) {
      sum += val.get();
    }
    result.set(sum);
    context.write(key, result);
  }
 
}

 
  public static void main(String[] args) throws Exception {
   /* Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "Q1");

     //TODO: Needs to be implemented 
	

    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
    */


    Configuration conf = new Configuration();
    Job job = Job.getInstance(conf, "Q1");
    job.setJarByClass(Q1.class);
     System.out.println("Hello World");
    job.setMapperClass(ProjectionMapper.class);
    job.setCombinerClass(LongSumReducer.class);
    job.setReducerClass(LongSumReducer.class);
    job.setOutputKeyClass(Text.class);
    job.setOutputValueClass(LongWritable.class); 
    FileInputFormat.addInputPath(job, new Path(args[0]));
    FileOutputFormat.setOutputPath(job, new Path(args[1]));
    System.exit(job.waitForCompletion(true) ? 0 : 1);
  }
}
