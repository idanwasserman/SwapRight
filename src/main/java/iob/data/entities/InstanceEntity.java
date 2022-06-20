package iob.data.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.GeoIndexed;
import org.springframework.data.redis.core.index.Indexed;

/*
 * INSTANCES
 * ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
 * INSTANCE_ID  | TYPE          | NAME         | ACTIVE   | CREATED_TIMESTAMP | USER_DOMAIN  | USER_DOMAIN  | USER_EMAIL   | LAT    | LNG    | INSTANCE_ATTRIBUTES | PARENT       |    
 * VARCHAR(255) | VARCHAR(255)  | VARCHAR(255) | BOOLEAN  | TIMESTAMP         | VARCHAR(255) | VARCHAR(255) | VARCHAR(255) | DOUBLE | DOUBLE | CLOB                | VARCHAR(255) |      
 * <PK>         |               |              |          |                   |              |              |              |        |        |                     | <FK>         |
 */ 

//@Entity
//@Table(name="INSTANCES")
@RedisHash("INSTANCES")
public class InstanceEntity implements Serializable {

	private static final long serialVersionUID = 5370671946156806585L;

	private @Id String id; // domain + <DELIMETER> + UUID
	
	private @Indexed String type;
	private @Indexed String name;
	private @Indexed boolean active;
	
	private Date createdTimestamp;
	private String userDomain; 		// CreatedBy -> UserId -> UserDomain
	private String userEmail; 		// CreatedBy -> UserId -> UserEmail
	
	private double lat; // Location -> lat
	private double lng; // Location -> lng
	
	private @GeoIndexed Point point;
	
	private Map<String, Object> instanceAttributes;
	
//	private InstanceEntity parent;
//	private Set<InstanceEntity> children;
	
	private Set<String> parentsIds;
	private Set<String> childrenIds;
	
	public InstanceEntity() {
//		this.children = new HashSet<>();
		this.parentsIds = new HashSet<>();
		this.childrenIds = new HashSet<>();
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
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

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public Map<String, Object> getInstanceAttributes() {
		return instanceAttributes;
	}

	public void setInstanceAttributes(Map<String, Object> instanceAttributes) {
		this.instanceAttributes = instanceAttributes;
	}
	
	public Set<String> getParentsIds() {
		return parentsIds;
	}

	public void setParentsIds(Set<String> parentsIds) {
		this.parentsIds = parentsIds;
	}

	public Set<String> getChildrenIds() {
		return childrenIds;
	}

	public void setChildrenIds(Set<String> childrenIds) {
		this.childrenIds = childrenIds;
	}

	public void addParent(String parentId) {
		this.parentsIds.add(parentId);
	}
	
	public void addChild(String childId) {
		this.childrenIds.add(childId);
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
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
		InstanceEntity other = (InstanceEntity) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "InstanceEntity [id=" + id + ", type=" + type + ", name=" + name + ", active=" + active
				+ ", createdTimestamp=" + createdTimestamp + ", userDomain=" + userDomain + ", userEmail=" + userEmail
				+ ", lat=" + lat + ", lng=" + lng + ", instanceAttributes=" + instanceAttributes 
				+ ", parents size: " + parentsIds.size() + ", children size: " + childrenIds.size() + "]";
	}

}
