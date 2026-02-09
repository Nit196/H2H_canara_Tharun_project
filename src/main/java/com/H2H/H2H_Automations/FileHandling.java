package com.H2H.H2H_Automations;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.H2H.Entity.ACKFILES;
import com.H2H.Entity.TBL_INWFILE;
import com.H2H.Entity.UPLOADFILES;
import com.H2H.Services.H2HServices;
import com.H2H.Services.ManageEmail;
import com.H2H.Services.Sftp_Connection;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

@Service
public class FileHandling {

	ChannelSftp channelSftp = null;
	Channel channel = null;
	Session session = null;

	private static final Logger logger = LoggerFactory.getLogger(FileHandling.class);

	private final Encryption_Decryption tools;

	private final H2HServices h2hservice;

	private final Sftp_Connection getSftp_connection;

	@Autowired
	private ManageEmail manageEmailService;

	public FileHandling(Encryption_Decryption tools, H2HServices h2hservice, Sftp_Connection getSftp_connection) {
		super();
		this.tools = tools;
		this.h2hservice = h2hservice;
		this.getSftp_connection = getSftp_connection;
		// this.manageEmailService = manageEmailService;
	}

	public void UploadToNpci(String sourcePath, String duplicatesPath, String signedPath, String uploadErrorPath,
			String originalPath, String sshKey, String passphrase, String username, String host, String port,
			String password, String pfxFile, String npciCert, String pfxPassword, String archivePath) throws Exception {

		ChannelSftp channelSftp = null;

		try {

			File sourceDir = new File(sourcePath);
			if (!sourceDir.exists() || !sourceDir.isDirectory()) {
				LogAppend.logError("Source directory not found");
				System.out.println("Source directory not found");
				return;
			}

			String[] files = sourceDir.list();
			if (files == null || files.length == 0) {
				LogAppend.logError("No files found in Source folder");
				System.out.println("No files found in Source folder");
				return;
			}

			// -----------------------------------------
			// PROCESS EACH FILE
			// -----------------------------------------
			for (String fileName : files) {

				boolean isDuplicate = false;

				try {
					String res = h2hservice.CheckIfFileExistsUpload(fileName);
					isDuplicate = res.equalsIgnoreCase("Y");
					LogAppend.logError("Duplicate check result: " + res);
					System.out.println("Duplicate check result: " + res);
				} catch (Exception ex) {
					LogAppend.logError("Error checking duplicate: " + ex.getMessage());
				}

				Path src = Paths.get(sourcePath, fileName);
				Path duplicate = Paths.get(duplicatesPath, fileName);
				Path signed = Paths.get(signedPath, fileName);
				Path original = Paths.get(originalPath, fileName);
				Path error = Paths.get(uploadErrorPath, fileName);
				Path archive = Paths.get(archivePath, fileName);

				try {
					if (!isDuplicate) {

						FileUtils.copyFile(src.toFile(), original.toFile(), true);
						LogAppend.logError("Copied to Original: " + fileName);
						System.out.println("Copied to Original: " + fileName);
						boolean encrypted = tools.filesEncryption(pfxFile, pfxPassword, src.toString(), npciCert,
								signed.toString());

						LogAppend.logError("Encryption status: " + encrypted);
						System.out.println("Encryption status: " + encrypted);

						if (encrypted) {
							FileUtils.copyFile(signed.toFile(), archive.toFile(), true);
							src.toFile().delete();
							LogAppend.logError("Copied to Archive: " + archive.toString());
							System.out.println("Copied to Archive: " + archive.toString());
						} else {
							FileUtils.copyFile(src.toFile(), error.toFile(), true);
							src.toFile().delete();
							LogAppend.logError("Copied to Error folder: " + error.toString());
							System.out.println("Copied to Error folder: " + error.toString());
						}

					} else {
						FileUtils.copyFile(src.toFile(), duplicate.toFile(), true);
						src.toFile().delete();
						LogAppend.logError("Duplicate moved: " + fileName);
						System.out.println("Duplicate moved: " + fileName);
					}
				} catch (Exception ex) {
					LogAppend.logError("Error processing file " + fileName + ": " + ex.getMessage());
					System.out.println("Error processing file " + fileName + ": " + ex.getMessage());

				}
			}

			// -----------------------------------------
			// CONNECT TO SFTP
			// -----------------------------------------
			try {
				channelSftp = getSftp_connection.get_SftpConnection(sshKey, passphrase, username, host,
						Integer.parseInt(port), password);
			} catch (Exception e) {
				LogAppend.logError("Failed to connect SFTP: " + e.getMessage());
				System.out.println("Failed to connect SFTP: " + e.getMessage());
				return;
			}

			// -----------------------------------------
			// UPLOAD SIGNED FILES TO NPCI
			// -----------------------------------------
			File signedDir = new File(signedPath);
			String[] signedFiles = signedDir.list();

			if (signedFiles == null || signedFiles.length == 0) {
				LogAppend.logError("No signed files found for upload");
				System.out.println("No signed files found for upload");

				return;
			}

			for (String signedFileName : signedFiles) {

				File signedFile = new File(signedPath + File.separator + signedFileName);

				try (FileInputStream fis = new FileInputStream(signedFile)) {

					// Always move to root directory
					channelSftp.cd("/");

					channelSftp.put(fis, signedFile.getName());
					LogAppend.logError("Uploaded to NPCI: " + signedFileName);
					System.out.println("Uploaded to NPCI: " + signedFileName);

					// Save in DB
					UPLOADFILES upload = new UPLOADFILES();
					upload.setFINAME(signedFileName);
					upload.setFILEEXT("txt/zip");
					upload.setPROCESS("File Encryption");
					upload.setREMARKS("SIGN/Encryption SUCCESS");
					upload.setSTATUS("S");
					upload.setLOCATION("/Upload/Signed");
					String rollDate = extractDate(signedFileName);
					upload.setFILE_RECEIVE_DATE(rollDate);

					try {
						h2hservice.saveuploadfiles(upload);
						LogAppend.logError("Inserted DB record for: " + signedFileName);
						System.out.println("Inserted DB record for: " + signedFileName);
					} catch (Exception ex) {
						LogAppend.logError("DB insert failed for: " + signedFileName + " | " + ex.getMessage());
						System.out.println("DB insert failed for: " + signedFileName + " | " + ex.getMessage());

					}

				} catch (Exception ex) {
					LogAppend.logError("Upload failed for: " + signedFileName + " | " + ex.getMessage());
					System.out.println("Upload failed for: " + signedFileName + " | " + ex.getMessage());
				}

				// Delete uploaded file
				try {
					if (signedFile.exists())
						signedFile.delete();
				} catch (Exception ignore) {
				}
			}

		} catch (Exception ex) {
			LogAppend.logError("General error in UploadToNpci: " + ex.getMessage());
			System.out.println("General error in UploadToNpci: " + ex.getMessage());
		} finally {
			// -----------------------------------------
			// SAFELY CLOSE SFTP CONNECTION
			// -----------------------------------------
			try {
				if (channelSftp != null && channelSftp.isConnected()) {
					channelSftp.disconnect();
					LogAppend.logError("SFTP connection closed");
					System.out.println("SFTP connection closed");
				}
			} catch (Exception ex) {
				LogAppend.logError("Error closing SFTP: " + ex.getMessage());
			}
		}

		System.out.println("UPLOAD Process Completed");

	}

