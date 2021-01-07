package com.example.demo.service;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.example.demo.model.CustomerDetails;
import org.bson.types.ObjectId;

public interface CustomerRepository extends MongoRepository<CustomerDetails,  ObjectId>{
	Optional<CustomerDetails> findByUsername(String username);
}
