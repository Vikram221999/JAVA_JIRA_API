package com.example.demo.controller;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

@RestController
@RequestMapping( "/jira" )
@CrossOrigin("*")
public class Jira_Testing {
	
	@GetMapping("/getall")
	public String getissue(){

		int maxResults = 1000;
		int startAt = 0;
		List<JsonNode> allIssues = new ArrayList<>();

		try {
			while (true) {
//				HttpResponse<JsonNode> response = Unirest.get("http://172.16.1.86:8082/rest/api/2/search")
//						.basicAuth("admin1", "123456")
//						.header("Accept", "application/json")
//						.queryString("jql", "project = MT")
				HttpResponse<JsonNode> response = Unirest.get("https://qim-dev.atlassian.net/rest/api/3/search")
								.basicAuth("vikram221999@gmail.com", "ATATT3xFfGF0J6q5-nYkfKbfxX0YVzpnVhhZccqAd7264owAGtdwV8nKAMQOeWfbWDcHW3mQxGHK7a5_A9CThW5JVW0M4-iLzMmZMVoXjdxhHbPsunG7s5s7RGuv0S7PoKwWwGBZ-TlPryh07vHN4uzf7ptW3ML0jgDUXRsq8UvjafLJSHQ906Q=77A7A214")
						.header("Accept", "application/json")
						.queryString("jql", "issuetype in (standardIssueTypes(), \"[System] Problem\")")
						.queryString("startAt", startAt)
						.queryString("maxResults", maxResults)
						.asJson();

				JsonNode responseBody = response.getBody();
				Object demo =response.getBody().getObject().get("issues");
				List<Object> listOfIsuue = new ArrayList<>();
				listOfIsuue.add(demo);
	
				System.out.println(demo.toString());
	
				
				//System.out.println(response.getBody());
				allIssues.add(responseBody);

				int total = responseBody.getObject().getInt("total");
				startAt += maxResults;

				if (startAt >= total) {
					// All data has been retrieved
					break;
				}
			}

			// Process or return allIssues as needed
			for (JsonNode issue : allIssues) {
//				System.out.println(issue);
			}
		} catch (UnirestException e) {
			throw new RuntimeException(e);
		}


		return "Sucess";
	}
	
	@GetMapping(value = "test")
	public String  saveAudit(){
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
	
	
	@GetMapping(value = "/search")
	public String  apiTest1( ){
		String jsonString="";
	 	ObjectMapper objectMapper = new ObjectMapper();
    	try {
			HttpResponse<JsonNode> response = Unirest.get("https://qim-dev.atlassian.net/rest/api/3/search")
					  .basicAuth("vikram221999@gmail.com", "ATATT3xFfGF0ptO0M_51PM26UY4MrnODCI8aSZuhMuR1VF3t7dtRekq5PdR56hIDT5JcJ17y6LzU84MesfABijAUxX2ZHEcAS4uP-D8QpZBkVfld42i21kS8-cHqAT7xdI2HZ_kY_FM1w27PslR80AXd-5FHsa-HmCGuV1Fq3SE_CWUzBP4OJ-c=B91C6C82"
					  		)
					  .header("Accept", "application/json")
					  .asJson();
			try {
				 JSONObject jsonObject = response.getBody().getObject();
		            // Convert the JSONObject to a com.fasterxml.jackson.databind.JsonNode
		            com.fasterxml.jackson.databind.JsonNode jacksonJsonNode = objectMapper.readTree(jsonObject.toString());
		            // Serialize the Jackson JsonNode to a JSON string	
		             jsonString = objectMapper.writeValueAsString(jacksonJsonNode);
				System.out.println(jsonString);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}
		} catch (UnirestException e) {
			e.printStackTrace();
		}
    	return jsonString;
	}
	
	
	

	@GetMapping(value = "search2")
	public String  apiTest2( ){
		String jsonString="";
	 	ObjectMapper objectMapper = new ObjectMapper();
    	try {
			HttpResponse<JsonNode> response = Unirest.get("https://qim-dev.atlassian.net/rest/api/3/search")
					  .basicAuth("vikram221999@gmail.com", "Vino@143"
					  		+ "")
					  .header("Accept", "application/json")
					  .asJson();
			System.err.println(response.getBody());
			try {
				 JSONObject jsonObject = response.getBody().getObject();
		            // Convert the JSONObject to a com.fasterxml.jackson.databind.JsonNode
		            com.fasterxml.jackson.databind.JsonNode jacksonJsonNode = objectMapper.readTree(jsonObject.toString());
		            // Serialize the Jackson JsonNode to a JSON string	
		             jsonString = objectMapper.writeValueAsString(jacksonJsonNode);
				System.out.println(jsonString);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

		} catch (UnirestException e) {
			e.printStackTrace();
		}
    	return jsonString;
	}
}
