package iob.logic.activities;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import iob.ActivitiesAPI.ActivityBoundary;
import iob.InstancesAPI.InstanceBoundary;
import iob.InstancesAPI.InstanceId;
import iob.UsersAPI.UserBoundary;
import iob.UsersAPI.UserRole;
import iob.data.daos.InstanceDao;
import iob.logic.EntityBoundaryConverter;
import iob.logic.Validator;
import iob.logic.exceptions.EmptyFieldException;
import iob.logic.exceptions.NotAcceptableException;
import iob.logic.exceptions.NotFoundException;
import iob.logic.services.EnhancedActivitiesService;
import iob.logic.services.EnhancedInstancesService;
import iob.logic.services.EnhancedUsersService;

public abstract class AbstractUseCase implements ActivityInvoker {
	
	protected EntityBoundaryConverter converter;
	protected EnhancedInstancesService instancesService;
	protected EnhancedUsersService userseService;
	protected EnhancedActivitiesService activitiesService;
	protected Validator validator;
	protected InstanceDao instanceDao;
	
	@Autowired
	public void setConverter(EntityBoundaryConverter converter) {
		this.converter = converter;
	}
	
	@Autowired public void setInstancesService(EnhancedInstancesService instancesService) {
		this.instancesService = instancesService;
	}
	
	@Autowired public void setInstancesService(EnhancedActivitiesService activitiesService) {
		this.activitiesService = activitiesService;
	}
	
	@Autowired
	public void setUserseService(EnhancedUsersService userseService) {
		this.userseService = userseService;
	}
	
	@Autowired
	public void setValidator(Validator validator) {
		this.validator = validator;
	}
	
	@Autowired
	public void setInstanceDao(InstanceDao instanceDao) {
		this.instanceDao = instanceDao;
	}
	
	@Override
	public abstract Object invoke(ActivityBoundary activity);
	
	public UserBoundary convertUser(String domain, String email, UserRole role) {
		return this.userseService.updateUser(domain, email, new UserBoundary(role));
	}
	
	public String getStringAttribute(Map<String, Object> attributes, String key) {
		String str = (String) attributes.get(key);
		this.validator.checkStringValidity(str, key);
		return str;
	}
	
	public Number getNumberAttribute(Map<String, Object> attributes, String key, Number defaultValue) {
		if (attributes.get(key) == null) {
			return defaultValue;
		} else {
			return (Number) attributes.get(key);
		}
	}
	
	public Map<String, Object> checkActivityAttributes(ActivityBoundary activity) {
		if (activity.getActivityAttributes() == null) {
			throw new EmptyFieldException("Activity is missing activityAttributes");
		}
		return activity.getActivityAttributes();
	}
	
	public InstanceBoundary getUserInstance(ActivityBoundary activity) {
		// Get user boundary
		String domain = activity.getInvokedBy().getUserId().getDomain();
		String email = activity.getInvokedBy().getUserId().getEmail();
		
		// Get users with the same name as user's id
		List<InstanceBoundary> user = this.instancesService.getAllInstancesByName(
				domain, 
				email, 
				this.converter.createUniqueId(domain, email), 
				10,	// size
				0,	// page
				true, 
				new String[] {"id"});
		
		if (user.isEmpty()) {
			throw new NotFoundException("Couldn't find instace to user with domain: " + domain + ", email: " + email);
		} else if (user.size() > 1) {
			throw new NotAcceptableException("Found more than 1 instance to user with domain: " + domain + ", email: " + email);
		}
		
		return user.get(0);
	}
	
	public String[] getSortByAttribute(Map<String, Object> attributes) {
		try {
			String str = this.getStringAttribute(attributes, Attributes.sortBy.name());
			return str.split(",");
		} catch (Exception e) {
			return new String[] { "createdTimestamp", "id" };
		}
	}
	
	public boolean getSortAscendingAttribute(Map<String, Object> attributes) {
		return attributes.get(Attributes.sortAscending.name()) == null ? 
				true : (boolean) attributes.get(Attributes.sortAscending.name());
	}
	
	public void bindInstances(InstanceBoundary child, InstanceBoundary parent) {
		this.instancesService.addChildToParent(
				parent.getCreatedBy().getUserId().getDomain(), 
				parent.getCreatedBy().getUserId().getEmail(), 
				parent.getInstanceId().getDomain(), 
				parent.getInstanceId().getId(), 
				new InstanceId(
						child.getInstanceId().getDomain(), 
						child.getInstanceId().getId()));
	}
	
	
}