	public void SFTPACKDownload(String pfxfile, String BANKCrts, String pfxpassword, String mDestErrorFiles,
			String mSourceFiles, String mOriginalFiles, String verifiedpathss, String SSHPRKEYFILE, String PASSPHRASE,
			String username, String host, String port, String password, String Bankname) throws JSchException {

		ChannelSftp channelSftp = null;
		int number = Integer.parseInt(port);

		try {
			// ------------------------ CONNECT TO SFTP -------------------------------
			try {
				channelSftp = getSftp_connection.get_SftpConnection(SSHPRKEYFILE, PASSPHRASE, username, host, number,
						password);

				if (channelSftp == null) {
					LogAppend.logError("SFTP Connection Failed. Channel is NULL.");
					return;
				}

				LogAppend.logError("Connected to NPCI SFTP for ACK Download at "
						+ LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));

			} catch (Exception ex) {
				LogAppend.logError("Error establishing SFTP connection: " + ex.getMessage());
				return;
			}

			// ----------------------------------------------------------------------
			// HIGH-PERFORMANCE FILTERED LISTING USING SERVER-SIDE FILTER
			// ----------------------------------------------------------------------
			List<ChannelSftp.LsEntry> validEntries = new ArrayList<>();

			channelSftp.ls("/" + Bankname + "/*", new ChannelSftp.LsEntrySelector() {
				@Override
				public int select(ChannelSftp.LsEntry entry) {

					String fileName = entry.getFilename();

					if (entry.getAttrs().isDir())
						return CONTINUE; // skip dirs
					if (fileName.startsWith("."))
						return CONTINUE; // skip hidden
					if (fileName.contains("Report"))
						return CONTINUE; // skip report files

					String ext = FilenameUtils.getExtension(fileName).toLowerCase();

					// allow only xml, zip, txt
					if (!ext.equals("xml") && !ext.equals("zip") && !ext.equals("txt"))
						return CONTINUE;

					validEntries.add(entry);
					return CONTINUE;
				}
			});

			// ----------------------------------------------------------------------
			// PROCESS VALID FILES
			// ----------------------------------------------------------------------
			for (ChannelSftp.LsEntry entry : validEntries) {

				String fileName = entry.getFilename();
				System.out.println("Processing Valid File: " + fileName);

				String ext = FilenameUtils.getExtension(fileName).toLowerCase();

				// Paths
				Path sourcePath = Paths.get(mSourceFiles, fileName);
				Path originalPath = Paths.get(mOriginalFiles, fileName);
				Path verifiedPath = Paths.get(verifiedpathss, fileName);
				Path errorPath = Paths.get(mDestErrorFiles, fileName);

				// ------------------------ CHECK DUPLICATE -------------------------------
				String duplicateFlag = "Y";
				try {
					duplicateFlag = h2hservice.CheckIfFileExistsACK(fileName);
				} catch (Exception ex) {
					LogAppend.logError("DB Duplicate Check Error: " + ex.getMessage());
				}

				if (!duplicateFlag.equals("N")) {
					LogAppend.logError("Duplicate ACK Found: " + fileName);
					continue;
				}

				// ------------------------ DOWNLOAD FILE -------------------------------
				try {
					channelSftp.cd("/" + Bankname);

					try (BufferedInputStream bis = new BufferedInputStream(channelSftp.get(fileName))) {
						Files.copy(bis, sourcePath, StandardCopyOption.REPLACE_EXISTING);
					}

					FileUtils.copyFile(sourcePath.toFile(), originalPath.toFile(), true);

					LogAppend.logError("Downloaded Successfully: " + fileName);

				} catch (Exception ex) {
					LogAppend.logError("Download Failed: " + ex.getMessage());
					FileUtils.copyFile(sourcePath.toFile(), errorPath.toFile(), true);
					continue;
				}

				// ------------------------ VERIFY FILE -------------------------------
				boolean verified = false;
				try {
					verified = tools.filesDecryptions(sourcePath.toString(), verifiedPath.toString(), pfxfile, BANKCrts,
							pfxpassword);
				} catch (Exception ex) {
					LogAppend.logError("Decryption Error: " + ex.getMessage());
				}

				// ------------------------ INSERT INTO DB -------------------------------
				ACKFILES ack = new ACKFILES();
				ack.setFINAME(fileName);
				ack.setFILEEXT(ext);
				ack.setRECIVEDATE(new Date());
				ack.setPROCESS("ACK Download");
				ack.setFILESIZE(entry.getAttrs().getSize() + "");

				ack.setFILE_RECEIVE_DATE(extractDate(fileName));

				if (!verified) {
					ack.setSTATUS("F");
					ack.setREMARKS("Verification Failed");
					FileUtils.copyFile(sourcePath.toFile(), errorPath.toFile(), true);
				} else {
					ack.setSTATUS("S");
					ack.setREMARKS("Verified Successfully");
				}

				h2hservice.saveACKFiles(ack);
				manageEmailService.manageEmail(ack);

				// Delete temp file
				try {
					Files.deleteIfExists(sourcePath);
				} catch (IOException ignored) {
				}
			}

		} catch (Exception ex) {
			LogAppend.logError("Unexpected error in ACK download: " + ex.getMessage());
		} finally {
			if (channelSftp != null && channelSftp.isConnected()) {
				try {
					channelSftp.disconnect();
				} catch (Exception ignored) {
				}
			}

			LogAppend.logError("NPCI SFTP ACK Download Process Completed");
		}
	}

