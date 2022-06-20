package iob.logic.services;

import java.util.List;

import iob.ActivitiesAPI.ActivityBoundary;

public interface EnhancedActivitiesService extends ActivitiesService {
	
	public List<ActivityBoundary> getAllActivities(
			String adminDomain, String adminEmail,
            int size, int page,
            boolean sortAscending, String[] sortBy);
}
