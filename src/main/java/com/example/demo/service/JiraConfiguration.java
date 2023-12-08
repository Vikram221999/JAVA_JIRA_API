package com.example.demo.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.springframework.stereotype.Service;

import com.atlassian.jira.rest.client.api.IssueRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClient;
import com.atlassian.jira.rest.client.api.JiraRestClientFactory;
import com.atlassian.jira.rest.client.api.SearchRestClient;
import com.atlassian.jira.rest.client.api.domain.Issue;
import com.atlassian.jira.rest.client.api.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
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
import com.opencsv.CSVWriter;

//import com.atlassian.jira.rest.client.internal.async.CustomAsynchronousJiraRestClient;
import io.atlassian.util.concurrent.Promise;



@Service
public class JiraConfiguration {

	//This method will get all issue and sent to GCP
	public String getIssuseFromDirectJira() throws InterruptedException, ExecutionException, FileNotFoundException, IOException {
		int startAt = 0;
		int maxResults = 100; // Set a value greater than the total number of issues you expect

		List<Issue> allIssues = new ArrayList<>();
		int total=0;
//      URI jiraUrl = URI.create( "https://qim-dev.atlassian.net");
//      String jiraUsername = "vikram221999@gmail.com";
//      String jiraPassword = "ATATT3xFfGF0FCziPC6apm-HE3MYsCuA41raP6_2eRZfV3d4DUhArH5_h4agsGoaBHj6r7siLxmdyBwZfbxudjAI5T0IMm9W_JdcgTjA-7_Bo1l6puDXe3OfON1aYJUF05qcDbpLUeefbs7rJcK7BXqLL0DE0Gqqr9S9eTgMxRzWvNTYHRo1rEc=CDFED67B";
//			String projectKey = "project =QJP";
			
		
		
		Set<String> fields = new HashSet<>();
		fields.add("summary");
		fields.add("issuetype");
		fields.add("description");
		fields.add("key");
		fields.add("created");
		fields.add("updated");
		fields.add("project");
		fields.add("status");
		
		URI jiraUrl = URI.create("https://someshwara2001dev.atlassian.net/");
		String jiraUsername = "someshwara2001@gmail.com";
		String jiraPassword = "ATATT3xFfGF0JOg8tv3LPA3w8pgXt0fOOXA_dczROHbTnHPas6HNmTqj5zK6-1osBJ5PKWooubp0afmaAbgc6vqtBhde6n8_F2lsAKlM5dFYY2erBJJfIzLM1ZofM6tm-cpSy5RHpb9US0x5jQz3Obm4ikYgli9TnztJ_D0KD9hLzT0L9pztgRw=8E4BDB02";
		//String projectKey = "project =DEMO";
		String jqlQuery = "project = DEMO AND summary is not EMPTY";
		
//		URI jiraUrl = URI.create("http://172.16.1.86:8082");
//		String projectKey = "project =MT";
//		String jiraUsername = "admin1";
//		String jiraPassword = "123456";
		
		int count =0;
		ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JodaModule());
		
		do {
			JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
			JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraUrl, jiraUsername, jiraPassword);

			 Promise<SearchResult> issuesPromise = restClient.getSearchClient().searchJql(jqlQuery, maxResults, startAt, fields);
			
			SearchResult searchResult = issuesPromise.claim();
			total=searchResult.getTotal();
		
			List<Issue> pageIssues = Lists.newArrayList(searchResult.getIssues());

