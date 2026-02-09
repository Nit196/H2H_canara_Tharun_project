package com.H2H.Entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class SMSConnection {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long FID;
    private String ip;
    private int port;
    private String emailId;
    private String password;
    private String gatewayURL;
    private String ports; 
    private String keyvalue;

    // Default constructor
    public SMSConnection() {}

    // Parameterized constructor
    public SMSConnection(String ip, int port, String emailId, String password, String gatewayURL, String ports, String keyvalue) {
        this.ip = ip;
        this.port = port;
        this.emailId = emailId;
        this.password = password;
        this.gatewayURL = gatewayURL;
        this.ports = ports;
        this.keyvalue = keyvalue;
    }

    // Getters and Setters
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGatewayURL() {
        return gatewayURL;
    }

    public void setGatewayURL(String gatewayURL) {
        this.gatewayURL = gatewayURL;
    }

    public String getPorts() {
        return ports;
    }

    public void setPorts(String ports) {
        this.ports = ports;
    }

    public String getkeyvalue() {
        return keyvalue;
    }

    public void setkeyvalue(String keyvalue) {
        this.keyvalue = keyvalue;
    }

    // Override toString() method for printing the object in a readable format
    @Override
    public String toString() {
        return "ConnectionConfig{" +
               "ip='" + ip + '\'' +
               ", port=" + port +
               ", emailId='" + emailId + '\'' +
               ", password='" + password + '\'' +
               ", gatewayURL='" + gatewayURL + '\'' +
               ", ports=" + String.join(", ", ports) +
               ", keyvalue='" + keyvalue + '\'' +
               '}';
    }

    
}
