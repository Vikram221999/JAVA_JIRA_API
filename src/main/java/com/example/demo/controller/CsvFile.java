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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.opencsv.CSVWriter;

@RestController
@RequestMapping("/jira1")
@CrossOrigin("*")
public class CsvFile {

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

	@GetMapping("/getallCSV")
	public String getissueCSV() throws IOException {

		int maxResults = 1000;
		int startAt = 0;
		List<JsonNode> allIssues = new ArrayList<>();
		// int total=0;
		String outputFilePath = "D:\\JAVA.csv";
		List<Object> listOfIsuue = new ArrayList<>();

		try (CSVWriter csvWriter = new CSVWriter(new FileWriter(outputFilePath))) {
			String[] header = { "Key", "Summary", "Issue Type" };
			csvWriter.writeNext(header);
			while (true) {
//				 String jiraBaseUrl = "https://qim-dev.atlassian.net";
//			        String projectKey = "QJP";
//			        String username = "vikram221999@gmail.com";
//			        String apiToken = "ATATT3xFfGF0K0M0dczSy-FqEcK_malSo4_clShvwjW_vsn8sIt8uApf0X5em6N7vtmHn2bsaeONhO_9XDpCrViRsi7d7WAgzlVIIP1QxvHaMcGsqDR_w5j32uRV7xkSNjW-v6Gq8hwbJ5yeje1nQZQz75ZaKOlYhY1BfKZu5iMIC-6QjqPb76s=01230740";
//
//			        // Build the Jira API search URL
//			        String apiUrl = jiraBaseUrl + "/rest/api/3/search";
//			        String jqlQuery = "project=" + projectKey;
//			        String fields = "issuetype,subtasks,summary,description";
//
//			        HttpResponse<JsonNode> response = Unirest.get(apiUrl)
//			                .basicAuth(username, apiToken)
//			                .header("Accept", "application/json")
//			                .queryString("jql", jqlQuery)
//			                .queryString("fields", fields)
//			                .asJson();

				String jiraBaseUrl = "http://172.16.1.86:8082";
				String projectKey = "MT";
				String username = "admin1";
				String apiToken = "123456";

				// Build the Jira API search URL
				String apiUrl = jiraBaseUrl + "/rest/api/2/search";
				String jqlQuery = "project=" + projectKey;
				String fields = "issuetype,subtasks,summary,description";

				HttpResponse<JsonNode> response = Unirest.get(apiUrl).basicAuth(username, apiToken)
						.header("Accept", "application/json").queryString("jql", jqlQuery).queryString("fields", fields)
						.queryString("startAt", startAt).queryString("maxResults", maxResults).asJson();

				JsonNode responseBody = response.getBody();

				ObjectMapper objectMapper = new ObjectMapper();
				Object demo = response.getBody().getObject().get("issues");
				com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(demo.toString());

				// Process each issue in the array and write to CSV
				for (com.fasterxml.jackson.databind.JsonNode issueNode : jsonNode) {
					String key = issueNode.path("key").asText();
					System.out.println(key);
					String summary = issueNode.path("fields").path("summary").asText();
					String issueType = issueNode.path("fields").path("issuetype").path("name").asText();

					// Write the data to CSV
					csvWriter.writeNext(new String[] { key, summary, issueType });
				}

				System.out.println("CSV file created successfully at: " + outputFilePath);

				if (response.getStatus() == 200) {
					System.out.println(demo);

				} else {
					System.err.println("Request failed with status: " + response.getStatus());
					System.err.println("Response body: " + response.getBody());
				}

				int total = responseBody.getObject().getInt("total");
				System.err.println(total);
				startAt += maxResults;

				if (startAt >= total) {
					break;
				}
			}

		} catch (UnirestException e) {
			throw new RuntimeException(e);
		}

		// Additional code for file upload...
		String apiUrl = "https://storage.googleapis.com/upload/storage/v1/b/pega_data/o?uploadType=media&name=JAVACSV.csv";
		String authorizationHeader = "Bearer ya29.a0AfB_byAu1zsbbscrVT_8U0BHPHW4i6nbZVQoPrm_giPAXwmMGBpi-Tqj_IU_slCKTQpNRuqqQ8ffL_Tw0q0VW-j1nWMw-1xjGLHe97WdYkt3d4xoHmTU6nZtbD72HcD0dq8SbN6lPQaeb4taWJ_PXc6vDXLZ4vSPULD_vQaCgYKAdYSARMSFQHGX2MiFrCv4JeQhfwZOVzmMEqSPg0173";
		String filePath = "D:\\JAVA.csv";
		String apiResponse = "";
		try {
			apiResponse = makeFileUploadRequest(apiUrl, authorizationHeader, filePath);
			System.out.println("API Response: " + apiResponse);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return apiResponse;
	}

}
