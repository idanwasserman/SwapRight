package iob.data.entities;

import org.springframework.data.annotation.Id;

import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@RedisHash("ACTIVITIES")
public class ActivityEntity implements Serializable {

	private static final long serialVersionUID = -1523526959018522631L;

	private @Id String id; // domain + <DELIMETER> + UUID
	
	private String type; 	
	private String instanceDomain; 	      // CreatedBy -> instanceId
	private String instanceId; 	          // CreatedBy -> instanceId
	private Date createdTimestamp;
	private String userDomain; 		      // invokedBy -> UserId
	private String userEmail; 		      // invokedBy -> UserId
	private Map<String, Object> activityAttributes;
	
	public ActivityEntity() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getInstanceDomain() {
		return instanceDomain;
	}

	public void setInstanceDomain(String instanceDomain) {
		this.instanceDomain = instanceDomain;
	}
	
	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	
	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}
	
	public String getUserDomain() {
		return userDomain;
	}

	public void setUserDomain(String userDomain) {
		this.userDomain = userDomain;
	}
	
	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public Map<String, Object> getActivityAttributes() {
		return activityAttributes;
	}

	public void setActivityAttributes(Map<String, Object> activityAttributes) {
		this.activityAttributes = activityAttributes;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActivityEntity other = (ActivityEntity) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "ActivityEntity [" +
				"id='" + id + '\'' +
				", type='" + type + '\'' +
				", instanceDomain='" + instanceDomain + '\'' +
				", instanceId='" + instanceId + '\'' +
				", createdTimestamp=" + createdTimestamp +
				", userDomain='" + userDomain + '\'' +
				", userEmail='" + userEmail + '\'' +
				", activityAttributes=" + activityAttributes +
				']';
	}
}
