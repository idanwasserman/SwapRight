package iob.logic.services;

import java.util.List;

import iob.InstancesAPI.InstanceBoundary;

public interface EnhancedInstancesService extends InstancesServicePlusPlus {
	
	public List<InstanceBoundary> getAllInstances(
			String userDomain, String userEmail,
			int size, int page,
            boolean sortAscending, String[] sortBy);
	
    public List<InstanceBoundary> getChildren(
            String userDomain, String userEmail,
            String instanceDomain, String parentId,
            int size, int page,
            boolean sortAscending, String[] sortBy);
    
    public List<InstanceBoundary> getParents(
            String userDomain, String userEmail,
            String instanceDomain, String childId,
            int size, int page,
            boolean sortAscending, String[] sortBy);

	public List<InstanceBoundary> getAllInstancesByName(
			String userDomain, String userEmail,
			String name, 
			int size, int page,
			boolean sortAscending, String[] sortBy);

	public List<InstanceBoundary> getAllInstancesByType(
			String userDomain, String userEmail,
			String type, 
			int size, int page,
			boolean sortAscending, String[] sortBy);

	public List<InstanceBoundary> getAllInstancesByLocation(
			String userDomain, String userEmail,
			double lat, double lng,	double distance,
			int size, int page,
			boolean sortAscending, String[] sortBy);

	public List<InstanceBoundary> getAllInstancesByCreation(
			String userDomain, String userEmail,
			String creationWindow,
			int size, int page,
			boolean sortAscending, String[] sortBy);



}
