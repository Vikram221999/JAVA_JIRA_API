
package com.example.demo.service;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Service;

import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import io.atlassian.util.concurrent.Promise;

import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

import com.atlassian.jira.rest.client.api.JiraRestClientFactory;





@Service
public class LinkIssue {

	public String createdIssuse(String payload) throws UnirestException, JsonMappingException, JsonProcessingException {
		JSONObject jsonObject = new JSONObject(payload);
		JSONObject fields = jsonObject.getJSONObject("fields");
		String summary = fields.getString("summary");
		String Key = jsonObject.getString("key");
		System.out.println("Summary: " + summary);
		System.out.println("key: " + Key);

		String jiraBaseUrl = "https://qim-dev.atlassian.net";
		String projectKey = "QJP";
		String username = "vikram221999@gmail.com";
		String apiToken = "ATATT3xFfGF0ZI1Vm5icm_avcfOUD5v2VfcY17eDS5HDfGRZtDVLE1OInmjhvjg7KNoPZ-SybB4rggv9Egj2hADZJaspUs8A7wvfRgX0urv6M1xBgj2BvT-Iw_rItNb3jdXVNNY_DURG6q6Kca_ZRHmoj7UhVQeund4S_daStHFGJanzpKD69_c=5E142F00";

		// Build the Jira API search URL
		String apiUrl = jiraBaseUrl + "/rest/api/3/search";
		String jqlQuery = "project = " + projectKey + " AND summary ~ \"" + summary + "\" ORDER BY Rank ASC";
		// String jqlQuery ="project = QJP AND summary ~ "+"\"Ford EcoSport Premium
		// Transmission issue in Edge 2020\""+" ORDER BY Rank ASC";
		// String jqlQuery = "project = QJP AND summary ~ "+summary+" ORDER BY Rank
		// ASC";
		// String jqlQuery ="project = QJP AND summary ~ "\"Ford EcoSport Premium
		// Transmission issue in Edge 2020\"" ORDER BY Rank ASC";
		// project = QJP AND summary ~ projectKey ORDER BY Rank ASC
//        String jqlQuery = "project=" + projectKey;
		String fields1 = "issuetype,summary,description";

		com.mashape.unirest.http.HttpResponse<com.mashape.unirest.http.JsonNode> response = Unirest.get(apiUrl)
				.basicAuth(username, apiToken).header("Accept", "application/json").queryString("jql", jqlQuery)
				.queryString("fields", fields1).asJson();

		System.out.println(response.getBody());
		com.mashape.unirest.http.JsonNode responseBody = response.getBody();
		ObjectMapper objectMapper = new ObjectMapper();
		Object demo = response.getBody().getObject().get("issues");

		// System.out.println(response.getBody().toString());
		com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(demo.toString());

		for (com.fasterxml.jackson.databind.JsonNode issueNode : jsonNode) {
			String key1 = issueNode.path("key").asText();
			String summary1 = issueNode.path("fields").path("summary").asText();
			System.out.println(key1 + "   -->   " + summary1);

			if (key1 != Key) {
				linksIssuse(Key, key1);
			}
		}

		return "Summary: " + summary;
	}

