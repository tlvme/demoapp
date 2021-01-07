package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.OrderDetails;
import com.example.demo.model.OrderRequest;
import com.example.demo.model.RestaurantDetails;
import com.example.demo.service.CustomerRepository;
import com.example.demo.service.DishRepository;
import com.example.demo.service.OrderRepository;
import com.example.demo.service.RestaurantRepository;
import com.example.demo.util.Utilities;
import com.example.demo.model.CustomerDetails;
import com.example.demo.model.DishDetails;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/customer")
public class CustomerController {

	@Autowired
	private DishRepository dishRepository;

	@Autowired
	private OrderRepository orderRepository;

	@Autowired
	private CustomerRepository customerRepository;
	
	@Autowired
	private RestaurantRepository restaurantRepository;
	
	@Autowired
	Utilities utils;

	@PostMapping("/order")
	@PreAuthorize("hasRole('CUSTOMER')")
	public String placeOrder(@RequestBody OrderRequest orderRequest, Authentication authentication) {
		
		if (!dishRepository.existsByName(orderRequest.getName())) return "not a valid dish";
		
		OrderDetails orderDetails = new OrderDetails();
		orderDetails = new OrderDetails();
		
		orderDetails.setDishName(orderRequest.getName());

		
		BeanUtils.copyProperties(orderRequest, orderDetails);

		String customer = authentication.getName();
		
		String orderId = utils.generateId(5);
		
		while (orderRepository.existsByOrderId(orderId))
			orderId = utils.generateId(5);
		
		orderDetails.setOrderId(orderId);
		
		Optional<CustomerDetails> opCustomerDetails = customerRepository.findByUsername(customer);
		if (opCustomerDetails.isEmpty())
			return "no such customer";
		
		Optional<RestaurantDetails> opRestaurantDetails = restaurantRepository.findByUsername("restaurant");
		if (opRestaurantDetails.isEmpty())
			return "no such restaurant";
		
		orderDetails.setRestaurantAddress(opRestaurantDetails.get().getAddress());
		orderDetails.setCustomerUsername(customer);
		orderDetails.setCustomerAddress(opCustomerDetails.get().getAddress());
		orderDetails.setStatus("Ordered");

		orderRepository.save(orderDetails);
		
		return "Order placed with order id " + orderDetails.getOrderId();
	}
	
	@PostMapping("/order/{orderId}/cancel")
	@PreAuthorize("hasRole('CUSTOMER')")
	public String cancelOrder(@PathVariable String orderId, Authentication authentication) 
	{
		if (!orderRepository.existsByOrderId(orderId)) return "order not found";
		
		OrderDetails orderDetails = orderRepository.findByOrderId(orderId).get();
		
		String customer = authentication.getName();
		
		if (!customer.equals(orderDetails.getCustomerUsername())) return "not the customer's order.";
		
		if (orderDetails.getStatus().equals("Cancelled")) return "order has already been cancelled.";
		
		if (orderDetails.getStatus().equals("Delivered")) return "order has already been delivered.";
		
		orderDetails.setStatus("Cancelled");
		
		orderRepository.save(orderDetails);
		
		return "Order with order id " + orderId + " cancelled.";
	}
	
	@GetMapping("/menu")
	@PreAuthorize("hasRole('CUSTOMER')")
	public List<DishDetails> getDishes() 
	{
		return dishRepository.findAll();
	}
	
	@GetMapping("/order/{orderId}/price")
	@PreAuthorize("hasRole('CUSTOMER')")
	public double getPrice(@PathVariable String orderId) 
	{
		if (!orderRepository.existsByOrderId(orderId)) throw new RuntimeException("order not found");
		
		OrderDetails orderDetails = orderRepository.findByOrderId(orderId).get();
		
		
		if (!dishRepository.existsByName(orderDetails.getDishName())) throw new RuntimeException("dish not found");
		
		DishDetails dishDetails = dishRepository.findByName(orderDetails.getDishName()).get();
		
		double baseCost = dishDetails.getCost() * orderDetails.getQuantity();
		
		double taxes = 0.05 * baseCost;
		
		double distance = utils.getDistance(orderDetails.getCustomerAddress(), orderDetails.getRestaurantAddress());
		
		double deliveryCost = distance;
		
		return baseCost + taxes + deliveryCost;
		
	}
	
	@GetMapping("/order/{orderId}/time")
	@PreAuthorize("hasRole('CUSTOMER')")
	public double getTime(@PathVariable String orderId) 
	{
		if (!orderRepository.existsByOrderId(orderId)) throw new RuntimeException("order not found");
		
		OrderDetails orderDetails = orderRepository.findByOrderId(orderId).get();
		
		
		if (!dishRepository.existsByName(orderDetails.getDishName())) throw new RuntimeException("dish not found");
		
		DishDetails dishDetails = dishRepository.findByName(orderDetails.getDishName()).get();
		
		double prepTime = dishDetails.getPrepTime() * orderDetails.getQuantity();
		
		double distance = utils.getDistance(orderDetails.getCustomerAddress(), orderDetails.getRestaurantAddress());
		
		double deliveryTime = distance * 1.5;
		
		return deliveryTime + prepTime;
		
	}
	
	
	
	
	
}
