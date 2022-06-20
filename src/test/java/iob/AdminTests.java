package iob;


import javax.annotation.PostConstruct;

import org.junit.jupiter.api.AfterEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.web.client.RestTemplate;



@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class AdminTests {
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
		this.userEmail = "admin@gmail.com";
	}
	
	@AfterEach
	public void teardown() {
		this.restTemplate
			.delete(this.url + "/iob/admin/instances/" + this.userDomain + "/" + this.userEmail);
	}


}
	
