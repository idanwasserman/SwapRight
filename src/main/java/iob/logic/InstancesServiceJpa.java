package iob.logic;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import iob.data.daos.InstanceDao;
import iob.data.entities.InstanceEntity;
import iob.data.entities.UserRoleEntity;
import iob.logic.exceptions.AccessDeniedException;
import iob.logic.exceptions.DeprecatedFunctionException;
import iob.logic.exceptions.NotAcceptableException;
import iob.logic.exceptions.NotFoundException;
import iob.logic.services.EnhancedInstancesService;
import iob.logic.services.EnhancedUsersService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.domain.geo.Metrics;
import org.springframework.stereotype.Service;

import iob.InstancesAPI.CreatedBy;
import iob.InstancesAPI.InstanceBoundary;
import iob.InstancesAPI.InstanceId;
import iob.UsersAPI.UserId;

import org.springframework.transaction.annotation.Transactional;


@Service
public class InstancesServiceJpa implements EnhancedInstancesService {
	
	private InstanceDao instanceDao;
	private EnhancedUsersService usersService;
	private EntityBoundaryConverter converter;
	private Validator validator;
	private String domainName;

	@Autowired
	public void setInstanceDao(InstanceDao instanceDao) {
		this.instanceDao = instanceDao;
	}
	
	@Autowired
	public void setUsersService(EnhancedUsersService usersService) {
		this.usersService = usersService;
	}
	
	@Autowired
	public void setConverter(EntityBoundaryConverter converter) {
		this.converter = converter;
	}
	
	@Autowired
	public void setValidator(Validator validator) {
		this.validator = validator;
	}
	
	@Value("${spring.application.name:defaultName}")
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
	@Override
	@Transactional(readOnly = false)
	public InstanceBoundary createInstance(
			String userDomain, String userEmail,
			InstanceBoundary instance) {
		// Check if user can perform this function
		UserRoleEntity[] validRoles = { UserRoleEntity.MANAGER };
		this.usersService.checkUser(userDomain, userEmail, validRoles, "Only MANAGER users can create instances");

		// Inject createdBy to instance
		instance.setCreatedBy(new CreatedBy(new UserId(userDomain, userEmail)));
		
		// Check instance's details validity
		this.validator.checkInstanceValidity(instance);

		// Convert to entity and generate ID & time-stamp
		InstanceEntity entity = this.converter.convertToEntity(instance);
		entity.setId(this.converter.createUniqueId(this.domainName, UUID.randomUUID().toString()));
		entity.setCreatedTimestamp(new Date());

		// Store instance in database and return instance boundary
		this.instanceDao.save(entity);
		return this.converter.convertToBoundary(entity);
	}

	@Override
	@Transactional(readOnly = false)
	public InstanceBoundary updateInstance(
			String userDomain, String userEmail,
			String instanceDomain, String instanceId,
			InstanceBoundary update) {
		// Check if user can perform this function
		UserRoleEntity[] validRoles = { UserRoleEntity.MANAGER };
		this.usersService.checkUser(userDomain, userEmail, validRoles, "Only MANAGER users can update instances");
		
		// Get instance by key
		String key = this.converter.createUniqueId(instanceDomain, instanceId);
		InstanceEntity existing = this.getInstanceEntityById(key);

		// Update relevant fields
		if (update.getType() != null)
			existing.setType(update.getType());
		if (update.getName() != null)
			existing.setName(update.getName());
		if (update.getActive() != null)
			existing.setActive(update.getActive());
		if (update.getLocation() != null) {
			if (update.getLocation().getLat() != null)
				existing.setLat(update.getLocation().getLat());
			if (update.getLocation().getLng() != null)
				existing.setLng(update.getLocation().getLng());
		}
		if (update.getInstanceAttributes() != null)
			existing.setInstanceAttributes(update.getInstanceAttributes());

		// Save changes in database and return instance boundary
		this.instanceDao.save(existing);
		return this.converter.convertToBoundary(existing);
	}

