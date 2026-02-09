package com.H2H.H2H_Automations;

//import java.beans.JavaBean;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.bouncycastle.util.io.Streams;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.bcpg.ArmoredOutputStream;
import org.bouncycastle.cert.jcajce.JcaCertStore;
import org.bouncycastle.cms.CMSException;
import org.bouncycastle.cms.CMSProcessableByteArray;
import org.bouncycastle.cms.CMSSignedData;
import org.bouncycastle.cms.CMSSignedDataGenerator;
import org.bouncycastle.cms.CMSTypedData;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.bouncycastle.cms.jcajce.JcaSignerInfoGeneratorBuilder;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPCompressedData;
import org.bouncycastle.openpgp.PGPCompressedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedData;
import org.bouncycastle.openpgp.PGPEncryptedDataGenerator;
import org.bouncycastle.openpgp.PGPEncryptedDataList;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPLiteralData;
import org.bouncycastle.openpgp.PGPObjectFactory;
import org.bouncycastle.openpgp.PGPOnePassSignatureList;
import org.bouncycastle.openpgp.PGPPrivateKey;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.bouncycastle.openpgp.PGPPublicKeyEncryptedData;
import org.bouncycastle.openpgp.PGPUtil;
import org.bouncycastle.openpgp.operator.KeyFingerPrintCalculator;
import org.bouncycastle.openpgp.operator.PublicKeyDataDecryptorFactory;
import org.bouncycastle.openpgp.operator.jcajce.JcaKeyFingerprintCalculator;
import org.bouncycastle.openpgp.operator.jcajce.JcaPGPKeyConverter;
import org.bouncycastle.openpgp.operator.jcajce.JcePGPDataEncryptorBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyDataDecryptorFactoryBuilder;
import org.bouncycastle.openpgp.operator.jcajce.JcePublicKeyKeyEncryptionMethodGenerator;
import org.bouncycastle.openssl.PEMException;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.operator.jcajce.JcaDigestCalculatorProviderBuilder;
import org.bouncycastle.util.encoders.Base64;
import org.springframework.stereotype.Component;

@Component
public class Encryption_Decryption {
	public static PGPPublicKey publickeys = null;
	public static PGPPrivateKey privatekeys = null;
	public static PrivateKey privatekey = null;
	public X509Certificate certificate = null;

	public static PGPPublicKey getPGPPublicKey(String publickeypath) throws FileNotFoundException, IOException,
			NoSuchAlgorithmException, InvalidKeySpecException, PGPException, CertificateException {
		Date time = new Date();
		FileInputStream fin = new FileInputStream(publickeypath);
		CertificateFactory f = CertificateFactory.getInstance("X.509");
		Certificate certificate = f.generateCertificate(fin);
		PublicKey publicKey = certificate.getPublicKey();
		JcaPGPKeyConverter pgpKeyConverter = new JcaPGPKeyConverter();
		PGPPublicKey publicke = pgpKeyConverter.getPGPPublicKey(1, publicKey, time);
		return publicke;
	}

	public static PGPPrivateKey getPGPPrivateKeys(String pfxfile, String pgppublickey, String password)
			throws KeyStoreException, FileNotFoundException, IOException, NoSuchAlgorithmException,
			CertificateException, UnrecoverableKeyException, InvalidKeySpecException, PGPException {
		PGPPublicKey publickeyss = getPGPPublicKey(pgppublickey);
		KeyStore ks = null;
		ks = KeyStore.getInstance("pkcs12");
		ks.load(new FileInputStream(pfxfile), password.toCharArray());
		String alias = (String) ks.aliases().nextElement();
		PrivateKey privatekey = (PrivateKey) ks.getKey(alias, password.toCharArray());
		JcaPGPKeyConverter pgpKeyConverter = new JcaPGPKeyConverter();
		privatekeys = pgpKeyConverter.getPGPPrivateKey(publickeyss, privatekey);
		return privatekeys;
	}

	public PublicKey getPublicKeydata(String publickeypath) throws FileNotFoundException, PEMException, IOException {
		File file = new File(publickeypath);
		FileReader keyReader = new FileReader(file);
		PEMParser pemParser = new PEMParser(keyReader);
		JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
		SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance(pemParser.readObject());
		PublicKey publicKey = converter.getPublicKey(publicKeyInfo);
		pemParser.close();
		return publicKey;
	}

