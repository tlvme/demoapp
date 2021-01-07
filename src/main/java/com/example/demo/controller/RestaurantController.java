package com.example.demo.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
import com.example.demo.util.PDFGenerator;
import com.example.demo.util.Utilities;
import com.example.demo.model.CustomerDetails;
import com.example.demo.model.DishDetails;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/restaurant")
public class RestaurantController {

	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private DishRepository dishRepository;

	@GetMapping("/orders")
	@PreAuthorize("hasRole('RESTAURANT')")
	public List<OrderDetails> getOrders() {
		return orderRepository.findAll();
	}

	@GetMapping(value = "/orders/{orderId}/invoice", produces = MediaType.APPLICATION_PDF_VALUE)
	@PreAuthorize("hasRole('RESTAURANT')")
	public ResponseEntity<InputStreamResource> getOrderInvoice(@PathVariable String orderId) throws IOException {
		if (!orderRepository.existsByOrderId(orderId))
			throw new RuntimeException("order not found");

		OrderDetails orderDetails = orderRepository.findByOrderId(orderId).get();

		if (!dishRepository.existsByName(orderDetails.getDishName()))
			throw new RuntimeException("dish not found");

		DishDetails dishDetails = dishRepository.findByName(orderDetails.getDishName()).get();

		ByteArrayInputStream bis = PDFGenerator.customerPDFReport(orderDetails, dishDetails);
		
		HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=customers.pdf");
 
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
	}

}
