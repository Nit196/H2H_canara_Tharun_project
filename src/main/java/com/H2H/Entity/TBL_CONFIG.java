package com.H2H.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class TBL_CONFIG {
    
//
    @Id 
    @GeneratedValue(strategy=GenerationType.IDENTITY)
	public int configID;
    
    
	public String CONFIGKEY; 
	public String CONFIGVALUE;
   	public String STATUS;
	
	
	
	public int getConfigID() {
		return configID;
	}
	public void setConfigID(int configID) {
		this.configID = configID;
	}
	public String getConfigKey() {
		return CONFIGKEY;
	}
	public void setConfigKey(String configKey) {
		this.CONFIGKEY = configKey;
	}
	public String getConfigValue() {
		return CONFIGVALUE;
	}
	public void setConfigValue(String configValue) {
		this.CONFIGVALUE = configValue;
	}
	public String getStatus() {
		return STATUS;
	}
	public void setStatus(String status) {
		this.STATUS = status;
	}
	
	
	@Override
	public String toString() {
		return "TBL_CONFIG [configID=" + configID + ", configKey=" + CONFIGKEY + ", configValue=" + CONFIGVALUE
				+ ", status=" + STATUS + "]";
	}
	
	
	

	
	
	
}
