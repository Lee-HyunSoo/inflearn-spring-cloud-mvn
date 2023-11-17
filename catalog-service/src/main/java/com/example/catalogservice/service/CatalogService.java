package com.example.catalogservice.service;

import com.example.catalogservice.jpa.CatalogEntity;

public interface CatalogService {

    /* 전체 catalog 반환 */
    Iterable<CatalogEntity> getAllCatalogs();
}
