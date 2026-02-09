package com.H2H.Services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.H2H.Controllers.H2HController;
import com.H2H.Dao.Database;
import com.H2H.Entity.ACKFILES;
import com.H2H.Entity.CONFIGVALUE;
import com.H2H.Entity.TBL_CONFIG;
import com.H2H.Entity.TBL_INWFILE;
import com.H2H.Entity.UPLOADFILES;
import com.H2H.Entity.User;
import com.H2H.Repo.AckRepos;
import com.H2H.Repo.H2HRepository;
import com.H2H.Repo.InwFile;
import com.H2H.Repo.UploadRepos;
import com.H2H.Repo.UserRepository;

@Service("H2HServices")
public class H2HServices {

	private static final Logger logger = LoggerFactory.getLogger(H2HController.class);

	@Autowired
	private H2HRepository h2hRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UploadRepos uploadRepository;

	@Autowired
	private AckRepos ackRepos;

	@Autowired
	private InwFile inwFile;

	@Autowired
	private Database database;

	@Autowired
	private PasswordEncoder passwordEncoder;

	public void saveuploadfiles(UPLOADFILES usedata) {
		uploadRepository.save(usedata);
	}

	public String CheckIfFileExistsUpload(String filename) {

		String filenameis = uploadRepository.findFilenameByFilename(filename);
		if (filenameis == null) {

			return "N";
		}

		System.out.println("findFilenameByFilename  value is " + filenameis);

		return "Y";
	}

	public String CheckIfFileExistsInwards(String filename) {

		String filenameis = inwFile.findInwardsByFilename(filename);
		if (filenameis == null) {

			return "N";
		}

		System.out.println("findFilenameByFilename  value is " + filenameis);

		return "Y";
	}

	public String CheckIfFileExistsACK(String filename) {

		String filenameis = ackRepos.findACKSsByFilename(filename);
		if (filenameis == null) {

			return "N";
		}

		System.out.println("findFilenameByFilename  value is " + filenameis);

		return "Y";
	}

	public List<String> getAllFilenames(String startdate, String Enddate) {
		return inwFile.findFilenamesBetweenDates(startdate, Enddate);
	}

	public List<String> getAllFilenamesupload(String startdate, String Enddate) {
		logger.info("startdate:---------" + startdate + " -------enddate:---------" + Enddate);
		return uploadRepository.findFilenamesBetweenDates(startdate, Enddate);
	}

	public void saveFileINwards(TBL_INWFILE usedata) {
		inwFile.save(usedata);
	}

	public void saveACKFiles(ACKFILES usedata) {
		ackRepos.save(usedata);
	}

	public void saveUser(User usedata) {
		usedata.setPassword(passwordEncoder.encode(usedata.getPassword()));
		userRepository.save(usedata);
	}

	// Getting All For Dashboard
	public Iterable<TBL_CONFIG> dashboard_service() {
		return this.h2hRepository.findAll();

	}

//	// update data to  Config table (pending)
	public void updateService(CONFIGVALUE cfg) {
		// this.h2hRepository.save(cfg);
		database.Update_db(cfg);
	}

	// search File form the Tables
	public String serachFiles(String Filename, String Table) throws Exception {

		if (Table.equals("ACKFILES")) {

			String t1 = this.database.searchAndDelete(Filename, Table);
			return t1;

		}

		else if (Table.equals("TBL_INWFILE")) {

			String t2 = this.database.searchAndDelete(Filename, Table);
			return t2;

		}

		else {
			String t3 = this.database.searchAndDelete(Filename, Table);
			return t3;

		}

	}

	public boolean checkApplicationRunning(String H2HApplication) {
		try {
			ProcessBuilder processBuilder = new ProcessBuilder("tasklist");
			Process process = processBuilder.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.contains(H2HApplication)) {
					return true;
				}
			}

			process.waitFor();
			reader.close();
		} catch (IOException | InterruptedException e) {
			// customLogger.logError("An error occurred: ", e);
			e.printStackTrace();
		}

		return false;
	}

}
