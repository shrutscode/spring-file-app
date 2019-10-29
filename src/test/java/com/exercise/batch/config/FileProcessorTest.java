package com.exercise.batch.config;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class FileProcessorTest {
	
	@Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
	
	 @Autowired
	 private FlatFileItemWriter<List<String>> writer;
	 
	 
    
    @Test
    public void launchJob() throws Exception {

	//testing a job
		/*
		 * JobExecution jobExecution = jobLauncherTestUtils.launchJob();
		 * 
		 * //Testing a individual step //JobExecution jobExecution =
		 * jobLauncherTestUtils.launchStep("step1");
		 * 
		 * assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
		 */
        
    }
    
    @Test
    public void writeToFile() throws Exception {
    	
    	List<String> strList = new ArrayList<String>();
    	Resource outputResource = new FileSystemResource("output/outputDataTest.txt");
    	
    	strList.add("Here is a test String");
    	strList.add("Writing to file in encrypted form");
    	strList.add("THis is a test program");
    	
    	strList.add("Here is a test String Here is a test String");
    	strList.add("Text encrypted using Caesar ciper logic ");
    	strList.add("This is the last line");
    	
    	writer.setResource(outputResource);
    	writer.write(Arrays.asList(strList));
    	
    	
    }
}


