//package iob.logic;
//
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//import javax.annotation.PostConstruct;
//
//import iob.logic.converters.UserConverter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import iob.UsersAPI.UserBoundary;
//import iob.data.UserEntity;
//
//@Service
//public class UsersServiceMockup implements UsersService {
//
//	private Map<String, UserEntity> usersDatabaseMockup;
//	private UserConverter converter;
//	private String domainName;
//
//	@Autowired
//	public void setConverter(UserConverter converter) {
//		this.converter = converter;
//	}
//
//	@Value("${spring.application.name:defaultName}")
//	public void setDomainName(String domainName) {
//		this.domainName = domainName;
//	}
//
//	@PostConstruct
//	public void init() {
//		// Database is using a thread safe collection
//		this.usersDatabaseMockup = Collections.synchronizedMap(new HashMap<>());
//	}
//
//	@Override
//	public UserBoundary createUser(UserBoundary user) {
//		UserEntity entity = this.converter.convertToEntity(user);
//		entity.setUserDomain(domainName);
//		String key = this.domainName + "&&" + entity.getUserEmail();
//
//		// Write entity to database
//		this.usersDatabaseMockup.put(
//				key,
//				entity); // value
//
//		return this.converter
//				.convertToBoundary(entity);
//	}
//
//
//	@Override
//	public UserBoundary login(
//			String userDomain,
//			String userEmail) {
//		String key = this.domainName + "&&" + userEmail;
//		UserEntity existing = this.usersDatabaseMockup
//				.get(key);
//
//		if(existing == null) {
//			throw new UserNotFoundException(
//					"Could not find user id: domain: " + userDomain + " , id: " + userEmail);
//		}
//
//		return this.converter
//				.convertToBoundary(existing);
//	}
//
//	@Override
//	public UserBoundary updateUser(
//			String userDomain,
//			String userEmail,
//			UserBoundary update) {
//		String key = this.domainName + "&&" + userEmail;
//		UserEntity existing = this.usersDatabaseMockup
//				.get(key);
//
//		if(existing == null) {
//			throw new UserNotFoundException(
//					"Could not find user id: domain: " + userDomain + " , id: " + userEmail);
//		}
//
//		// update relevant fields
//		boolean dirtyFlag = false;
//
//		if (update.getRole() != null) {
//			dirtyFlag = true;
//			existing.setRole(update.getRole());
//		}
//
//		if (update.getUsername() != null) {
//			dirtyFlag = true;
//			existing.setUsername(update.getUsername());
//		}
//
//		if (update.getAvatar() != null) {
//			dirtyFlag = true;
//			existing.setAvatar(update.getAvatar());
//		}
//
//		if (dirtyFlag) {
//			this.usersDatabaseMockup
//				.put(key, existing);
//		}
//
//		return this.converter
//				.convertToBoundary(existing);
//	}
//
//	@Override
//	public List<UserBoundary> getAllUsers(
//			String adminDomain,
//			String adminEmail) {
//		return this.usersDatabaseMockup
//				.values()
//				.stream()
//				.map(this.converter::convertToBoundary)
//				.collect(Collectors.toList());
//	}
//
//	@Override
//	public void deleteAllUsers(
//			String adminDomain,
//			String adminEmail) {
//		this.usersDatabaseMockup.clear();
//	}
//
//}
