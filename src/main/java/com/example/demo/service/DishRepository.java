package com.example.demo.service;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.model.DishDetails;
import java.util.Optional;

public interface DishRepository extends MongoRepository<DishDetails, Integer>{
	Optional<DishDetails> findByName(String name);
	Boolean existsByName(String name);
}
