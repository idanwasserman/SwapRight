package iob.logic;

import iob.ActivitiesAPI.ActivityBoundary;
import iob.ActivitiesAPI.ActivityId;
import iob.ActivitiesAPI.InvokedBy;
import iob.InstancesAPI.*;
import iob.UsersAPI.UserBoundary;
import iob.UsersAPI.UserId;
import iob.UsersAPI.UserRole;
import iob.data.entities.ActivityEntity;
import iob.data.entities.InstanceEntity;
import iob.data.entities.UserEntity;
import iob.data.entities.UserRoleEntity;

import org.springframework.data.geo.Point;
import org.springframework.stereotype.Component;

@Component
public class EntityBoundaryConverter {
    final String DELIMITER = "&&";
    final int DOMAIN_PART = 0;
    final int UUID_PART = 1;
    final int EMAIL_PART = 1;

    // Default values for User conversion
    final boolean defaultActive = false;
    final double defaultLocationLat = 0.0;
    final double defaultLocationLng = 0.0;

    public String createUniqueId(String domain, String id) {
        return domain + DELIMITER + id;
    }

    public String getPartFromId(String id, int part) {
        return id.split(DELIMITER)[part];
    }

    public ActivityEntity convertToEntity(ActivityBoundary input) {
        ActivityEntity output = new ActivityEntity();

        if (input.getType() != null) {
            output.setType(input.getType());
        }

        if (input.getActivityId() != null) {
            if (input.getActivityId().getDomain() != null &&
                    input.getActivityId().getId() != null) {
                String id = createUniqueId(input.getActivityId().getDomain(), input.getActivityId().getId());
                output.setId(id);
            }
        }

        if (input.getCreatedTimestamp() != null) {
            output.setCreatedTimestamp(input.getCreatedTimestamp());
        }

        if (input.getInvokedBy() != null) {
            if (input.getInvokedBy().getUserId() != null) {
                if (input.getInvokedBy().getUserId().getDomain() != null) {
                    output.setUserDomain(input
                            .getInvokedBy()
                            .getUserId()
                            .getDomain());
                }

                if (input.getInvokedBy().getUserId().getEmail() != null) {
                    output.setUserEmail(input
                            .getInvokedBy()
                            .getUserId()
                            .getEmail());
                }
            }
        }

        if (input.getInstance() != null) {
            if (input.getInstance().getInstanceId() != null) {
                if (input.getInstance().getInstanceId().getDomain() != null) {
                       output.setInstanceDomain(input
                            .getInstance()
                            .getInstanceId()
                            .getDomain());
                }

                if (input.getInstance().getInstanceId().getId() != null) {
                       output.setInstanceId(input
                               .getInstance()
                               .getInstanceId()
                               .getId());
                }
            }
        }

        output.setActivityAttributes(input.getActivityAttributes());


        return output;
    }

    public ActivityBoundary convertToBoundary(ActivityEntity input) {
        ActivityBoundary output = new ActivityBoundary();

        String domain = getPartFromId(input.getId(), DOMAIN_PART);
        String uuid = getPartFromId(input.getId(), UUID_PART);
        output.setActivityId(
                new ActivityId(domain, uuid));

        output.setType(input.getType());

        output.setInstance(
                new Instance(
                        new InstanceId(
                                input.getInstanceDomain(),
                                input.getInstanceId())));

        output.setCreatedTimestamp(input.getCreatedTimestamp());

        output.setInvokedBy(
                new InvokedBy(
                        new UserId(
                                input.getUserDomain(),
                                input.getUserEmail())));

        output.setActivityAttributes(input.getActivityAttributes());

        return output;
    }

