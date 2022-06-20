package iob.ActivitiesAPI;

import iob.UsersAPI.UserId;

public class InvokedBy {
	
	private UserId userId;
	
	public InvokedBy() {
	}

	public InvokedBy(UserId userId) {
		this();
		this.userId = userId;
	}

	public UserId getUserId() {
		return userId;
	}

	public void setUserId(UserId userId) {
		this.userId = userId;
	}

}
