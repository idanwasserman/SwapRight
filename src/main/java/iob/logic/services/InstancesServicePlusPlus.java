package iob.logic.services;

import iob.InstancesAPI.InstanceBoundary;
import iob.InstancesAPI.InstanceId;

import java.util.List;

public interface InstancesServicePlusPlus extends InstancesService {

    public void addChildToParent(
            String userDomain,
            String userEmail,
            String instanceDomain,
            String parentId,
            InstanceId childId);

    @Deprecated
    public InstanceBoundary getParent(
            String userDomain,
            String userEmail,
            String instanceDomain,
            String childId);

    @Deprecated
    public List<InstanceBoundary> getChildren(
            String userDomain,
            String userEmail,
            String instanceDomain,
            String parentId);
}