	public boolean sign(File inptFile, String OutputFile, PrivateKey privatekeys, X509Certificate certificate)
			throws Exception {
		boolean res = false;

		try {
			FileInputStream fin = null;
			fin = new FileInputStream(inptFile);
			byte[] _data = new byte[(int) inptFile.length()];
			fin.read(_data);
			fin.close();
			Provider provider = new BouncyCastleProvider();
			String signatureDigestAlgorithm = "SHA256withRSA";
			CMSSignedData sigData = null;
			StringBuffer xmlBuffer = new StringBuffer();
			xmlBuffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>");
			xmlBuffer.append("\n");
			xmlBuffer.append("<Envelope>");
			xmlBuffer.append("\n");
			xmlBuffer.append("\t");
			xmlBuffer.append("<OrgContent>");
			byte[] dataBase64 = Base64.encode(_data);

			for (int i = 0; i < dataBase64.length; ++i) {
				xmlBuffer.append((char) dataBase64[i]);
			}

			xmlBuffer.append("</OrgContent>");
			xmlBuffer.append("\n");
			xmlBuffer.append("\t");
			xmlBuffer.append("<Signature>");
			// System.out.println(privatekeys.getAlgorithm());
			List certs = new ArrayList();
			certs.add(certificate);
			String algo = certificate.getSigAlgName();
			CMSSignedDataGenerator dataGenerator = new CMSSignedDataGenerator();
			ContentSigner sha1Signer = (new JcaContentSignerBuilder(algo)).setProvider(provider).build(privatekeys);
			dataGenerator.addSignerInfoGenerator((new JcaSignerInfoGeneratorBuilder(
					(new JcaDigestCalculatorProviderBuilder()).setProvider(provider).build()))
					.build(sha1Signer, certificate));
			dataGenerator.addCertificates(new JcaCertStore(certs));
			CMSTypedData msg = new CMSProcessableByteArray(_data);
			sigData = dataGenerator.generate(msg, false);
			byte[] encrypted = sigData.getEncoded();
			encrypted = Base64.encode(encrypted);

			for (int j = 0; j < encrypted.length; ++j) {
				xmlBuffer.append((char) encrypted[j]);
			}

			System.out.println("");
			xmlBuffer.append("</Signature>");
			xmlBuffer.append("\n");
			xmlBuffer.append("\t");
			xmlBuffer.append("<Certificate>");
			byte[] certificateCode = certificate.getEncoded();
			certificateCode = Base64.encode(certificateCode);

			for (int k = 0; k < certificateCode.length; ++k) {
				xmlBuffer.append((char) certificateCode[k]);
			}

			xmlBuffer.append("</Certificate>");
			xmlBuffer.append("\n");
			xmlBuffer.append("</Envelope>");
			File targetEncodedFile = new File(OutputFile + "PKCS");
			FileOutputStream stream = new FileOutputStream(targetEncodedFile);

			try {
				stream.write(xmlBuffer.toString().getBytes());
				stream.flush();
				stream.close();
			} catch (Throwable var23) {
				try {
					stream.close();
				} catch (Throwable var22) {
					var23.addSuppressed(var22);
				}

				throw var23;
			}

			stream.close();
			res = true;
			return res;
		} catch (Exception var24) {
			System.out.println("Error during sign is" + var24.getMessage());
			throw new Exception("Error is " + var24.getMessage());
		}
	}

