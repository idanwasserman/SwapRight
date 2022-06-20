package iob.UsersAPI;

/*
UserId sample JSON:
{
	"userId":{ 
		"domain":"2022a.demo", 
		"email":"user2@demo.com"
	}
}
*/
public class UserId {

	private String domain;
	private String email;
	
	public UserId() {
	}

	public UserId(String domain, String email) {
		this();
		this.domain = domain;
		this.email = email;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "UserId [domain=" + domain + ", email=" + email + "]";
	}
	
}
