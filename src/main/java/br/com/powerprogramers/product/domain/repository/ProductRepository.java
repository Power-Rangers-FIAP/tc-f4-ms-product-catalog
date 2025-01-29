package br.com.powerprogramers.product.domain.repository;

import br.com.powerprogramers.product.domain.entity.ProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/** Repository class for JPA connection. */
@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

  /**
   * Finds all registered products according to the past filter.
   *
   * @param pageable Page for the answer
   * @param name product name
   * @param description product description
   * @param active product status
   * @return product page
   */
  @Query(
      """
            SELECT p FROM product p
              WHERE p.active = :active
              AND (:name IS NULL OR p.name like %:name%)
              AND (:description IS NULL OR p.description like %:description%)
              ORDER BY p.id ASC
          """)
  Page<ProductEntity> findAllProducts(
      Pageable pageable, String name, String description, Boolean active);
}
