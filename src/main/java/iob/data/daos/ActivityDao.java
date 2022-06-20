package iob.data.daos;
import org.springframework.data.repository.PagingAndSortingRepository;

import iob.data.entities.ActivityEntity;

public interface ActivityDao
		extends PagingAndSortingRepository<ActivityEntity, String> { // String is ActivityId
		//extends CrudRepository<ActivityEntity, String> { // String is ActivityId

}

