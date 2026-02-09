package com.H2H.Services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.H2H.Entity.TBL_CONFIG;
import com.H2H.H2H_Automations.LogAppend;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

@Service
public class Sftp_Connection {

//	@Autowired
//	private Custome_Logger customLogger;

	@Autowired
	private H2HServices h2hServices;

	String host = null;
	int port;
	String username = null;
	String password = null;
	String BS_SHORTCODE = null;
	String SSHPRKEYFILE = null;
	String PASSPHRASE = null;
	ChannelSftp channelSftp = null;

	public HashMap getDataFromDatabase() {
		Iterable<TBL_CONFIG> data = this.h2hServices.dashboard_service();
		HashMap<String, String> map = new HashMap<String, String>();

		data.forEach(e -> {

			if (e.getConfigKey().equals("SS_USERNAME") || e.getConfigKey().equals("SS_HOST_ADDR")
					|| e.getConfigKey().equals("SS_PORTNO") || e.getConfigKey().equals("SS_PASS")
					|| e.getConfigKey().equals("SS_SSHPRKEYFILE") || e.getConfigKey().equals("SS_PASSPHRASE")
					|| e.getConfigKey().equals("BS_SHORTCODE") || e.getConfigKey().equals("SS_PASS")) {

				System.out.println(
						"Data received From Database is:" + " " + e.getConfigKey() + " ---------" + e.getConfigValue());

				map.put(e.getConfigKey(), e.getConfigValue());
			}
		});
		// System.out.println("DATa from database is------------>>"+map.toString());
		return map;
	}

	public ChannelSftp get_SftpConnection(String SSHPRKEYFILE, String PASSPHRASE, String username, String host,
			int port, String password) {
		ChannelSftp channel = null;
		System.out.println(" SSHPRKEYFILE :" + SSHPRKEYFILE + " || " + "PASSPHRASE" + PASSPHRASE + "username :" + "|| "
				+ username + " || " + "host :" + host + "port :" + port + " || " + "password" + password);
		try {
			JSch jsch = new JSch();

			// jsch.addIdentity(SSHPRKEYFILE,PASSPHRASE);

			Session session = jsch.getSession(username, host, port);
			session.setPassword(password);
			session.setConfig("StrictHostKeyChecking", "no");
			session.connect();
			channel = (ChannelSftp) session.openChannel("sftp");
			channel.connect();

			System.out.println("isConnected :" + channel.isConnected());
			System.out.println("isEOF :" + channel.isEOF());
		} catch (JSchException e) {
			System.out.println("SEsssion is Down :" + e);
			System.out.println("SEsssion is Down :" + e.getMessage());
		}

		return channel;
	}

	public void uploadFile(File signedFiledata, ChannelSftp channelSftps) {

		FileInputStream fis = null;

		try {
			fis = new FileInputStream(signedFiledata);

			// Change to the root directory on the SFTP server
			channelSftps.cd("/");

			// Upload the file to the SFTP server
			channelSftps.put(fis, signedFiledata.getName());
			LogAppend.logError(signedFiledata.getName() + " ::  Successfully Uplaoded to NPCI End");

		} catch (Exception e) {

		} finally {
			// Close the FileInputStream and delete the file
			try {
				if (fis != null) {
					fis.close();
				}
			} catch (IOException e) {

			}

		}
	}

}
