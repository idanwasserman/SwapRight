package iob.ActivitiesAPI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import iob.logic.services.ActivitiesService;

@RestController
public class ActivityController {
	
	private ActivitiesService activities;
	@Autowired
	public ActivityController(ActivitiesService activities) {
		this.activities = activities;
	}
	
	@RequestMapping(
			path = "/iob/activities",
			method = RequestMethod.POST,
			produces = MediaType.APPLICATION_JSON_VALUE,
			consumes = MediaType.APPLICATION_JSON_VALUE)
	public Object invokeActivity (@RequestBody ActivityBoundary activity) {
		return activities.invokeActivity(activity);
	}
	
}