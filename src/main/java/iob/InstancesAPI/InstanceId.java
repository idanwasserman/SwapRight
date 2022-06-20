package iob.InstancesAPI;

/*
InstanceId sample JSON:
{ 
	"domain":"2022a.demo", 
	"id":"352" 
}
*/
public class InstanceId {
	
	private String domain;
	private String id;
	
	public InstanceId() {
	}

	public InstanceId(String domain, String id) {
		this();
		this.domain = domain;
		this.id = id;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "InstanceId [domain=" + domain + ", id=" + id + "]";
	}
	
}
