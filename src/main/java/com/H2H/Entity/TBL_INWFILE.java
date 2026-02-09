package com.H2H.Entity;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class TBL_INWFILE {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int FID;

//	@Column(columnDefinition = "VARCHAR2(255 CHAR)")

	private String FINAME;
	private String FILESIZE;
	private String FILEEXT;
	private String PROCESS;
	private String STATUS;
	private Date CURRFOLDER;
	private String LOCATION;
	private Date RECIVEDATE;
	private String REMARKS;
	private String FILE_RECEIVE_DATE;

	public String getFILE_RECEIVE_DATE() {
		return FILE_RECEIVE_DATE;
	}

	public void setFILE_RECEIVE_DATE(String fILE_RECEIVE_DATE) {
		FILE_RECEIVE_DATE = fILE_RECEIVE_DATE;
	}

	// Getters and Setters and ToString Method
	public int getFID() {
		return FID;
	}

	public void setFID(int fID) {
		FID = fID;
	}

	public String getFINAME() {
		return FINAME;
	}

	public void setFINAME(String fINAME) {
		FINAME = fINAME;
	}

	public String getFILESIZE() {
		return FILESIZE;
	}

	public void setFILESIZE(String fILESIZE) {
		FILESIZE = fILESIZE;
	}

	public String getFILEEXT() {
		return FILEEXT;
	}

	public void setFILEEXT(String fILEEXT) {
		FILEEXT = fILEEXT;
	}

	public String getPROCESS() {
		return PROCESS;
	}

	public void setPROCESS(String pROCESS) {
		PROCESS = pROCESS;
	}

	public String getSTATUS() {
		return STATUS;
	}

	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}

	public Date getCURRFOLDER() {
		return CURRFOLDER;
	}

	public void setCURRFOLDER(Date cURRFOLDER) {
		CURRFOLDER = cURRFOLDER;
	}

	public String getLOCATION() {
		return LOCATION;
	}

	public void setLOCATION(String lOCATION) {
		LOCATION = lOCATION;
	}

	public Date getRECIVEDATE() {
		return RECIVEDATE;
	}

	public void setRECIVEDATE(Date rECIVEDATE) {
		RECIVEDATE = rECIVEDATE;
	}

	public String getREMARKS() {
		return REMARKS;
	}

	public void setREMARKS(String rEMARKS) {
		REMARKS = rEMARKS;
	}

	// ToString
	@Override
	public String toString() {

		return "UPLOADFILES [FID=" + FID + ", FINAME=" + FINAME + ", FILESIZE=" + FILESIZE + ", FILEEXT=" + FILEEXT
				+ ", PROCESS=" + PROCESS + ", STATUS=" + STATUS + ", CURRFOLDER=" + CURRFOLDER + ", LOCATION="
				+ LOCATION + ", RECIVEDATE=" + RECIVEDATE + ", REMARKS=" + REMARKS + "]";
	}

}
