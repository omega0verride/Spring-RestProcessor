package org.indritbreti.restprocessordemo.API.DemoEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, org.indritbreti.restprocessordemo.API.DemoEntity.ProductDynamicQueryRepository {
    Product findProductById(Long id);
}
