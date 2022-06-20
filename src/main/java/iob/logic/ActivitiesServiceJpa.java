package iob.logic;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import iob.logic.activities.ActivityInvoker;
import iob.logic.exceptions.DeprecatedFunctionException;
import iob.logic.services.EnhancedActivitiesService;
import iob.logic.services.EnhancedInstancesService;
import iob.logic.services.EnhancedUsersService;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import iob.ActivitiesAPI.ActivityBoundary;
import iob.ActivitiesAPI.ActivityId;
import iob.data.daos.ActivityDao;
import iob.data.entities.UserRoleEntity;

@Service
public class ActivitiesServiceJpa implements EnhancedActivitiesService {

	private ActivityDao activityDao;
	private EnhancedInstancesService instancesService;
	private EnhancedUsersService usersService;
	private ApplicationContext applicationContext;
	private EntityBoundaryConverter converter;
	private Validator validator;
	private String domainName;
	
	@Autowired
	public void setActivityDao(ActivityDao activityDao) {
		this.activityDao = activityDao;
	}
	
	@Autowired
	public void setInstancesService(EnhancedInstancesService instancesService) {
		this.instancesService = instancesService;
	}
	
	@Autowired
	public void setUsersService(EnhancedUsersService usersService) {
		this.usersService = usersService;
	}
	
	@Autowired
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}
	
	@Autowired
	public void setConverter(EntityBoundaryConverter converter) {
		this.converter = converter;
	}
	
	@Autowired public void setValidator(Validator validator) {
		this.validator = validator;
	}
	
	@Value("${spring.application.name:defaultName}")
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
	@Override
	@Transactional(readOnly = false)
	public Object invokeActivity(ActivityBoundary activity) {
		
		// Check activity's details validity
		this.validator.checkActivityValidity(activity);
		
		// Check that user's role is PLAYER
		String userDomain = activity.getInvokedBy().getUserId().getDomain();
		String userEmail = activity.getInvokedBy().getUserId().getEmail();
		UserRoleEntity[] validRoles = { UserRoleEntity.PLAYER };
		this.usersService.checkUser(userDomain, userEmail, validRoles, "Only PLAYER users can invoke activities");

		// Check that instance is active
		this.instancesService.getSpecificInstance(
				userDomain, userEmail, 
				activity.getInstance().getInstanceId().getDomain(), // instance domain
				activity.getInstance().getInstanceId().getId());	// instance id
		// If instance is not active --> method above will throw exception
		// because a PLAYER user is trying to retrieve it
		
		// Generate ID & time-stamp
		activity.setActivityId(new ActivityId(
				this.domainName, 
				UUID.randomUUID().toString()));
		activity.setCreatedTimestamp(new Date());
		
		// Invoke activity
		Object retval = execute(activity);
		
		// Store activity entity in database and return activity boundary 
		this.activityDao.save(this.converter.convertToEntity(activity));
		
		return retval;
	}

	private Object execute(ActivityBoundary activity) {
		final String defaultUseCase = "ListActivityTypes";
		
		// Get use case to perform from activity's type
		ActivityInvoker activityInvoker = null;
		try {
			activityInvoker = this.applicationContext.getBean(
					activity.getType(), ActivityInvoker.class);
		} catch (BeansException e) {
			activityInvoker = this.applicationContext.getBean(
					defaultUseCase, ActivityInvoker.class);
		}
		
		// Invoke activity
		return activityInvoker.invoke(activity);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteActivities(String adminDomain, String adminEmail) {
		// Check if user can perform this function
		UserRoleEntity[] validRoles = { UserRoleEntity.ADMIN };
		this.usersService.checkUser(adminDomain, adminEmail, validRoles, "Only ADMIN users can delete all activities");
		
		this.activityDao.deleteAll();
	}

	@Override
	@Transactional(readOnly = true)
	public List<ActivityBoundary> getAllActivities(
			String adminDomain, String adminEmail, 
			int size, int page,
			boolean sortAscending, String[] sortBy) {
		// Check if user can perform this function
		UserRoleEntity[] validRoles = { UserRoleEntity.ADMIN };
		this.usersService.checkUser(adminDomain, adminEmail, validRoles, "Only ADMIN users can get all activities");
		
		// FIXME
		Direction direction = sortAscending ? Direction.ASC : Direction.DESC;
		Pageable pageable = PageRequest.of(page, size, direction, sortBy);
		return this.activityDao
				.findAll(pageable) 						// Page<ActivityEntity>
				.stream() 								// Stream<ActivityEntity>
				.map(this.converter::convertToBoundary) // Stream<ActivityBoundary>
				.collect(Collectors.toList()); 			// List<ActivityBoundary>
	}
	
	
	// Deprecated functions:

	@Override
	@Transactional(readOnly = true)
	public List<ActivityBoundary> getAllActivities(String adminDomain, String adminEmail) {
		throw new DeprecatedFunctionException(
				"The method getAllActivities(String, String) from the type ActivitiesService is deprecated");
	}

}
