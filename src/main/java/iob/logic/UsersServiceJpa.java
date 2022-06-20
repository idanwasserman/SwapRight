package iob.logic;

import iob.logic.exceptions.AccessDeniedException;
import iob.logic.exceptions.DeprecatedFunctionException;
import iob.logic.exceptions.NotAcceptableException;
import iob.logic.exceptions.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

import iob.logic.services.EnhancedUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import iob.UsersAPI.UserBoundary;
import iob.UsersAPI.UserId;
import iob.data.daos.UserDao;
import iob.data.entities.UserEntity;
import iob.data.entities.UserRoleEntity;

@Service
public class UsersServiceJpa implements EnhancedUsersService{

	private UserDao userDao;
	private EntityBoundaryConverter converter;
	private Validator validator;
	private String domainName;
	
	@Autowired
	public void setConverter(EntityBoundaryConverter converter) {
		this.converter = converter;
	}
	
	@Autowired
	public void setValidator(Validator validator) {
		this.validator = validator;
	}
	
	@Autowired
	public void setActivityDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	@Value("${spring.application.name:defaultName}")
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
	@Override
	@Transactional(readOnly = false)
	public UserBoundary createUser(UserBoundary user) {
		try {
			if (this.login(this.domainName, user.getUserId().getEmail()) != null) {
				throw new NotAcceptableException(
						"User with email: " + user.getUserId().getEmail() + " already exists");
			}
		} catch (NotFoundException e) {
			// user is not existing in the DB
		}
		
		user.setUserId(new UserId(this.domainName, user.getUserId().getEmail()));
		this.validator.checkUserValidity(user);
		// Function above will throw exception if there are invalid fields
		
		UserEntity entity = this.converter.convertToEntity(user);
		entity.setId(this.converter.createUniqueId(this.domainName, user.getUserId().getEmail()));
		
		// Store user in database and return user boundary
		entity = this.userDao.save(entity);
		return this.converter.convertToBoundary(entity);
	}

	@Override
	@Transactional(readOnly = false)
	public UserBoundary login(String userDomain, String userEmail) {
		// Try to find user in database, if user does not exist -> throw exception
		UserEntity existing = findEntity(userDomain, userEmail);
		return this.converter.convertToBoundary(existing);
	}

	@Override
	@Transactional(readOnly = false)
	public UserBoundary updateUser(
			String userDomain, String userEmail, 
			UserBoundary update) {
		// Try to find user in database, if user does not exist -> throw exception
		UserEntity existing = findEntity(userDomain, userEmail);

		// Update relevant fields
		if (update.getRole() != null) {
			existing.setRole(
					this.converter.convertRole(update.getRole()));
		}
		if (update.getUsername() != null) {
			existing.setUsername(update.getUsername());
		}
		if (update.getAvatar() != null) {
			existing.setAvatar(update.getAvatar());
		}
		
		// Save changes in database and return user boundary
		this.userDao.save(existing);
		return this.converter.convertToBoundary(existing);
	}

	private UserEntity findEntity(String userDomain, String userEmail) {
		String key = this.converter.createUniqueId(userDomain, userEmail);
		UserEntity existing = this.userDao
				.findById(key)
				.orElseThrow(() -> new NotFoundException(
					"Could not find user id: domain: " + userDomain + " , id: " + userEmail));
		return existing;
	}

	@Override
	@Transactional(readOnly = false)
	public void deleteAllUsers(String adminDomain, String adminEmail) {
		// Check if user can perform this function
		UserRoleEntity[] validRoles = { UserRoleEntity.ADMIN };
		this.checkUser(adminDomain, adminEmail, validRoles, "Only ADMIN users can delete all users");
		
		this.userDao.deleteAll();		
	}

	@Override
	@Transactional(readOnly = true)
	public List<UserBoundary> getAllUsers(
			String adminDomain, String adminEmail, 
			int size, int page,
			boolean sortAscending, String[] sortBy) {
		// Check if user can perform this function
		UserRoleEntity[] validRoles = { UserRoleEntity.ADMIN };
		this.checkUser(adminDomain, adminEmail, validRoles, "Only ADMIN users can get all users");
			
		// Get all users and return them
		Direction direction = sortAscending ? Direction.ASC : Direction.DESC;
		Pageable pageable = PageRequest.of(page, size, direction, sortBy);
		return this.userDao
			.findAll(pageable) 						// Page<UserEntity>
			.stream() 								// Stream<UserEntity>
			.map(this.converter::convertToBoundary) // Stream<UserBoundary>
			.collect(Collectors.toList()); 			// List<UserBoundary>
	}

	@Override
	public UserRoleEntity checkUser(
			String userDomain, String userEmail, 
			UserRoleEntity[] validRoles, String msg) {
		
		UserEntity user = this.converter.convertToEntity(
				this.login(userDomain, userEmail));
		UserRoleEntity userRole = user.getRole();
		
		for (UserRoleEntity validRole : validRoles) {
			if (userRole == validRole) return userRole;
		}
		// If getting here --> User's role is not one of the valid roles 
		throw new AccessDeniedException(msg);
	}
	
	
	// Deprecated functions:

	@Override
	public List<UserBoundary> getAllUsers(String adminDomain, String adminEmail) {
		throw new DeprecatedFunctionException(
				"The method getAllUsers(String, String) from the type UsersService is deprecated");
	}

}
