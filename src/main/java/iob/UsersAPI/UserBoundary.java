package iob.UsersAPI;

/*
UserBoundary sample JSON:
{ 
	"userId":{
		"domain":"2022a.demo",
		"email":"user@demo.com"
	},
	"role":"PLAYER",
	"username":"Demo User",
	"avatar":"J"
}
*/

public class UserBoundary {
	
	private UserId userId;
	private UserRole role;
	private String username;
	private String avatar;
	
	public UserBoundary() {
	}
	
	public UserBoundary(
			UserId userId,
			UserRole role,
			String username, 
			String avatar) {
		this();
		this.userId = userId;
		this.role = role;
		this.username = username;
		this.avatar = avatar;
	}

	public UserBoundary(NewUserBoundary newUser) {
		this();
		this.setUserId(new UserId("domain", newUser.getEmail()));
		this.role = newUser.getRole();
		this.username = newUser.getUsername();
		this.avatar = newUser.getAvatar();
	}

	public UserBoundary(UserRole role) {
		this.role = role;
	}

	public UserId getUserId() {
		return userId;
	}

	public void setUserId(UserId userId) {
		this.userId = userId;
	}

	public UserRole getRole() {
		return role;
	}

	public void setRole(UserRole role) {
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
	public String toString() {
		return "UserBoundary [userId=" + userId + ", role=" + role + ", username=" + username + ", avatar=" + avatar
				+ "]";
	}
	
}
