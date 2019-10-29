package com.exercise.batch.model;



public class FileInput {
	
	private String filePath;
	private int threadCount;
	private String outPath;
	
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public int getThreadCount() {
		return threadCount;
	}
	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}
	public String getOutPath() {
		return outPath;
	}
	public void setOutPath(String outPath) {
		this.outPath = outPath;
	}
	
	
	
	@Override
	public boolean equals(Object o) {
		
		if(this.filePath == ((FileInput)o).getFilePath() && this.threadCount==((FileInput)o).getThreadCount())
			return true;
		
		return false;
		
	}
	

}
