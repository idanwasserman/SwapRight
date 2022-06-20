package iob.ActivitiesAPI;

import java.util.Date;
import java.util.Map;

import iob.InstancesAPI.Instance;

/*
ActivityBoundary sample JSON:
{ 
	"activityId": { 
		"domain":"2022a.demo", 
		"id":"352" 
	}, 
	"type":"demoActivityType", 
	"instance":{
		"instanceId":{
			"domain":"2022a.demo",
			"id":"352"
		}
	},
	"createdTimestamp":"2021-10-24T19:55:05.248+0000",  
	"invokedBy":{
		"userId":{
			"domain":"2022a.demo",
			"email":"user3@demo.com"
		}
	},
	"activityAttributes":{ 
		"key1":"can be set to any value you wish", 
		"key2":{
			"key2Subkey1":"can be nested json"
		}
	} 
}
*/

public class ActivityBoundary {
	
	private ActivityId activityId;
	private String type;
	private Instance instance;
	private Date createdTimestamp;
	private InvokedBy invokedBy;
	private Map<String, Object> activityAttributes;
	
	public ActivityBoundary() {
	}

	public ActivityBoundary(
			String type, 
			Instance instance, 
			InvokedBy invokedBy,
			Map<String, Object> activityAttributes) {
		super();
		this.type = type;
		this.instance = instance;
		this.invokedBy = invokedBy;
		this.activityAttributes = activityAttributes;
	}

	public ActivityBoundary(
			ActivityId activityId, 
			String type, 
			Instance instance, 
			Date createdTimestamp,
			InvokedBy invokedBy,
			Map<String, Object> activityAttributes) {
		this();
		this.activityId = activityId;
		this.type = type;
		this.instance = instance;
		this.createdTimestamp = createdTimestamp;
		this.invokedBy = invokedBy;
		this.activityAttributes = activityAttributes;
	}

	public ActivityId getActivityId() {
		return activityId;
	}

	public void setActivityId(ActivityId activityId) {
		this.activityId = activityId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Instance getInstance() {
		return instance;
	}

	public void setInstance(Instance instance) {
		this.instance = instance;
	}

	public Date getCreatedTimestamp() {
		return createdTimestamp;
	}

	public void setCreatedTimestamp(Date createdTimestamp) {
		this.createdTimestamp = createdTimestamp;
	}

	public Map<String, Object> getActivityAttributes() {
		return activityAttributes;
	}

	public void setActivityAttributes(Map<String, Object> activityAttributes) {
		this.activityAttributes = activityAttributes;
	}

	public InvokedBy getInvokedBy() {
		return invokedBy;
	}

	public void setInvokedBy(InvokedBy invokedBy) {
		this.invokedBy = invokedBy;
	}

	@Override
	public String toString() {
		return "ActivityBoundary [activityId=" + activityId + ", type=" + type + ", instance=" + instance
				+ ", createdTimestamp=" + createdTimestamp + ", invokedBy=" + invokedBy
				+ ", activityAttributes=" + activityAttributes + "]";
	}

	
}