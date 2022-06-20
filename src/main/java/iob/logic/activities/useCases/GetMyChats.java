package iob.logic.activities.useCases;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import iob.ActivitiesAPI.ActivityBoundary;
import iob.InstancesAPI.InstanceBoundary;
import iob.logic.activities.AbstractUseCase;
import iob.logic.activities.Attributes;
import iob.logic.activities.InstancesTypes;

@Component("GetMyChats")
public class GetMyChats extends AbstractUseCase {

	@Override
	public Object invoke(ActivityBoundary activity) {
		Map<String, Object> attributes = this.checkActivityAttributes(activity);
		
		final int DEFAULT_SIZE = 10;
		final int DEFAULT_PAGE = 0;
		
		String userId = this.getUserInstance(activity).getInstanceId().getId();
		
		int size = (int) this.getNumberAttribute(attributes, Attributes.size.name(), DEFAULT_SIZE);
		int page = (int) this.getNumberAttribute(attributes, Attributes.page.name(), DEFAULT_PAGE);
		
		// Get all instances by type == CHAT
		List<InstanceBoundary> allChats = this.instancesService.getAllInstancesByType(
				activity.getInvokedBy().getUserId().getDomain(), 
				activity.getInvokedBy().getUserId().getEmail(), 
				InstancesTypes.CHAT.name(), size * 32, page, false, new String[] { "id" });
		
		return allChats
				.stream()
				.filter(i -> i.getName().contains(userId))
				.skip(size * page)
				.limit(size)
				.collect(Collectors.toList());
	}

}
