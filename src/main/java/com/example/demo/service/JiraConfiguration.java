package com.example.demo.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import com.google.api.client.util.Lists;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
//import com.atlassian.jira.rest.client.internal.async.CustomAsynchronousJiraRestClient;
import io.atlassian.util.concurrent.Promise;



@Service
public class JiraConfiguration {

//	public String getIssuseFromDirectJira() {
//		int startAt = 0;
//		int maxResults = 1000; // Set a value greater than the total number of issues you expect
//
//		List<Issue> allIssues = new ArrayList<>();
//
//		do {
//			URI jiraUrl = URI.create("https://someshwara2001dev.atlassian.net/");
//			String jiraUsername = "someshwara2001@gmail.com";
//			String jiraPassword = "ATATT3xFfGF0NcsLASySHSOYVlp3ySRyhxPa9txJ5b3YVdaCAEpO9W3CXL1SxvKz5YuPchoUJ0ESwyLFc0a0Pm5rl0C56zPpMEotx5fifhd6POgyU0GD8_lozIZiLyK862CK_Dp36eHsZNmSunGgo9IEczQ4emSOCJN9bA3DgkUX6GLHKoePEds=5409BDE4";
//			JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
//			JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraUrl, jiraUsername, jiraPassword);
//
//			Promise<SearchResult> issuesPromise = restClient.getSearchClient().searchJql("project = DEMO");
//			SearchResult searchResult = issuesPromise.claim();
//
//			List<Issue> pageIssues = Lists.newArrayList(searchResult.getIssues());
//			allIssues.addAll(pageIssues);
//
//			startAt += maxResults;
//		} while (startAt < searchResult.getTotal());
//		return null;
//	}

//	public String getIssuseFromDirectJira1() throws JsonProcessingException {
//		
//		int maxResults = 1000;
//		int startAt = 0;
//		List<Issue> allIssues = new ArrayList<>();
//
//		while (true) {
//
//			 // Authenticate with JIRA
//			 URI jiraUrl = URI.create( "https://someshwara2001dev.atlassian.net/");
//		        String jiraUsername = "someshwara2001@gmail.com";
//		        String jiraPassword = "ATATT3xFfGF0NcsLASySHSOYVlp3ySRyhxPa9txJ5b3YVdaCAEpO9W3CXL1SxvKz5YuPchoUJ0ESwyLFc0a0Pm5rl0C56zPpMEotx5fifhd6POgyU0GD8_lozIZiLyK862CK_Dp36eHsZNmSunGgo9IEczQ4emSOCJN9bA3DgkUX6GLHKoePEds=5409BDE4";
//		       // Set<String> fields = "issuetype,subtasks,summary,description";
//		        Iterable<String> fields = Collections.singletonList("issuetype,subtasks,summary,description");
//		        
//		        JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
//		    JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraUrl, jiraUsername, jiraPassword);
// 
//		    SearchRestClient.SearchResultPromise issuesPromise = restClient.getSearchClient()
//	                .searchJql("project = DEMO AND issuetype is not EMPTY", maxResults, startAt, fields);
//
//		    
//		    System.out.println("//////////kjdfghjk/////////////////////////");
//		    // Fetch issues from JIRA
//		    // Example: Fetch all issues from a project
//		    Promise<SearchResult> issues = restClient.getSearchClient().searchJql("project = DEMO AND issuetype is not EMPTY", maxResults, startAt);
//		//    Promise<SearchResult> issues = restClient.getSearchClient().searchJql("project = DEMO", maxResults, startAt,fields);
//		   
//		    SearchResult searchResult = issues.claim();
//		    // Print the SearchResult
//		 //   System.out.println("Search Result: " + searchResult);
//			
//		    // Convert issues to JSON
//		    ObjectMapper objectMapper = new ObjectMapper();
//		    objectMapper.registerModule(new JodaModule());
//		    String jsonIssues = objectMapper.writeValueAsString(searchResult);
//		    System.out.println(jsonIssues);
//			int total =searchResult.getTotal(); 
//					//jsonIssues..getObject().getInt("total");
//			
//			
//			
//			startAt += maxResults;
//			System.out.println(startAt);
//
//			if (startAt >= total) {
//
//				break;
//			}
//		}
//		return null;
//	}
	
	
	public String getIssuseFromDirectJira4() throws IOException {
		
		int maxResults = 1000;
		int startAt = 0;
		List<Issue> allIssues = new ArrayList<>();
		
        // Authenticate with JIRA
//			 URI jiraUrl = URI.create( "https://someshwara2001dev.atlassian.net/");
//	            String jiraUsername = "someshwara2001@gmail.com";
//	            String jiraPassword = "ATATT3xFfGF0NcsLASySHSOYVlp3ySRyhxPa9txJ5b3YVdaCAEpO9W3CXL1SxvKz5YuPchoUJ0ESwyLFc0a0Pm5rl0C56zPpMEotx5fifhd6POgyU0GD8_lozIZiLyK862CK_Dp36eHsZNmSunGgo9IEczQ4emSOCJN9bA3DgkUX6GLHKoePEds=5409BDE4";
//			
	             
            URI jiraUrl = URI.create( "https://qim-dev.atlassian.net");
            String jiraUsername = "vikram221999@gmail.com";
            String jiraPassword = "ATATT3xFfGF0ZI1Vm5icm_avcfOUD5v2VfcY17eDS5HDfGRZtDVLE1OInmjhvjg7KNoPZ-SybB4rggv9Egj2hADZJaspUs8A7wvfRgX0urv6M1xBgj2BvT-Iw_rItNb3jdXVNNY_DURG6q6Kca_ZRHmoj7UhVQeund4S_daStHFGJanzpKD69_c=5E142F00";
//            Set<String> fields = new HashSet<>();
//            fields.add("issueType");
//            fields.add("summary");
//            fields.add("description");
            
            Set<String> fields = new HashSet<>();
            fields.add("issueType");
            fields.add("summary");
            fields.add("description");
            fields.add("created");
            fields.add("updated");
            fields.add("customField1");
            
            JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
            JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraUrl, jiraUsername, jiraPassword);
		
		
		while (true) {
			
			System.out.println("///////////////////////////////////");
			//project = DEMO
            Promise<SearchResult> issues = restClient.getSearchClient().searchJql("project = QJP", maxResults, startAt, fields);
            SearchResult searchResult = issues.claim();
            // Print the SearchResult
            System.out.println("Search Result: " + searchResult);
        	 
            // Convert issues to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JodaModule());
            String jsonIssues = objectMapper.writeValueAsString(searchResult);
            System.out.println(jsonIssues);
            
          
           
            int total =searchResult.getTotal();
            System.err.println(total);
			startAt += maxResults;
			System.out.println(startAt);

			if (startAt >= total) {

				break;
			}
		}
		restClient.close();
		
