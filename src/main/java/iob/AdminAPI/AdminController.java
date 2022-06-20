package iob.AdminAPI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import iob.ActivitiesAPI.ActivityBoundary;
import iob.UsersAPI.UserBoundary;
import iob.logic.services.EnhancedActivitiesService;
import iob.logic.services.EnhancedInstancesService;
import iob.logic.services.EnhancedUsersService;

@RestController
public class AdminController {
	
	private EnhancedActivitiesService activities;
	private EnhancedInstancesService instances;
	private EnhancedUsersService users;

	@Autowired
	public AdminController(
			EnhancedActivitiesService activities,
			EnhancedInstancesService instances,
			EnhancedUsersService users) {
		this.activities = activities;
		this.instances = instances;
		this.users = users;
	}
	
	@RequestMapping(
			path="/iob/admin/users/{userDomain}/{userEmail}",
			method = RequestMethod.DELETE)
	public void DeleteAllUsers (		
			@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail) {
		users.deleteAllUsers(userDomain, userEmail);
	}
	
	@RequestMapping(
			path="/iob/admin/instances/{userDomain}/{userEmail}",
			method = RequestMethod.DELETE)
	public void DeleteAllInstances (	
			@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail) {
		instances.deleteAllInstances(userDomain, userEmail);
	}
	
	@RequestMapping(
			path="/iob/admin/activities/{userDomain}/{userEmail}",
			method = RequestMethod.DELETE)
	public void DeleteAllActivities (		
			@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail) {
		activities.deleteActivities(userDomain, userEmail);
	}
	
	@RequestMapping(
			path = "/iob/admin/users/{userDomain}/{userEmail}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public UserBoundary[] exportAllUsers(		
			@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		boolean sortAscending = true;
		String[] sortBy = new String[]{ "role", "id" };
		return this.users
				.getAllUsers(userDomain, userEmail, size, page, sortAscending, sortBy)
				.toArray(new UserBoundary[0]); 
	}
	
	@RequestMapping(
			path = "/iob/admin/activities/{userDomain}/{userEmail}",
			method = RequestMethod.GET,
			produces = MediaType.APPLICATION_JSON_VALUE)
	public ActivityBoundary[] exportAllActivities(		
			@PathVariable("userDomain") String userDomain,
			@PathVariable("userEmail") String userEmail,
			@RequestParam(name = "size", required = false, defaultValue = "10") int size,
			@RequestParam(name = "page", required = false, defaultValue = "0") int page) {
		boolean sortAscending = true;
		String[] sortBy = new String[]{ "type", "createdTimestamp", "id" };
		return this.activities
				.getAllActivities(userDomain, userEmail, size, page, sortAscending, sortBy)
				.toArray(new ActivityBoundary[0]);
	}
	
}

