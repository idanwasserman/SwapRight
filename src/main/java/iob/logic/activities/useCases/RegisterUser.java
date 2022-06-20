package iob.logic.activities.useCases;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import iob.ActivitiesAPI.ActivityBoundary;
import iob.InstancesAPI.CreatedBy;
import iob.InstancesAPI.InstanceBoundary;
import iob.InstancesAPI.Location;
import iob.UsersAPI.UserId;
import iob.UsersAPI.UserRole;
import iob.logic.activities.AbstractUseCase;
import iob.logic.activities.Attributes;
import iob.logic.activities.InstancesTypes;

@Component("RegisterUser")
public class RegisterUser extends AbstractUseCase {

	@Override
	public Object invoke(ActivityBoundary activity) {
		Map<String, Object> activityAttributes = this.checkActivityAttributes(activity);
		
		// User's details
		String userDomain = activity.getInvokedBy().getUserId().getDomain();
		String userEmail = activity.getInvokedBy().getUserId().getEmail();

		// Create user instance and return the result
		InstanceBoundary userInstance = new InstanceBoundary(
				InstancesTypes.USER.name(), 
				this.converter.createUniqueId(userDomain, userEmail), // instance name
				true, 
				new CreatedBy(new UserId(userDomain, userEmail)), 
				getLocation(activityAttributes), 
				extractDetails(activityAttributes));
		
		// Convert user role to MANAGER before creating the instance
		this.convertUser(userDomain, userEmail, UserRole.MANAGER);
		
		userInstance = this.instancesService.createInstance(userDomain, userEmail, userInstance);
		
		// Convert user role to PLAYER after creating the instance
		this.convertUser(userDomain, userEmail, UserRole.PLAYER);
		
		return userInstance;
	}

	private Map<String, Object> extractDetails(Map<String, Object> attributes) {
		Map<String, Object> extraDetails = new HashMap<>();
		
		// Extra details for users
		extraDetails.put(Attributes.phone.name(), 
				this.getStringAttribute(attributes, Attributes.phone.name()));
		
		extraDetails.put(Attributes.pictureUrl.name(), // picture URL can be null
				attributes.get(Attributes.pictureUrl.name()));
				
		return extraDetails;
	}

	private Location getLocation(Map<String, Object> attributes) {
		final double DEFAULT_LAT = 32.115139;
		final double DEFAULT_LNG = 34.817804;
		return new Location(
				(double)this.getNumberAttribute(attributes, Attributes.lat.name(), DEFAULT_LAT), 
				(double)this.getNumberAttribute(attributes, Attributes.lng.name(), DEFAULT_LNG));
	}

}
