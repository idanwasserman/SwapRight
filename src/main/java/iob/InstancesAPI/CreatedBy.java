package iob.InstancesAPI;

import iob.UsersAPI.UserId;

/*
 CreatedBy sample JSON:
 {
 	"createdBy":{
 		"userId":{
 			"domain":"2022a.demo",
 			"email":"user2@demo.com"
 		}
 	}
 }
 */
public class CreatedBy {
	
	private UserId userId;
	
	public CreatedBy() {
	}

	public CreatedBy(UserId userId) {
		this();
		this.userId = userId;
	}

	public UserId getUserId() {
		return userId;
	}

	public void setUserId(UserId userId) {
		this.userId = userId;
	}

	@Override
	public String toString() {
		return "CreatedBy [userId=" + userId + "]";
	}
	
}
