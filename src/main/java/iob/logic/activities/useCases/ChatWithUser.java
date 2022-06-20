package iob.logic.activities.useCases;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import iob.ActivitiesAPI.ActivityBoundary;
import iob.InstancesAPI.CreatedBy;
import iob.InstancesAPI.InstanceBoundary;
import iob.InstancesAPI.Location;
import iob.UsersAPI.UserBoundary;
import iob.UsersAPI.UserId;
import iob.UsersAPI.UserRole;
import iob.logic.activities.AbstractUseCase;
import iob.logic.activities.Attributes;
import iob.logic.activities.InstancesTypes;
import iob.logic.exceptions.NotAcceptableException;

@Component("ChatWithUser")
public class ChatWithUser extends AbstractUseCase {

	@Override
	public Object invoke(ActivityBoundary activity) {
		Map<String, Object> attributes = this.checkActivityAttributes(activity);

		// Get the comment to add to the chat
		String comment = this.getStringAttribute(attributes, Attributes.comment.name());
		
		// Get sending user
		String sendingUserDomain = activity.getInvokedBy().getUserId().getDomain();
		String sendingUserEmail = activity.getInvokedBy().getUserId().getEmail();
		UserBoundary user = this.userseService.login(sendingUserDomain, sendingUserEmail);
		InstanceBoundary sendingUser = this.getUserInstance(activity);
		
		// Get the getting user
		String gettingUserId = this.getStringAttribute(attributes, "userId");
		InstanceBoundary gettingUser = this.instancesService.getSpecificInstance(
				sendingUserDomain, sendingUserEmail, sendingUserDomain, gettingUserId);
		
		// Create a unique name for the chat between the users
		String chatName = createChatNameByUsersIds(sendingUser.getInstanceId().getId(), gettingUserId);
		
		// Get all instances by name
		List<InstanceBoundary> allChatsByName = this.instancesService.getAllInstancesByName(
				sendingUserDomain, 
				sendingUserEmail, 
				chatName, 
				10, 0, true, new String[] { "id" });
		
		// Check there is only one chat
		if (allChatsByName.size() > 1) {
			throw new NotAcceptableException(
					"There is not supposed to be 2 chats for the same 2 users");
		}
		
		// Get the chat itself
		InstanceBoundary chat;
		boolean newChat = false;
		if (allChatsByName.size() == 1) {
			chat = allChatsByName.get(0);
		} else {
			newChat = true;
			chat = new InstanceBoundary(
					InstancesTypes.CHAT.name(), 
					chatName, 
					true, 
					new CreatedBy(new UserId(sendingUserDomain, sendingUserEmail)), 
					new Location(), 
					new HashMap<>());
		}
		
		// add the new comment to the chat
		updateChatComments(comment, chat, user.getAvatar() + ": ");
		
		// Convert user role to MANAGER
		this.convertUser(sendingUserDomain, sendingUserEmail, UserRole.MANAGER);
		
		if (newChat) {
			// Create new chat instance
			chat = this.instancesService.createInstance(sendingUserDomain, sendingUserEmail, chat);
			
			// Bind chat to the users
			this.bindInstances(sendingUser, chat);
			this.bindInstances(gettingUser, chat);
		} else {
			// update chat instance
			this.instancesService.updateInstance(
					sendingUserDomain, 
					sendingUserEmail, 
					chat.getInstanceId().getDomain(), 
					chat.getInstanceId().getId(), 
					chat);
		}
		
		// Convert user role to PLAYER
		this.convertUser(sendingUserDomain, sendingUserEmail, UserRole.PLAYER);
		
		return chat;
	}

	private void updateChatComments(String comment, InstanceBoundary chat, String prefix) {
		final char c = 127;
		final String DELIMETER = c + "\n";
		
		String comments = (String) chat.getInstanceAttributes().get(Attributes.comments.name());
		comment = prefix + comment;
		if (comments == null || comments.isEmpty()) {
			comments = comment;
		} else {
			comments += (DELIMETER + comment);
		}
		chat.getInstanceAttributes().put(Attributes.comments.name(), comments);
	}

	private String createChatNameByUsersIds(String id1, String id2) {
		final String ID_DELIMITER = "@@";
		if (id1.compareTo(id2) > 0) {
			return id2 + ID_DELIMITER + id1;
		} else {
			return id1 + ID_DELIMITER + id2;
		}
	}

}
