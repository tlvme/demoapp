package com.example.demo.controller;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.AddressResponse;
import com.example.demo.model.OrderDetails;
import com.example.demo.service.OrderRepository;
import com.example.demo.util.Utilities;



@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/driver")
public class DriverController {

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	Utilities utils;
	
	@PostMapping("/order/{orderId}/pickup")
	@PreAuthorize("hasRole('DRIVER')")
	public String cancelOrder(@PathVariable String orderId, Authentication authentication) 
	{
		if (!orderRepository.existsByOrderId(orderId)) return "order not found";
		
		OrderDetails orderDetails = orderRepository.findByOrderId(orderId).get();
		
		if (orderDetails.getStatus().equals("Cancelled")) return "Order has been cancelled.";
		
		if (orderDetails.getStatus().equals("Delivered")) return "Invalid request. Order has already been delivered.";
		
		if (orderDetails.getStatus().equals("Picked Up")) return "Invalid request. Order has already been delivered.";
		
		
		orderDetails.setStatus("Picked Up");
		
		orderRepository.save(orderDetails);
		
		return "Order with order id " + orderId + " picked up by " + authentication.getName();
	}
	
	@PostMapping("/order/{orderId}/deliver")
	@PreAuthorize("hasRole('DRIVER')")
	public String deliverOrder(@PathVariable String orderId, Authentication authentication) 
	{
		if (!orderRepository.existsByOrderId(orderId)) return "order not found";
		
		OrderDetails orderDetails = orderRepository.findByOrderId(orderId).get();
		
		if (orderDetails.getStatus().equals("Cancelled")) return "Order has been cancelled.";
		
		if (orderDetails.getStatus().equals("Delivered")) return "Invalid request. Order has already been delivered.";
		
		if (!orderDetails.getStatus().equals("Picked Up")) return "Can't deliver before picking up the order.";
		
		
		orderDetails.setStatus("Delivered");
		
		orderRepository.save(orderDetails);
		
		return "Order with order id " + orderId + " delivered by " + authentication.getName();
	}
	
	@GetMapping("/order/{orderId}")
	@PreAuthorize("hasRole('DRIVER')")
	public AddressResponse getAddress(@PathVariable String orderId) 
	{
		if (!orderRepository.existsByOrderId(orderId)) throw new RuntimeException("order not found");
		
		OrderDetails orderDetails = orderRepository.findByOrderId(orderId).get();
		
		AddressResponse addressResponse = new AddressResponse();
		
		BeanUtils.copyProperties(orderDetails, addressResponse);
		
		return addressResponse;
	}
}
	

