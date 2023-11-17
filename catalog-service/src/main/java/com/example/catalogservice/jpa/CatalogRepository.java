package com.example.catalogservice.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CatalogRepository extends JpaRepository<CatalogEntity, Long> {

    /* 단일 데이터 조회 */
    CatalogEntity findByProductId(String productId);
}
