package com.example.demo.util;

import java.security.SecureRandom;
import java.util.Random;

import org.springframework.stereotype.Component;

import com.example.demo.model.Address;

@Component
public class Utilities {
	
	private final Random RANDOM = new SecureRandom();
	private final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	
	public String generateId(int length) 
	{
		return generateRandomString(length);
	}
	
	public String generateRandomString(int length) 
	{
		StringBuilder returnValue = new StringBuilder(length);
		
		for (int i = 0; i < length; i++) 
		{
			returnValue.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
		}
		
		return new String(returnValue);
	}
	
	public double getDistance(Address address1, Address address2) 
	{
		double xdiff = address1.getX() - address2.getX();
		
		xdiff = xdiff * xdiff;
		
		double ydiff = address1.getY() - address2.getY();
		
		ydiff = ydiff * ydiff;
		
		return Math.sqrt(xdiff + ydiff);
	}
}
