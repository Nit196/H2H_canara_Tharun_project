package com.H2H.Entity;

import org.springframework.stereotype.Service;

@Service
public class CONFIGVALUE 
{
	
	  public String BS_SHORTNAME;
	  public String BS_SHORTCODE;
	  public String  SS_HOST_ADDR;
	  public String  SS_PORTNO;
	  public String SS_USERNAME;
	  public String  SS_PASS;
	  public String  SS_SSHPRKEYFILE;
	  public String   SS_PASSPHRASE;
	  public String SS_ROOTFLD;
	  public String SS_INBOXFLD;
	  
	  public String   BANK_FLDNAME;
	   public String  Main_DataSource;
	   
	   public String  US_SRCFLDNAME; 
	   public String  US_OGFLDNAME;
	   public String  US_ARFLDNAME;
	   public String US_SIGFLDNAME;
	   public String  US_PACKFLDNAME;
	   public String  US_ERRORFLD;
	   
	   public String   DS_INBX_FLDNAME;
	   public String   DS_ERRORFLD;
	   public String   DS_OGFLDNAME;
	   public String  DS_VERIFLDNAME;
	   public String  DS_ARCFLDNAME;
	   
	   public String   BANK_SOURCE;
	   public String   BANK_ORIGINAL;
	   public String  BANK_VERIFIED;
	   public String  BANK_ERROR;
	   public String   BANK_PKCFLDNAME;
	   public String   File_Ext;
	   @Override
	public String toString() {
		return "ConfigValue [BS_SHORTNAME=" + BS_SHORTNAME + ", BS_SHORTCODE=" + BS_SHORTCODE + ", SS_HOST_ADDR="
				+ SS_HOST_ADDR + ", SS_PORTNO=" + SS_PORTNO + ", SS_USERNAME=" + SS_USERNAME + ", SS_PASS=" + SS_PASS
				+ ", SS_SSHPRKEYFILE=" + SS_SSHPRKEYFILE + ", SS_PASSPHRASE=" + SS_PASSPHRASE + ", SS_ROOTFLD="
				+ SS_ROOTFLD + ", SS_INBOXFLD=" + SS_INBOXFLD + ", BANK_FLDNAME=" + BANK_FLDNAME + ", Main_DataSource="
				+ Main_DataSource + ", US_SRCFLDNAME=" + US_SRCFLDNAME + ", US_OGFLDNAME=" + US_OGFLDNAME
				+ ", US_ARFLDNAME=" + US_ARFLDNAME + ", US_SIGFLDNAME=" + US_SIGFLDNAME + ", US_PACKFLDNAME="
				+ US_PACKFLDNAME + ", US_ERRORFLD=" + US_ERRORFLD + ", DS_INBX_FLDNAME=" + DS_INBX_FLDNAME
				+ ", DS_ERRORFLD=" + DS_ERRORFLD + ", DS_OGFLDNAME=" + DS_OGFLDNAME + ", DS_VERIFLDNAME="
				+ DS_VERIFLDNAME + ", DS_ARCFLDNAME=" + DS_ARCFLDNAME + ", BANK_SOURCE=" + BANK_SOURCE
				+ ", BANK_ORIGINAL=" + BANK_ORIGINAL + ", BANK_VERIFIED=" + BANK_VERIFIED + ", BANK_ERROR=" + BANK_ERROR
				+ ", BANK_PKCFLDNAME=" + BANK_PKCFLDNAME + ", File_Ext=" + File_Ext + ", Bnk_Crt_Path=" + Bnk_Crt_Path
				+ ", Bnk_Crt_Pwd=" + Bnk_Crt_Pwd + ", Bnk_Pbl_Crt=" + Bnk_Pbl_Crt + ", Bnk_Crt_alias=" + Bnk_Crt_alias
				+ ", NPCI_Crt_Path=" + NPCI_Crt_Path + "]";
	}
	public String getBS_SHORTNAME() {
		return BS_SHORTNAME;
	}
	public void setBS_SHORTNAME(String bS_SHORTNAME) {
		BS_SHORTNAME = bS_SHORTNAME;
	}
	public String getBS_SHORTCODE() {
		return BS_SHORTCODE;
	}
	public void setBS_SHORTCODE(String bS_SHORTCODE) {
		BS_SHORTCODE = bS_SHORTCODE;
	}
	public String getSS_HOST_ADDR() {
		return SS_HOST_ADDR;
	}
	public void setSS_HOST_ADDR(String sS_HOST_ADDR) {
		SS_HOST_ADDR = sS_HOST_ADDR;
	}
	public String getSS_PORTNO() {
		return SS_PORTNO;
	}
	public void setSS_PORTNO(String sS_PORTNO) {
		SS_PORTNO = sS_PORTNO;
	}
	public String getSS_USERNAME() {
		return SS_USERNAME;
	}
	public void setSS_USERNAME(String sS_USERNAME) {
		SS_USERNAME = sS_USERNAME;
	}
	public String getSS_PASS() {
		return SS_PASS;
	}
	public void setSS_PASS(String sS_PASS) {
		SS_PASS = sS_PASS;
	}
	public String getSS_SSHPRKEYFILE() {
		return SS_SSHPRKEYFILE;
	}
	public void setSS_SSHPRKEYFILE(String sS_SSHPRKEYFILE) {
		SS_SSHPRKEYFILE = sS_SSHPRKEYFILE;
	}
	public String getSS_PASSPHRASE() {
		return SS_PASSPHRASE;
	}
	public void setSS_PASSPHRASE(String sS_PASSPHRASE) {
		SS_PASSPHRASE = sS_PASSPHRASE;
	}
	public String getSS_ROOTFLD() {
		return SS_ROOTFLD;
	}
	public void setSS_ROOTFLD(String sS_ROOTFLD) {
		SS_ROOTFLD = sS_ROOTFLD;
	}
	public String getSS_INBOXFLD() {
		return SS_INBOXFLD;
	}
	public void setSS_INBOXFLD(String sS_INBOXFLD) {
		SS_INBOXFLD = sS_INBOXFLD;
	}
	public String getBANK_FLDNAME() {
		return BANK_FLDNAME;
	}
	public void setBANK_FLDNAME(String bANK_FLDNAME) {
		BANK_FLDNAME = bANK_FLDNAME;
	}
	public String getMain_DataSource() {
		return Main_DataSource;
	}
	public void setMain_DataSource(String main_DataSource) {
		Main_DataSource = main_DataSource;
	}
	public String getUS_SRCFLDNAME() {
		return US_SRCFLDNAME;
	}
	public void setUS_SRCFLDNAME(String uS_SRCFLDNAME) {
		US_SRCFLDNAME = uS_SRCFLDNAME;
	}
	public String getUS_OGFLDNAME() {
		return US_OGFLDNAME;
	}
	public void setUS_OGFLDNAME(String uS_OGFLDNAME) {
		US_OGFLDNAME = uS_OGFLDNAME;
	}
	public String getUS_ARFLDNAME() {
		return US_ARFLDNAME;
	}
	public void setUS_ARFLDNAME(String uS_ARFLDNAME) {
		US_ARFLDNAME = uS_ARFLDNAME;
	}
	public String getUS_SIGFLDNAME() {
		return US_SIGFLDNAME;
	}
	public void setUS_SIGFLDNAME(String uS_SIGFLDNAME) {
		US_SIGFLDNAME = uS_SIGFLDNAME;
	}
	public String getUS_PACKFLDNAME() {
		return US_PACKFLDNAME;
	}
	public void setUS_PACKFLDNAME(String uS_PACKFLDNAME) {
		US_PACKFLDNAME = uS_PACKFLDNAME;
	}
	public String getUS_ERRORFLD() {
		return US_ERRORFLD;
	}
	public void setUS_ERRORFLD(String uS_ERRORFLD) {
		US_ERRORFLD = uS_ERRORFLD;
	}
	public String getDS_INBX_FLDNAME() {
		return DS_INBX_FLDNAME;
	}
	public void setDS_INBX_FLDNAME(String dS_INBX_FLDNAME) {
		DS_INBX_FLDNAME = dS_INBX_FLDNAME;
	}
	public String getDS_ERRORFLD() {
		return DS_ERRORFLD;
	}
	public void setDS_ERRORFLD(String dS_ERRORFLD) {
		DS_ERRORFLD = dS_ERRORFLD;
	}
	public String getDS_OGFLDNAME() {
		return DS_OGFLDNAME;
	}
	public void setDS_OGFLDNAME(String dS_OGFLDNAME) {
		DS_OGFLDNAME = dS_OGFLDNAME;
	}
	public String getDS_VERIFLDNAME() {
		return DS_VERIFLDNAME;
	}
	public void setDS_VERIFLDNAME(String dS_VERIFLDNAME) {
		DS_VERIFLDNAME = dS_VERIFLDNAME;
	}
	public String getDS_ARCFLDNAME() {
		return DS_ARCFLDNAME;
	}
	public void setDS_ARCFLDNAME(String dS_ARCFLDNAME) {
		DS_ARCFLDNAME = dS_ARCFLDNAME;
	}
	public String getBANK_SOURCE() {
		return BANK_SOURCE;
	}
	public void setBANK_SOURCE(String bANK_SOURCE) {
		BANK_SOURCE = bANK_SOURCE;
	}
	public String getBANK_ORIGINAL() {
		return BANK_ORIGINAL;
	}
	public void setBANK_ORIGINAL(String bANK_ORIGINAL) {
		BANK_ORIGINAL = bANK_ORIGINAL;
	}
	public String getBANK_VERIFIED() {
		return BANK_VERIFIED;
	}
	public void setBANK_VERIFIED(String bANK_VERIFIED) {
		BANK_VERIFIED = bANK_VERIFIED;
	}
	public String getBANK_ERROR() {
		return BANK_ERROR;
	}
	public void setBANK_ERROR(String bANK_ERROR) {
		BANK_ERROR = bANK_ERROR;
	}
	public String getBANK_PKCFLDNAME() {
		return BANK_PKCFLDNAME;
	}
	public void setBANK_PKCFLDNAME(String bANK_PKCFLDNAME) {
		BANK_PKCFLDNAME = bANK_PKCFLDNAME;
	}
	public String getFile_Ext() {
		return File_Ext;
	}
	public void setFile_Ext(String file_Ext) {
		File_Ext = file_Ext;
	}
	public String getBnk_Crt_Path() {
		return Bnk_Crt_Path;
	}
	public void setBnk_Crt_Path(String bnk_Crt_Path) {
		Bnk_Crt_Path = bnk_Crt_Path;
	}
	public String getBnk_Crt_Pwd() {
		return Bnk_Crt_Pwd;
	}
	public void setBnk_Crt_Pwd(String bnk_Crt_Pwd) {
		Bnk_Crt_Pwd = bnk_Crt_Pwd;
	}
	public String getBnk_Pbl_Crt() {
		return Bnk_Pbl_Crt;
	}
	public void setBnk_Pbl_Crt(String bnk_Pbl_Crt) {
		Bnk_Pbl_Crt = bnk_Pbl_Crt;
	}
	public String getBnk_Crt_alias() {
		return Bnk_Crt_alias;
	}
	public void setBnk_Crt_alias(String bnk_Crt_alias) {
		Bnk_Crt_alias = bnk_Crt_alias;
	}
	public String getNPCI_Crt_Path() {
		return NPCI_Crt_Path;
	}
	public void setNPCI_Crt_Path(String nPCI_Crt_Path) {
		NPCI_Crt_Path = nPCI_Crt_Path;
	}
	public String  Bnk_Crt_Path;
	   public String   Bnk_Crt_Pwd;
	   public String   Bnk_Pbl_Crt;
	   public String   Bnk_Crt_alias;
	   public String   NPCI_Crt_Path;
}
