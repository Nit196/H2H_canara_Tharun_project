package com.H2H.Controllers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.H2H.Config.FilesTwoDates;
import com.H2H.Entity.CONFIGVALUE;
import com.H2H.Entity.FileStatus;
import com.H2H.Entity.JsonRequest;
import com.H2H.Entity.TBL_CONFIG;
import com.H2H.Entity.User;
import com.H2H.H2H_Automations.FileHandling;
import com.H2H.H2H_Automations.LogAppend;
import com.H2H.Repo.Day_Wise_File;
import com.H2H.Repo.Day_Wise_Upload;
import com.H2H.Repo.InwFile;
import com.H2H.SecurityAuth.UserDetailsServiceImpl;
//import com.H2H.Repo.Day_Wise_File;
import com.H2H.Services.H2HServices;
import com.H2H.Services.Sftp_Connection;
import com.H2H.Services.Sftp_Files_count;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.SftpException;

@Controller
public class H2HController {


	private static final Logger logger = LoggerFactory.getLogger(H2HController.class);
	
	private final H2HServices h2hServices;

	private final FileHandling fileHandling;	
	
	private final CONFIGVALUE config;
	
	private final Sftp_Files_count sftp_connection;
		
	private final FileStatus fileStatus;
	
	private final FilesTwoDates filesTwoDates;
	
	private final Sftp_Connection getSftp_connection;
	
	private final UserDetailsService userDetailsService;
	
	private final UserDetailsServiceImpl userDetailsServices;
	
	private final H2HServices h2hServicess;
	
	private final Day_Wise_File day_Wise_File;

	private final Day_Wise_Upload day_Wise_Upload;
	
	private final InwFile inwfile;
	
	
	public H2HController(H2HServices h2hServices, FileHandling fileHandling, CONFIGVALUE config,
			Sftp_Files_count sftp_connection, com.H2H.Entity.FileStatus fileStatus, FilesTwoDates filesTwoDates,
			Sftp_Connection getSftp_connection, UserDetailsService userDetailsService,
			UserDetailsServiceImpl userDetailsServices, H2HServices h2hServicess,
			com.H2H.Repo.Day_Wise_File day_Wise_File, Day_Wise_Upload day_Wise_Upload, InwFile inwfile) {
		super();
		this.h2hServices = h2hServices;
		this.fileHandling = fileHandling;
		this.config = config;
		this.sftp_connection = sftp_connection;
		this.fileStatus = fileStatus;
		this.filesTwoDates = filesTwoDates;
		this.getSftp_connection = getSftp_connection;
		this.userDetailsService = userDetailsService;
		this.userDetailsServices = userDetailsServices;
		this.h2hServicess = h2hServicess;
		this.day_Wise_File = day_Wise_File;
		this.day_Wise_Upload = day_Wise_Upload;
		this.inwfile = inwfile;
	}

	@Value("${Bank_certi_Path}")
	private String Bank_certi_Path;

	@Value("${Npci_certi_Path}")
	private String Npci_certi_Path;

	@Value("${SSHPRKEYFILE}")
	private String SSHPRKEYFILE;
	@Value("${PASSPHRASE}")
	private String PASSPHRASE;

	@Value("${Npci_User_name}")
	private String Npci_User_name;

	@Value("${Npci_User_IP}")
	private String Npci_User_IP;

	@Value("${Npci_User_Port}")
	private String Npci_User_Port;

	@Value("${Npci_User_Password}")
	private String Npci_User_Password;

	@Value("${Bank_PFX_File}")
	private String Bank_PFX_File;

	@Value("${Bank_PFX_Password}")
	private String Bank_PFX_Password;
	@Value("${upload_source_path}")
	private String upload_source_path;

	@Value("${ApplicationName}")
	private String ApplicationName;
	@Value("${upload_source_Pending}")
	private String upload_source_Pending;

	@Value("${upload_source_Signed}")
	private String upload_source_Signed;

	@Value("${upload_source_Archive}")
	private String upload_source_Archive;

	@Value("${upload_source_Error}")
	private String upload_source_Error;

	@Value("${upload_source_Duplicate}")
	private String upload_source_Duplicate;
	@Value("${upload_source_Original}")
	private String upload_source_Original;
	// Inbox Folder Configurations
	@Value("${Inbox_source_Actual}")
	private String Inbox_source_Actual;

	@Value("${Inbox_source_path}")
	private String Inbox_source_path;

	@Value("${Inbox_source_Archive}")
	private String Inbox_source_Archive;

	@Value("${Inbox_source_Original}")
	private String Inbox_source_Original;

	@Value("${Inbox_source_Errors}")
	private String Inbox_source_Errors;

	@Value("${Inbox_source_Verified}")
	private String Inbox_source_Verified;

	@Value("${Bankcode_source_path}")
	private String Bankcode_source_path;

	@Value("${Bankcode_source_Duplicate}")
	private String Bankcode_source_Duplicate;

	@Value("${Bankcode_source_Errors}")
	private String Bankcode_source_Errors;

	@Value("${Bankcode_source_Verified}")
	private String Bankcode_source_Verified;

	@Value("${BankCode_source_Original}")
	private String BankCode_source_Original;

	@Value("${BANK_SHORT_CODE}")
	private String BANK_SHORT_CODE;

	@Value("${InboxFLD}")
	private String InboxFLD;