//	public void SFTPACKDownload(String pfxfile, String BANKCrts, String pfxpassword, String mDestErrorFiles,
//			String mSourceFiles, String mOriginalFiles, String verifiedpathss, String SSHPRKEYFILE, String PASSPHRASE,
//			String username, String host, String port, String password, String Bankname) throws JSchException {
//
//		ChannelSftp channelSftp = null;
//		int number = Integer.parseInt(port);
//
//		try {
//			// ------------------------ CONNECT TO SFTP -------------------------------
//			try {
//				channelSftp = getSftp_connection.get_SftpConnection(SSHPRKEYFILE, PASSPHRASE, username, host, number,
//						password);
//
//				if (channelSftp == null) {
//					LogAppend.logError("SFTP Connection Failed. Channel is NULL.");
//					System.out.println("SFTP Connection Failed. Channel is NULL.");
//					return;
//				}
//
//				System.out.println("Connected to NPCI SFTP for ACK Download");
//				LogAppend.logError("Connected to NPCI SFTP for ACK Download at "
//						+ LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
//
//			} catch (Exception ex) {
//				LogAppend.logError("Error establishing SFTP connection: " + ex.getMessage());
//				System.out.println("Error establishing SFTP connection: " + ex.getMessage());
//				return;
//			}
//
//			// ------------------------ LIST REMOTE FILES -------------------------------
//			Vector<ChannelSftp.LsEntry> entries;
//			try {
//				entries = channelSftp.ls("/" + Bankname);
//			} catch (SftpException sf) {
//				LogAppend.logError("Failed to list files in remote directory: " + sf.getMessage());
//				System.out.println("Failed to list remote directory: " + sf.getMessage());
//				return;
//			}
//
//			for (ChannelSftp.LsEntry entry : entries) {
//
//				String FileName = entry.getFilename();
//				System.out.println("Processing File: " + FileName);
//
//				// Skip non-required files
//				if (FileName.contains("Report")) {
//					continue;
//				}
//
//				String ext = FilenameUtils.getExtension(FileName).toLowerCase();
//				if (!ext.equals("xml") && !ext.equals("zip")) {
//					LogAppend.logError("Skipping invalid file type: " + FileName);
//					System.out.println("Skipping invalid file type: " + FileName);
//					continue;
//				}
//
//				// Paths
//				Path mSourceFile = Paths.get(mSourceFiles, FileName);
//				Path mOriginalFile = Paths.get(mOriginalFiles, FileName);
//				Path verifiedpaths = Paths.get(verifiedpathss, FileName);
//				Path mDestErrorFile = Paths.get(mDestErrorFiles, FileName);
//
//				// ------------------------ CHECK DUPLICATE -------------------------------
//				String IsDuplicate = "Y";
//				try {
//					IsDuplicate = h2hservice.CheckIfFileExistsACK(FileName);
//				} catch (Exception ex) {
//					LogAppend.logError("DB Duplicate Check Error: " + ex.getMessage());
//					System.out.println("DB Duplicate Check Error: " + ex.getMessage());
//				}
//
//				if (!IsDuplicate.equals("N")) {
//					LogAppend.logError("Duplicate ACK Found: " + FileName);
//					System.out.println("Duplicate ACK Found: " + FileName);
//					continue;
//				}
//
//				// ------------------------ DOWNLOAD FILE -------------------------------
//				try {
//					channelSftp.cd("/" + Bankname);
//
//					try (BufferedInputStream bis = new BufferedInputStream(channelSftp.get(FileName))) {
//						Files.copy(bis, mSourceFile, StandardCopyOption.REPLACE_EXISTING);
//					}
//					LogAppend.logError("Downloaded Successfully: " + FileName);
//					System.out.println("Downloaded Successfully: " + FileName);
//					FileUtils.copyFile(mSourceFile.toFile(), mOriginalFile.toFile(), true);
//
//				} catch (SftpException sf) {
//
//					LogAppend.logError("SFTP Download Failed: " + sf.getMessage());
//					FileUtils.copyFile(mSourceFile.toFile(), mDestErrorFile.toFile(), true);
//					continue;
//				} catch (IOException io) {
//					LogAppend.logError("IO Error During ACK Download: " + io.getMessage());
//					continue;
//				}
//
//				// ------------------------ VERIFY FILE -------------------------------
//				boolean verificationFlag = false;
//				try {
//					verificationFlag = tools.filesDecryptions(mSourceFile.toString(), verifiedpaths.toString(), pfxfile,
//							BANKCrts, pfxpassword);
//				} catch (Exception ex) {
//					LogAppend.logError("Decryption Exception: " + ex.getMessage());
//				}
//
//				// ------------------------ SAVE ACK DETAILS -------------------------------
//				ACKFILES ack = new ACKFILES();
//				ack.setFINAME(FileName);
//				ack.setFILEEXT(ext);
//				ack.setRECIVEDATE(new Date());
//				ack.setPROCESS("ACK Download");
//				ack.setFILESIZE(entry.getAttrs().getSize() + "");
//
//				String rollDate = extractDate(FileName);
//				ack.setFILE_RECEIVE_DATE(rollDate);
//
//				if (!verificationFlag) {
//					LogAppend.logError("ACK Verification Failed: " + FileName);
//					FileUtils.copyFile(mSourceFile.toFile(), mDestErrorFile.toFile(), true);
//					ack.setSTATUS("F");
//					ack.setREMARKS("Verification Failed");
//				} else {
//					LogAppend.logError("ACK Verification Success: " + FileName);
//					ack.setSTATUS("S");
//					ack.setREMARKS("Verified Successfully");
//				}
//
//				h2hservice.saveACKFiles(ack);
//				manageEmailService.manageEmail(ack);
//
//				// Delete source file
//				try {
//					Files.deleteIfExists(mSourceFile);
//				} catch (IOException io) {
//					LogAppend.logError("Failed to delete local temp file: " + io.getMessage());
//				}
//			}
//
//		} catch (Exception ex) {
//			LogAppend.logError("Unexpected error in ACK download: " + ex.getMessage());
//			System.out.println("Unexpected error in ACK download: " + ex.getMessage());
//
//		} finally {
//			// ------------------------ SAFE DISCONNECT -------------------------------
//			if (channelSftp != null && channelSftp.isConnected()) {
//				try {
//					channelSftp.disconnect();
//				} catch (Exception ex) {
//					LogAppend.logError("Error closing SFTP connection: " + ex.getMessage());
//				}
//			}
//
//			LogAppend.logError("NPCI SFTP ACK Download Process Completed");
//			System.out.println("NPCI SFTP ACK Download Process Completed");
//		}
//	}

	private String extractDate(String fileName) {
		Matcher matcher = Pattern.compile("(\\d{8})").matcher(fileName);
		return matcher.find() ? matcher.group(1) : null;
	}

	public void SFTPInwardsDownload(String pfxfile, String BANKCrts, String pfxpassword, String mDestErrorFiles,
			String mSourceFiles, String mOriginalFiles, String verifiedpathss, String SSHPRKEYFILE, String PASSPHRASE,
			String username, String host, String port, String password, String Bankname) {

		ChannelSftp channelSftp = null;
		Session session = null;

		try {

			int portNumber = Integer.parseInt(port);

			LogAppend.logError("Connected to SFTP host: " + host);
			System.out.println("Connected to SFTP host: " + host);

			// ------------------------ CONNECT TO SFTP -------------------------------
			try {
				channelSftp = getSftp_connection.get_SftpConnection(SSHPRKEYFILE, PASSPHRASE, username, host,
						portNumber, password);

				if (channelSftp == null) {
					LogAppend.logError("SFTP Connection Failed. Channel is NULL.");
					System.out.println("SFTP Connection Failed. Channel is NULL.");
					return;
				}

				System.out.println("Connected to NPCI SFTP for ACK Download");
				LogAppend.logError("Connected to NPCI SFTP for ACK Download at "
						+ LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));

			} catch (Exception ex) {
				LogAppend.logError("Error establishing SFTP connection: " + ex.getMessage());
				System.out.println("Error establishing SFTP connection: " + ex.getMessage());
				return;
			}

			// -----------------------------
			// Navigate to bank directory
			// -----------------------------
			channelSftp.cd("/" + Bankname);
			LogAppend.logError("Navigated to remote folder: /" + Bankname);
			System.out.println("Navigated to remote folder: /" + Bankname);

			// -----------------------------
			// Read file list
			// -----------------------------
			List<String> fileNames = listFilesFromSFTP(channelSftp, "./");

			for (String fileName : fileNames) {

				try {

					// Skip reports
					if (fileName.endsWith("Report.zip") || fileName.contains("Report")) {
						LogAppend.logError("Skipping report file: " + fileName);
						System.out.println("Skipping report file: " + fileName);
						continue;
					}

					// Validate extension
					if (!isValidFileExtension(fileName)) {
						LogAppend.logError("Invalid extension skipped: " + fileName);
						System.out.println("Invalid extension skipped: " + fileName);
						continue;
					}

					// Duplicate check
					if (!"N".equals(h2hservice.CheckIfFileExistsInwards(fileName))) {
						LogAppend.logError("Duplicate file skipped: " + fileName);
						System.out.println("Duplicate file skipped: " + fileName);
						continue;
					}

					// -----------------------------
					// Build local paths
					// -----------------------------
					Path sourceFile = Paths.get(mSourceFiles, fileName);
					Path originalFile = Paths.get(mOriginalFiles, fileName);
					Path verifiedPath = Paths.get(verifiedpathss, fileName);
					Path errorFile = Paths.get(mDestErrorFiles, fileName);

					// -----------------------------
					// Download the file
					// -----------------------------
					try (InputStream inputStream = channelSftp.get(fileName)) {
						Files.copy(inputStream, sourceFile, StandardCopyOption.REPLACE_EXISTING);
					}

					LogAppend.logError("Downloaded file: " + fileName);
					System.out.println("Downloaded file: " + fileName);

					// Copy original backup
					FileUtils.copyFile(sourceFile.toFile(), originalFile.toFile());

					// -----------------------------
					// Verify + Decrypt the file
					// -----------------------------
					boolean isVerified = tools.filesDecryptions(sourceFile.toString(), verifiedPath.toString(), pfxfile,
							BANKCrts, pfxpassword);

					if (!isVerified) {
						FileUtils.copyFile(sourceFile.toFile(), errorFile.toFile());
						LogAppend.logError("Verification FAILED: " + fileName);
						System.out.println("Verification FAILED: " + fileName);
						continue;
					}

					// -----------------------------
					// Extract Roll Date
					// -----------------------------
					String rollDate = extractRollDate(fileName);

					// -----------------------------
					// Save DB Entry
					// -----------------------------
					TBL_INWFILE record = new TBL_INWFILE();
					record.setFINAME(fileName);
					record.setFILEEXT(FilenameUtils.getExtension(fileName));
					record.setPROCESS("Inward Download");
					record.setSTATUS("S");
					record.setREMARKS("Successfully Downloaded");
					record.setLOCATION("/Inward/Verified");
					record.setFILE_RECEIVE_DATE(rollDate);

					h2hservice.saveFileINwards(record);
					LogAppend.logError("Verified & Saved DB record: " + fileName);
					System.out.println("Verified & Saved DB record: " + fileName);

					// Remove temp file
					Files.deleteIfExists(sourceFile);

				} catch (Exception innerEx) {
					LogAppend.logError("Error processing file " + fileName + " :: " + innerEx.getMessage());
					System.out.println("Error processing file " + fileName + " :: " + innerEx.getMessage());
				}
			}

		} catch (Exception e) {
			LogAppend.logError("SFTP download failed :: " + e.getMessage());
			System.out.println("SFTP download failed :: " + e.getMessage());

		} finally {
			// -----------------------------
			// CLOSE SFTP RESOURCES
			// -----------------------------
			if (channelSftp != null && channelSftp.isConnected()) {
				channelSftp.disconnect();
				LogAppend.logError("SFTP Channel closed.");
				System.out.println("SFTP Channel closed.");
			}

			if (session != null && session.isConnected()) {
				session.disconnect();
				LogAppend.logError("SFTP Session closed.");
				System.out.println("SFTP Session closed.");
			}
		}
	}

	// Method to check if the file extension is valid (xml, txt, zip)
	private boolean isValidFileExtension(String fileName) {
		String extension = FilenameUtils.getExtension(fileName).toLowerCase();
		boolean isValid = extension.equals("xml") || extension.equals("txt") || extension.equals("zip");
		if (!isValid) {
			LogAppend.logError("Invalid file extension for file: " + fileName + " "
					+ LocalTime.now().format(DateTimeFormatter.ofPattern("hh:mm:ss a")));
		}
		return isValid;
	}

	public Map<String, Object> get_SftpConnectionWithSession(String keyFile, String passphrase, String user,
			String host, int port, String password) {

		Map<String, Object> map = new HashMap<>();

		try {
			JSch jsch = new JSch();

			if (keyFile != null && !keyFile.isEmpty()) {
				jsch.addIdentity(keyFile, passphrase);
			}

			Session session = jsch.getSession(user, host, port);
			session.setPassword(password);

			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);

			session.connect(30000);

			Channel channel = session.openChannel("sftp");
			channel.connect();

			map.put("session", session);
			map.put("channel", (ChannelSftp) channel);

		} catch (Exception e) {
			throw new RuntimeException("SFTP connection failed: " + e.getMessage());
		}

		return map;
	}

