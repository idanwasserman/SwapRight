package iob.logic.activities.useCases;

import java.util.Map;

import org.springframework.stereotype.Component;

import iob.ActivitiesAPI.ActivityBoundary;
import iob.InstancesAPI.InstanceBoundary;
import iob.UsersAPI.UserRole;
import iob.logic.activities.AbstractUseCase;
import iob.logic.activities.Attributes;

/**
 * comments are saved as one long string
 * the comments are separated by a delimiter
 */
@Component("CommentOnPost")
public class CommentOnPost extends AbstractUseCase {

	@Override
	public Object invoke(ActivityBoundary activity) {
		final char c = 127;
		final String DELIMETER = "" + c;
		
		Map<String, Object> activityAttributes = this.checkActivityAttributes(activity);
		
		// Get user that comments details
		String userDomain = activity.getInvokedBy().getUserId().getDomain();
		String userEmail = activity.getInvokedBy().getUserId().getEmail();
				
		// Get the message to comment
		String comment = this.getStringAttribute(activityAttributes, Attributes.comment.name());
		
		// Get the post
		String instanceDomain = activity.getInstance().getInstanceId().getDomain();
		String instanceId = activity.getInstance().getInstanceId().getId();
		InstanceBoundary post = this.instancesService
				.getSpecificInstance(userDomain, userEmail, instanceDomain, instanceId);
		
		// Update comments
		String comments = (String) post.getInstanceAttributes().get(Attributes.comments.name());
		comments += (DELIMETER + comment);
		post.getInstanceAttributes().put(Attributes.comments.name(), comments);
		
		// Convert user role to MANAGER before updating the post
		this.convertUser(userDomain, userEmail, UserRole.MANAGER);
		
		this.instancesService.updateInstance(userDomain, userEmail, instanceDomain, instanceId, post);
		
		// Convert user role back to PLAYER
		this.convertUser(userDomain, userEmail, UserRole.PLAYER);
		
		// Return comments as array of strings
		return comments.split(DELIMETER);
	}

}
