package iob.data.daos;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import iob.data.entities.InstanceEntity;

public interface InstanceDao 
		extends PagingAndSortingRepository<InstanceEntity, String> {
	
	public List<InstanceEntity> findAllByActiveIsTrue();

	public List<InstanceEntity> findAllByActiveIsTrue(
			Pageable pageable);

	public List<InstanceEntity> findAllByParent_Id(
			@Param("parentId") String ParentId, 
			Pageable pageable);
	
	public List<InstanceEntity> findAllByParent_IdAndActiveIsTrue(
			@Param("parentId") String ParentId, 
			Pageable pageable);
	
	public List<InstanceEntity> findAllByName(
			@Param("name") String name, 
			Pageable pageable);

	public List<InstanceEntity> findAllByNameAndActiveIsTrue(
			@Param("name") String name, 
			Pageable pageable);
	
	public List<InstanceEntity> findAllByType(
			@Param("type") String type, 
			Pageable pageable);

	public List<InstanceEntity> findAllByTypeAndActiveIsTrue(
			@Param("type") String type, 
			Pageable pageable);

	public List<InstanceEntity> findAllByActive(
			@Param("active") boolean active,
			Sort sort);

	public List<InstanceEntity> findByPointNear(
			Point point, 
			Distance distance,
			Pageable pageable);

	public List<InstanceEntity> findByActiveIsTrueAndPointNear(
			Point point, 
			Distance distance,
			Pageable pageable);

}