	@Override
	@Transactional(readOnly = true)
	public InstanceBoundary getSpecificInstance(
			String userDomain, String userEmail,
			String instanceDomain, String instanceId) {
		// Check if user can perform this function
		UserRoleEntity[] validRoles = { UserRoleEntity.MANAGER, UserRoleEntity.PLAYER };
		UserRoleEntity userRoleEntity = this.usersService
				.checkUser(userDomain, userEmail, validRoles, "ADMIN users cannot retrieve specific instances");
		
		// Get instance by key
		String key = this.converter.createUniqueId(instanceDomain, instanceId);
		InstanceEntity instanceEntity = getInstanceEntityById(key);
		
		// If user's role is player -> return only active instance
		if (userRoleEntity == UserRoleEntity.PLAYER) {
			if (!instanceEntity.getActive()) {
				throw new NotFoundException(
						"Instance with instancId: domain: " + instanceDomain + ", id: " + instanceId + " is NOT active");
			}
		}
		
		return this.converter.convertToBoundary(instanceEntity);
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAllInstances(String adminDomain, String adminEmail) {
		// Check if user can perform this function
		UserRoleEntity[] validRoles = { UserRoleEntity.ADMIN };
		this.usersService.checkUser(adminDomain, adminEmail, validRoles, "Only ADMIN users can delete all instances");
		
		this.instanceDao.deleteAll();
	}

	@Override
	@Transactional(readOnly = false)
	public void addChildToParent(
			String userDomain, String userEmail,
			String instanceDomain, String parentId,
			InstanceId childId) {	
		// Check if user can perform this function
		UserRoleEntity[] validRoles = { UserRoleEntity.MANAGER };
		this.usersService.checkUser(userDomain, userEmail, validRoles, "Only MANAGER users can bind instances");
		
		// Get parent
		InstanceEntity parent = getInstanceEntityById(
				this.converter.createUniqueId(instanceDomain, parentId));
				
		// Get child
		InstanceEntity child = getInstanceEntityById(
				this.converter.createUniqueId(childId.getDomain(), childId.getId()));
				
		// Bind parent & child
		parent.addChild(child.getId()); 
		child.addParent(parent.getId());
		
		// Save changes in database
		this.instanceDao.save(child); // saves both child and parent as they're linked
		this.instanceDao.save(parent);
	}

	@Override
	@Transactional(readOnly = true)
	public List<InstanceBoundary> getAllInstances(
			String userDomain, String userEmail, 
			int size, int page,
			boolean sortAscending, String[] sortBy) {
		
		// Check if user can perform this function
		UserRoleEntity[] validRoles = { UserRoleEntity.MANAGER, UserRoleEntity.PLAYER };
		UserRoleEntity userRoleEntity = this.usersService
				.checkUser(userDomain, userEmail, validRoles, "Only MANAGER, PLAYER users can get all instances");
		
		// Get ALL instances
		Direction direction = sortAscending ? Direction.ASC : Direction.DESC;
		Pageable pageable = PageRequest.of(page, size, direction, sortBy);
		
		// PLAYER users will get only ACTIVE instances
		switch (userRoleEntity) {
		case MANAGER:
			return this.instanceDao
					.findAll(pageable) 						// Page<InstanceEntity>
					.stream() 								// Stream<InstanceEntity>
					.map(this.converter::convertToBoundary) // Stream<InstanceBoundary>
					.collect(Collectors.toList()); 			// List<InstanceBoundary>
			
		case PLAYER:
			return this.instanceDao
					.findAllByActiveIsTrue(pageable)		// Page<InstanceEntity>
					.stream() 								// Stream<InstanceEntity>
					.map(InstanceEntity.class::cast)
					.map(this.converter::convertToBoundary) // Stream<InstanceBoundary>
					.collect(Collectors.toList()); 			// List<InstanceBoundary>

		default:
			throw new AccessDeniedException("Other users cannot perform this operation");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<InstanceBoundary> getChildren(
			String userDomain, String userEmail, 
			String instanceDomain, String parentId, 
			int size, int page, 
			boolean sortAscending, String[] sortBy) {
		// Check if user can perform this function
		UserRoleEntity[] validRoles = { UserRoleEntity.MANAGER, UserRoleEntity.PLAYER };
		UserRoleEntity userRoleEntity = this.usersService
				.checkUser(userDomain, userEmail, validRoles, "Only MANAGER, PLAYER users can get instance's children");
		
		// Check that instance exists in database
		String key = this.converter.createUniqueId(instanceDomain, parentId);
		InstanceEntity parent = this.getInstanceEntityById(key);

		// Get children IDs from parent
		Set<String> childrenIds = parent.getChildrenIds();
		Direction direction = sortAscending ? Direction.ASC : Direction.DESC;
		
		// PLAYER users will get only ACTIVE instances
		switch (userRoleEntity) {
		case MANAGER:
			return StreamSupport.stream(this.instanceDao
					.findAll(Sort.by(direction, sortBy)).spliterator(), false)
					.filter(i -> childrenIds.contains(i.getId()))
					.skip(page * size)
					.limit(size)
					.map(this.converter::convertToBoundary)
					.collect(Collectors.toList());
			
		case PLAYER:
			return this.instanceDao
					.findAllByActive(true, Sort.by(direction, sortBy))
					.stream()
					.filter(i -> childrenIds.contains(i.getId()))
					.skip(page * size)
					.limit(size)
					.map(this.converter::convertToBoundary)
					.collect(Collectors.toList());

		default:
			throw new AccessDeniedException("Other users cannot perform this operation");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<InstanceBoundary> getParents(
			String userDomain, String userEmail, 
			String instanceDomain, String childId,
			int size, int page,
			boolean sortAscending, String[] sortBy) {
		// Check if user can perform this function
		UserRoleEntity[] validRoles = { UserRoleEntity.MANAGER, UserRoleEntity.PLAYER };
		UserRoleEntity userRoleEntity = this.usersService
				.checkUser(userDomain, userEmail, validRoles, "Only MANAGER, PLAYER users can get instance's parent");
		
		// Check that instance exists in database
		String key = this.converter.createUniqueId(instanceDomain, childId);
		InstanceEntity child = this.getInstanceEntityById(key);
		
		// Get parents IDs from child
		Set<String> parentsIds = child.getParentsIds();
		Direction direction = sortAscending ? Direction.ASC : Direction.DESC;
		
		// PLAYER users will get only ACTIVE instances
		switch (userRoleEntity) {
		case MANAGER:
			return StreamSupport.stream(this.instanceDao
					.findAll(Sort.by(direction, sortBy)).spliterator(), false)
					.filter(i -> parentsIds.contains(i.getId()))
					.skip(page * size)
					.limit(size)
					.map(this.converter::convertToBoundary)
					.collect(Collectors.toList());
			
		case PLAYER:
			return this.instanceDao
					.findAllByActive(true, Sort.by(direction, sortBy))
					.stream()
					.filter(i -> parentsIds.contains(i.getId()))
					.skip(page * size)
					.limit(size)
					.map(this.converter::convertToBoundary)
					.collect(Collectors.toList());

		default:
			throw new AccessDeniedException("Other users cannot perform this operation");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<InstanceBoundary> getAllInstancesByName(
			String userDomain, String userEmail,
			String name, int size, int page,
			boolean sortAscending, String[] sortBy) {
		// Check if user can perform this function
		UserRoleEntity[] validRoles = { UserRoleEntity.MANAGER, UserRoleEntity.PLAYER };
		UserRoleEntity userRoleEntity = this.usersService
				.checkUser(userDomain, userEmail, validRoles, "Only MANAGER, PLAYER users can search for instances");
		
		// Get ALL instances by name
		Direction direction = sortAscending ? Direction.ASC : Direction.DESC;
		Pageable pageable = PageRequest.of(page, size, direction, sortBy);

		// PLAYER users will get only ACTIVE instances
		switch (userRoleEntity) {
		case MANAGER:
			return this.instanceDao
					.findAllByName(name, pageable)
					.stream()
					.map(this.converter::convertToBoundary)
					.collect(Collectors.toList());
			
		case PLAYER:
			return this.instanceDao
					.findAllByNameAndActiveIsTrue(name, pageable)
					.stream()
					.map(this.converter::convertToBoundary)
					.collect(Collectors.toList());

		default:
			throw new AccessDeniedException("Other users cannot perform this operation");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<InstanceBoundary> getAllInstancesByType(
			String userDomain, String userEmail,
			String type, int size, int page,
			boolean sortAscending, String[] sortBy) {
		// Check if user can perform this function
		UserRoleEntity[] validRoles = { UserRoleEntity.MANAGER, UserRoleEntity.PLAYER };
		UserRoleEntity userRoleEntity = this.usersService
				.checkUser(userDomain, userEmail, validRoles, "Only MANAGER, PLAYER users can search for instances");
		
		// Get ALL instances by type
		Direction direction = sortAscending ? Direction.ASC : Direction.DESC;
		Pageable pageable = PageRequest.of(page, size, direction, sortBy);
		
		// PLAYER users will get only ACTIVE instances
		switch (userRoleEntity) {
		case MANAGER:
			return this.instanceDao
					.findAllByType(type, pageable)
					.stream()
					.map(this.converter::convertToBoundary)
					.collect(Collectors.toList());

		case PLAYER:
			return this.instanceDao
					.findAllByTypeAndActiveIsTrue(type, pageable)
					.stream()
					.map(this.converter::convertToBoundary)
					.collect(Collectors.toList());

		default:
			throw new AccessDeniedException("Other users cannot perform this operation");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<InstanceBoundary> getAllInstancesByLocation(
			String userDomain, String userEmail,
			double lat, double lng,	double distance,
			int size, int page,
			boolean sortAscending, String[] sortBy) {
		// Check if user can perform this function
		UserRoleEntity[] validRoles = { UserRoleEntity.MANAGER, UserRoleEntity.PLAYER };
		UserRoleEntity userRoleEntity = this.usersService
				.checkUser(userDomain, userEmail, validRoles, "Only MANAGER, PLAYER users can search for instances");
		
		// Get ALL instances by location
		Direction direction = sortAscending ? Direction.ASC : Direction.DESC;
		Pageable pageable = PageRequest.of(page, size, direction, sortBy);
		
		// PLAYER users will get only ACTIVE instances
		switch (userRoleEntity) {
		case MANAGER:
			return this.instanceDao
					.findByPointNear(new Point(lat, lng), new Distance(distance, Metrics.KILOMETERS), pageable)
					.stream()
					.map(this.converter::convertToBoundary)
					.collect(Collectors.toList());

		case PLAYER:
			return this.instanceDao
					.findByActiveIsTrueAndPointNear(new Point(lat, lng), new Distance(distance, Metrics.KILOMETERS), pageable)
					.stream()
					.map(this.converter::convertToBoundary)
					.collect(Collectors.toList());

		default:
			throw new AccessDeniedException("Other users cannot perform this operation");
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<InstanceBoundary> getAllInstancesByCreation(
			String userDomain, String userEmail,
			String creationWindow,
			int size, int page,
			boolean sortAscending, String[] sortBy) {
		// Check if user can perform this function
		UserRoleEntity[] validRoles = { UserRoleEntity.MANAGER, UserRoleEntity.PLAYER };
		UserRoleEntity userRoleEntity = this.usersService
				.checkUser(userDomain, userEmail, validRoles, "Only MANAGER, PLAYER users can search for instances");
		
		// Get ALL instances by creation
		Direction direction = sortAscending ? Direction.ASC : Direction.DESC;
		Date tillDate = convertCreationWindowToDate(creationWindow);
		
		// PLAYER users will get only ACTIVE instances
		switch (userRoleEntity) {
		case MANAGER:
			return StreamSupport.stream(this.instanceDao
					.findAll(Sort.by(direction, sortBy)).spliterator(), false)
					.filter(i -> i.getCreatedTimestamp().after(tillDate))
					.skip(page * size)
					.limit(size)
					.map(this.converter::convertToBoundary)
					.collect(Collectors.toList());

		case PLAYER:
			return this.instanceDao.findAllByActive(true, Sort.by(direction, sortBy))
					.stream()
					.filter(i -> i.getCreatedTimestamp().after(tillDate))
					.skip(page * size)
					.limit(size)
					.map(this.converter::convertToBoundary)
					.collect(Collectors.toList());

		default:
			throw new AccessDeniedException("Other users cannot perform this operation");
		}
	}

	private Date convertCreationWindowToDate(String creationWindow) {
		final int HOURS_IN_ONE_DAY = 24;
		final long MILLISECONDS_IN_ONE_HOUR = 60 * 60 * 1000;
		final long MILLISECONDS_IN_ONE_DAY = MILLISECONDS_IN_ONE_HOUR * HOURS_IN_ONE_DAY;
		
		long[] millisecondsArr = {
				MILLISECONDS_IN_ONE_HOUR,
				MILLISECONDS_IN_ONE_DAY,
				MILLISECONDS_IN_ONE_DAY * 7,
				MILLISECONDS_IN_ONE_DAY * 30
		};
		
		String[] creationWindowOptions = {
				"LAST_HOUR", 
				"LAST_24_HOURS", 
				"LAST_7_DAYS", 
				"LAST_30_DAYS"
		};
		
		int arrLength = creationWindowOptions.length;
		for (int i = 0; i < arrLength; i++) {
			if (creationWindow.equals(creationWindowOptions[i])) {
				return new Date(new Date().getTime() - millisecondsArr[i]);
			}
		}
		
		throw new NotAcceptableException(creationWindow + " is not a proper creation window");
	}

	private InstanceEntity getInstanceEntityById(String id) {
		return this.instanceDao
			.findById(id)
			.orElseThrow(() -> new NotFoundException(
					"Could not find instance with id: " + id));
	}

	
	// Deprecated methods:
	
	@Override
	public List<InstanceBoundary> getAllInstances(
			String userDomain,
			String userEmail) {
		throw new DeprecatedFunctionException(
				"The method getAllInstances(String, String) from the type InstancesService is deprecated");
	}

	@Override
	public List<InstanceBoundary> getChildren(
			String userDomain,
			String userEmail,
			String instanceDomain,
			String parentId) {
		throw new DeprecatedFunctionException(
				"The method getChildren(String, String, String, String) from the type InstancesServicePlusPlus is deprecated");
	}

	@Override
	public InstanceBoundary getParent(
			String userDomain,
			String userEmail,
			String instanceDomain,
			String childId) {
		throw new DeprecatedFunctionException(
				"The method getParent(String, String, String, String) from the type InstancesServicePlusPlus is deprecated");
	}
	
}


