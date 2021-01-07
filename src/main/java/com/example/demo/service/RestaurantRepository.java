package com.example.demo.service;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.model.RestaurantDetails;

import java.util.Optional;

import org.bson.types.ObjectId;

public interface RestaurantRepository extends MongoRepository<RestaurantDetails, ObjectId> {
	Optional<RestaurantDetails> findByUsername(String username);
}