	public boolean filesEncryptionss(String pfxfile, String pfxpassword, String safefile, String certfile,
			String Encrytefile) throws KeyStoreException, IOException, FileNotFoundException, NoSuchAlgorithmException,
			CertificateException, UnrecoverableKeyException, CMSException, OperatorCreationException,
			InvalidKeySpecException, PGPException {
		try {

			X509Certificate certificate = null;
			PrivateKey privatekey = null;
			PGPPublicKey publickeyss = null;
			FileInputStream fin = null;
			KeyStore ks = null;
			ks = KeyStore.getInstance("pkcs12");
			ks.load(new FileInputStream(pfxfile), pfxpassword.toCharArray());
			String alias = (String) ks.aliases().nextElement();
			privatekey = (PrivateKey) ks.getKey(alias, pfxpassword.toCharArray());
			certificate = (X509Certificate) ks.getCertificate(alias);
			File samplefile = new File(safefile);

			boolean boolsss = sign(samplefile, Encrytefile, privatekey, certificate);
			try {
				publickeyss = getPGPPublicKey(certfile);
			} catch (NoSuchAlgorithmException | CertificateException | InvalidKeySpecException | PGPException
					| IOException var39) {
				System.out.println(var39);
			}
			System.out.println(publickeyss);
			boolean armor = true;
			boolean withIntegrityCheck = true;
			OutputStream out = new FileOutputStream(Encrytefile);
			if (publickeyss == null) {
				throw new PGPException("Recipient public key not found or invalid.");
			} else {
				Security.addProvider(new BouncyCastleProvider());
				if (armor) {
					out = new ArmoredOutputStream((OutputStream) out);
				}

				ByteArrayOutputStream bOut = new ByteArrayOutputStream();
				PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(1);
				PGPUtil.writeFileToLiteralData(comData.open(bOut), 'b', new File(Encrytefile + "PKCS"));
				comData.close();
				JcePGPDataEncryptorBuilder c = (new JcePGPDataEncryptorBuilder(9))
						.setWithIntegrityPacket(withIntegrityCheck).setSecureRandom(new SecureRandom())
						.setProvider("BC");
				PGPEncryptedDataGenerator cPk = new PGPEncryptedDataGenerator(c);
				JcePublicKeyKeyEncryptionMethodGenerator d = (new JcePublicKeyKeyEncryptionMethodGenerator(publickeyss))
						.setProvider(new BouncyCastleProvider()).setSecureRandom(new SecureRandom());
				cPk.addMethod(d);
				byte[] bytes = bOut.toByteArray();
				OutputStream cOut = cPk.open((OutputStream) out, (long) bytes.length);
				cOut.write(bytes);
				cOut.close();
				((OutputStream) out).close();
				cOut = null;
				out = null;
				samplefile.delete();
				File dragetfiles = new File(Encrytefile + "PKCS");
				dragetfiles.delete();
				boolean res = true;
				return res;
			}
		} catch (Exception var40) {
			System.out.println(var40);
			return false;
		}
	}

	public boolean filesEncryption(String pfxfile, String pfxpassword, String safefile, String certfile,
			String Encrytefile) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException,
			UnrecoverableKeyException, CMSException, OperatorCreationException, InvalidKeySpecException, PGPException,
			Exception {
		X509Certificate certificate = null;
		PrivateKey privatekey = null;
		PGPPublicKey publickeyss = null;

		try (FileInputStream pfxStream = new FileInputStream(pfxfile)) {
			KeyStore ks = KeyStore.getInstance("pkcs12");
			ks.load(pfxStream, pfxpassword.toCharArray());
			String alias = (String) ks.aliases().nextElement();
			privatekey = (PrivateKey) ks.getKey(alias, pfxpassword.toCharArray());
			certificate = (X509Certificate) ks.getCertificate(alias);
		}

		File samplefile = new File(safefile);

		// Sign the file
		boolean signed = sign(samplefile, Encrytefile, privatekey, certificate);
		if (!signed) {
			return false;
		}

		// Delete the sample file after signing
		if (!samplefile.delete()) {
			System.out.println("Failed to delete the sample file.");
		}

		// Load the public key
		try {
			publickeyss = getPGPPublicKey(certfile);
		} catch (NoSuchAlgorithmException | CertificateException | InvalidKeySpecException | PGPException
				| IOException e) {
			System.out.println(e);
			return false;
		}

		if (publickeyss == null) {
			throw new PGPException("Recipient public key not found or invalid.");
		}

		boolean armor = true;
		boolean withIntegrityCheck = true;

		try (ByteArrayOutputStream bOut = new ByteArrayOutputStream();
				OutputStream fileOut = new FileOutputStream(Encrytefile);) {
			Security.addProvider(new BouncyCastleProvider());

			// Compress data
			PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(PGPCompressedDataGenerator.ZLIB);
			try (OutputStream compressedOut = comData.open(bOut)) {
				PGPUtil.writeFileToLiteralData(compressedOut, PGPLiteralData.BINARY, new File(Encrytefile + "PKCS"));
			} finally {
				comData.close();
			}

			// Encrypt data
			JcePGPDataEncryptorBuilder encryptorBuilder = new JcePGPDataEncryptorBuilder(
					PGPEncryptedDataGenerator.CAST5).setWithIntegrityPacket(withIntegrityCheck)
					.setSecureRandom(new SecureRandom()).setProvider("BC");

			PGPEncryptedDataGenerator encryptedDataGen = new PGPEncryptedDataGenerator(encryptorBuilder);
			JcePublicKeyKeyEncryptionMethodGenerator keyEncryptor = new JcePublicKeyKeyEncryptionMethodGenerator(
					publickeyss).setProvider("BC").setSecureRandom(new SecureRandom());

			encryptedDataGen.addMethod(keyEncryptor);

			try (OutputStream armoredOut = armor ? new ArmoredOutputStream(fileOut) : fileOut;
					OutputStream encryptedOut = encryptedDataGen.open(armoredOut, bOut.size())) {
				encryptedOut.write(bOut.toByteArray());
			}

			// Delete temporary PKCS file
			File dragetfiles = new File(Encrytefile + "PKCS");
			if (!dragetfiles.delete()) {
				System.out.println("Failed to delete temporary PKCS file.");
			}

			return true;
		} catch (IOException | PGPException e) {
			System.out.println(e);
			return false;
		}
	}

