package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.opencsv.CSVWriter;

@Service
public class JiraToGcpService {
	final String authorizationHeader = "Bearer ya29.a0AfB_byA8wpvcmi-t0jslGXVD2KtHu2onW8QJbZ8m42QWaZtg_cKBS_wW-VSBmfjh_oraHhTfrir7sOdygc4HCRpwLKgu-PXB0Y_SmHqCliylhoSJI7mHmzkfHPG5LsHkAxaNlmR1CsuieKH4qdDZ-GQo8PtwOmxmO51ooQaCgYKAWYSARMSFQHGX2Mir1ybeeopbuF9EB-IOHfBWg0173";

	public String uploadJsonFileToGcp() throws IOException {

		int maxResults = 1000;
		int startAt = 0;
		List<Object> allIssues = new ArrayList<>();
		//String outputFilePath = "D:\\JavaJiraToGcp.json";
		String outputFilePath = "src/main/resources/files/JavaJiraToGcp.json";

		// try (FileWriter fileWriter = new FileWriter(outputFilePath)) {
		try {
			while (true) {

				// Own Cloud Account

					 String jiraBaseUrl = "https://qim-dev.atlassian.net";
				        String projectKey = "QJP";
				        String username = "vikram221999@gmail.com";
				        String apiToken = "ATATT3xFfGF0K0M0dczSy-FqEcK_malSo4_clShvwjW_vsn8sIt8uApf0X5em6N7vtmHn2bsaeONhO_9XDpCrViRsi7d7WAgzlVIIP1QxvHaMcGsqDR_w5j32uRV7xkSNjW-v6Gq8hwbJ5yeje1nQZQz75ZaKOlYhY1BfKZu5iMIC-6QjqPb76s=01230740";
				
				        // Build the Jira API search URL
				        String apiUrl = jiraBaseUrl + "/rest/api/3/search";
				        String jqlQuery = "project=" + projectKey;
				        String fields = "issuetype,subtasks,summary,description";
				
				        HttpResponse<JsonNode> response = Unirest.get(apiUrl)
				                .basicAuth(username, apiToken)
				                .header("Accept", "application/json")
				                .queryString("jql", jqlQuery)
				                .queryString("fields", fields)
				                .asJson();
				        

//				String jiraBaseUrl = "http://172.16.1.86:8082";
//				String projectKey = "MT";
//				String username = "admin1";
//				String apiToken = "123456";
//
//				// Build the Jira API search URL
//				String apiUrl = jiraBaseUrl + "/rest/api/2/search";
//				String jqlQuery = "project=" + projectKey;
//				String fields = "issuetype,summary,description";
//
//				HttpResponse<JsonNode> response = Unirest.get(apiUrl).basicAuth(username, apiToken)
//						.header("Accept", "application/json").queryString("jql", jqlQuery).queryString("fields", fields)
//						.queryString("startAt", startAt).queryString("maxResults", maxResults).asJson();

				JsonNode responseBody = response.getBody();
				Object demo = response.getBody().getObject().get("issues");
				// System.out.println(demo.toString());

				// fileWriter.write(demo.toString());

				if (response.getStatus() == 200) {
					allIssues.add(demo);

				} else {
					System.err.println("Request failed with status: " + response.getStatus());
					System.err.println("Response body: " + response.getBody());
				}

				int total = responseBody.getObject().getInt("total");
				
				startAt += maxResults;
				System.out.println(startAt);

				if (startAt >= total) {

					break;
				}
			}
			try (FileWriter fileWriter = new FileWriter(outputFilePath)) {
				//System.out.println(allIssues.toString());
				fileWriter.write(allIssues.toString());

				System.out.println("JSON written to file: " + outputFilePath);
				System.out.println("JSON file written successfully!");
			}
		} catch (UnirestException e) {
			throw new RuntimeException(e);
		}

		String apiUrl = "https://storage.googleapis.com/upload/storage/v1/b/pega_data/o?uploadType=media&name=JsonJavaJiraToGcp.json";
		//String authorizationHeader = "Bearer ya29.a0AfB_byCYEJXV7-dYQdaTQmkhV6mCS-SrqU0_BufZ__2S8C-6hOUjg26AeQb8ewlZLoI0LPVtRNonUGF94gvmnbTRgLLHW8OeWCglDa1m2KJOv8F6ABVhWEmmPK7E5mCeMruC1g-Xleb3v_4-SHjAp0KfO7kQxYqx3Wbe5AaCgYKAW4SARMSFQHGX2MiMd-KBbhOA7oDp4u66zNtdg0173";
		String filePath = "src/main/resources/files/JavaJiraToGcp.json";

		String apiResponse = "";
		try {
			apiResponse = makeFileUploadRequest(apiUrl, authorizationHeader, filePath);
			System.out.println("API Response: " + apiResponse);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return apiResponse;
	}

	private static String makeFileUploadRequest(String apiUrl, String authorizationHeader, String filePath)
			throws Exception {
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
			byte[] buffer = new byte[8192];
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

	public String uploadCsvFileToGcp() throws IOException {

		int maxResults = 1000;
		int startAt = 0;
		String outputFilePath = "src/main/resources/files/JavaJiraToGcp.csv";
	//	String outputFilePath = "D:\\JavaJiraToGcp.csv";

		try (CSVWriter csvWriter = new CSVWriter(new FileWriter(outputFilePath))) {
			String[] header = { "IssueKey","IssueId","IssueSelf", "Summary", "IssueType","IssueTypeId","IssueTypeSelf","IssueTypeDescription","IssueTypeiconUrl","SubtaskStatus" };
			csvWriter.writeNext(header);
			while (true) {
//					 String jiraBaseUrl = "https://qim-dev.atlassian.net";
//				        String projectKey = "QJP";
//				        String username = "vikram221999@gmail.com";
//				        String apiToken = "ATATT3xFfGF0K0M0dczSy-FqEcK_malSo4_clShvwjW_vsn8sIt8uApf0X5em6N7vtmHn2bsaeONhO_9XDpCrViRsi7d7WAgzlVIIP1QxvHaMcGsqDR_w5j32uRV7xkSNjW-v6Gq8hwbJ5yeje1nQZQz75ZaKOlYhY1BfKZu5iMIC-6QjqPb76s=01230740";
				//
//				        // Build the Jira API search URL
//				        String apiUrl = jiraBaseUrl + "/rest/api/3/search";
//				        String jqlQuery = "project=" + projectKey;
//				        String fields = "issuetype,subtasks,summary,description";
				//
//				        HttpResponse<JsonNode> response = Unirest.get(apiUrl)
//				                .basicAuth(username, apiToken)
//				                .header("Accept", "application/json")
//				                .queryString("jql", jqlQuery)
//				                .queryString("fields", fields)
//				                .asJson();

				String jiraBaseUrl = "http://172.16.1.86:8082";
				String projectKey = "MT";
				String username = "admin1";
				String apiToken = "123456";
				// Build the Jira API search URL
				String apiUrl = jiraBaseUrl + "/rest/api/2/search";
				String jqlQuery = "project=" + projectKey;
				String fields = "issuetype,summary,description";
				HttpResponse<JsonNode> response = Unirest.get(apiUrl).basicAuth(username, apiToken)
						.header("Accept", "application/json").queryString("jql", jqlQuery).queryString("fields", fields)
						.queryString("startAt", startAt).queryString("maxResults", maxResults).asJson();

				JsonNode responseBody = response.getBody();
				ObjectMapper objectMapper = new ObjectMapper();
				Object demo = response.getBody().getObject().get("issues");
				com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(demo.toString());

				// Process each issue in the array and write to CSV
				for (com.fasterxml.jackson.databind.JsonNode issueNode : jsonNode) {
					
					//System.out.println(key);
					
					String key = issueNode.path("key").asText();
					String issueId = issueNode.path("id").asText();
					String issueSelf = issueNode.path("self").asText();
					String summary = issueNode.path("fields").path("summary").asText();
					String issueType = issueNode.path("fields").path("issuetype").path("name").asText();
					String IssueTypeId = issueNode.path("fields").path("issuetype").path("id").asText();
					String issueTypeDescription = issueNode.path("fields").path("issuetype").path("description").asText();
					String issueTypeSelf = issueNode.path("fields").path("issuetype").path("self").asText();
					String issueTypeiconUrl = issueNode.path("fields").path("issuetype").path("iconUrl").asText();
					String SubTaskStatus = issueNode.path("fields").path("issuetype").path("subtask").asText();					
					// Write the data to CSV
					csvWriter.writeNext(new String[] { key,issueId, summary,issueSelf, issueType,IssueTypeId,issueTypeSelf,issueTypeDescription,issueTypeiconUrl,SubTaskStatus});
				}

			//	System.out.println("CSV file created successfully at: " + outputFilePath);

				if (response.getStatus() == 200) {
					//System.out.println(demo);

				} else {
					System.err.println("Request failed with status: " + response.getStatus());
					//System.err.println("Response body: " + response.getBody());
				}

				int total = responseBody.getObject().getInt("total");
				//System.err.println(total);
				startAt += maxResults;
				System.out.println(startAt);
				if (startAt >= total) {
					break;
				}
			}

		} catch (UnirestException e) {
			throw new RuntimeException(e);
		}

		// Additional code for file upload...
		String apiUrl = "https://storage.googleapis.com/upload/storage/v1/b/pega_data/o?uploadType=media&name=CSVJavaJiraToGcp.csv";
		//String authorizationHeader = "Bearer ya29.a0AfB_byAu1zsbbscrVT_8U0BHPHW4i6nbZVQoPrm_giPAXwmMGBpi-Tqj_IU_slCKTQpNRuqqQ8ffL_Tw0q0VW-j1nWMw-1xjGLHe97WdYkt3d4xoHmTU6nZtbD72HcD0dq8SbN6lPQaeb4taWJ_PXc6vDXLZ4vSPULD_vQaCgYKAdYSARMSFQHGX2MiFrCv4JeQhfwZOVzmMEqSPg0173";
		String filePath = "D:\\JavaJiraToGcp.csv";
		String apiResponse = "";
		try {
			apiResponse = makeFileUploadRequest(apiUrl, authorizationHeader, filePath);
			System.out.println("API Response: " + apiResponse);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return apiResponse;
	}
	
	public String handleWebhook(String payload) {
		System.out.println(payload);
		System.out.println("Working >>>>>>>>>>>>>>>>>>>>>>>>");
//		 String issueKey = (String) payload.get("issueKey");
//	        String summary = (String) payload.get("summary");
//	        
//	        System.out.println(issueKey);
//	        System.out.println(summary);
//		
		return "Succes";
	}
	
	
	
	
	public String getIssuseFromJira2() {

		int maxResults = 1000;
		int startAt = 0;
		List<JsonNode> allIssues = new ArrayList<>();

		try {
			while (true) {
//					HttpResponse<JsonNode> response = Unirest.get("http://172.16.1.86:8082/rest/api/2/search")
//							.basicAuth("admin1", "123456")
//							.header("Accept", "application/json")
//							.queryString("jql", "project = MT")
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

		} catch (UnirestException e) {
			throw new RuntimeException(e);
		}

		return "Sucess";
	}
	
	public String createdIssuseFromJira(Object object){
		
		System.out.println(object.toString());
		
		
		
		return object.toString();
		
		
	}
	
	

	

	public String getIssuseFromJira() {

		int maxResults = 1000;
		int startAt = 0;
		List<JsonNode> allIssues = new ArrayList<>();

		try {
			while (true) {
//					HttpResponse<JsonNode> response = Unirest.get("http://172.16.1.86:8082/rest/api/2/search")
//							.basicAuth("admin1", "123456")
//							.header("Accept", "application/json")
//							.queryString("jql", "project = MT")
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

		} catch (UnirestException e) {
			throw new RuntimeException(e);
		}

		return "Sucess";
	}

	// to get basic issue getMethod().
	/*
	 * public String apiTest1() { String jsonString = ""; ObjectMapper objectMapper
	 * = new ObjectMapper(); try { HttpResponse<JsonNode> response =
	 * Unirest.get("https://qim-dev.atlassian.net/rest/api/3/search").basicAuth(
	 * "vikram221999@gmail.com",
	 * "ATATT3xFfGF0ptO0M_51PM26UY4MrnODCI8aSZuhMuR1VF3t7dtRekq5PdR56hIDT5JcJ17y6LzU84MesfABijAUxX2ZHEcAS4uP-D8QpZBkVfld42i21kS8-cHqAT7xdI2HZ_kY_FM1w27PslR80AXd-5FHsa-HmCGuV1Fq3SE_CWUzBP4OJ-c=B91C6C82")
	 * .header("Accept", "application/json").asJson(); try { JSONObject jsonObject =
	 * response.getBody().getObject(); // Convert the JSONObject to a
	 * com.fasterxml.jackson.databind.JsonNode
	 * com.fasterxml.jackson.databind.JsonNode jacksonJsonNode =
	 * objectMapper.readTree(jsonObject.toString()); // Serialize the Jackson
	 * JsonNode to a JSON string jsonString =
	 * objectMapper.writeValueAsString(jacksonJsonNode);
	 * System.out.println(jsonString); } catch (JsonProcessingException e) {
	 * e.printStackTrace(); } } catch (UnirestException e) { e.printStackTrace(); }
	 * return jsonString; }
	 */

}
