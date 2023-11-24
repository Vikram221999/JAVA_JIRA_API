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
				//System.out.println(response.getBody());
				ObjectMapper objectMapper = new ObjectMapper();
				objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
				
                Object demo = response.getBody().getObject().get("issues");
                String jsonString = objectMapper.writeValueAsString(demo);
                
                JsonFactory jsonFactory = new JsonFactory();
                JsonParser jsonParser = jsonFactory.createParser(jsonString);

               // ObjectMapper objectMapper = new ObjectMapper();

                List<Map<String, Object>> resultList = new ArrayList<>();

                while (jsonParser.nextToken() != JsonToken.END_ARRAY) {
                    if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
                        Map<String, Object> data = objectMapper.readValue(jsonParser, new TypeReference<Map<String, Object>>(){});
                        resultList.add(data);
                    }
                }

                // Process resultList as needed
                System.out.println(resultList);
                
                

                if (response.getStatus() == 200) {
                   System.out.println(demo);
                    listOfIsuue.add(jsonString);
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

            try (CSVWriter writer = new CSVWriter(new FileWriter(outputFilePath))) {
                // Write header
                String[] header = {"IssueKey", "Summary", "Description" /* Add other field names as needed */};
                writer.writeNext(header);

                // Iterate through the issues and write data to CSV
                for (Object issue : listOfIsuue) {
                	 ObjectMapper objectMapper = new ObjectMapper();
                	    Map<String, Object> issueMap = objectMapper.convertValue(issue, Map.class);

                	    String issueKey = issueMap.get("key").toString();

                	    // Assuming "fields" is an object within each issue
                	    Map<String, Object> fields = (Map<String, Object>) issueMap.get("fields");

                	    // Assuming "summary" and "description" are fields within the "fields" object
                	    String summary = fields.get("summary").toString();
                	    String description = fields.get("description").toString();
                    String[] data = {issueKey, summary, description /* Add other values as needed */};
                    writer.writeNext(data);
                }

                System.out.println("CSV written to file: " + outputFilePath);
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (UnirestException e) {
            throw new RuntimeException(e);
        }

        // Additional code for file upload...
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

	
	
	
}