	public boolean filesEncryptionnew(String pfxfile, String pfxpassword, String safefile, String certfile,
			String Encrytefile) throws KeyStoreException, IOException, FileNotFoundException, NoSuchAlgorithmException,
			CertificateException, UnrecoverableKeyException, CMSException, OperatorCreationException,
			InvalidKeySpecException, PGPException {
		try {
			X509Certificate certificate = null;
			PrivateKey privatekey = null;
			PGPPublicKey publickeyss = null;
			FileInputStream fin = null;
			KeyStore ks = null;
			ks = KeyStore.getInstance("pkcs12");
			ks.load(new FileInputStream(pfxfile), pfxpassword.toCharArray());
			String alias = (String) ks.aliases().nextElement();
			privatekey = (PrivateKey) ks.getKey(alias, pfxpassword.toCharArray());
			certificate = (X509Certificate) ks.getCertificate(alias);
			File samplefile = new File(safefile);

			boolean boolsss = sign(samplefile, Encrytefile, privatekey, certificate);
			try {
				publickeyss = getPGPPublicKey(certfile);
			} catch (NoSuchAlgorithmException | CertificateException | InvalidKeySpecException | PGPException
					| IOException var39) {
				System.out.println(var39);
			}
			System.out.println(publickeyss);
			boolean armor = true;
			boolean withIntegrityCheck = true;
			OutputStream out = new FileOutputStream(Encrytefile);
			if (publickeyss == null) {
				throw new PGPException("Recipient public key not found or invalid.");
			} else {
				Security.addProvider(new BouncyCastleProvider());
				if (armor) {
					out = new ArmoredOutputStream((OutputStream) out);
				}

				ByteArrayOutputStream bOut = new ByteArrayOutputStream();
				PGPCompressedDataGenerator comData = new PGPCompressedDataGenerator(1);
				PGPUtil.writeFileToLiteralData(comData.open(bOut), 'b', new File(Encrytefile + "PKCS"));
				comData.close();
				JcePGPDataEncryptorBuilder c = (new JcePGPDataEncryptorBuilder(9))
						.setWithIntegrityPacket(withIntegrityCheck).setSecureRandom(new SecureRandom())
						.setProvider("BC");
				PGPEncryptedDataGenerator cPk = new PGPEncryptedDataGenerator(c);
				JcePublicKeyKeyEncryptionMethodGenerator d = (new JcePublicKeyKeyEncryptionMethodGenerator(publickeyss))
						.setProvider(new BouncyCastleProvider()).setSecureRandom(new SecureRandom());
				cPk.addMethod(d);
				byte[] bytes = bOut.toByteArray();
				OutputStream cOut = cPk.open((OutputStream) out, (long) bytes.length);
				cOut.write(bytes);
				cOut.close();
				((OutputStream) out).close();
				cOut = null;
				out = null;
				// samplefile.delete();
				File dragetfiles = new File(Encrytefile + "PKCS");
				dragetfiles.delete();
				boolean res = true;
				return res;

			}
		} catch (Exception var40) {
			System.out.println(var40);
			return false;
		}
	}

