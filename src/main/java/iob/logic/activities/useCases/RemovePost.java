package iob.logic.activities.useCases;

import org.springframework.stereotype.Component;

import iob.ActivitiesAPI.ActivityBoundary;
import iob.InstancesAPI.InstanceBoundary;
import iob.UsersAPI.UserRole;
import iob.logic.activities.AbstractUseCase;
import iob.logic.exceptions.NotAcceptableException;


/**
 *  Removing a post is reflected by 
 *  changing their ACTIVE attribute to false
 *
 */
@Component("RemovePost")
public class RemovePost extends AbstractUseCase {

	@Override
	public Object invoke(ActivityBoundary activity) {
		// Get user that invoke activity details
		String userDomain = activity.getInvokedBy().getUserId().getDomain();
		String userEmail = activity.getInvokedBy().getUserId().getEmail();
		String postDomain = activity.getInstance().getInstanceId().getDomain();
		String postId = activity.getInstance().getInstanceId().getId();
		
		// Get the post to remove
		InstanceBoundary post = this.instancesService
				.getSpecificInstance(userDomain, userEmail, postDomain, postId);
		
		// Check that the user that wants to remove the post is also the one who posted it
		if (!post.getCreatedBy().getUserId().getDomain().equals(userDomain) 
				|| !post.getCreatedBy().getUserId().getEmail().equals(userEmail)) {
			throw new NotAcceptableException("Posts can be removed by post's owner and admins only");
		}
		
		// Convert user role to MANAGER before updating the instance
		this.convertUser(userDomain, userEmail, UserRole.MANAGER);
		
		// Remove post --> set active to false
		post.setActive(false);
		this.instancesService.updateInstance(userDomain, userEmail, postDomain, postId, post);
		
		// Convert user role to PLAYER after updating the instance
		this.convertUser(userDomain, userEmail, UserRole.PLAYER);

		return post;
	}

}
