package iob.logic;

import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import iob.ActivitiesAPI.ActivityBoundary;
import iob.InstancesAPI.InstanceBoundary;
import iob.InstancesAPI.InstanceId;
import iob.UsersAPI.UserBoundary;
import iob.UsersAPI.UserId;
import iob.UsersAPI.UserRole;
import iob.logic.exceptions.EmptyFieldException;
import iob.logic.exceptions.NotAcceptableException;


@Component
public class Validator {
	
	private static final String TYPE = "Type";
	private static final String EMAIL = "Email";
	private static final String ID = "ID";
	private static final String NAME = "Name";
	private static final String USERNAME = "Username";
	private static final String AVATAR = "Avatar";
	private static final String INSTANCE_DOMAIN = "Instance domain";
	private static final String USER_DOMAIN = "User domain";
	private static final String EMAIL_PATTERN = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";

	public void checkActivityValidity(ActivityBoundary activity) {
		checkStringValidity(activity.getType(), TYPE); 	// Check activity's type
		if (activity.getInstance() != null) { 			// Check activity's instance
			checkInstanceIdValidity(activity.getInstance().getInstanceId());
		} else {
			throw new EmptyFieldException("Instance is missing");
		}
		if (activity.getInvokedBy() != null) {			// Check activity's invokedBy
			checkUserIdValidity(activity.getInvokedBy().getUserId());
		} else {
			throw new EmptyFieldException("Invoked by is missing");
		}
	}
	
	private void checkUserIdValidity(UserId userId) {
		if (userId == null) {
			throw new EmptyFieldException("User Id is missing");
		}
		checkStringValidity(userId.getDomain(), USER_DOMAIN);
		checkStringValidity(userId.getEmail(), EMAIL);
		checkEmailValidity(userId.getEmail());
	}

	private void checkInstanceIdValidity(InstanceId instanceId) {
		if (instanceId == null) {
			throw new EmptyFieldException("Instance Id is missing");
		}
		checkStringValidity(instanceId.getDomain(), INSTANCE_DOMAIN);
		checkStringValidity(instanceId.getId(), ID);
	}

	public void checkInstanceValidity(InstanceBoundary instance) {
		checkStringValidity(instance.getType(), TYPE);		// Check instance's type
		checkStringValidity(instance.getName(), NAME);		// Check instance's name
		if (instance.getCreatedBy() != null) {				// Check instance's createdBy
			checkUserIdValidity(instance.getCreatedBy().getUserId());
		} else {
			throw new EmptyFieldException("Created by is missing");
		}
	}

	public void checkStringValidity(String str, String field) {
		if (str == null || str.isEmpty()) {
			throw new EmptyFieldException(field + " is missing");
		}
	}

	public void checkUserValidity(UserBoundary user) {
		checkUserIdValidity(user.getUserId()); 				// Check user's id
		checkStringValidity(user.getUsername(), USERNAME); 	// Check user's username
		checkStringValidity(user.getAvatar(), AVATAR); 		// Check user's avatar
	}
	
	private void checkEmailValidity(String email) {
		String regex = EMAIL_PATTERN;
		Pattern pat = Pattern.compile(regex);
		if (!pat.matcher(email).matches()) {
			throw new NotAcceptableException("Invalid email");
		}
	}
	
	public boolean isValidUserRole(String role) {
		try {
			UserRole.valueOf(role);
			return true;
		} catch (Exception e) {
			throw new NotAcceptableException("Invalid user role");
		}
	}
	
}
