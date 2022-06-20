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
//import iob.logic.converters.InstanceConverter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//
//import iob.InstancesAPI.InstanceBoundary;
//import iob.data.InstanceEntity;
//
////@Service
//public class InstancesServiceMockup implements InstancesService {
//
//	private Map<String, InstanceEntity> instancesDatabaseMockup;
//	private InstanceConverter converter;
//	private String domainName;
//
//	@Autowired
//	public void setConverter(InstanceConverter converter) {
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
//		// database is using a thread safe collection
//		this.instancesDatabaseMockup = Collections.synchronizedMap(new HashMap<>());
//	}
//
//	@Override
//	public InstanceBoundary createInstance(
//			String userDomain,
//			String userEmail,
//			InstanceBoundary instance) {
//		InstanceEntity entity = this.converter.convertToEntity(instance);
//		entity.setId(this.converter.createIdFromDomainAndUuid(this.domainName, UUID.randomUUID().toString()));
//		entity.setUserDomain(userDomain);
//		entity.setUserEmail(userEmail);
//		entity.setCreatedTimestamp(new Date());
//
//		// write entity to database
//		this.instancesDatabaseMockup.put(
//				entity.getId(),
//				entity);
//
//		return this.converter
//				.convertToBoundary(entity);
//	}
//
//	@Override
//	public InstanceBoundary updateInstance(
//			String userDomain,
//			String userEmail,
//			String instanceDomain,
//			String instanceId,
//			InstanceBoundary update) {
//		// retrieve existing instance
//		String key = instanceDomain + "&&" + instanceId;
//		InstanceEntity existing = this.instancesDatabaseMockup
//				.get(key);
//
//		if (existing == null) {
//			throw new InstanceNotFoundException(
//					"Could not find instance id: domain: " + instanceDomain + " , id: " + instanceId);
//		}
//
//		// update relevant fields
//		boolean dirtyFlag = false;
//
//		if (update.getType() != null) {
//			dirtyFlag = true;
//			existing.setType(update.getType());
//		}
//
//		if (update.getName() != null) {
//			dirtyFlag = true;
//			existing.setName(update.getName());
//		}
//
//		if (update.getActive() != null) {
//			dirtyFlag = true;
//			existing.setActive(update.getActive());
//		}
//
//		if (update.getLocation() != null) {
//			if (update.getLocation().getLat() != null) {
//				dirtyFlag = true;
//				existing.setLat(update.getLocation().getLat());
//			}
//
//			if (update.getLocation().getLng() != null) {
//				dirtyFlag = true;
//				existing.setLng(update.getLocation().getLng());
//			}
//		}
//
//		if (update.getInstanceAttributes() != null) {
//			dirtyFlag = true;
//		}
//
//		if (dirtyFlag) {
//			this.instancesDatabaseMockup
//				.put(
//						key,
//						existing);
//		}
//
//		return this.converter
//				.convertToBoundary(existing);
//	}
//
//	@Override
//	public List<InstanceBoundary> getAllInstances(
//			String userDomain,
//			String userEmail) {
//		return this.instancesDatabaseMockup
//				.values()
//				.stream()
//				.map(this.converter::convertToBoundary)
//				.collect(Collectors.toList());
//	}
//
//	@Override
//	public InstanceBoundary getSpecificInstance(
//			String userDomain,
//			String userEmail,
//			String instanceDomain,
//			String instanceId) {
//		String key = instanceDomain + "&&" + instanceId;
//		InstanceEntity entity = this.instancesDatabaseMockup
//				.get(key);
//
//		if (entity != null) {
//			return this.converter
//					.convertToBoundary(entity);
//		} else {
//			throw new InstanceNotFoundException(
//					"Could not find instance with InstanceId: domain: " + instanceDomain + " , id: " + instanceId);
//		}
//	}
//
//	@Override
//	public void deleteAllInstances(
//			String adminDomain,
//			String adminEmail) {
//		this.instancesDatabaseMockup.clear();
//	}
//
//}