    public InstanceEntity convertToEntity(InstanceBoundary input) {
        InstanceEntity output = new InstanceEntity();

        if (input.getInstanceId() != null) {
            if (input.getInstanceId().getDomain() != null &&
                    input.getInstanceId().getId() != null) {
                String id = createUniqueId(input.getInstanceId().getDomain(), input.getInstanceId().getId());
                output.setId(id);
            }
        }

        if (input.getType() != null) {
            output.setType(input.getType());
        }

        if (input.getName() != null) {
            output.setName(input.getName());
        }

        if (input.getActive() != null) {
            output.setActive(input.getActive());
        } else {
            output.setActive(defaultActive);
        }

        if (input.getCreatedTimestamp() != null) {
            output.setCreatedTimestamp(input.getCreatedTimestamp());
        }

        if (input.getCreatedBy() != null) {
            if (input.getCreatedBy().getUserId() != null) {
                if (input.getCreatedBy().getUserId().getDomain() != null) {
                    output.setUserDomain(input
                            .getCreatedBy()
                            .getUserId()
                            .getDomain());
                }

                if (input.getCreatedBy().getUserId().getEmail() != null) {
                    output.setUserEmail(input
                            .getCreatedBy()
                            .getUserId()
                            .getEmail());
                }
            }
        }

        if (input.getLocation() != null) {
        	double lat = 0, lng = 0;
            if (input.getLocation().getLat() != null) {
            	lat = input.getLocation().getLat();
                output.setLat(input.getLocation().getLat());
            } else {
                output.setLat(defaultLocationLat);
            }
            if (input.getLocation().getLng() != null) {
            	lng = input.getLocation().getLng();
                output.setLng(input.getLocation().getLng());
            } else {
                output.setLng(defaultLocationLng);
            }
            output.setPoint(new Point(lat, lng));
        }

        output.setInstanceAttributes(input.getInstanceAttributes());

        return output;
    }

    public InstanceBoundary convertToBoundary(InstanceEntity input) {
        InstanceBoundary output = new InstanceBoundary();

        String domain = getPartFromId(input.getId(), DOMAIN_PART);
        String uuid = getPartFromId(input.getId(), UUID_PART);
        output.setInstanceId(
                new InstanceId(domain, uuid));

        output.setType(input.getType());

        output.setName(input.getName());

        output.setActive(input.getActive());

        output.setCreatedTimestamp(input.getCreatedTimestamp());

        output.setCreatedBy(
                new CreatedBy(
                        new UserId(
                                input.getUserDomain(),
                                input.getUserEmail())));

        output.setLocation(
                new Location(
                        input.getLat(),
                        input.getLng()));

        output.setInstanceAttributes(input.getInstanceAttributes());

        return output;
    }
    
    public UserEntity convertToEntity(UserBoundary input) {
        UserEntity output = new UserEntity();

        if (input.getUserId() != null) {
            if (input.getUserId().getDomain() != null &&
                    input.getUserId().getEmail() != null) {
                String id = createUniqueId(input.getUserId().getDomain(), input.getUserId().getEmail());
                output.setId(id);
            }
        }

        if (input.getRole() != null) {
            output.setRole(convertRole(input.getRole()));
        }

        if (input.getUsername() != null) {
            output.setUsername(input.getUsername());
        }

        if (input.getAvatar() != null) {
            output.setAvatar(input.getAvatar());
        }

        return output;
    }

    public UserBoundary convertToBoundary(UserEntity input) {
        UserBoundary output = new UserBoundary();

        String domain = getPartFromId(input.getId(), DOMAIN_PART);
        String email = getPartFromId(input.getId(), EMAIL_PART);
        output.setUserId(
                new UserId(domain, email));

        output.setRole(convertRole(input.getRole()));

        output.setUsername(input.getUsername());

        output.setAvatar(input.getAvatar());

        return output;
    }

    public UserRoleEntity convertRole(UserRole role) {
        if (role == null)
            return null;
        else
            return UserRoleEntity.valueOf(role.name());
    }

    public UserRole convertRole(UserRoleEntity role) {
        if (role == null)
            return null;
        else
            return UserRole.valueOf(role.name());
    }
}
