package iob.data.daos;
import org.springframework.data.repository.PagingAndSortingRepository;

import iob.data.entities.UserEntity;

public interface UserDao 
	extends PagingAndSortingRepository<UserEntity, String> { // String is UserId
	//CrudRepository<UserEntity, String> { // String is UserId

}

