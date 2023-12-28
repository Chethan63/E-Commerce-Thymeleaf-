package com.jsp.ecommerce.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.jsp.ecommerce.dto.Product;
import com.jsp.ecommerce.repository.ProductRepository;

@Repository
public class ProductDao {

	@Autowired
	ProductRepository productRespository;

	public void save(Product product) {
		productRespository.save(product);
	}

	public List<Product> fetchAll() {

		return productRespository.findAll();
	}

	public Product findById(int id) {
		return productRespository.findById(id).orElse(null);
	}

	public void Delete(Product product) {
		productRespository.delete(product);
	}

	public List<Product> fetchDisplayProducts() {
		return productRespository.findByDisplayTrue();
	}


	

}
