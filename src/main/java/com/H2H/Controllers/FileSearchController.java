package com.H2H.Controllers;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Below are the controller for 

@RestController
@RequestMapping("/api/files")
public class FileSearchController {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private static Logger logger = LoggerFactory.getLogger(FileSearchController.class);

	@GetMapping("/search")
	public List<Map<String, Object>> searchFiles(@RequestParam String keyword, @RequestParam String table) {


		String sql = "SELECT fid, finame FROM " + table + " WHERE finame LIKE ?";

		return jdbcTemplate.queryForList(sql, "%" + keyword + "%");
	}

	@DeleteMapping("/delete")
	public ResponseEntity<String> deleteFile(@RequestParam int id, @RequestParam String table) {

		String sql = "DELETE FROM " + table + " WHERE fid = ?";
		int rows = jdbcTemplate.update(sql, id);

		if (rows > 0) {
			return ResponseEntity.ok("Deleted");
		} else {
			return ResponseEntity.status(404).body("Not found");
		}
	}

}
