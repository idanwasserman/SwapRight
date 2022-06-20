package iob.data.entities;

import java.io.Serializable;
import java.util.Objects;

import org.springframework.data.annotation.Id;

import org.springframework.data.redis.core.RedisHash;

@RedisHash("USERS")
public class UserEntity implements Serializable {

	private static final long serialVersionUID = 7567786999122154509L;

	@Id
	private String id; // domain + <DELIMETER> + email
	
	private UserRoleEntity role;
	private String username;
	private String avatar;
	
	public UserEntity() {
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public UserRoleEntity getRole() {
		return role;
	}
	
	public void setRole(UserRoleEntity role) {
		this.role = role;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
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
		UserEntity other = (UserEntity) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "UserEntity [" +
				"id='" + id + '\'' +
				", role='" + role + '\'' +
				", username='" + username + '\'' +
				", avatar='" + avatar + '\'' +
				']';
	}

}
