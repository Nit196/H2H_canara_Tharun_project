package com.H2H.Services;

import java.util.HashMap;
import java.util.Vector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;

@Service
public class Sftp_Files_count {
	// @Autowired
	// private Custome_Logger customLogger;

	@Autowired
	private Sftp_Connection sftp_Connection;
	String host = null;
	int port = 0;
	String ports = null;
	String username = null;
	String password = null;
	String BS_SHORTCODE = null;
	String SSHPRKEYFILE = null;
	String PASSPHRASE = null;
	ChannelSftp channelSftp = null;
	ChannelSftp channel = null;

	int[] count_sftp = new int[3];

	public int[] Sftp_connection() {

		try {
			HashMap map = sftp_Connection.getDataFromDatabase();
			host = map.get("SS_HOST_ADDR").toString();
			ports = (String) map.get("SS_PORTNO");
			port = Integer.parseInt(ports);
			username = map.get("SS_USERNAME").toString();
			password = map.get("SS_PASS").toString();
			BS_SHORTCODE = map.get("BS_SHORTCODE").toString();
			SSHPRKEYFILE = map.get("SS_SSHPRKEYFILE").toString();
			PASSPHRASE = map.get("SS_PASSPHRASE").toString();
			try {
				ChannelSftp channel = sftp_Connection.get_SftpConnection(SSHPRKEYFILE, PASSPHRASE, username, host, port,
						password);
				channelSftp = (ChannelSftp) channel;
				channelSftp.cd("./");
				Vector<LsEntry> root = channelSftp.ls("./");
				int Root_Count = root.size() - 2;
				channelSftp.cd("./" + "Inbox");
				Vector<LsEntry> inbox = channelSftp.ls("./");
				int Inbox_count = inbox.size();
				channelSftp.cd("/" + "Inbox");
				Vector<LsEntry> Bankcode = channelSftp.ls("./");
				int Bank_count = Bankcode.size();
				count_sftp[0] = Root_Count;
				count_sftp[1] = Inbox_count;
				count_sftp[2] = Bank_count;
				channel.disconnect();

			} catch (Exception e) {
				System.out.println("not connected to NPCI");
				// customLogger.logError("An error occurred: ", e);
			}

		} catch (Exception e) {
			System.out.println("Sftp_connection()  not working :" + e);
			// customLogger.logError("An error occurred: ", e);

			e.printStackTrace();
		}

		return count_sftp;

	}

}
