package iob.logic.activities.useCases;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import iob.ActivitiesAPI.ActivityBoundary;
import iob.logic.activities.AbstractUseCase;
import iob.logic.activities.ActivityInvoker;

@Component("ListActivityTypes")
public class ListActivityTypes extends AbstractUseCase {

	private ApplicationContext applicationContext;
	
	@Autowired
	public ListActivityTypes(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	@Override
	public Object invoke(ActivityBoundary activity) {
		String[] activityTypes = this.applicationContext
				.getBeanNamesForType(ActivityInvoker.class);
		Map<String, Object> retval = new HashMap<>();
		retval.put("activityTypes", activityTypes);
		return retval;
	}

}
