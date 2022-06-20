package iob.logic.activities.useCases;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.domain.geo.Metrics;
import org.springframework.stereotype.Component;

import iob.ActivitiesAPI.ActivityBoundary;
import iob.InstancesAPI.InstanceBoundary;
import iob.logic.activities.AbstractUseCase;
import iob.logic.activities.Attributes;
import iob.logic.activities.InstancesTypes;


/**
 * Searching items near user's location
 * Using DAO here to implement pagination properly
 */
@Component("SearchNearbyPosts")
public class SearchNearbyPosts extends AbstractUseCase {


	@Override
	public Object invoke(ActivityBoundary activity) {
		Map<String, Object> attributes = this.checkActivityAttributes(activity);
		
		final int DEFAULT_SIZE = 10;
		final int DEFAULT_PAGE = 0;
		
		// Get user's instance for his location
		InstanceBoundary userInstance = this.getUserInstance(activity);
		
		int size = (int) this.getNumberAttribute(attributes, Attributes.size.name(), DEFAULT_SIZE * 3);
		int page = (int) this.getNumberAttribute(attributes, Attributes.page.name(), DEFAULT_PAGE);
		
		return this.instanceDao
				.findByPointNear(
						getPointFromUser(userInstance), 
						getDistanceDetails(attributes), 
						getPaginationDeatils(attributes, size, page))
				.stream()
				.filter(i -> i.getType().equals(InstancesTypes.POST.name()))
				.skip(size * page)
				.limit(size)
				.map(this.converter::convertToBoundary)
				.collect(Collectors.toList());
	}
	
	private Distance getDistanceDetails(Map<String, Object> attributes) {
		final double DEFAULT_DISTANCE = 10.0;
		double distance = (double) this.getNumberAttribute(attributes, Attributes.distance.name(), DEFAULT_DISTANCE);
		return new Distance(distance, Metrics.KILOMETERS);
	}

	private Point getPointFromUser(InstanceBoundary userInstance) {
		return new Point(
				userInstance.getLocation().getLat(), 
				userInstance.getLocation().getLng());
	}

	private Pageable getPaginationDeatils(Map<String, Object> attributes, int size, int page) {
		String[] sortBy = this.getSortByAttribute(attributes);
		Direction direction = this.getSortAscendingAttribute(attributes) ? Direction.ASC : Direction.DESC;
		return PageRequest.of(page, size * 7, direction, sortBy);
	}

}
