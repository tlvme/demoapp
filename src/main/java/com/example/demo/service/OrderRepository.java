package com.example.demo.service;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import org.bson.types.ObjectId;
import com.example.demo.model.OrderDetails;

public interface OrderRepository extends MongoRepository<OrderDetails, ObjectId>{
	Optional<OrderDetails> findByOrderId(String publicId);
	
	Boolean existsByOrderId(String orderId);
}
