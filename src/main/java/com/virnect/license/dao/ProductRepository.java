package com.virnect.license.dao;

import com.virnect.license.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * @author jeonghyeon.chang (johnmark)
 * @project PF-License
 * @email practice1356@gmail.com
 * @description
 * @since 2020.04.13
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductType_NameAndName(String productType, String productName);
}
