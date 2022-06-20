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


@Component("CreatePost")
public class CreatePost extends AbstractUseCase {

	@Override
	public Object invoke(ActivityBoundary activity) {
		this.checkActivityAttributes(activity);
		
		// User's details
		String userDomain = activity.getInvokedBy().getUserId().getDomain();
		String userEmail = activity.getInvokedBy().getUserId().getEmail();
		String avatar = this.userseService.login(
				activity.getInvokedBy().getUserId().getDomain(), 
				activity.getInvokedBy().getUserId().getEmail()).getAvatar();
		
		// Create post with details in activity attributes
		InstanceBoundary post = createPostInstance(activity, userDomain, userEmail, avatar);
		
		// Get user's instance
		InstanceBoundary user = getUserInstance(activity);
		
		// Convert user role to MANAGER before creating the post
		this.convertUser(userDomain, userEmail, UserRole.MANAGER);
		
		try {
			// Save post in DB
			post = this.instancesService.createInstance(userDomain, userEmail, post);
			
			// Bind post to item & user to post
			this.bindInstances(post, user);
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			// Convert user role back to PLAYER
			this.convertUser(userDomain, userEmail, UserRole.PLAYER);
		}
		
		return post;
	}

	private InstanceBoundary createPostInstance(
			ActivityBoundary activity, 
			String userDomain, 
			String userEmail,
			String avatar) {	
		// Create post instance
		return new InstanceBoundary(
				InstancesTypes.POST.name(), 
				this.getStringAttribute(activity.getActivityAttributes(), Attributes.name.name()), 
				true, 
				new CreatedBy(new UserId(userDomain, userEmail)), 
				getLocationFromAttributesOrElseUser(activity), 
				getPostInstanceAttributes(activity.getActivityAttributes(), avatar));

	}

	/**
	 * Try to retrieve location from activity attributes 
	 * if user wants the post to have other location than user's location.
	 * 
	 * If user didn't mention location in activity attributes
	 * the post will have the same location as its user.
	 * @param activity that invoked
	 * @return location for instance
	 */
	private Location getLocationFromAttributesOrElseUser(ActivityBoundary activity) {
		Location location = new Location();
		try {
			location.setLat((double) activity.getActivityAttributes().get(Attributes.lat.name()));
			location.setLng((double) activity.getActivityAttributes().get(Attributes.lng.name()));
		} catch (Exception e) {
			InstanceBoundary userInstance = this.getUserInstance(activity);
			location.setLat(userInstance.getLocation().getLat());
			location.setLng(userInstance.getLocation().getLng());
		}
		return location;
	}

	private Map<String, Object> getPostInstanceAttributes(
			Map<String, Object> activityAttributes, String avatar) {
		
		// Get post's attributes and pack them
		Map<String, Object> instanceAttributes = new HashMap<>();
		
		instanceAttributes.put(Attributes.posterNickname.name(), avatar);
		
		instanceAttributes.put(Attributes.category.name(), 
				this.getStringAttribute(activityAttributes, Attributes.category.name()));
		
		instanceAttributes.put(Attributes.description.name(), 
				this.getStringAttribute(activityAttributes, Attributes.description.name()));
		
		instanceAttributes.put(Attributes.pictureUrl.name(), 
				this.getStringAttribute(activityAttributes, Attributes.pictureUrl.name()));
		
		instanceAttributes.put(Attributes.amount.name(), 
				this.getNumberAttribute(activityAttributes, Attributes.amount.name(), 1));
		
		instanceAttributes.put(Attributes.isGive.name(), 
				activityAttributes.get(Attributes.isGive.name()) == null ? 
						true : activityAttributes.get(Attributes.isGive.name()));
		
		instanceAttributes.put(Attributes.comments.name(), "Comments: ");
		
		return instanceAttributes;
	}	

}