		return "Done";
		
	}
	
	public String getIssuseFromDirectJira3() throws IOException {
		

		int maxResults = 1000;
		int startAt = 0;
		List<Issue> allIssues = new ArrayList<>();
		
        // Authenticate with JIRA
//			 URI jiraUrl = URI.create( "https://someshwara2001dev.atlassian.net/");
//	            String jiraUsername = "someshwara2001@gmail.com";
//	            String jiraPassword = "ATATT3xFfGF0NcsLASySHSOYVlp3ySRyhxPa9txJ5b3YVdaCAEpO9W3CXL1SxvKz5YuPchoUJ0ESwyLFc0a0Pm5rl0C56zPpMEotx5fifhd6POgyU0GD8_lozIZiLyK862CK_Dp36eHsZNmSunGgo9IEczQ4emSOCJN9bA3DgkUX6GLHKoePEds=5409BDE4";
//			
	             
            URI jiraUrl = URI.create( "https://qim-dev.atlassian.net");
            String jiraUsername = "vikram221999@gmail.com";
            String jiraPassword = "ATATT3xFfGF0ZI1Vm5icm_avcfOUD5v2VfcY17eDS5HDfGRZtDVLE1OInmjhvjg7KNoPZ-SybB4rggv9Egj2hADZJaspUs8A7wvfRgX0urv6M1xBgj2BvT-Iw_rItNb3jdXVNNY_DURG6q6Kca_ZRHmoj7UhVQeund4S_daStHFGJanzpKD69_c=5E142F00";
	        JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
            JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraUrl, jiraUsername, jiraPassword);
		
		
		while (true) {
			
			System.out.println("///////////////////////////////////");
			//project = DEMO
            Promise<SearchResult> issues = restClient.getSearchClient().searchJql("project = QJP", maxResults, startAt, null);
            SearchResult searchResult = issues.claim();
            // Print the SearchResult
         //   System.out.println("Search Result: " + searchResult);
        	
            // Convert issues to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JodaModule());
            String jsonIssues = objectMapper.writeValueAsString(searchResult);
            System.out.println(jsonIssues);
            
            
            String serviceAccountKeyPath = "src/main/resources/files/GCP.json";

         // Create Google Cloud Storage client with credentials
         GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(serviceAccountKeyPath));
         Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

         // Content of the JSON file as bytes
         byte[] contentBytes = jsonIssues.getBytes(StandardCharsets.UTF_8);

         // Set the name of your Google Cloud Storage bucket and the desired blob (object) name
         String bucketName = "pega_data";
         String blobName = "jira-issues.json";

         // Create BlobInfo with the specified content type (application/json in this case)
         BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, blobName))
                 .setContentType("application/json")
                 .build();

         // Upload the JSON content to Google Cloud Storage
         Blob blob = storage.create(blobInfo, contentBytes);

         // Print a message indicating the successful upload
         System.out.println("Issues uploaded to Google Cloud Storage: gs://" + bucketName + "/" + blobName);
                   
           
            int total =searchResult.getTotal();
            System.err.println(total);
			startAt += maxResults;
			System.out.println(startAt);

			if (startAt >= total) {

				break;
			}
		}
		restClient.close();
		
		return "Done";
		
		
	}
	

		
		

}
