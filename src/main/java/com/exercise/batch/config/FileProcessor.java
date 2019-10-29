package com.exercise.batch.config;

import static java.lang.Math.toIntExact;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.batch.item.ItemProcessor;
import com.exercise.batch.model.FileInput;


public class FileProcessor implements ItemProcessor<FileInput, List<String> >, Runnable {
 
    
    private FileChannel _channel;
    private long _startLocation;
    private int _size;
    int _sequence_number;
    private static List<String> encryptedList;
    
    public FileProcessor() {
    	super();
    }

    public FileProcessor(long loc, int size, FileChannel chnl, int sequence)
    {
        _startLocation = loc;
        _size = size;
        _channel = chnl;
        _sequence_number = sequence;
        encryptedList = new ArrayList<String>();
    }

    @Override
    public void run()
    {
        try
        {
            System.out.println("Reading the channel: " + _startLocation + ":" + _size);

            //allocate memory
            ByteBuffer buff = ByteBuffer.allocate(_size);

            //Read file chunk to RAM
            _channel.read(buff, _startLocation);

            //chunk to String
            String string_chunk = new String(buff.array(), Charset.forName("UTF-8"));
            
            //encrypt string and write to file
            encryptString(string_chunk, 3);

            System.out.println("Done Reading the channel: " + _startLocation + ":" + _size);

        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

	@Override
	public List<String> process(FileInput item) throws Exception {
		
		FileInputStream fileInputStream = new FileInputStream(item.getFilePath());
        FileChannel channel = fileInputStream.getChannel();
        long remaining_size = channel.size(); //get the total number of bytes in the file
        long chunk_size = remaining_size / item.getThreadCount(); //file_size/threads

        //Max allocation size allowed is ~2GB
        if (chunk_size > (Integer.MAX_VALUE - 5))
        {
            chunk_size = (Integer.MAX_VALUE - 5);
        }

        //thread pool
        ExecutorService executor = Executors.newFixedThreadPool(item.getThreadCount());

        long start_loc = 0;//file pointer
        int i = 0; //loop counter
        while (remaining_size >= chunk_size)
        {
            //launches a new thread
            executor.execute(new FileProcessor(start_loc, toIntExact(chunk_size), channel, i));
            remaining_size = remaining_size - chunk_size;
            start_loc = start_loc + chunk_size;
            i++;
        }

        //load the last remaining piece
        executor.execute(new FileProcessor(start_loc, toIntExact(remaining_size), channel, i));

        //Tear Down
        executor.shutdown();

        //Wait for all threads to finish
        while (!executor.isTerminated())
        {
            //wait for infinity time
        }
        System.out.println("Finished all threads");
        fileInputStream.close();
		return encryptedList;
	}
	
	/**
     * Caesar cipher encryption method, shift by 4 
     * @param chunk string to convert
     * @param shift number of letters to shift
     * @return encrypted string
     */
    public static String encryptString(String chunk, int shift) {
    	
    	shift %= 26;
        if (shift == 0) return chunk;
        StringBuilder sb = new StringBuilder(chunk.length());
        for (int i = 0; i < chunk.length(); i++) {
            int c = chunk.charAt(i);

            if (c >= 'A' && c <= 'Z') {
                c += shift;
                if (c > 'Z') c -= 26;
            } else if (c >= 'a' && c <= 'z') {
                c += shift;
                if (c > 'z') c -= 26;
            }

            sb.append((char) c);
        }
//        try {
//        writeToFile(sb);
//        }
//        catch(IOException fe) {
//        	System.out.println("Error in writing to file");
//        	
//        }
         encryptedList.add(sb.toString());
        return "";
    	
    }
    

}
