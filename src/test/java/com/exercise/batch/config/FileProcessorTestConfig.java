package com.exercise.batch.config;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;

import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;

import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import com.exercise.batch.model.FileInput;

@Configuration
@EnableBatchProcessing(modular = true)
public class FileProcessorTestConfig {
	
		
	private FlatFileItemReader<FileInput> reader = new FlatFileItemReader<>();
	
	private ExecutionContext executionContext = new ExecutionContext();

		
	@Before
	public void setUp() {

		reader.setResource(new FileSystemResource("input/inputData.csv"));
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
	}
	
	@Test
	public void testReadFlatFileToModel() throws UnexpectedInputException, ParseException, Exception {
		
		FileInput fi = new FileInput() ;
		fi.setFilePath("C:\\Users\\shrut\\Documents\\InputFile.txt");
		fi.setThreadCount(3);
		
		reader.open(executionContext);
		FileInput fi2 = reader.read();
		/*
		 * if(fi.getFilePath() == fi2.getFilePath() &&
		 * fi.getThreadCount()==fi2.getThreadCount()) assert true;
		 */
		
		Assert.assertEquals(fi.getFilePath(), fi2.getFilePath());
		Assert.assertEquals(fi.getThreadCount(), fi2.getThreadCount());
		
		reader.close();		
		
	}
	

	
	
}
