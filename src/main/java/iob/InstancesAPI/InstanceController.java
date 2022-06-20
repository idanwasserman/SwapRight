package iob.InstancesAPI;

import iob.logic.services.EnhancedInstancesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class InstanceController {
	
	private EnhancedInstancesService instances;
	
	@Autowired
	public InstanceController(EnhancedInstancesService instances) {
		this.instances = instances;
	}
	
	@RequestMapping(
			path = "/iob/instances/{userDomain}/{userEmail}",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public InstanceBoundary createInstance(
			@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail,
			@RequestBody InstanceBoundary instance) {
		return this.instances.createInstance(userDomain, userEmail, instance);
	}
	
	@RequestMapping(
			path = "/iob/instances/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}/children",
			method = RequestMethod.PUT,
			consumes = MediaType.APPLICATION_JSON_VALUE)	
	public void bindInstanceToChild(
			@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail,
			@PathVariable("instanceDomain") String instanceDomain,
			@PathVariable("instanceId") String instanceId,
			@RequestBody InstanceId iId) {
		this.instances.addChildToParent(
				userDomain, userEmail, 
				instanceDomain, instanceId, 
				iId);
	}
	
	@RequestMapping(
			path = "/iob/instances/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}/children",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public InstanceBoundary[] getInstanceChildren(
			@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail,
			@PathVariable("instanceDomain") String instanceDomain,
			@PathVariable("instanceId") String instanceId,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		boolean sortAscending = true;
		String[] sortBy = new String[]{"createdTimestamp", "id"};
		return this.instances
				.getChildren(userDomain, userEmail,instanceDomain, instanceId, size, page, sortAscending, sortBy)
				.toArray(new InstanceBoundary[0]);
	}
	
	@RequestMapping(
			path = "/iob/instances/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}/parents",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public InstanceBoundary[] getInstanceParents(
			@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail,
			@PathVariable("instanceDomain") String instanceDomain,
			@PathVariable("instanceId") String instanceId,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		boolean sortAscending = true;
		String[] sortBy = new String[]{"createdTimestamp", "id"};
		return this.instances
				.getParents(userDomain, userEmail, instanceDomain, instanceId, size, page, sortAscending, sortBy)
				.toArray(new InstanceBoundary[0]);
	}
	
	@RequestMapping(
			path = "/iob/instances/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}",
			method = RequestMethod.PUT,
			consumes = MediaType.APPLICATION_JSON_VALUE)	
	public void updateInstance(
			@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail,
			@PathVariable("instanceDomain") String instanceDomain,
			@PathVariable("instanceId") String instanceId,
			@RequestBody InstanceBoundary update) {
		this.instances.updateInstance(
				userDomain, userEmail, 
				instanceDomain, instanceId, 
				update);
	}
	
	@RequestMapping(
			path = "/iob/instances/{userDomain}/{userEmail}/{instanceDomain}/{instanceId}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public InstanceBoundary retrieveInstance(
			@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail,
			@PathVariable("instanceDomain") String domain,
			@PathVariable("instanceId") String id) {
		return this.instances.getSpecificInstance(
				userDomain, userEmail, 
				domain, id);
	}
	
	@RequestMapping(
			path = "/iob/instances/{userDomain}/{userEmail}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public InstanceBoundary[] getAllInstances(
			@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		boolean sortAscending = true;
		String[] sortBy = new String[]{"createdTimestamp", "id"};
		return this.instances
			.getAllInstances(userDomain, userEmail, size, page,  sortAscending, sortBy)
			.toArray(new InstanceBoundary[0]);
		}
	
}