//	public List<String> listFilesFromSFTP(ChannelSftp channel, String path) {
//		List<String> fileNames = new ArrayList<>();
//
//		try {
//			Vector<ChannelSftp.LsEntry> entries = channel.ls(path);
//
//			for (ChannelSftp.LsEntry entry : entries) {
//				if (!entry.getAttrs().isDir()) {
//					fileNames.add(entry.getFilename());
//				}
//			}
//
//		} catch (Exception e) {
//			LogAppend.logError("Failed to list files: " + e.getMessage());
//		}
//
//		return fileNames;
//	}
//	public List<String> listFilesFromSFTP(ChannelSftp channel, String path) {
//		List<String> fileNames = new ArrayList<>();
//
//		try {
//			// Use wildcard filtering on the server side
//			Vector<ChannelSftp.LsEntry> entries = channel.ls(path + "/*");
//
//			for (ChannelSftp.LsEntry entry : entries) {
//
//				String name = entry.getFilename();
//
//				// Skip directories
//				if (entry.getAttrs().isDir())
//					continue;
//
//				// Skip hidden files (. and ..)
//				if (name.startsWith("."))
//					continue;
//
//				// Skip report files
//				if (name.contains("Report"))
//					continue;
//
//				fileNames.add(name);
//			}
//
//		} catch (Exception e) {
//			LogAppend.logError("Failed to list files: " + e.getMessage());
//		}
//
//		return fileNames;
//	}
	public List<String> listFilesFromSFTP(ChannelSftp channel, String path) {
		List<String> fileNames = new ArrayList<>();

		try {
			// Server-side wildcard filtering
			Vector<ChannelSftp.LsEntry> entries = channel.ls(path + "/*");

			for (ChannelSftp.LsEntry entry : entries) {

				String fileName = entry.getFilename();

				// Skip directories
				if (entry.getAttrs().isDir())
					continue;

				// Skip hidden files
				if (fileName.startsWith("."))
					continue;

				// Skip any Report files
				if (fileName.contains("Report"))
					continue;

				// Allowed extensions only
				String ext = FilenameUtils.getExtension(fileName).toLowerCase();
				if (!ext.equals("xml") && !ext.equals("zip") && !ext.equals("txt"))
					continue;

				fileNames.add(fileName);
			}

		} catch (Exception e) {
			LogAppend.logError("Failed to list files: " + e.getMessage());
		}

		return fileNames;
	}

	private String extractRollDate(String fileName) {
		Matcher m = Pattern.compile("(\\d{8})").matcher(fileName);
		return m.find() ? m.group(1) : null;
	}

}
