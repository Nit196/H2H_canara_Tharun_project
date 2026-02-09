package com.H2H.Entity;

import org.springframework.stereotype.Service;

@Service
public class FileStatus {

	private String folder;
	private String filestatus;
	private String fromdate;
	private String todate;
	private String filename;
	
	
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	//Getters and Setters
	public String getFolder() {
		return folder;
	}
	public void setFolder(String folder) {
		this.folder = folder;
	}
	public String getFilestatus() {
		return filestatus;
	}
	public void setFilestatus(String filestatus) {
		this.filestatus = filestatus;
	}
	public String getFromdate() {
		return fromdate;
	}
	public void setFromdate(String fromdate) {
		this.fromdate = fromdate;
	}
	public String getTodate() {
		return todate;
	}
	public void setTodate(String todate) {
		this.todate = todate;
	}
	@Override
	public String toString() {
		return "FileStatus [folder=" + folder + ", filestatus=" + filestatus + ", fromdate=" + fromdate + ", todate="
				+ todate + ", filename=" + filename + "]";
	}
	
	
	
}