	@Value("${BankCodeID}")
	private String BankCodeID;
	// Table Related Information
//	@Value("${ACKFILES}")
//	private  String ACKFILES;

	@Value("${TBL_INWFILE}")
	private String TBL_INWFILE;

	@GetMapping("/login_from_nach")
	public void loginWithEncryptedCredentials(@RequestParam String encUser, @RequestParam String encPass,
			HttpServletResponse response, HttpSession session) throws IOException {
		try {
			String username = encUser;
			String password = encPass;

			System.out.println("username " + username);
			System.out.println("password " + password);

			UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(username,
					password);
			Authentication authentication = authenticationManager.authenticate(authRequest);

			SecurityContextHolder.getContext().setAuthentication(authentication);

			// Save session info
			session.setAttribute("username", username);

			// Update login status in DB
			userDetailsServices.updateUserLoginStatus(username, true);

			// Redirect to dashboard
			response.sendRedirect("/ReadCertificate");

		} catch (Exception e) {
			response.sendRedirect("/login?error=true");
		}
	}

	@RequestMapping(value = "DeleteServerLog", method = { RequestMethod.GET, RequestMethod.POST })
	public String DeleteServerLog(@RequestParam("fileName") String fileName) {
		try {
			Path basePath = Paths.get(System.getProperty("user.dir"));
			Path applogs = Paths.get(basePath.toString(), "AppLogs");
			Path fileToDelete = applogs.resolve(fileName); // Build path to specific file

			File file = fileToDelete.toFile();
			if (file.exists()) {
				if (file.delete()) {
					System.out.println("Deleted file: " + fileName);
				} else {
					System.out.println("Failed to delete file: " + fileName);
				}
			} else {
				System.out.println("File not found: " + fileName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "redirect:/serverlogs"; // Use redirect to refresh page
	}

	@GetMapping("/hello")
	public ResponseEntity<String> sayHello() throws Exception {

//		System.out.println("...........Upload Process Start..........");
//		LogAppend.logError("...........Upload Process Start..........");
		// upload--------------------
//		fileHandling.UploadToNpci(upload_source_path, upload_source_Duplicate, upload_source_Signed,
//				upload_source_Error, upload_source_Original, SSHPRKEYFILE, PASSPHRASE, Npci_User_name, Npci_User_IP,
//				Npci_User_Port, Npci_User_Password, Bank_PFX_File, Npci_certi_Path, Bank_PFX_Password,
//				upload_source_Archive);
//		System.out.println("...........Upload Process End..........");
//		LogAppend.logError("...........Upload Process End..........");
//		System.out.println("...........Inward Process Start..........");
//		LogAppend.logError("...........Inward Process Start..........");
//		fileHandling.SFTPInwardsDownload(Bank_PFX_File, Npci_certi_Path, Bank_PFX_Password, Bankcode_source_Errors,
//				Bankcode_source_path, BankCode_source_Original, Bankcode_source_Verified, SSHPRKEYFILE, PASSPHRASE,
//				Npci_User_name, Npci_User_IP, Npci_User_Port, Npci_User_Password, BankCodeID);
//		System.out.println("...........Inward Process End..........");
//		LogAppend.logError("...........Inward Process End..........");
		System.out.println("...........ACK Process Start..........");

		LogAppend.logError("...........ACK Process Start..........");
		fileHandling.SFTPACKDownload(Bank_PFX_File, Npci_certi_Path, Bank_PFX_Password, Inbox_source_Errors,
				Inbox_source_path, Inbox_source_Original, Inbox_source_Verified, SSHPRKEYFILE, PASSPHRASE,
				Npci_User_name, Npci_User_IP, Npci_User_Port, Npci_User_Password, InboxFLD);
		System.out.println("...........ACK Process End..........");
		LogAppend.logError("...........ACK Process End..........");
		
		return new ResponseEntity<>("its not be Okey", HttpStatus.OK);
	}

	@RequestMapping(value = "/serverlogs", method = {RequestMethod.GET, RequestMethod.POST})
	public String serverlogs(
	        @RequestParam(defaultValue = "1") int page,
	        Model model) {

	    int pageSize = 10;

	    Path basePath = Paths.get(System.getProperty("user.dir"));
	    Path applogs = Paths.get(basePath.toString(), "AppLogs");
	    File textfile = new File(applogs.toString());
	    File[] files = textfile.listFiles();

	    if (files == null || files.length == 0) {
	        model.addAttribute("files_info", Collections.emptyList());
	        model.addAttribute("currentPage", 1);
	        model.addAttribute("totalPages", 1);
	        return "serverlogs";
	    }

	    Arrays.sort(files, Comparator.comparingLong(File::lastModified).reversed());

	    List<HashMap<String, Object>> fileList = new ArrayList<>();
	    for (File file : files) {
	        HashMap<String, Object> fileDetails = new HashMap<>();
	        fileDetails.put("fileName", file.getName());
	        fileDetails.put("fileSize", file.length() / 1000 + " kb");
	        fileDetails.put("modificationDate", file.lastModified());
	        fileList.add(fileDetails);
	    }

	    int totalFiles = fileList.size();
	    int totalPages = (int) Math.ceil((double) totalFiles / pageSize);

	    int fromIndex = (page - 1) * pageSize;
	    int toIndex = Math.min(fromIndex + pageSize, totalFiles);

	    List<HashMap<String, Object>> pageList = fileList.subList(fromIndex, toIndex);

	    model.addAttribute("files_info", pageList);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", totalPages);

	    return "serverlogs";
	}


	@RequestMapping(value = "/fileprocess", method = { RequestMethod.GET, RequestMethod.POST })
	public String FileProcess() {
		return "FileProcess";
	}

	@RequestMapping(value = "/manageTable", method = { RequestMethod.GET, RequestMethod.POST })
	public String manageTable() {
		return "manageTable";
	}

	/*
	 * @GetMapping("/login") public String login(Model mode ) {
	 * //mode.addAttribute("error", "Invalid username or password"); return "login";
	 * 
	 * }
	 */

	@GetMapping("/login")
	public String login(@RequestParam(value = "error", required = false) String errorParam, Model model) {
		if (errorParam != null) {
			// This error message will be parsed in JS
			model.addAttribute("error", "Invalid username or password");
		}
		return "login";
	}

	/*
	 * @GetMapping("/login_failure") public String login_failure(Model mode ) {
	 * System.out.println("Invalid username or password");
	 * 
	 * mode.addAttribute("error", "Invalid username or password"); return "login";
	 * 
	 * }
	 */
	@GetMapping("/login_failure")
	public String login_failure(Model model) {
		System.out.println("user name password expired");
		model.addAttribute("error", "user name password expired ");
		return "redirect:/login";
	}

	@GetMapping("/logout")
	public String logout(HttpServletRequest request, HttpServletResponse response) {
		// Invalidate the session
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.invalidate();
		}
		System.out.println("================ Logout ================");

		// Redirect to the login page
		return "redirect:/login";
	}

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;

	@RequestMapping(value = "/manualsign", method = { RequestMethod.GET, RequestMethod.POST })
	public String Manual_sign(HttpServletRequest request) {
		if (request.getMethod().equals("POST")) {
			return "Manual_Sign_Encryption";
		}

		return "Manual_Sign_Encryption";
	}

	@RequestMapping(value = "/adminpage", method = { RequestMethod.GET, RequestMethod.POST })
	public String AdminDashboard(@ModelAttribute User userdata, HttpServletRequest request)

	{

		if (request.getMethod().equals("POST")) {

			System.out.print("Creating New User....");

			h2hServices.saveUser(userdata);
			System.out.print("Creating New User....  Done");

			return "AdminDashboard";
		}

		return "AdminDashboard";
	}

	@PostMapping("/update")
	public String update(@ModelAttribute CONFIGVALUE cfg) {
		String Host = cfg.getSS_HOST_ADDR();
		this.h2hServices.updateService(cfg);
		return "redirect:/Configurations";
	}

	@GetMapping("/welcome")
	public String welcome() {

		return "welcome";
	}

	@GetMapping("/dashboard")
	public String dashboardss(Model model) {
		// upload
		File count1 = new File(upload_source_path);
		if (count1.exists() && count1.isDirectory()) {
			String[] files1 = count1.list();
			model.addAttribute("upload_source_path", files1);
			model.addAttribute("upload_source_path1", files1.length);
		}
		File count3 = new File(upload_source_Signed);
		if (count3.exists() && count3.isDirectory()) {
			String[] files3 = count3.list();
			model.addAttribute("upload_source_Signed", files3.length);
			model.addAttribute("upload_source_Signed1", files3);
		}
		File count4 = new File(upload_source_Archive);
		if (count4.exists() && count4.isDirectory()) {
			String[] files4 = count4.list();
			model.addAttribute("upload_source_Archive", files4.length);
			model.addAttribute("upload_source_Archive1", files4);
		}
		File count5 = new File(upload_source_Error);
		if (count5.exists() && count5.isDirectory()) {
			String[] files5 = count5.list();
			model.addAttribute("upload_source_Error", files5.length);
			model.addAttribute("upload_source_Error1", files5);
		}
		File count6 = new File(Inbox_source_path);
		if (count6.exists() && count6.isDirectory()) {
			String[] files6 = count6.list();
			model.addAttribute("Inbox_source_path", files6.length);
			model.addAttribute("Inbox_source_path1", files6);
		}

		File count7 = new File(Inbox_source_Archive);
		if (count7.exists() && count7.isDirectory()) {
			String[] files7 = count7.list();
			model.addAttribute("Inbox_source_Archive", files7.length);
			model.addAttribute("Inbox_source_Archive1", files7);
		}

		File count8 = new File(Inbox_source_Errors);
		if (count8.exists() && count8.isDirectory()) {
			String[] files8 = count8.list();
			model.addAttribute("Inbox_source_Errors", files8.length);
			model.addAttribute("Inbox_source_Errors1", files8);
		}

		File count9 = new File(Inbox_source_Verified);
		if (count9.exists() && count9.isDirectory()) {
			String[] files9 = count8.list();
			model.addAttribute("Inbox_source_Verified", files9.length);
			model.addAttribute("Inbox_source_Verified1", files9);
		}

		File count10 = new File(Bankcode_source_path);

		if (count10.exists() && count10.isDirectory()) {
			String[] files10 = count10.list();
			model.addAttribute("Bankcode_source_path", files10.length);
			model.addAttribute("Bankcode_source_path1", files10);
		}

		File count12 = new File(Bankcode_source_Errors);
		if (count12.exists() && count12.isDirectory()) {
			String[] files12 = count12.list();
			model.addAttribute("Bankcode_source_Errors", files12.length);
			model.addAttribute("Bankcode_source_Errors1", files12);
		}

		File count13 = new File(Bankcode_source_Verified);
		if (count13.exists() && count13.isDirectory()) {
			String[] files13 = count13.list();
			model.addAttribute("Bankcode_source_Verified", files13.length);
			model.addAttribute("Bankcode_source_Verified1", files13);
		}
		LocalDate currentDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
		String formattedDate = currentDate.format(formatter);

		File folder1 = new File(Inbox_source_Verified);
		if (folder1.exists() && folder1.isDirectory()) {
			String[] files = folder1.list();
			model.addAttribute("Inbox_source_Verified_current_date", files.length);
			model.addAttribute("Inbox_source_Verified_current_date1", files);
		}

		// Bankcode current count
		File folder2 = new File(Bankcode_source_Verified);
		if (folder2.exists() && folder2.isDirectory()) {
			String[] files = folder2.list();
			model.addAttribute("Bank_source_Verified_current_date", files.length);
			model.addAttribute("Bank_source_Verified_current_date1", files);
		}

		int c[] = sftp_connection.Sftp_connection();
		model.addAttribute("Root_count", c[0]);
		model.addAttribute("Inbox_count", c[1]);
		model.addAttribute("Bank_count", c[2]);

		return "DashBoardJSP";
	}

	@GetMapping("/dashboard1")
	public String dashboard() {
		// List<String> listfile = h2hServices.getAllFilenames();
//        List<String> listfileupload = h2hServices.getAllFilenamesupload();
//        
//        
//        
//        listfileupload.forEach(file -> System.out.println("ename: " + file));
//
//        Map<String, List<String>> groupedFiles = listfile.stream()
//            .filter(filename ->
//                (filename.startsWith("ACH-CR") && filename.contains("INW")) ||
//                (filename.startsWith("ACH-DR") && filename.contains("INW")) ||
//                (filename.startsWith("ACH-DR") && filename.contains("RES.txt")) ||
//                (filename.startsWith("MMS-CREATE") && filename.contains("INW")) ||
//                (filename.startsWith("MMS-CREATE") && filename.contains("INP-ACK"))
//            )
//            .collect(Collectors.groupingBy(filename -> {
//                if (filename.startsWith("ACH-CR") && filename.contains("INW")) {
//                    return "ACH-CR";
//                } else if (filename.startsWith("ACH-DR") && filename.contains("INW")) {
//                    return "ACH-DR";
//                } else if (filename.startsWith("ACH-DR") && filename.contains("RES")) {
//                    return "ACH-DR-RES";
//                } else if (filename.startsWith("MMS-CREATE") && filename.contains("INP-ACK")) {
//                    return "MMS-CREATE-ACK";
//                } else if (filename.startsWith("MMS-CREATE") && filename.contains("INW")) {
//                    return "MMS-CREATE";
//                } else {
//                    return "OTHER"; // Optional fallback
//                }
//            }));
//
//        // Print for debugging
//        groupedFiles.forEach((category, files) -> {
//            System.out.println("Category: " + category);
//            files.forEach(file -> System.out.println("  Filename: " + file));
//        });

		return "nwDasboard"; // Thymeleaf view name
	}

	@GetMapping("/fetchdashboarddatainward") // file download
	public ResponseEntity<Map<String, Map<String, List<String>>>> fetchDashboardDataInward(
			@RequestParam String startDate, @RequestParam String endDate) {
		List<String> listfile = h2hServices.getAllFilenames(startDate, endDate);
		System.out.println("********************************************");
//		logger.info(listfile.toString());
		// Group files by main category and then by sub-type (INW, ACK, RES)
		Map<String, Map<String, List<String>>> groupedFiles = listfile.stream()
				.filter(filename -> (filename.startsWith("ACH-CR") && filename.contains("INW"))
						|| (filename.startsWith("ACH-DR") && filename.contains("INW"))
						|| (filename.startsWith("APB-CR") && filename.contains("INW"))
						|| (filename.startsWith("ACH-DR") && filename.contains("RES.txt"))
						|| (filename.startsWith("MMS") && filename.contains("INW"))
						|| (filename.startsWith("MMS") && filename.contains("INP-ACK")))
				.collect(Collectors.groupingBy(filename -> {
					if (filename.startsWith("ACH-CR"))
						return "ACH-CR";
					else if (filename.startsWith("ACH-DR"))
						return "ACH-DR";
					else if (filename.startsWith("APB-CR"))
						return "APB-CR";
					else if (filename.startsWith("MMS"))
						return "MMS";
					else
						return "OTHER";
				}, Collectors.groupingBy(filename -> {
					if (filename.contains("INW"))
						return "INW";
					else if (filename.contains("RES"))
						return "RES";
					else if (filename.contains("INP-ACK"))
						return "ACK";
					else
						return "OTHER";
				})));

		// Debug print
//		groupedFiles.forEach((category, subMap) -> {
//			System.out.println("Category: " + category);
//			subMap.forEach((subType, files) -> {
//				System.out.println("  SubType: " + subType);
//				files.forEach(file -> System.out.println("    Filename: " + file));
//			});
//		});

		return ResponseEntity.ok(groupedFiles);
	}

	@GetMapping("/fetchdashboarddataupload") // File upload
	public ResponseEntity<Map<String, Map<String, List<String>>>> postDashboardDataupload(
			@RequestParam String startDate, @RequestParam String endDate) {
		List<String> listfileupload = h2hServices.getAllFilenamesupload(startDate, endDate);		
		// Group files by main category and then by sub-type (INW, ACK, RES)
		Map<String, Map<String, List<String>>> groupedFiles = listfileupload.stream()
				.filter(filename -> (filename.startsWith("ACH-DR") && filename.contains("INP.txt"))
						|| (filename.startsWith("APB-CR") && filename.contains("RTN.txt")) ||
						/* (filename.startsWith("ACH-DR") && filename.contains("RES")) || */
						(filename.startsWith("ACH-DR") && filename.contains("RTN.txt"))
						|| (filename.startsWith("MMS") && filename.contains("ACCEPT.txt")))
				.collect(Collectors.groupingBy(filename -> {
					if (filename.startsWith("ACH-CR"))
						return "ACH-CR";
					else if (filename.startsWith("ACH-DR"))
						return "ACH-DR";
					else if (filename.startsWith("APB-CR"))
						return "APB-CR";
					else if (filename.startsWith("MMS"))
						return "MMS";
					else
						return "OTHER";
				}, Collectors.groupingBy(filename -> {
					if (filename.contains("INP"))
						return "INP";
					/* else if (filename.contains("RES")) return "RES"; */
					else if (filename.contains("RTN"))
						return "RTN";
					else if (filename.contains("ACCEPT"))
						return "ACCEPT";
					else
						return "OTHER";
				})));

		// Debug print
//		groupedFiles.forEach((category, subMap) -> {
//			System.out.println("Category: " + category);
//			subMap.forEach((subType, files) -> {
//				System.out.println("  SubType: " + subType);
//				files.forEach(file -> System.out.println("    Filename: " + file));
//			});
//		});

		return ResponseEntity.ok(groupedFiles);
	}

	@RequestMapping(value = "/FileStatus", method = { RequestMethod.GET, RequestMethod.POST })
	public String FileStatus(@ModelAttribute FileStatus status, HttpServletRequest request, Model model)

	{

		if (request.getMethod().equals("POST")) {
			String fromdate = status.getFromdate();
			String Todate = status.getTodate();
			if (status.getFolder().equals("Upload") && status.getFilestatus().equals("Error")) {
				SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

				try {
					Date startDate = dateFormat.parse(fromdate);
					Date endDate = dateFormat.parse(Todate);

					List f = this.filesTwoDates.listFilesBetweenDates(upload_source_Error, startDate, endDate);
					model.addAttribute("files", f);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (status.getFolder().equals("Upload") && status.getFilestatus().equals("PendingACK")) {

			}

			else if (status.getFolder().equals("Upload") && status.getFilestatus().equals("Archived")) {

			}
		}
		return "FileStatus";
	}

	@GetMapping("/Configurations")
	public String Configurations(Model model) {
		Iterable<TBL_CONFIG> data = this.h2hServices.dashboard_service();
		HashMap<String, String> map = new HashMap<String, String>();
		data.forEach(e -> {

			map.put(e.getConfigKey(), e.getConfigValue());
		});
		model.addAttribute("map", map);
		return "configjsp";
	}

	@GetMapping("/SearchDelete")
	public String showSearchDeletePage() {
		return "SearchDelete";
	}

	@PostMapping("/SearchDelete")
	// @ResponseBody
	public String searchDelete(@RequestParam("Filename") String filename, @RequestParam("Table") String Table,
			Model model) {
		try {
			String t = this.h2hServices.serachFiles(filename, Table);
			if (t != null) {
				model.addAttribute("File", t);
			}

		} catch (Exception e) {
			return "An error occurred: " + e.getMessage();
		}
		return "SearchDelete";
	}

	@RequestMapping(value = "/NpciRootData", method = { RequestMethod.GET, RequestMethod.POST })
	public String NpciRootData(@ModelAttribute FileStatus status, Model model, HttpServletRequest request)
			throws SftpException {
		List<String> list1 = new ArrayList<>();
		String ports = null;
		String host = null;
		int port = 0;
		String username = null;
		String password = null;
		String SSHPRKEYFILE = null;
		String PASSPHRASE = null;

		ChannelSftp channelSftp = null;
		String filename = null;
		String BS_SHORTCODE = null;
		HashMap map = getSftp_connection.getDataFromDatabase();
		BS_SHORTCODE = (String) map.get("BS_SHORTCODE");
		model.addAttribute("shortname", BS_SHORTCODE);
		try {

			if (request.getMethod().equals("POST")) {

				filename = status.getFilename();
				host = (String) map.get("SS_HOST_ADDR");
				ports = (String) map.get("SS_PORTNO");
				port = Integer.parseInt(ports);
				username = (String) map.get("SS_USERNAME");
				password = (String) map.get("SS_PASS");
				SSHPRKEYFILE = (String) map.get("SS_SSHPRKEYFILE");
				PASSPHRASE = (String) map.get("SS_PASSPHRASE");
				try {
					channelSftp = getSftp_connection.get_SftpConnection(SSHPRKEYFILE, PASSPHRASE, username, host, port,
							password);
				} catch (Exception es) {
					System.out.println("SFTP not connected  ..." + es);
				}
				if (status.getFolder().equals(BANK_SHORT_CODE)) {

					channelSftp.cd("./" + BS_SHORTCODE);
					Vector<LsEntry> files = channelSftp.ls("./");
					for (LsEntry entry : files) {

						list1.add(entry.getFilename());
						if (entry.getFilename().equals(filename)) {
							String remoteFile = entry.getFilename();
							String localFile = Bankcode_source_path + "//" + entry.getFilename();

							System.out.println("file name --- " + filename);

						}
					}

					model.addAttribute("listdata", list1);

				} else if (status.getFolder().equals("Inbox")) {
					channelSftp.cd("./" + "Inbox");
					Vector<LsEntry> files = channelSftp.ls("./");
					for (LsEntry entry : files) {
						list1.add(entry.getFilename());
						if (entry.getFilename().equals(filename)) {
							// System.out.println("<<<<<<<<<<-----Inside the inbox
							// condtion--->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
							String remoteFile = entry.getFilename();
							String localFile = Inbox_source_Actual + "//" + entry.getFilename();
							// downloadFile(channelSftp, remoteFile, localFile);
							// model.addAttribute("filename", filename);
						}
					}
					model.addAttribute("listdata", list1);
				}

			}
		} catch (Exception es) {
			System.out.println(es);
		}

		if (channelSftp != null) {
			channelSftp.disconnect();
		}
		return "EXPORTNPCIFILELIST";
	}

	@RequestMapping(value = "/NpciRootData1", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<String> npciRootData1(@RequestBody JsonRequest jsonRequest) {
		String filename = null;
		filename = jsonRequest.getFilename();

		System.out.println("filename ---" + filename);
		List<String> list1 = new ArrayList<>();
		String ports = null;
		String host = null;
		int port = 0;
		String username = null;
		String password = null;
		String BS_SHORTCODE = null;
		String SSHPRKEYFILE = null;
		String PASSPHRASE = null;
		// String filename=null;
		ChannelSftp channelSftp = null;
		HashMap map = getSftp_connection.getDataFromDatabase();
		System.out.println("Complete Required Data :" + map);
		host = (String) map.get("SS_HOST_ADDR");
		ports = (String) map.get("SS_PORTNO");
		port = Integer.parseInt(ports);
		username = (String) map.get("SS_USERNAME");
		password = (String) map.get("SS_PASS");
		SSHPRKEYFILE = (String) map.get("SS_SSHPRKEYFILE");
		BS_SHORTCODE = (String) map.get("BS_SHORTCODE");
		PASSPHRASE = (String) map.get("SS_PASSPHRASE");

		System.out.println("Host :" + host + "||" + "port :" + port + "||" + "username :" + username + " || "
				+ "password :" + password + " || " + "SSHPRKEYFILE :" + SSHPRKEYFILE + " BS_SHORTCODE :" + BS_SHORTCODE
				+ " || " + "PASSPHRASE :" + PASSPHRASE);

		try {
			channelSftp = getSftp_connection.get_SftpConnection(SSHPRKEYFILE, PASSPHRASE, username, host, port,
					password);
		} catch (Exception es) {
			System.out.println("SFTP not connected  ..." + es); // customLogger.logError("An error occurred: ", es);
		}
		System.out.println("channelSftp--" + channelSftp);
		try {
			// if ("ACK".equalsIgnoreCase(filename)) {
			if (filename.contains("ACK")) {
				String file_ = day_Wise_File.findFilenameByFilename(filename);
				if (file_ == null) {
					channelSftp.cd("./" + "Inbox");
					String localFile = Inbox_source_Actual + "/" + filename;
					downloadFile(channelSftp, filename, localFile);
					if (channelSftp != null) {
						channelSftp.disconnect();
					}
					return ResponseEntity.ok("{ \"message\": \"File downloaded successfully\"}");
				} else {
					return ResponseEntity.ok("{ \"message\": \"File Alredy downloaded \"}");
				}
			} else {
				channelSftp.cd("./" + BANK_SHORT_CODE);
				String localFile = Bankcode_source_path + "/" + filename;
				String inw = inwfile.findFilenameByFilename(filename);
				System.out.println("return data is:" + inw);
				if (inw == null) {
					downloadFile(channelSftp, filename, localFile);
					if (channelSftp != null) {
						channelSftp.disconnect();
						System.out.println(
								"-------------- NPCI Connectin Closed For NpciRootData1 ---------------------");
					}
					return ResponseEntity.ok("{ \"message\": \"File downloaded successfully\"}");
				}

				else {
					return ResponseEntity.ok("{ \"message\": \"File Alredy downloaded\"}");
				}
			}
		} catch (Exception e) {
			System.out.println("Exception is:--" + e.getMessage());

			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.body("{ \"message\": \"Failed to download file\"}");
		}
	}

	private static void downloadFile(ChannelSftp channelSftp, String remoteFile, String localFile)
			throws SftpException {
		try {
			channelSftp.get(remoteFile, localFile);
		} catch (Exception e) {

			e.printStackTrace();

			System.out.println("File  not downloaded is the : " + localFile);

		}

	}

	// ShowPDF File
	@GetMapping("/ShowPdf")
	public ResponseEntity<byte[]> downloadPdf() throws IOException {
		Resource resource = new ClassPathResource("Workshop on H2H Solution V 1-4.pdf");

		try (InputStream inputStream = resource.getInputStream();
				ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

			byte[] buffer = new byte[1024];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}

			byte[] pdfBytes = outputStream.toByteArray();

			return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(pdfBytes);
		}
	}

	private Process batProcess;

	@RequestMapping(value = "/SingEnc", method = { RequestMethod.GET, RequestMethod.POST })
	public String SingEnc(HttpServletRequest request) throws IOException {
		if (request.getMethod().equals("POST")) {

		}
		return "Sign_Verifcation";
	}

	// Help Desk
	@GetMapping("/HelpDesk")
	public String HelpDesk() {
		return "HelpDesk";
	}

	@RequestMapping(value = "/show_files", method = { RequestMethod.GET, RequestMethod.POST })
	public String showUploadPage(Model model, HttpServletRequest request) {
		List<String> lst_Ack = new ArrayList<String>();
		List<String> lst_Inw = new ArrayList<String>();
		String listfiles_Ack[] = null;

		File file_Ack = new File(Inbox_source_Verified);
		try {
			listfiles_Ack = file_Ack.list();

			for (String list_ : listfiles_Ack) {
				lst_Ack.add(list_);
			}

		} catch (Exception e) {
			System.out.println("Exception is :" + e);
		}

		File file_Inward = new File(Bankcode_source_Verified);
		try {
			String listfiles_Inw[] = file_Inward.list();
			for (String list_ : listfiles_Inw) {
				lst_Inw.add(list_);
			}
		} catch (Exception e) {
			System.out.println("Exception is " + e);
		}

		model.addAttribute("lst_Ack", lst_Ack);
		model.addAttribute("lst_Inw", lst_Inw);

		return "Upload_Download_File"; // Return the name of the HTML file (without extension)
	}

	@GetMapping("/download/{fileName}")
	public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {

		String FILE_DIRECTORY = null;
		if (fileName.contains("ACK")) {
			FILE_DIRECTORY = Inbox_source_Verified;
		} else {
			FILE_DIRECTORY = Bankcode_source_Verified;
		}

		Resource resource = new FileSystemResource(new File(FILE_DIRECTORY + "//" + fileName));

		String contentType;
		try {
			contentType = Files.probeContentType(resource.getFile().toPath());
		} catch (IOException e) {
			contentType = "application/octet-stream";

		}

		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	@GetMapping("/download_log/{fileName}")
	public ResponseEntity<Resource> downloadFile_log(@PathVariable String fileName) {

		Path basePath = Paths.get(System.getProperty("user.dir"));
		Path applogs = Paths.get(basePath.toString(), "AppLogs");
		// String FILE_DIRECTORY = "C://Users//nitinv//Desktop//log";
		Resource resource = new FileSystemResource(new File(applogs.toString() + "//" + fileName));
		String contentType;
		try {
			contentType = Files.probeContentType(resource.getFile().toPath());
		} catch (IOException e) {
			contentType = "application/octet-stream";

		}

		// Fallback to application/octet-stream if unable to determine content type
		if (contentType == null) {
			contentType = "application/octet-stream";
		}

		return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}

	@PostMapping("/upload")
	public ResponseEntity<String> handleFileUpload(@RequestParam("files") MultipartFile[] files) {
//    	System.out.println("Fetch by the upload Ajax....");
		StringBuilder message = new StringBuilder();
		int a = files.length;
		for (MultipartFile file : files) {
			if (file.isEmpty()) {
				message.append("File ").append(file.getOriginalFilename()).append(" is empty. ");
				continue;
			}

			String filePath = upload_source_path + "//" + file.getOriginalFilename();
			ChannelSftp channelSftp = null;
			String ports = null;
			String host = null;
			int port = 0;
			String username = null;
			String password = null;
			String BS_SHORTCODE = null;
			String SSHPRKEYFILE = null;
			String PASSPHRASE = null;
			HashMap map = getSftp_connection.getDataFromDatabase();
			host = (String) map.get("SS_HOST_ADDR");
			ports = (String) map.get("SS_PORTNO");
			port = Integer.parseInt(ports);
			username = (String) map.get("SS_USERNAME");
			password = (String) map.get("SS_PASS");
			SSHPRKEYFILE = (String) map.get("SS_SSHPRKEYFILE");
			BS_SHORTCODE = (String) map.get("BS_SHORTCODE");
			PASSPHRASE = (String) map.get("SS_PASSPHRASE");
			try {
				channelSftp = getSftp_connection.get_SftpConnection(SSHPRKEYFILE, PASSPHRASE, username, host, port,
						password);
				if (channelSftp != null) {
					file.transferTo(new File(filePath));
					System.out.println("..........Sftp Connection successfully........");
				}

				else {
					message.append("SFTP Connection Issue Please Upload After Some Time..");
					System.out.println("........Sftp Connection Issue........");
				}
			}

			catch (Exception es) {
				System.out.println(".......SFTP not connected  ..." + es);
				message.append("Failed to upload ").append(file.getOriginalFilename()).append(": ")
						.append(es.getMessage()).append(". ");
			}
		}
		if (a == 1) {
			message.append("File Uploaded Successfully");
		} else {
			message.append("Files Uploaded Successfully");
		}
		return ResponseEntity.ok().body(message.toString());
	}

	// Read Bank and Npci Certificate
	@GetMapping(value = "/ReadCertificate")
	public String ReadCertificate(@ModelAttribute FileStatus status, HttpServletRequest request, Model model) {
//    	if(request.getMethod().equals("POST"))
//    	{

		try {
			String Bank_certificateFilePath = Bank_certi_Path;
			String Ncpi_certificateFilePath = Npci_certi_Path;
			System.out.println(Bank_certi_Path);
			System.out.println(Npci_certi_Path);
			FileInputStream fis_bank = null;
			FileInputStream fis_Npci = null;
			Certificate certificate_Bank = null;
			Certificate certificate_Npci = null;
			X509Certificate x509Certificate_Bank = null;
			X509Certificate x509Certificate_Npci = null;

			try {
				// Load the Bank certificate
				fis_bank = new FileInputStream(Bank_certificateFilePath);
				CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
				certificate_Bank = certificateFactory.generateCertificate(fis_bank);
				fis_bank.close();
			} catch (Exception e) {
				System.out.println(" Bank_certificateFilePath  path is mismatched.");
			}

			try {
				// Load the Npci certificate
				fis_Npci = new FileInputStream(Ncpi_certificateFilePath);
				CertificateFactory certificateFactory_Npci = CertificateFactory.getInstance("X.509");
				certificate_Npci = certificateFactory_Npci.generateCertificate(fis_Npci);
				fis_Npci.close();

				// Convert to X509Certificate

				x509Certificate_Bank = (X509Certificate) certificate_Bank;
				x509Certificate_Npci = (X509Certificate) certificate_Npci;
			} catch (Exception is) {
				System.out.println("Ncpi_certificateFilePath path is mismatched.");
			}

			try {
				// Print Bank certificate details
				System.out.println("Type_Bank: " + x509Certificate_Bank.getType());
				System.out.println("Issuer_Bank: " + x509Certificate_Bank.getIssuerDN());
				System.out.println("Subject_Bank: " + x509Certificate_Bank.getSubjectDN());
				System.out.println("Valid from_Bank: " + x509Certificate_Bank.getNotBefore());
				System.out.println("Valid until_Bank: " + x509Certificate_Bank.getNotAfter());

				// Print NPCI certificate details
				System.out.println("Type_NPCI: " + x509Certificate_Npci.getType());
				System.out.println("Issuer_NPCI: " + x509Certificate_Npci.getIssuerDN());
				System.out.println("Subject_NPCI: " + x509Certificate_Npci.getSubjectDN());
				System.out.println("Valid from_NPCI: " + x509Certificate_Npci.getNotBefore());
				System.out.println("Valid until_NPCI: " + x509Certificate_Npci.getNotAfter());

				// Returning Bank Certificate
				model.addAttribute("Type_Bank", x509Certificate_Bank.getType());
				model.addAttribute("Issuer_Bank", x509Certificate_Bank.getIssuerDN());
				model.addAttribute("Valid_from_Bank", x509Certificate_Bank.getNotBefore());
				model.addAttribute("Valid_until_Bank", x509Certificate_Bank.getNotAfter());

				// Returning Npci Certificate
				model.addAttribute("Type_Npci", x509Certificate_Npci.getType());
				model.addAttribute("Issuer_Npci", x509Certificate_Npci.getIssuerDN());
				model.addAttribute("Valid_from_Npci", x509Certificate_Npci.getNotBefore());
				model.addAttribute("Valid_until_Npci", x509Certificate_Npci.getNotAfter());

			} catch (Exception notis) {
				System.out.println("X509Certificate && x509Certificate_Npci is null ");
			}

		} catch (Exception e) {
			// customLogger.logError("An error occurred: ", e);
			e.printStackTrace();
		}

//    	}
		return "Certificate";
	}

	@GetMapping("/checkCertificateExpiry")
	@ResponseBody
	public String checkCertificateExpiry() {
		String certificatePath = Bank_certi_Path; // Path to your certificate file
		System.out.println(Bank_certi_Path);
		try {
			InputStream inStream = new FileInputStream(new File(certificatePath));
			CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
			X509Certificate cert = (X509Certificate) certFactory.generateCertificate(inStream);
			inStream.close();

			LocalDate expiryDate = cert.getNotAfter().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
			LocalDate currentDate = LocalDate.now();

			long daysUntilExpiry = ChronoUnit.DAYS.between(currentDate, expiryDate);

			if (daysUntilExpiry <= 0) {
				return "Certificate has expired";
			} else if (daysUntilExpiry <= 15) {
				return "Certificate is about to expire within " + daysUntilExpiry + " days";
			} else {
				return "";
			}

		} catch (Exception e) {
			e.printStackTrace();

			return "Error occurred while checking certificate expiry";
		}
	}

	// Day Wise File Status
	@RequestMapping(value = "/Day_Wise_File", method = { RequestMethod.GET, RequestMethod.POST })
	public String Day_Wise_File(Model model) throws ParserConfigurationException, SAXException, IOException {

		HashMap<String, String> filestatus = new HashMap<String, String>();
		List<String> upload_file = new ArrayList<String>();
		List<String> file_Ack = day_Wise_File.findFilenameByCurrentDate(); // for ACK file
		List<String> file_upload = day_Wise_Upload.findFilenameByCurrentDate(); // for Upload file

		// For Ack file count and the status
		for (String file_ : file_Ack) {
			File f_ACK = ResourceUtils.getFile(Inbox_source_Verified + "//" + file_);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document document = builder.parse(f_ACK);
			NodeList nodeList = document.getElementsByTagName("GrpSts");
			if (nodeList.getLength() > 0) {
				Node node = nodeList.item(0);
				String grpSts = node.getTextContent();

				filestatus.put(file_, grpSts);
			}
		}
		for (String file_ : file_upload) {

			upload_file.add(file_);
		}
		model.addAttribute("FileStatus", filestatus);
		model.addAttribute("upload_file", upload_file);

		return "Day_Wise_File";
	}

}
