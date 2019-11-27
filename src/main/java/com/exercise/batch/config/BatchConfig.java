package com.exercise.batch.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import java.util.List;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import com.exercise.batch.model.FileInput;
 
@Configuration
@EnableBatchProcessing
public class BatchConfig {
     
    @Autowired
    private JobBuilderFactory jobs;
 
    @Autowired
    private StepBuilderFactory steps;
    
    private Resource outputResource = new FileSystemResource("output/outputData.txt");
    
     
    @Bean
    public FlatFileItemReader<FileInput> reader() 
    {
    	//Create reader instance
        FlatFileItemReader<FileInput> reader = new FlatFileItemReader<FileInput>();
         
        //Set input file location
        reader.setResource(new FileSystemResource("input/inputData.csv"));         
               
        //Configure how each line will be parsed and mapped to different values
        reader.setLineMapper(new DefaultLineMapper<FileInput>() {
            {                
                setLineTokenizer(new DelimitedLineTokenizer(";") {
                    {
                        setNames(new String[] { "filePath", "threadCount" });
                    }
                });
                //Set values in FileInput class
                setFieldSetMapper(new BeanWrapperFieldSetMapper<FileInput>() {
                    {
                        setTargetType(FileInput.class);
                    }
                });
            }
        });
        return reader;
    }
    
    @Bean
	ItemProcessor<FileInput, List<String>> fileProcessor() {
		return new FileProcessor();
	}
    
    @Bean
    public FlatFileItemWriter<List<String>> writer() 
    {
        //Create writer instance
        FlatFileItemWriter<List<String>> writer = new FlatFileItemWriter<>();
         
        //Set output file location
        writer.setResource(outputResource);
         
        //All job repetitions should "append" to same output file
        writer.setAppendAllowed(false);         
        
        writer.setLineAggregator(new PassThroughLineAggregator<List<String>>());
        
        return writer;
    }
    
    @Bean
    public Job readAndEncryptFilesJob() {
        return jobs
                .get("readAndEncryptFilesJob")
                .incrementer(new RunIdIncrementer())
                .start(step1())
                .build();
    }
 
    @Bean
    public Step step1() {
        return steps.get("step1").<FileInput, List<String>>chunk(5)
                .reader(reader())
                .processor(fileProcessor())
                .writer(writer())
                .build();
    }
}
