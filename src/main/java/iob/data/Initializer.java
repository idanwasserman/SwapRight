package iob.data;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import iob.InstancesAPI.InstanceBoundary;
import iob.InstancesAPI.InstanceId;
import iob.UsersAPI.NewUserBoundary;
import iob.UsersAPI.UserBoundary;
import iob.UsersAPI.UserRole;

@Component
@Profile("manual-testing")
public class Initializer implements CommandLineRunner {
	
	private RestTemplate restTemplate;
	private String url;
	private int port;
	private String domainName;
	
	String postInstanceUrl;
	String createUserUrl;
	String invokeActivityUrl;
	String adminEmail;
	String managerEmail;
	String playerEmail;

	
	@Value("${server.port:8080}")
	public void setPort(int port) {
		this.port = port;
	}
	
	@Value("${spring.application.name:demo.domain.name}")
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
	@PostConstruct
	public void init() {
		this.restTemplate = new RestTemplate();
		this.url = "http://localhost:" + port + "/iob/instances";
		this.postInstanceUrl = this.url + "/{userDomain}/{userEmail}";
		this.createUserUrl = "http://localhost:" + port + "/iob/users";
		this.invokeActivityUrl = "http://localhost:" + port + "/iob/activities";
		this.adminEmail = "admin@mail.com";
		this.managerEmail = "manager@mail.com";
		this.playerEmail = "player@mail.com";
	}

	@Override
	public void run(String... args) throws Exception {
		createUsersAndInstances();
	}

	private void createUsersAndInstances() {
		// Create user of each role
		// Create ADMIN user
		NewUserBoundary newAdminUser = new NewUserBoundary(adminEmail, UserRole.ADMIN, "demoAdmin", "A");
		System.err.println(this.restTemplate
				.postForObject(createUserUrl, newAdminUser, UserBoundary.class));
		
		// Create MANAGER user
		NewUserBoundary newManagerUser = new NewUserBoundary(managerEmail, UserRole.MANAGER, "demoManager", "M");
		System.err.println(
				this.restTemplate.postForObject(createUserUrl, newManagerUser, UserBoundary.class));
		
		// Create PLAYER user
		NewUserBoundary newPlayerUser = new NewUserBoundary(playerEmail, UserRole.PLAYER, "demoPlayer", "P");
		System.err.println(
				this.restTemplate.postForObject(createUserUrl, newPlayerUser, UserBoundary.class));
		
		// Create HELPER instance for invoking activities
		InstanceBoundary instance = new InstanceBoundary("ADMIN", "adminInstance", true, null, null, null);
		instance.setInstanceId(new InstanceId(domainName, "ADMIN"));
		InstanceBoundary retval = this.restTemplate
				.postForObject(postInstanceUrl, instance, InstanceBoundary.class, this.domainName, this.managerEmail);
		System.err.println(retval);
		
		// Create user instance for each user
		Map<String, Object> attributes = new HashMap<>();
		attributes.put("phone", "05424424242");
		attributes.put("profileUrl", "adminprofileurl.jpg");
		attributes.put("lat", "32.22");
		attributes.put("lng", "52.365");
		InstanceBoundary userInstance = new InstanceBoundary("USER", "2022a.bar.yacobi&&admin@mail.com", true, null, null, attributes);
		System.err.println(this.restTemplate
				.postForObject(postInstanceUrl, userInstance, InstanceBoundary.class, this.domainName, this.managerEmail));
		
		attributes.put("profileUrl", "managerprofileurl.jpg");
		userInstance.setName("2022a.bar.yacobi&&manager@mail.com");
		System.err.println(this.restTemplate
				.postForObject(postInstanceUrl, userInstance, InstanceBoundary.class, this.domainName, this.managerEmail));
		
		attributes.put("profileUrl", "playerprofileurl.jpg");
		userInstance.setName("2022a.bar.yacobi&&player@mail.com");
		System.err.println(this.restTemplate
				.postForObject(postInstanceUrl, userInstance, InstanceBoundary.class, this.domainName, this.managerEmail));
	
	}

}
