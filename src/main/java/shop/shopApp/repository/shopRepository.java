package shop.shopApp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import shop.shopApp.model.shopModel;

@Repository
public interface shopRepository extends JpaRepository<shopModel, Long> {

	@Query(value="select * from inventory a where a.item = :item", nativeQuery=true)
    List<shopModel> getItem(String item);
}