	public String linksIssuse(String issueKey1, String issueKey2) {

		String jiraBaseUrl = "https://qim-dev.atlassian.net";
		String username = "vikram221999@gmail.com";
		String apiToken = "ATATT3xFfGF0ZI1Vm5icm_avcfOUD5v2VfcY17eDS5HDfGRZtDVLE1OInmjhvjg7KNoPZ-SybB4rggv9Egj2hADZJaspUs8A7wvfRgX0urv6M1xBgj2BvT-Iw_rItNb3jdXVNNY_DURG6q6Kca_ZRHmoj7UhVQeund4S_daStHFGJanzpKD69_c=5E142F00";
		String projectKey = "QJP";
//		String issueKey1 = "QJP-38";
//		String issueKey2 = "QJP-37";

		// Step 1: Get Authentication Token
		String result = null;
		String authHeader = getAuthHeader(username, apiToken);
		try {
			HttpClient client = HttpClients.createDefault();

			// Build the request to link issues
			String linkEndpoint = jiraBaseUrl + "/rest/api/2/issueLink";
			String requestBody = "{" + "\"type\": {\"name\": \"Relates\"}," + "\"inwardIssue\": {\"key\": \""
					+ issueKey1 + "\"}," + "\"outwardIssue\": {\"key\": \"" + issueKey2 + "\"}" + "}";

			HttpPost request = new HttpPost(linkEndpoint);
			request.setHeader(HttpHeaders.AUTHORIZATION, authHeader);
			request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");
			request.setEntity(new StringEntity(requestBody));

			// Send the request and get the response
			HttpResponse response = client.execute(request);

			// Print the response
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String line;
			StringBuilder responseContent = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				responseContent.append(line);
			}

			result = "Response Code: " + response.getStatusLine().getStatusCode();
			System.out.println("Response Code: " + response.getStatusLine().getStatusCode());
			System.out.println("Response Body: " + responseContent.toString());

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	private static String getAuthHeader(String username, String apiToken) {
		String credentials = username + ":" + apiToken;
		return "Basic " + java.util.Base64.getEncoder().encodeToString(credentials.getBytes());
	}
	
	public String getIssuseFromDirectJira() {
		try {
			System.out.println("///////////////////////////////////");
            // Authenticate with JIRA
			 URI jiraUrl = URI.create( "https://someshwara2001dev.atlassian.net/");
	            String jiraUsername = "someshwara2001@gmail.com";
	            String jiraPassword = "ATATT3xFfGF0NcsLASySHSOYVlp3ySRyhxPa9txJ5b3YVdaCAEpO9W3CXL1SxvKz5YuPchoUJ0ESwyLFc0a0Pm5rl0C56zPpMEotx5fifhd6POgyU0GD8_lozIZiLyK862CK_Dp36eHsZNmSunGgo9IEczQ4emSOCJN9bA3DgkUX6GLHKoePEds=5409BDE4";
			
	            int startAt = 0;
	    		int maxResults = 1000; 
//            URI jiraUrl = URI.create( "https://qim-dev.atlassian.net");
//            String jiraUsername = "vikram221999@gmail.com";
//            String jiraPassword = "ATATT3xFfGF0ZI1Vm5icm_avcfOUD5v2VfcY17eDS5HDfGRZtDVLE1OInmjhvjg7KNoPZ-SybB4rggv9Egj2hADZJaspUs8A7wvfRgX0urv6M1xBgj2BvT-Iw_rItNb3jdXVNNY_DURG6q6Kca_ZRHmoj7UhVQeund4S_daStHFGJanzpKD69_c=5E142F00";
            JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
            JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraUrl, jiraUsername, jiraPassword);
 
            System.out.println("//////////kjdfghjk/////////////////////////");
            // Fetch issues from JIRA
            // Example: Fetch all issues from a project
            Promise<SearchResult> issues = restClient.getSearchClient().searchJql("project = DEMO", maxResults, startAt, null);
            SearchResult searchResult = issues.claim();
            // Print the SearchResult
         //   System.out.println("Search Result: " + searchResult);
        	
            // Convert issues to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JodaModule());
            String jsonIssues = objectMapper.writeValueAsString(searchResult);
            System.out.println(jsonIssues);
 
//            // Authenticate with Google Cloud Storage
//            String serviceAccountKeyPath = "/path/to/your-service-account-key.json";
//            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(serviceAccountKeyPath));
//            Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
// 
//            // Upload JSON issues to Google Cloud Storage
//            byte[] contentBytes = jsonIssues.getBytes(StandardCharsets.UTF_8);
//            
//            String bucketName = "your-bucket-name";
//            String blobName = "jira-issues.json";
//
//            // Create BlobInfo with the specified content type
//            BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, blobName))
//                    .setContentType("application/json")
//                    .build();
//
//            // Create the Blob in Google Cloud Storage
//            Blob blob = storage.create(blobInfo, contentBytes);
//           // Blob blob = storage. .create("jira-issues.json", contentBytes, "application/json");
// 
//            System.out.println("Issues uploaded to Google Cloud Storage: gs://your-bucket-name/jira-issues.json");
// 
//            // Close the JIRA client
//            restClient.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
		return "Success";
	}
	
	
	

}
