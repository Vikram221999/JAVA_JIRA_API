package com.example.demo.controller;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.JiraConfiguration;
import com.example.demo.service.JiraToGcpService;
import com.example.demo.service.LinkIssue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mashape.unirest.http.exceptions.UnirestException;

@RestController
@RequestMapping("/jira")
@CrossOrigin("*")
public class JiraToGcpController{
	@Autowired
	private JiraToGcpService gcpService;
	
	@Autowired
	private LinkIssue issue;
	
	@Autowired
	private JiraConfiguration configuration ;
	

    @PostMapping("/jira/webhook")
    public String handleWebhook(@RequestHeader("Authorization") String authHeader,@RequestBody String requestBod ) {
    	//,@RequestBody String issueData
        // Process the incoming issue data
       // System.out.println("Received Jira Webhook with Issue Data: " + issueData);
        // Check if the Authorization header contains the correct credentials
    	//System.out.println(payload.toString());
    	System.out.println("ytyhgjihuyftdghvjk");
        if (!isAuthorized(authHeader)) {
            // If not authorized, return a 403 Forbidden response
           return "403 Forbidden";
        }

        // Your logic to handle the GET request goes here
        // For simplicity, this example returns a success message
        return "Webhook GET request handled successfully";
    }

    private boolean isAuthorized(String authHeader) {
        // Extract and decode the credentials from the Authorization header
        // For simplicity, this example assumes the header is in the format "Basic base64(username:password)"
        String credentials = authHeader.substring("Basic ".length());
        String decodedCredentials = new String(java.util.Base64.getDecoder().decode(credentials));

        // Compare the decoded credentials with the expected username and password 
        return decodedCredentials.equals("admin" + ":" + "root");
    }
	@GetMapping("/uploadJsonFileToGcp")
	public String uploadJsonFileToGcp() throws IOException {

		return gcpService.uploadJsonFileToGcp();
	}

	@GetMapping("/uploadCsvFileToGcp")
	public String uploadCsvFileToGcp() throws IOException {

		return gcpService.uploadCsvFileToGcp();
	}
	
	@PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(@RequestBody String payload) throws UnirestException, JsonMappingException, JsonProcessingException {
//		@RequestBody Object payload
//		Object payload = null;
		
		issue.createdIssuse( payload);
		//String result=gcpService.handleWebhook(payload);
		return ResponseEntity.ok("Done");
        
	}
    
    @PostMapping("/createdIssue")
    public String createdIssuseFromJira(@RequestBody Object object){
    	
    	//gcpService.createdIssuseFromJira(object);
		return gcpService.createdIssuseFromJira(object);
    	
    }
    
    
    @PostMapping("/createIssueLink")
    public String createdIssuseFromJira(){
    	
    	
    	//gcpService.createdIssuseFromJira(object);
		return issue.linksIssuse(null, null);
    }
    
    @GetMapping("/issuesFromDirectJira")
	public String getIssuseFromDirectJira() throws IOException, InterruptedException, ExecutionException {

		return configuration.getIssuseFromDirectJira();
//				issue.getIssue();
	}
//    @GetMapping("/issuesFromJira11")
//	public String getIssuseFromJira1() {
//
//		return issue.getIssue();
//	}

	@GetMapping("/issuesFromJira")
	public String getIssuseFromJira() {

		return gcpService.getIssuseFromJira();
	}

	@GetMapping(value = "test")
	public String saveAudit() {
		System.out.println("////////////WOrking");
		return "Sucess";
	}
	@PostMapping(value = "test1")
	public String saveAudit1(@RequestBody Object object) {
		System.out.println(object.toString());
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
