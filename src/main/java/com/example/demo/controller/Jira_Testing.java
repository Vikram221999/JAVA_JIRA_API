package com.example.demo.controller;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
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
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

@RestController
@RequestMapping("/jira")
@CrossOrigin("*")
public class Jira_Testing {

	@GetMapping("/getall")
	public String getissue() throws IOException {

		int maxResults = 1000;
		int startAt = 0;
		List<JsonNode> allIssues = new ArrayList<>();
		// int total=0;

		try {
			while (true) {
//				HttpResponse<JsonNode> response = Unirest.get("http://172.16.1.86:8082/rest/api/2/search")
//						.basicAuth("admin1", "123456")
//						.header("Accept", "application/json")
//						.queryString("jql", "project = MT")
//				HttpResponse<JsonNode> response = Unirest.get("https://qim-dev.atlassian.net/rest/api/3/search")
//						.basicAuth("vikram221999@gmail.com",
//								"ATATT3xFfGF0kwaHKdctaCBQgXIwfQcEE0Ui-D3saW4wsS3C8tZzQCA-ODfKDKiO8MRk6qvXYyqbx40L0pL5B1Rg2NDOCUYcvlRFFFrYwmJHHco1625cniIyk5F0YCHeDgZUkL4N3AD4BmN_Oj_h6weRusFNOc4GGwbIcN7s3q-7kov_xYxE1ls=58B29C10")
//					
				HttpResponse<JsonNode> response = Unirest.get("http://172.16.1.86:8082/rest/api/2/search")
			.basicAuth("admin1", "123456")
			.header("Accept", "application/json")
			.queryString("jql", "project = MT")
						.queryString("startAt", startAt).queryString("maxResults", maxResults).asJson();

				// JsonNode responseBody = response.getBody();
				JsonNode responseBody = response.getBody();
				Object demo = response.getBody().getObject().get("issues");
				List<Object> listOfIsuue = new ArrayList<>();
				listOfIsuue.add(demo);
				System.err.println(listOfIsuue);

				// JsonNode responseBody = response.getBody();
				// String jsonAsString = responseBody.toString();
				// System.out.println(response.getBody().toString());
				// Object demo =response.getBody().getObject().get("issues");
				if (response.getStatus() == 200) {
					String outputFilePath = "D:\\file.json";
					System.out.println(listOfIsuue.toString());
					try (FileWriter fileWriter = new FileWriter(outputFilePath)) {
						System.err.println(listOfIsuue.toString());
						fileWriter.write(listOfIsuue.toString());
					}
					System.out.println("JSON written to file: " + outputFilePath);
					// String fileName = "output.json";

					System.out.println("JSON file written successfully!");
				} else {
					System.err.println("Request failed with status: " + response.getStatus());
					System.err.println("Response body: " + response.getBody());
				}

				// System.out.println(demo.toString());

				// System.out.println(response.getBody());
				// allIssues.add(responseBody);

				int total = responseBody.getObject().getInt("total");
				startAt += maxResults;

				if (startAt >= total) {
					// All data has been retrieved
					break;
				}
			}

			// Process or return allIssues as needed
//			for (JsonNode issue : allIssues) {
////				System.out.println(issue);
//			}
		} catch (UnirestException e) {
			throw new RuntimeException(e);
		}
		
		String apiUrl = "https://storage.googleapis.com/upload/storage/v1/b/pega_data/o?uploadType=media&name=java.txt";
        String authorizationHeader = "Bearer ya29.a0AfB_byCWdjCEYPG5641HiKMW9N2yHSWJ7J3z9O8MBry4OkVKQDeT7N68Kgt9buku4CARJrOIE6Y7PdYCj_f-hRAMLdE52Dtq3GC1YdVc-4xdBVqZ6FP5MhVad906g-OMQ0qbf8cUs7rueiT4Y3AKlZuUlNUvEXdM5imAwgaCgYKAUUSARMSFQHGX2MiMUtw2mflGh44eXSVU9JVig0173";
        String filePath = "D:\\file.json";

        String apiResponse="";
        try {
             apiResponse = makeFileUploadRequest(apiUrl, authorizationHeader, filePath);
            System.out.println("API Response: " + apiResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    
		

		return apiResponse;
	}
	
	
	
	
		 

	 private static String makeFileUploadRequest(String apiUrl, String authorizationHeader, String filePath) throws Exception {
	        URL url = new URL(apiUrl);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

	        // Set the request method
	        connection.setRequestMethod("POST");

	        // Set the authorization header
	        connection.setRequestProperty("Authorization", authorizationHeader);

	        // Enable input/output streams for writing and reading data
	        connection.setDoOutput(true);

	        // Create a DataOutputStream to write the file content
	        try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
	             FileInputStream fileInputStream = new FileInputStream(new File(filePath))) {

	            // Read the file content and write it to the output stream
	            byte[] buffer = new byte[4096];
	            int bytesRead;
	            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
	                outputStream.write(buffer, 0, bytesRead);
	            }

	            // Flush and close the output stream
	            outputStream.flush();
	        }

	        // Get the response code
	        int responseCode = connection.getResponseCode();
	        System.out.println("Response Code: " + responseCode);

	        // Read the response from the API
	        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        StringBuilder response = new StringBuilder();
	        String line;

	        while ((line = reader.readLine()) != null) {
	            response.append(line);
	        }
	        reader.close();

	        // Close the connection
	        connection.disconnect();

	        return response.toString();
	    }
	
	
	

	@GetMapping("/getall1")
	public String getissue1() {

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
						.basicAuth("vikram221999@gmail.com",
								"ATATT3xFfGF0kwaHKdctaCBQgXIwfQcEE0Ui-D3saW4wsS3C8tZzQCA-ODfKDKiO8MRk6qvXYyqbx40L0pL5B1Rg2NDOCUYcvlRFFFrYwmJHHco1625cniIyk5F0YCHeDgZUkL4N3AD4BmN_Oj_h6weRusFNOc4GGwbIcN7s3q-7kov_xYxE1ls=58B29C10")
						.header("Accept", "application/json").queryString("jql", "project =QJP")
						.queryString("startAt", startAt).queryString("maxResults", maxResults).asJson();

				JsonNode responseBody = response.getBody();
				Object demo = response.getBody().getObject().get("issues");
				List<Object> listOfIsuue = new ArrayList<>();
				listOfIsuue.add(demo);

				System.out.println(demo.toString());

				// System.out.println(response.getBody());
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

	@GetMapping(value = "/search")
	public String apiTest1() {
		String jsonString = "";
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			HttpResponse<JsonNode> response = Unirest.get("https://qim-dev.atlassian.net/rest/api/3/search").basicAuth(
					"vikram221999@gmail.com",
					"ATATT3xFfGF0ptO0M_51PM26UY4MrnODCI8aSZuhMuR1VF3t7dtRekq5PdR56hIDT5JcJ17y6LzU84MesfABijAUxX2ZHEcAS4uP-D8QpZBkVfld42i21kS8-cHqAT7xdI2HZ_kY_FM1w27PslR80AXd-5FHsa-HmCGuV1Fq3SE_CWUzBP4OJ-c=B91C6C82")
					.header("Accept", "application/json").asJson();
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
	public String apiTest2() {
		String jsonString = "";
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			HttpResponse<JsonNode> response = Unirest.get("https://qim-dev.atlassian.net/rest/api/3/search")
					.basicAuth("vikram221999@gmail.com", "Vino@143" + "").header("Accept", "application/json").asJson();
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
