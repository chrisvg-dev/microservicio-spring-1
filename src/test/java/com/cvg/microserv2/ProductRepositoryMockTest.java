package com.cvg.microserv2;

import com.cvg.microserv2.entity.Category;
import com.cvg.microserv2.entity.Product;
import com.cvg.microserv2.repository.ProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Date;
import java.util.List;

@DataJpaTest
public class ProductRepositoryMockTest {
    @Autowired
    private ProductRepository productRepository;

    @Test
    public void whenFindByCategory_thenReturnProductList() {
        Product product01 = Product.builder()
                .name("computer")
                .category( Category.builder().id(1L).name("algo").build() )
                .description("")
                .stock(Double.parseDouble("10"))
                .price(Double.parseDouble("12"))
                .status("Created")
                .createAt(new Date()).build();

        productRepository.save(product01);

        List<Product> found = productRepository.findByCategory(product01.getCategory());

        Assertions.assertThat(found.size()).isEqualTo(2);
    }
}
