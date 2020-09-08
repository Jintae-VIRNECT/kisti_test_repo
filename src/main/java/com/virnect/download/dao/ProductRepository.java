package com.virnect.download.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.virnect.download.domain.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
	Optional<Product> findByName(String productName);
}
