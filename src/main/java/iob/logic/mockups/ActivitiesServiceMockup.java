//package iob.logic;
//
//import java.util.Collections;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.UUID;
//import java.util.stream.Collectors;
//
//import javax.annotation.PostConstruct;
//
//import iob.logic.converters.ActivityConverter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import iob.ActivitiesAPI.ActivityBoundary;
//import iob.data.ActivityEntity;
//
//@Service
//public class ActivitiesServiceMockup implements ActivitiesService {
//
//	private Map<String, ActivityEntity> activitiesDatabaseMockup;
//	private ActivityConverter converter;
//	private String domainName;
//
//	private static final String defaultUserEmail = "user@demo.com";
//	private static final String defaultType = "demoActivityType";
//
//	@Autowired
//	public void setConverter(ActivityConverter converter) {
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
//		System.err.println("Domain: " + this.domainName);
//		// Database is using a thread safe collection
//		this.activitiesDatabaseMockup = Collections.synchronizedMap(new HashMap<>());
//	}
//
//	@Override
//	public Object invokeActivity(
//			ActivityBoundary activity) {
//		if (activity.getInstance() == null) {
//			throw new RuntimeException(
//					"Activities should be performed by users");
//		}
//
//		ActivityEntity entity = this.converter
//				.convertToEntity(activity);
//
//		// Server generated activity ID and creation time-stamp
//		entity.setActivityId(UUID.randomUUID().toString());
//		entity.setActivityDomain(this.domainName);
//		String key = this.domainName + "&&" + entity.getActivityId();
//		entity.setCreatedTimestamp(new Date());
//
//		// Check there are no nulls fields
//		if (entity.getType() == null) {
//			entity.setType(defaultType);
//		}
//		if (entity.getUserDomain() == null) {
//			entity.setUserDomain(this.domainName);
//		}
//		if (entity.getUserEmail() == null) {
//			entity.setUserEmail(defaultUserEmail);
//		}
//
//		// Store activity in database
//		activitiesDatabaseMockup.put(key, entity);
//
//		return this.converter
//				.convertToBoundary(entity);
//	}
//
//	@Override
//	public List<ActivityBoundary> getAllActivities(
//			String adminDomain,
//			String adminEmail) {
//		return this.activitiesDatabaseMockup
//				.values()
//				.stream()
//				.map(this.converter::convertToBoundary)
//				.collect(Collectors.toList());
//	}
//
//	@Override
//	public void deleteActivities(
//			String adminDomain,
//			String adminEmail) {
//		this.activitiesDatabaseMockup.clear();
//	}
//
//}