			allIssues.addAll(pageIssues);
			System.out.println(count++);
			System.err.println(startAt);
			startAt += maxResults;
		} while (startAt < total);
		String jsonAllIssues = objectMapper.writeValueAsString(allIssues);
		//System.out.println(jsonAllIssues);
		  String serviceAccountKeyPath = "src/main/resources/files/GCP.json";

	         // Create Google Cloud Storage client with credentials
	         GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(serviceAccountKeyPath));
	         Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

	         // Content of the JSON file as bytes
	         byte[] contentBytes = jsonAllIssues.getBytes(StandardCharsets.UTF_8);

	         // Set the name of your Google Cloud Storage bucket and the desired blob (object) name
	         String bucketName = "pega_data";
	         String blobName = "jirafinal.json";

	         // Create BlobInfo with the specified content type (application/json in this case)
	         BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, blobName))
	                 .setContentType("application/json")
	                 .build();

	         // Upload the JSON content to Google Cloud Storage
	         Blob blob = storage.create(blobInfo, contentBytes);

	         // Print a message indicating the successful upload
	         System.out.println("Issues uploaded to Google Cloud Storage: gs://" + bucketName + "/" + blobName);
		
		return "Done";
	}

	
	public String getIssuseFromDirectJiraCSV() throws InterruptedException, ExecutionException, FileNotFoundException, IOException {
		int startAt = 0;
		int maxResults = 100; // Set a value greater than the total number of issues you expect

		List<Issue> allIssues = new ArrayList<>();
		int total=0;
		String gcsObjectName = "output.csv";
		
		
		
//      URI jiraUrl = URI.create( "https://qim-dev.atlassian.net");
//      String jiraUsername = "vikram221999@gmail.com";
//      String jiraPassword = "ATATT3xFfGF0FCziPC6apm-HE3MYsCuA41raP6_2eRZfV3d4DUhArH5_h4agsGoaBHj6r7siLxmdyBwZfbxudjAI5T0IMm9W_JdcgTjA-7_Bo1l6puDXe3OfON1aYJUF05qcDbpLUeefbs7rJcK7BXqLL0DE0Gqqr9S9eTgMxRzWvNTYHRo1rEc=CDFED67B";
//			String projectKey = "project =QJP";
			
		
		
		Set<String> fields = new HashSet<>();
		fields.add("summary");
		fields.add("issuetype");
		fields.add("description");
		fields.add("key");
		fields.add("created");
		fields.add("updated");
		fields.add("project");
		fields.add("status");
		URI jiraUrl = URI.create("https://someshwara2001dev.atlassian.net/");
		String jiraUsername = "someshwara2001@gmail.com";
		String jiraPassword = "ATATT3xFfGF0JOg8tv3LPA3w8pgXt0fOOXA_dczROHbTnHPas6HNmTqj5zK6-1osBJ5PKWooubp0afmaAbgc6vqtBhde6n8_F2lsAKlM5dFYY2erBJJfIzLM1ZofM6tm-cpSy5RHpb9US0x5jQz3Obm4ikYgli9TnztJ_D0KD9hLzT0L9pztgRw=8E4BDB02";
		//String projectKey = "project =DEMO";
		String jqlQuery = "project = DEMO AND summary is not EMPTY";
		
//		URI jiraUrl = URI.create("http://172.16.1.86:8082");
//		String projectKey = "project =MT";
//		String jiraUsername = "admin1";
//		String jiraPassword = "123456";
		
		int count =0;
		ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JodaModule());
        try (CSVWriter csvWriter = new CSVWriter(new FileWriter(gcsObjectName))) {
			String[] header = { "IssueKey","IssueId","IssueSelf", "Summary", "IssueType","IssueTypeId","IssueTypeSelf","IssueTypeDescription","IssueTypeiconUrl","SubtaskStatus" };
			csvWriter.writeNext(header);
		do {
			JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
			JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraUrl, jiraUsername, jiraPassword);

			 Promise<SearchResult> issuesPromise = restClient.getSearchClient().searchJql(jqlQuery, maxResults, startAt, fields);
			
			SearchResult searchResult = issuesPromise.claim();
			total=searchResult.getTotal();
		
			List<Issue> pageIssues = Lists.newArrayList(searchResult.getIssues());

			allIssues.addAll(pageIssues);
			System.out.println(count++);
			System.err.println(startAt);
			startAt += maxResults;
		} while (startAt < total);
        }
		String jsonAllIssues = objectMapper.writeValueAsString(allIssues);
		//System.out.println(jsonAllIssues);
		  String serviceAccountKeyPath = "src/main/resources/files/GCP.json";

	         // Create Google Cloud Storage client with credentials
	         GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(serviceAccountKeyPath));
	         Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

	         // Content of the JSON file as bytes
	         byte[] contentBytes = jsonAllIssues.getBytes(StandardCharsets.UTF_8);

	         // Set the name of your Google Cloud Storage bucket and the desired blob (object) name
	         String bucketName = "pega_data";
	         String blobName = "jirafinal.json";

	         // Create BlobInfo with the specified content type (application/json in this case)
	         BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, blobName))
	                 .setContentType("application/json")
	                 .build();

	         // Upload the JSON content to Google Cloud Storage
	         Blob blob = storage.create(blobInfo, contentBytes);

	         // Print a message indicating the successful upload
	         System.out.println("Issues uploaded to Google Cloud Storage: gs://" + bucketName + "/" + blobName);
		
		return "Done";
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public String getIssuseFromDirectJira4() throws IOException {
		
		int maxResults = 100;
		int startAt = 0;
		List<Issue> allIssues = new ArrayList<>();
		
        // Authenticate with JIRA
//			 URI jiraUrl = URI.create( "https://someshwara2001dev.atlassian.net/");
//	            String jiraUsername = "someshwara2001@gmail.com";
//	            String jiraPassword = "ATATT3xFfGF0NcsLASySHSOYVlp3ySRyhxPa9txJ5b3YVdaCAEpO9W3CXL1SxvKz5YuPchoUJ0ESwyLFc0a0Pm5rl0C56zPpMEotx5fifhd6POgyU0GD8_lozIZiLyK862CK_Dp36eHsZNmSunGgo9IEczQ4emSOCJN9bA3DgkUX6GLHKoePEds=5409BDE4";
//		String projectKey = "DEMO";
	            
	            URI jiraUrl = URI.create("http://172.16.1.86:8082");
				String projectKey = "project =MT";
				String jiraUsername = "admin1";
				String jiraPassword = "123456";
	            
	            
	             
//            URI jiraUrl = URI.create( "https://qim-dev.atlassian.net");
//            String jiraUsername = "vikram221999@gmail.com";
//            String jiraPassword = "ATATT3xFfGF0FCziPC6apm-HE3MYsCuA41raP6_2eRZfV3d4DUhArH5_h4agsGoaBHj6r7siLxmdyBwZfbxudjAI5T0IMm9W_JdcgTjA-7_Bo1l6puDXe3OfON1aYJUF05qcDbpLUeefbs7rJcK7BXqLL0DE0Gqqr9S9eTgMxRzWvNTYHRo1rEc=CDFED67B";
//				String projectKey = "QJP";
				
				Set<String> fields = new HashSet<>();
            fields.add("issuetype");
            fields.add("summary");
            fields.add("description");
            
            JiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
            JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraUrl, jiraUsername, jiraPassword);
		
		
		while (true) {
			
			Promise<SearchResult> issues = restClient.getSearchClient().searchJql(projectKey);
            SearchResult searchResult = issues.claim();
            // Print the SearchResult
        //    System.out.println("Search Result: " + searchResult);
            // Convert issues to JSON
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JodaModule());
            String jsonIssues = objectMapper.writeValueAsString(searchResult.getIssues());
            List<Issue> pageIssues = Lists.newArrayList(searchResult.getIssues());
			allIssues.addAll(pageIssues);
            
          //  System.out.println(jsonIssues);
            
          
           
            int total =searchResult.getTotal();
            System.err.println(total);
			startAt += maxResults;
			System.out.println(startAt);

			if (startAt >= total) {

				break;
			}
		}
		System.out.println(allIssues.size());
		//System.out.println(allIssues.toString());
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
        String jiraPassword = "ATATT3xFfGF0FCziPC6apm-HE3MYsCuA41raP6_2eRZfV3d4DUhArH5_h4agsGoaBHj6r7siLxmdyBwZfbxudjAI5T0IMm9W_JdcgTjA-7_Bo1l6puDXe3OfON1aYJUF05qcDbpLUeefbs7rJcK7BXqLL0DE0Gqqr9S9eTgMxRzWvNTYHRo1rEc=CDFED67B";
       
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
         String blobName = "jira.json";

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
