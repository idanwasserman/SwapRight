package iob.ActivitiesAPI;

import java.util.Objects;

/*
InstanceId sample JSON:
{
	"instanceId":{ 
		"domain":"2022a.demo", 
		"id":"352" 
	}
}
*/
public class ActivityId {
	
	private String domain;
	private String id;
	
	public ActivityId() {
	}

	public ActivityId(String domain, String id) {
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
	public int hashCode() {
		return Objects.hash(domain, id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActivityId other = (ActivityId) obj;
		return domain.equals(other.domain) && id.equals(other.id);
	}
	
}