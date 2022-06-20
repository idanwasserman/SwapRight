package iob.InstancesAPI;

import org.springframework.web.bind.annotation.RestController;

import iob.logic.services.EnhancedInstancesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class SearchController {
	
	private EnhancedInstancesService instances;

	@Autowired
	public SearchController(EnhancedInstancesService instances) {
		super();
		this.instances = instances;
	}
	
	@RequestMapping(
			path="/iob/instances/{userDomain}/{userEmail}/search/byName/{name}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public InstanceBoundary[] searchInstancesByName(
			@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail,
			@PathVariable("name") String name,
			@RequestParam(name="size", required = false, defaultValue = "10") int size, 
			@RequestParam(name="page", required = false, defaultValue = "0") int page) {
		String[] sortBy = new String[]{ "createdTimestamp", "id" };
		boolean sortAscending = true;
		return this.instances
			.getAllInstancesByName(userDomain, userEmail, name, size, page, sortAscending, sortBy)
			.toArray(new InstanceBoundary[0]);
	}
	
	@RequestMapping(
			path="/iob/instances/{userDomain}/{userEmail}/search/byType/{type}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public InstanceBoundary[] searchInstancesByType(
			@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail,
			@PathVariable("type") String type,
			@RequestParam(name="size", required = false, defaultValue = "10") int size, 
			@RequestParam(name="page", required = false, defaultValue = "0") int page) {
		String[] sortBy = new String[]{"createdTimestamp", "id"};
		boolean sortAscending = true;
		return this.instances
			.getAllInstancesByType(userDomain, userEmail, type, size, page, sortAscending, sortBy)
			.toArray(new InstanceBoundary[0]);
	}
	
	@RequestMapping(
			path="/iob/instances/{userDomain}/{userEmail}/search/near/{lat}/{lng}/{distance}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public InstanceBoundary[] searchInstancesByLocation(
			@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail,
			@PathVariable("lat") double lat,
			@PathVariable("lng") double lng,
			@PathVariable("distance") double distance,
			@RequestParam(name="size", required = false, defaultValue = "10") int size, 
			@RequestParam(name="page", required = false, defaultValue = "0") int page) {
		String[] sortBy = new String[]{"createdTimestamp", "id"};
		boolean sortAscending = true;
		return this.instances
			.getAllInstancesByLocation(userDomain, userEmail, lat, lng, distance, size, page,	sortAscending, sortBy)
			.toArray(new InstanceBoundary[0]);
	}
	
	@RequestMapping(
			path="/iob/instances/{userDomain}/{userEmail}/search/created/{creationWindow}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public InstanceBoundary[] searchInstancesByCreation(
			@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail,
			@PathVariable("creationWindow") String creationWindow,
			@RequestParam(name="size", required = false, defaultValue = "10") int size, 
			@RequestParam(name="page", required = false, defaultValue = "0") int page) {
		String[] sortBy = new String[]{ "createdTimestamp", "id" };
		boolean sortAscending = true;
		return this.instances
			.getAllInstancesByCreation(userDomain, userEmail, creationWindow, size, page, sortAscending, sortBy)
			.toArray(new InstanceBoundary[0]);
	}
}
