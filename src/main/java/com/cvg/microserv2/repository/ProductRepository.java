package com.cvg.microserv2.repository;

import com.cvg.microserv2.entity.Category;
import com.cvg.microserv2.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    public List<Product> findByCategory(Category category);
}
