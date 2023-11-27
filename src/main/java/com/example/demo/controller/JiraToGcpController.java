package com.example.demo.controller;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.service.JiraToGcpService;

@RestController
@RequestMapping("/jira")
@CrossOrigin("*")
public class JiraToGcpController{
	@Autowired
	private JiraToGcpService gcpService;

	@GetMapping("/uploadJsonFileToGcp")
	public String uploadJsonFileToGcp() throws IOException {

		return gcpService.uploadJsonFileToGcp();
	}

	@GetMapping("/uploadCsvFileToGcp")
	public String uploadCsvFileToGcp() throws IOException {

		return gcpService.uploadCsvFileToGcp();
	}

	@GetMapping("/issuesFromJira")
	public String getIssuseFromJira() {

		return gcpService.getIssuseFromJira();
	}

	@GetMapping(value = "test")
	public String saveAudit() {
		System.out.println("////////////WOrking");
		return "Sucess";
	}

	@PutMapping("message")
	public String getMessage(@RequestBody String issueData) {

		System.out.println("////////////WOrking");
		System.out.println(issueData);
		return "Hello Jira";
	}

	@PostMapping("message1")
	public String getMessage1(@RequestBody String issueData) {

		System.out.println(issueData);
		return "Hello Jira";
	}

	

}
