package com.cvg.microserv2.controller;

import com.cvg.microserv2.entity.Category;
import com.cvg.microserv2.entity.Product;
import com.cvg.microserv2.services.ProductService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * https://www.youtube.com/watch?v=p5uMy2DLE7A&list=PLxy6jHplP3Hi_W8iuYSbAeeMfaTZt49PW&index=7&ab_channel=DigitalLabAcademy
 */

@RestController
@RequestMapping(value = "/products")
public class ProductController {

    @Autowired
    private ProductService productService;



    @GetMapping
    public ResponseEntity<List<Product>> listProduct(@RequestParam(value = "categoryId", required = false) Long categoryId) {
        List<Product> products = new ArrayList<>();
        if (categoryId == null) {
            products = this.productService.listAllProduct();

            return products.isEmpty()
                    ? ResponseEntity.noContent().build()
                    : ResponseEntity.ok(products);
        } else {
            products = this.productService.findByCategory(Category.builder().id(categoryId).build());

            return products.isEmpty()
                    ? ResponseEntity.notFound().build()
                    : ResponseEntity.ok(products);
        }
    }

    @GetMapping(value = "/{productId}")
    public ResponseEntity<Product> getProduct(@PathVariable(value = "productId", required = false) Long productId) {
        Product p = this.productService.getProduct(productId);
        if (p == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(p);
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product, BindingResult result) {

        if (result.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, this.formatMessage(result));
        }

        Product nuevoProducto = this.productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable("productId") Long productId, @RequestBody Product product) {
        if (productId == null || productId < 0) {
            return ResponseEntity.noContent().build();
        }
        product.setId(productId);
        Product updatedProduct = this.productService.updateProduct(product);

        if (updatedProduct == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedProduct);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Product> deleteProduct(@PathVariable Long id) {
        Product deletedProduct = this.productService.deleteProduct(id);

        if (deletedProduct == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(deletedProduct);
    }

    @GetMapping("/{productId}/stock")
    public ResponseEntity<Product> updateStock(@PathVariable Long productId, @RequestParam(value = "quantity", required = true) Double stock){
        if (productId == null || productId < 1) {
            return ResponseEntity.noContent().build();
        }
        if (stock == null || stock < 1) {
            return ResponseEntity.noContent().build();
        }
        Product updateStock = this.productService.updateStock(productId, stock);
        if (updateStock == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updateStock);

    }

    private String formatMessage(BindingResult result) {
        List<Map<String, String>> errors = result.getFieldErrors().stream()
                .map(err -> {
                    Map<String, String> error = new HashMap<>();
                    error.put(err.getField(), err.getDefaultMessage());
                    return error;
                    }).collect(Collectors.toList());

        ErrorMessage errorMessage = ErrorMessage.builder().code("01").messages(errors).build();

        /**
         * GENERAR UN JSON STRING USANDO JACKSON
         */
        ObjectMapper mapper = new ObjectMapper();
        String jsonResult = "";

        try {
            jsonResult = mapper.writeValueAsString(errorMessage);
        } catch (JsonProcessingException e){
            e.printStackTrace();
        }

        return jsonResult;
    }
}