	public boolean filesDecryptionss(String inboxsource, String verifypath, String pfxfile, String pgppublickey,
			String pass) {
		FileOutputStream foss = null;
		boolean res = false;
		InputStream unc = null;
		InputStream in = null;
		Object fingerPrintCalculator = null;

		try {
			in = new FileInputStream(new File(inboxsource));
			PGPPrivateKey pub = getPGPPrivateKeys(pfxfile, pgppublickey, pass);
			Security.addProvider(new BouncyCastleProvider());
			in = PGPUtil.getDecoderStream(in);
			PGPObjectFactory pgpF = new PGPObjectFactory(in, (KeyFingerPrintCalculator) fingerPrintCalculator);
			Object o = pgpF.nextObject();
			PGPEncryptedDataList enc;
			if (o instanceof PGPEncryptedDataList) {
				enc = (PGPEncryptedDataList) o;
			} else {
				enc = (PGPEncryptedDataList) pgpF.nextObject();
			}

			Iterator<PGPEncryptedData> it = enc.getEncryptedDataObjects();
			PGPPrivateKey sKey = null;

			PGPPublicKeyEncryptedData pbe;
			for (pbe = null; sKey == null && it.hasNext(); sKey = pub) {
				pbe = (PGPPublicKeyEncryptedData) it.next();
			}

			if (sKey == null) {
				throw new IllegalArgumentException("Secret key for message not found.");
			}

			PublicKeyDataDecryptorFactory b = (new JcePublicKeyDataDecryptorFactoryBuilder()).setProvider("BC")
					.setContentProvider("BC").build(sKey);
			InputStream clear = pbe.getDataStream(b);
			PGPObjectFactory plainFact = new PGPObjectFactory(clear, (KeyFingerPrintCalculator) fingerPrintCalculator);
			Object message = plainFact.nextObject();
			System.out.println("Secret key info 3:: " + pbe.getKeyID() + new Date());
			if (message instanceof PGPCompressedData) {
				PGPCompressedData cData = (PGPCompressedData) message;
				PGPObjectFactory pgpFact = new PGPObjectFactory(cData.getDataStream(),
						(KeyFingerPrintCalculator) fingerPrintCalculator);
				message = pgpFact.nextObject();
			}

			if (message instanceof PGPLiteralData) {
				PGPLiteralData ld = (PGPLiteralData) message;
				unc = ld.getInputStream();
				File file = new File(verifypath + "PGP");
				File files = new File(inboxsource);

				Files.copy(unc, file.toPath(), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
				unc.close();
				in.close();
				boolean bo = verifyWithBC(verifypath + "PGP", verifypath);
				file.delete();
				files.delete();

				if (pbe.isIntegrityProtected() && !pbe.verify()) {
					System.out.println("Message failed integrity check");
					throw new PGPException("Message failed integrity check");
				}

				res = true;
				return res;
			}
		} catch (Exception var25) {
			System.out.println(var25);
		}

		return false;
	}

	public boolean filesDecryptions(String inboxSource, String verifyPath, String pfxFile, String pgpPublicKey,
			String pass) {
		FileOutputStream fos = null;
		boolean res = false;
		InputStream unc = null;
		InputStream in = null;
		KeyFingerPrintCalculator fingerPrintCalculator = new JcaKeyFingerprintCalculator();

		try {
			in = new FileInputStream(new File(inboxSource));
			PGPPrivateKey pub = getPGPPrivateKeys(pfxFile, pgpPublicKey, pass);
			Security.addProvider(new BouncyCastleProvider());
			in = PGPUtil.getDecoderStream(in);
			PGPObjectFactory pgpF = new PGPObjectFactory(in, fingerPrintCalculator);
			Object o = pgpF.nextObject();
			PGPEncryptedDataList enc;

			if (o instanceof PGPEncryptedDataList) {
				enc = (PGPEncryptedDataList) o;
			} else {
				enc = (PGPEncryptedDataList) pgpF.nextObject();
			}

			Iterator<PGPEncryptedData> it = enc.getEncryptedDataObjects();
			PGPPrivateKey sKey = null;
			PGPPublicKeyEncryptedData pbe = null;

			while (it.hasNext() && sKey == null) {
				pbe = (PGPPublicKeyEncryptedData) it.next();
				// Assume pub is valid and try to decrypt with it
				sKey = pub;
			}

			if (sKey == null) {
				throw new IllegalArgumentException("Secret key for message not found.");
			}

			JcePublicKeyDataDecryptorFactoryBuilder decryptorFactoryBuilder = new JcePublicKeyDataDecryptorFactoryBuilder()
					.setProvider("BC").setContentProvider("BC");
			InputStream clear = pbe.getDataStream(decryptorFactoryBuilder.build(sKey));
			PGPObjectFactory plainFact = new PGPObjectFactory(clear, fingerPrintCalculator);
			Object message = plainFact.nextObject();

			if (message instanceof PGPCompressedData) {
				PGPCompressedData cData = (PGPCompressedData) message;
				PGPObjectFactory pgpFact = new PGPObjectFactory(cData.getDataStream(), fingerPrintCalculator);
				message = pgpFact.nextObject();
			}

			if (message instanceof PGPLiteralData) {
				PGPLiteralData ld = (PGPLiteralData) message;
				unc = ld.getInputStream();
				File file = new File(verifyPath + "PGP");
				File files = new File(inboxSource);

				// Ensure the parent directory exists
				file.getParentFile().mkdirs();

				try (OutputStream foss = new FileOutputStream(file)) {
					Streams.pipeAll(unc, foss);
					if (foss != null)
						in.close();
				} catch (IOException e) {
					System.err.println("Error writing file: " + e.getMessage());
				}

				boolean bo = verifyWithBC(verifyPath + "PGP", verifyPath);
				file.delete();
				files.delete();

				if (pbe.isIntegrityProtected() && !pbe.verify()) {
					System.out.println("Message failed integrity check");
					throw new PGPException("Message failed integrity check");
				}

				res = true;
			}
		} catch (Exception e) {
			System.err.println("Error during decryption: " + e.getMessage());
		} finally {
			// Close streams in the finally block to ensure they are closed even if an
			// exception occurs
			try {
				if (unc != null)
					unc.close();
				if (in != null)
					in.close();

			} catch (IOException e) {
				System.err.println("Error closing streams: " + e.getMessage());
			}
		}

		return res;
	}

	public static boolean verifyWithBC(String _Sfile, String _Ofile) throws Exception {
		byte[] _signatureInBytes = null;
		byte[] _dataInBytes = null;
		X509Certificate _certificate = null;
		Signature sign = Signature.getInstance("SHA256withRSA");
		XMLParsers parser = new XMLParsers(new File(_Sfile));
		parser.parse();
		_certificate = parser.getCertificate();
		_dataInBytes = parser.getOriginalBytes();
		_signatureInBytes = parser.getSignature();
		Security.addProvider(new BouncyCastleProvider());
		CMSSignedData s = new CMSSignedData(new CMSProcessableByteArray(_dataInBytes), _signatureInBytes);
		SignerInformationStore signers = s.getSignerInfos();
		SignerInformation signerInfo = (SignerInformation) signers.getSigners().iterator().next();
		boolean result = false;
		result = signerInfo.verify(
				(new JcaSimpleSignerInfoVerifierBuilder()).setProvider("BC").build(_certificate.getPublicKey()));
		FileOutputStream fos = new FileOutputStream(_Ofile);
		byte[] Original_Content = parser.getOriginalBytes();
		fos.write(Original_Content, 0, Original_Content.length);
		fos.close();
		return result;
	}

	public static boolean _RowVerify(String _Sfile, String _Ofile) {
		boolean status = false;

		try {
			Signature sign = Signature.getInstance("SHA256withRSA");
			XMLParsers parser = new XMLParsers(new File(_Sfile));
			parser.parse();
			X509Certificate certificate = parser.getCertificate();
			sign.initVerify(certificate.getPublicKey());
			sign.update(parser.getOriginalBytes());
			status = sign.verify(parser.getSignature());
			if (status) {
				FileOutputStream fos = new FileOutputStream(_Ofile);
				fos.write(parser.getOriginalBytes(), 0, parser.getOriginalBytes().length);
				fos.close();
			}
			System.out.println("\n" + sign.verify(parser.getSignature()));
		} catch (SignatureException var7) {
			var7.printStackTrace();
		} catch (InvalidKeyException var8) {
		} catch (Exception var9) {
			System.out.println("Error" + var9.getMessage());
		}

		return status;
	}

}
