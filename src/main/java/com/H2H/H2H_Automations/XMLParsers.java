package com.H2H.H2H_Automations;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.bouncycastle.util.encoders.Base64;
import org.w3c.dom.Document;

public class XMLParsers {
	   private File xmlFile = null;
	   private String original64 = null;
	   private String signature64 = null;
	   private String certificate64 = null;

	   public XMLParsers(File _xmlFile) {
	      this.xmlFile = _xmlFile;
	   }

	   public void parse() throws Exception {
	      DocumentBuilderFactory dbFactory = null;
	      DocumentBuilder dBuilder = null;
	      Document doc = null;

	      try 
	      {
	         if (!this.xmlFile.exists()) 
	         {
	            System.out.println("PKCS XML File not Exist");
	            throw new Exception("PKCS XML File not Exist");
	         } else 
	         {
	        	 
	            dbFactory = DocumentBuilderFactory.newInstance();
	            dBuilder = dbFactory.newDocumentBuilder();
	            doc = dBuilder.parse(this.xmlFile);
	            this.original64 = doc.getElementsByTagName("OrgContent").item(0).getTextContent();
	            this.signature64 = doc.getElementsByTagName("Signature").item(0).getTextContent();
	            this.certificate64 = doc.getElementsByTagName("Certificate").item(0).getTextContent();
	         }
	      } catch (Exception var5) {
	         doc = null;
	         System.out.println("Verification XML Exception :" + var5.getMessage());
	         throw new Exception("Verification XML Exception :" + var5.getMessage());
	      }
	   }

	   public String getOriginal64() {
	      return this.original64;
	   }

	   public String getSignature64() {
	      return this.signature64;
	   }

	   public String getCertificate64() {
	      return this.certificate64;
	   }

	   public byte[] getOriginalBytes() {
	      return Base64.decode(this.original64);
	   }

	   public String getOriginalString() {
	      return new String(Base64.decode(this.original64));
	   }

	   public byte[] getSignature() {
	      return Base64.decode(this.signature64);
	   }

	   public byte[] getCertificateBytes() {
	      return Base64.decode(this.certificate64.getBytes());
	   }

	   public X509Certificate getCertificate() throws CertificateException {
	      return (X509Certificate)CertificateFactory.getInstance("X.509").generateCertificate(new ByteArrayInputStream(this.getCertificateBytes()));
	   }

//	   public static void main(String[] args) throws Exception 
//	   {
//	   }
	}
