package iob;

import static org.assertj.core.api.Assertions.assertThat;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;

import iob.InstancesAPI.InstanceBoundary;


@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class InstancesTests {
	private RestTemplate restTemplate;
	private String url;
	private int port;
	private String userDomain;
	private String userEmail;
	
	@LocalServerPort
	public void setPort(int port) {
		this.port = port;
	}
	
	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + this.port;
		this.userDomain = "2022a.bar.yacobi";
		this.userEmail = "demo@gmail.com";
	}
	
	@AfterEach
	public void teardown() {
		this.restTemplate
			.delete(this.url + "/iob/admin/instances/" + this.userDomain + "/" + this.userEmail);
	}
	
	@Test
	@DisplayName("test that get all instances when the database is cleaned returns an empty array")
	public void testGetAllInstancesReturnsEmptyArray() throws Exception {
		InstanceBoundary[] arr = this.restTemplate.getForObject(
				this.url + "/iob/instances/{userDomain}/{userEmail}", 
				InstanceBoundary[].class,
				this.userDomain,
				this.userEmail);
		
		assertThat(arr).isNotNull().isEmpty();
	}
	
	@Test
	@DisplayName("test that instances contains one instance exactly after creating only one")
	public void testInstancesContainsOneInstanceAfterCreatingOne() throws Exception {
		InstanceBoundary instance = new InstanceBoundary();
		instance.setName("demoName");
		instance.setType("PLAYER");

		String currTestUrl = this.url + "/iob/instances/{userDomain}/{userEmail}";
		
		this.restTemplate.postForObject(
				currTestUrl, 
				instance, 
				InstanceBoundary.class,
				this.userDomain,
				this.userEmail);
		
		InstanceBoundary[] arr = this.restTemplate.getForObject(
				currTestUrl, 
				InstanceBoundary[].class,
				this.userDomain,
				this.userEmail);
		
		assertThat(arr).hasSize(1);
	}

}
	
