package iob.logic.activities;

import iob.ActivitiesAPI.ActivityBoundary;

public interface ActivityInvoker {
	
	public Object invoke(ActivityBoundary activity);

}
