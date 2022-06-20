package iob.InstancesAPI;

/*
Instance sample JSON:
{ 
	"instanceId":{
		"domain":"2022a.demo",
		"id":"352"
	}
}
*/

public class Instance {
	
	private InstanceId instanceId;
	
	public Instance() {
	}
	
	public Instance(InstanceId instanceId) {
		this();
		this.instanceId = instanceId;
	}

	public InstanceId getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(InstanceId instanceId) {
		this.instanceId = instanceId;
	}

	@Override
	public String toString() {
		return "Instance [instanceId=" + instanceId + "]";
	}
	
}
