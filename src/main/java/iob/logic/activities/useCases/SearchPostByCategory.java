package iob.logic.activities.useCases;

import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;

import iob.ActivitiesAPI.ActivityBoundary;
import iob.logic.activities.AbstractUseCase;
import iob.logic.activities.Attributes;
import iob.logic.activities.InstancesTypes;

/**
 * Using DAO here to implement pagination properly
 * 
 */
@Component("SearchPostByCategory")
public class SearchPostByCategory extends AbstractUseCase {

	@Override
	public Object invoke(ActivityBoundary activity) {
		// Get category to filter
		Map<String, Object> attributes = this.checkActivityAttributes(activity);
		String category = this.getStringAttribute(attributes, Attributes.category.name());
		
		// Get sortBy & sortAsc & size & page for pagination
		int page = (int) this.getNumberAttribute(attributes, Attributes.page.name(), 0);
		int size = (int) this.getNumberAttribute(attributes, Attributes.size.name(), 10);
		String[] sortBy = this.getSortByAttribute(attributes);
		Direction direction = this.getSortAscendingAttribute(attributes) ? Direction.ASC : Direction.DESC;

		// Get all active instances
		return this.instanceDao.findAllByActive(true, Sort.by(direction, sortBy))
				.stream()
				// Filter instances by type == POST
				.filter(i -> i.getType().equals(InstancesTypes.POST.name()))	
				.collect(Collectors.toList())
				.stream()
				// Filter all posts by category
				.filter(i -> this.getStringAttribute(
						i.getInstanceAttributes(), Attributes.category.name()).equals(category))
				// Pagination
				.skip(size * page)
				.limit(size)
				// Convert to boundary
				.map(this.converter::convertToBoundary)
				.collect(Collectors.toList());
	}

}
