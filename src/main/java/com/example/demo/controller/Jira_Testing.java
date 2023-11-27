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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
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
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
//import com.opencsv.CSVWriter;
import com.opencsv.CSVWriter;

@RestController
@RequestMapping("/jira")
@CrossOrigin("*")
public class Jira_Testing {

	@GetMapping("/getall")
	public String getissue() throws IOException {

		int maxResults = 1000;
		int startAt = 0;
		List<Object> allIssues = new ArrayList<>();
		// int total=0;
		String outputFilePath = "D:\\file.json";
		

	//	try (FileWriter fileWriter = new FileWriter(outputFilePath)) {
		try {
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
//			        
			        
			        
			        String jiraBaseUrl = "http://172.16.1.86:8082";
			        String projectKey = "MT";
			        String username = "admin1";
			        String apiToken = "123456";

			        // Build the Jira API search URL
			        String apiUrl = jiraBaseUrl + "/rest/api/2/search";
			        String jqlQuery = "project=" + projectKey;
			        String fields = "issuetype,subtasks,summary,description";

			        HttpResponse<JsonNode> response = Unirest.get(apiUrl)
			                .basicAuth(username, apiToken)
			                .header("Accept", "application/json")
			                .queryString("jql", jqlQuery)
			                .queryString("fields", fields)
			                .queryString("startAt", startAt).queryString("maxResults", maxResults)
			                .asJson();
				

				JsonNode responseBody = response.getBody();
				Object demo = response.getBody().getObject().get("issues");
				//System.out.println(demo.toString());
				
			//	fileWriter.write(demo.toString());
				
				
				
				ObjectMapper objectMapper = new ObjectMapper();
				 com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(demo.toString());

		            // Process each issue in the array
//		            for (com.fasterxml.jackson.databind.JsonNode issueNode : jsonNode) {
//		                String key = issueNode.path("key").asText();
//		                String summary = issueNode.path("fields").path("summary").asText();
//		                String issueType = issueNode.path("fields").path("issuetype").path("name").asText();
//
//		                // Print or process the extracted information
//		                System.out.println("Key: " + key);
//		                System.out.println("Summary: " + summary);
//		                System.out.println("Issue Type: " + issueType);
//		                System.out.println("-----------");
//		            }
				
				if (response.getStatus() == 200) {
					allIssues.add(demo);

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
			try(FileWriter fileWriter = new FileWriter(outputFilePath)){
				System.out.println(allIssues.toString());
			fileWriter.write(allIssues.toString());
			
			System.out.println("JSON written to file: " + outputFilePath);
			System.out.println("JSON file written successfully!");
			}
		} catch (UnirestException e) {
			throw new RuntimeException(e);
		}

		String apiUrl = "https://storage.googleapis.com/upload/storage/v1/b/pega_data/o?uploadType=media&name=java.txt";
		String authorizationHeader = "Bearer ya29.a0AfB_byCYEJXV7-dYQdaTQmkhV6mCS-SrqU0_BufZ__2S8C-6hOUjg26AeQb8ewlZLoI0LPVtRNonUGF94gvmnbTRgLLHW8OeWCglDa1m2KJOv8F6ABVhWEmmPK7E5mCeMruC1g-Xleb3v_4-SHjAp0KfO7kQxYqx3Wbe5AaCgYKAW4SARMSFQHGX2MiMd-KBbhOA7oDp4u66zNtdg0173";
		String filePath = "D:\\file.json";

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
	
	@GetMapping("/getallCSV1")
	public String getissueCSV1() throws IOException {

		int maxResults = 1000;
		int startAt = 0;
		List<JsonNode> allIssues = new ArrayList<>();
		// int total=0;
		String outputFilePath = "D:\\JAVA.csv";
		List<Object> listOfIsuue = new ArrayList<>();

		 CsvMapper csvMapper = new CsvMapper();
	        CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder().setUseHeader(true);
	        CsvSchema csvSchema = csvSchemaBuilder.build();

	        
	        try (FileWriter fileWriter = new FileWriter(outputFilePath)) {
	            CsvGenerator csvGenerator = csvMapper.getFactory().createGenerator(fileWriter);
	
			while (true) {
//					HttpResponse<JsonNode> response = Unirest.get("http://172.16.1.86:8082/rest/api/2/search")
//							.basicAuth("admin1", "123456")
//							.header("Accept", "application/json")
//							.queryString("jql", "project = MT")
				HttpResponse<JsonNode> response = Unirest.get("https://qim-dev.atlassian.net/rest/api/3/search")
						.basicAuth("vikram221999@gmail.com",
								"ATATT3xFfGF0K0M0dczSy-FqEcK_malSo4_clShvwjW_vsn8sIt8uApf0X5em6N7vtmHn2bsaeONhO_9XDpCrViRsi7d7WAgzlVIIP1QxvHaMcGsqDR_w5j32uRV7xkSNjW-v6Gq8hwbJ5yeje1nQZQz75ZaKOlYhY1BfKZu5iMIC-6QjqPb76s=01230740")
//						
//					HttpResponse<JsonNode> response = Unirest.get("http://172.16.1.86:8082/rest/api/2/search")
//				.basicAuth("admin1", "123456")
						.header("Accept", "application/json").queryString("jql", "project =QJP")
						.queryString("startAt", startAt).queryString("maxResults", maxResults).asJson();

				// JsonNode responseBody = response.getBody();
				JsonNode responseBody = response.getBody();
				Object demo = response.getBody().getObject().get("issues");

				
				
				
				 // Write the header using CsvSchema
	            csvGenerator.writeStartArray();
	            for (CsvSchema.Column column : csvSchema) {
	                csvGenerator.writeString(column.getName());
	            }
	            csvGenerator.writeEndArray();

	            // Iterate through the list of issues
	          //  for (Object issue : listOfIssues) {
	            	
	            	System.err.println("////////////////////" );
	            	System.err.println(demo );
	                if (demo instanceof ObjectNode) {
	                	System.out.println("23456787654323456");
	                    // Write each record
	                    csvGenerator.writeStartArray();
	                    for (CsvSchema.Column column : csvSchema) {
	                        String fieldName = column.getName();
	                        String value = ((ObjectNode) demo).has(fieldName) ?
	                                ((ObjectNode) demo).get(fieldName).asText() :
	                                ""; // You might need to handle different data types accordingly
	                        csvGenerator.writeString(value);
	                    }
	                    csvGenerator.writeEndArray();
	                }
	         //   }

	            csvGenerator.close();
				
				
				
				
				
				
				// System.err.println(listOfIsuue);
				if (response.getStatus() == 200) {
					System.out.println(demo);
					listOfIsuue.add(demo);

					// System.out.println(listOfIsuue.toString());

				} else {
					System.err.println("Request failed with status: " + response.getStatus());
					System.err.println("Response body: " + response.getBody());
				}
				int total = responseBody.getObject().getInt("total");
				System.err.println(total);
				startAt += maxResults;

				if (startAt >= total) {
					// All data has been retrieved
					break;
				}
			}
			
			convertJsonToCsv(listOfIsuue,outputFilePath);
	
			System.out.println("CSV written to file: " + outputFilePath);
		
		} catch (UnirestException e) {
			throw new RuntimeException(e);
		}

		String apiUrl = "https://storage.googleapis.com/upload/storage/v1/b/pega_data/o?uploadType=media&name=JAVACSV.csv";
		String authorizationHeader = "Bearer ya29.a0AfB_byAbsZunQD56L7yC1mN565XMwPrycg0rdbQmPe5fGSjG3qy16tH02kunN1gC9LyNfNQxIYk9Ne6dMV0Ew8RTar4oThQ0VVUUlk6e8dzK68CUvPlwgDKcDtzsQd1LjwLfgo2S7V1aZoW2cF163hvDcE1bS1F5U2RWHgaCgYKAeASARMSFQHGX2Mi1vmbqq249Xdd63olbtyZ-A0173";
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
	
	

	@GetMapping("/getallCSV")
	public String getissueCSV() throws IOException {

		int maxResults = 1000;
		int startAt = 0;
		List<JsonNode> allIssues = new ArrayList<>();
		// int total=0;
		String outputFilePath = "D:\\JAVA.csv";
		List<Object> listOfIsuue = new ArrayList<>();

		try {
			while (true) {
//					HttpResponse<JsonNode> response = Unirest.get("http://172.16.1.86:8082/rest/api/2/search")
//							.basicAuth("admin1", "123456")
//							.header("Accept", "application/json")
//							.queryString("jql", "project = MT")
				HttpResponse<JsonNode> response = Unirest.get("https://qim-dev.atlassian.net/rest/api/3/search")
						.basicAuth("vikram221999@gmail.com",
								"ATATT3xFfGF0K0M0dczSy-FqEcK_malSo4_clShvwjW_vsn8sIt8uApf0X5em6N7vtmHn2bsaeONhO_9XDpCrViRsi7d7WAgzlVIIP1QxvHaMcGsqDR_w5j32uRV7xkSNjW-v6Gq8hwbJ5yeje1nQZQz75ZaKOlYhY1BfKZu5iMIC-6QjqPb76s=01230740")
//						
//					HttpResponse<JsonNode> response = Unirest.get("http://172.16.1.86:8082/rest/api/2/search")
//				.basicAuth("admin1", "123456")
						.header("Accept", "application/json").queryString("jql", "project =QJP")
						.queryString("startAt", startAt).queryString("maxResults", maxResults).asJson();

				// JsonNode responseBody = response.getBody();
				JsonNode responseBody = response.getBody();
				Object demo = response.getBody().getObject().get("issues");

				// System.err.println(listOfIsuue);
				if (response.getStatus() == 200) {
					System.out.println(demo);
					listOfIsuue.add(demo);

					// System.out.println(listOfIsuue.toString());

				} else {
					System.err.println("Request failed with status: " + response.getStatus());
					System.err.println("Response body: " + response.getBody());
				}
				int total = responseBody.getObject().getInt("total");
				System.err.println(total);
				startAt += maxResults;

				if (startAt >= total) {
					// All data has been retrieved
					break;
				}
			}
			System.out.println(listOfIsuue);
			convertJsonToCsv(listOfIsuue,outputFilePath);
	
			System.out.println("CSV written to file: " + outputFilePath);
		
		} catch (UnirestException e) {
			throw new RuntimeException(e);
		}

		String apiUrl = "https://storage.googleapis.com/upload/storage/v1/b/pega_data/o?uploadType=media&name=JAVACSV.csv";
		String authorizationHeader = "Bearer ya29.a0AfB_byAbsZunQD56L7yC1mN565XMwPrycg0rdbQmPe5fGSjG3qy16tH02kunN1gC9LyNfNQxIYk9Ne6dMV0Ew8RTar4oThQ0VVUUlk6e8dzK68CUvPlwgDKcDtzsQd1LjwLfgo2S7V1aZoW2cF163hvDcE1bS1F5U2RWHgaCgYKAeASARMSFQHGX2Mi1vmbqq249Xdd63olbtyZ-A0173";
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
	
		
	
	
	
	
	 private static void convertJsonToCsv(List<Object> listOfIssues, String outputFilePath) throws IOException {
	        CsvMapper csvMapper = new CsvMapper();
	        CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder().setUseHeader(true);
	        CsvSchema csvSchema = csvSchemaBuilder.build();

	        try (FileWriter fileWriter = new FileWriter(outputFilePath)) {
	            CsvGenerator csvGenerator = csvMapper.getFactory().createGenerator(fileWriter);

	            // Write the header using CsvSchema
	            csvGenerator.writeStartArray();
	            for (CsvSchema.Column column : csvSchema) {
	                csvGenerator.writeString(column.getName());
	            }
	            csvGenerator.writeEndArray();

	            // Iterate through the list of issues
	            for (Object issue : listOfIssues) {
	            	
	            	System.err.println("////////////////////");
	                if (issue instanceof ObjectNode) {
	                	System.out.println("23456787654323456");
	                    // Write each record
	                    csvGenerator.writeStartArray();
	                    for (CsvSchema.Column column : csvSchema) {
	                        String fieldName = column.getName();
	                        String value = ((ObjectNode) issue).has(fieldName) ?
	                                ((ObjectNode) issue).get(fieldName).asText() :
	                                ""; // You might need to handle different data types accordingly
	                        csvGenerator.writeString(value);
	                    }
	                    csvGenerator.writeEndArray();
	                }
	            }

	            csvGenerator.close();
	        }

	        System.out.println("CSV written to file: " + outputFilePath);
	    }
	
	
	

//	public static void exportToCSV(List<Object> data, String fileName) throws IOException {
//		System.err.println(data.get(0));
//        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
//        	  writer.writeNext(data.stream().map(Object::toString).toArray(String[]::new));
////            for (List<Object> row : data) {
////                // Convert each object in the row to a String and add it to the CSVWriter
////                writer.writeNext(row.stream().map(Object::toString).toArray(String[]::new));
////            }
//        }
//	}

//	 private void convertJsonToCsv(List<Object> listOfIssues, String outputFilePath) throws IOException {
//		    // Create a CSVPrinter
//		    try (CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(outputFilePath), CSVFormat.DEFAULT)) {
//		        // Iterate through the list of issues
//		        for (Object issue : listOfIssues) {
//		            // Convert JSON to CSV and write to the CSVPrinter
//		            ObjectMapper objectMapper = new ObjectMapper();
//		            objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
//		            com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.valueToTree(issue);
//		            CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder();
//		            CsvSchema csvSchema = csvSchemaBuilder.build().withHeader();
//		            String csvString = objectMapper.writer(csvSchema).writeValueAsString(jsonNode);
//		            csvPrinter.printRecord(csvString);
//		        }
//		    }
//		    System.out.println("CSV written to file: " + outputFilePath);
//		}
//	 

//	private static void convertJsonToCsv(List<Object> listOfIssues, String outputFilePath) throws IOException {
//		CsvMapper csvMapper = new CsvMapper();
//		CsvSchema.Builder csvSchemaBuilder = CsvSchema.builder().setUseHeader(true);
//		CsvSchema csvSchema = csvSchemaBuilder.build();
//
//		try (FileWriter fileWriter = new FileWriter(outputFilePath)) {
//			CsvGenerator csvGenerator = csvMapper.getFactory().createGenerator(fileWriter);
//
//			// Write the header using CsvSchema
//			csvGenerator.writeStartArray();
//			for (CsvSchema.Column column : csvSchema) {
//				csvGenerator.writeString(column.getName());
//			}
//			csvGenerator.writeEndArray();
//
//			// Iterate through the list of issues
//			for (Object issue : listOfIssues) {
//				System.out.println("/////////////////////");
//				
//			//	if (issue instanceof ObjectNode) {
//					System.out.println("//////////INNNNNNNNNN///////////");
//					// Write each record
//					csvGenerator.writeStartArray();
//					for (CsvSchema.Column column : csvSchema) {
//						String fieldName = column.getName();
//						String value = ((ObjectNode) issue).has(fieldName)
//								? ((ObjectNode) issue).get(fieldName).asText()
//								: ""; // You might need to handle different data types accordingly
//						System.out.println(value);
//						csvGenerator.writeString(value);
//					}
//					csvGenerator.writeEndArray();
//			//	}
//			}
//
//			csvGenerator.close();
//		}
//
//		System.out.println("CSV written to file: " + outputFilePath);
//
//	}

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

	
//	@GetMapping("/getallCSV2")
//	public String getissueCSV2() throws Exception {
//
//		int maxResults = 1000;
//		int startAt = 0;
//		List<JsonNode> allIssues = new ArrayList<>();
//		// int total=0;
//		String outputFilePath = "D:\\JAVA.csv";
//		List<Object> listOfIsuue = new ArrayList<>();
//
//		try {
//			while (true) {
////					HttpResponse<JsonNode> response = Unirest.get("http://172.16.1.86:8082/rest/api/2/search")
////							.basicAuth("admin1", "123456")
////							.header("Accept", "application/json")
////							.queryString("jql", "project = MT")
//				HttpResponse<JsonNode> response = Unirest.get("https://qim-dev.atlassian.net/rest/api/3/search")
//						.basicAuth("vikram221999@gmail.com",
//								"ATATT3xFfGF0K0M0dczSy-FqEcK_malSo4_clShvwjW_vsn8sIt8uApf0X5em6N7vtmHn2bsaeONhO_9XDpCrViRsi7d7WAgzlVIIP1QxvHaMcGsqDR_w5j32uRV7xkSNjW-v6Gq8hwbJ5yeje1nQZQz75ZaKOlYhY1BfKZu5iMIC-6QjqPb76s=01230740")
////						
////					HttpResponse<JsonNode> response = Unirest.get("http://172.16.1.86:8082/rest/api/2/search")
////				.basicAuth("admin1", "123456")
//						.header("Accept", "application/json").queryString("jql", "project =QJP")
//						.queryString("startAt", startAt).queryString("maxResults", maxResults).asJson();
//
//				// JsonNode responseBody = response.getBody();
//				JsonNode responseBody = response.getBody();
//				Object demo = response.getBody().getObject().get("issues");
//				
//				ObjectMapper objectMapper = new ObjectMapper();
//				
//				
//				JsonNode jsonNode = objectMapper.readTree(response.getBody().toString());
//
//	            // Extract data from JSON and write to CSV
//	            String csvFilePath = "output.csv";
//	            try (FileWriter fileWriter = new FileWriter(csvFilePath);
//	                 CSVPrinter csvPrinter = new CSVPrinter(fileWriter, CSVFormat.DEFAULT.withHeader("Key", "Summary"))) {
//
//	                // Assuming the JSON structure has an array of "issues"
//	                for (JsonNode issueNode : jsonNode.get("issues")) {
//	                    String key = issueNode.get("key").asText();
//	                    String summary = issueNode.get("fields").get("summary").asText();
//	                    csvPrinter.printRecord(key, summary);
//	                }
//	            }
//
//	            System.out.println("CSV file has been created successfully.")
//				
//				
//				
//				
//				
//	            com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(responseBody.toString());
//	            
//	            System.out.println(listOfIsuue.toString());
//	            List<String[]> data = extractData(jsonNode);
//	            
//	            writeCsv("JAVA.csv", data);
//	            
//	            System.out.println("Conversion successful. CSV written to output.csv");
//
//				// System.err.println(listOfIsuue);
//				if (response.getStatus() == 200) {
//					System.out.println(demo);
//					listOfIsuue.add(demo);
//
//					// System.out.println(listOfIsuue.toString());
//					
//
//				} else {
//					System.err.println("Request failed with status: " + response.getStatus());
//					System.err.println("Response body: " + response.getBody());
//				}
//				int total = responseBody.getObject().getInt("total");
//				System.err.println(total);
//				startAt += maxResults;
//
//				if (startAt >= total) {
//					// All data has been retrieved
//					break;
//				}
//			}
//			
//		
//			
//			try {
//	            // Parse JSON
////	            ObjectMapper objectMapper = new ObjectMapper();
////	            JsonNode jsonNode = objectMapper.readTree(jsonInput);
//
//	            // Extract data
//	    //        List<String[]> data = extractData(jsonNode);
//
//	            // Write to CSV
//	   //         writeCsv("output.csv", data);
//
//	            System.out.println("Conversion successful. CSV written to output.csv");
//
//	        } catch (Exception e) {
//	            e.printStackTrace();
//	        }
//			
//			
//			
//			
//			System.out.println(listOfIsuue);
//		//	convertJsonToCsv(listOfIsuue,outputFilePath);
//	
//			System.out.println("CSV written to file: " + outputFilePath);
//		
//		} catch (UnirestException e) {
//			throw new RuntimeException(e);
//		}
//
//		String apiUrl = "https://storage.googleapis.com/upload/storage/v1/b/pega_data/o?uploadType=media&name=JAVACSV.csv";
//		String authorizationHeader = "Bearer ya29.a0AfB_byAbsZunQD56L7yC1mN565XMwPrycg0rdbQmPe5fGSjG3qy16tH02kunN1gC9LyNfNQxIYk9Ne6dMV0Ew8RTar4oThQ0VVUUlk6e8dzK68CUvPlwgDKcDtzsQd1LjwLfgo2S7V1aZoW2cF163hvDcE1bS1F5U2RWHgaCgYKAeASARMSFQHGX2Mi1vmbqq249Xdd63olbtyZ-A0173";
//		String filePath = "D:\\JAVA.csv";
//		String apiResponse = "";
//		try {
//			apiResponse = makeFileUploadRequest(apiUrl, authorizationHeader, filePath);
//			System.out.println("API Response: " + apiResponse);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return apiResponse;
//	}
//
//	
//	private static List<String[]> extractData(com.fasterxml.jackson.databind.JsonNode jsonNode) {
//		System.err.println("<<<<<<<<<<<<<<<"+jsonNode);
//        List<String[]> data = new ArrayList<>();
//
//        // Extract headers
//        Iterator<String> fieldNames = jsonNode.get(0).get("fields").fieldNames();
//        List<String> headers = new ArrayList<>();
//        headers.add("Issue Type");
//        headers.add("Key");
//        headers.add("Summary");
//        headers.add("Assignee");
//        headers.add("Reporter");
//        headers.add("Priority");
//        headers.add("Status");
//        headers.add("Resolution");
//        headers.add("Created");
//        headers.add("Updated");
//        headers.add("Due date");
//        data.add(headers.toArray(new String[0]));
//
//        // Extract issue data
//        com.fasterxml.jackson.databind.JsonNode fields = jsonNode.get(0).get("fields");
//        List<String> issueData = new ArrayList<>();
//        issueData.add(fields.get("issuetype").get("name").asText());
//        issueData.add(jsonNode.get(0).get("key").asText());
//        issueData.add(fields.get("summary").asText());
//        issueData.add(fields.get("assignee").asText());
//        issueData.add(fields.get("reporter").asText());
//        issueData.add(fields.get("priority").get("name").asText());
//        issueData.add(fields.get("status").get("name").asText());
//        issueData.add(fields.get("resolution") != null ? fields.get("resolution").asText() : "");
//        issueData.add(fields.get("created").asText());
//        issueData.add(fields.get("updated").asText());
//        issueData.add(""); // Placeholder for "Due date" since it's not available in the provided JSON
//        data.add(issueData.toArray(new String[0]));
//
//        return data;
//    }
//	
//	private static void writeCsv(String fileName, List<String[]> data) throws Exception {
//        try (CSVWriter writer = new CSVWriter(new FileWriter(fileName))) {
//            writer.writeAll(data);
//        }
//    }
	
	
	
}
