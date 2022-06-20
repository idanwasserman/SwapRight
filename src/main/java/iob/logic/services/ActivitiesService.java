package iob.logic.services;

import java.util.List;

import iob.ActivitiesAPI.ActivityBoundary;

public interface ActivitiesService {
	
	public Object invokeActivity(
			ActivityBoundary activity);
	
	@Deprecated
	public List<ActivityBoundary> getAllActivities(
			String adminDomain, 
			String adminEmail);
	
	public void deleteActivities(
			String adminDomain, 
			String adminEmail);

}
