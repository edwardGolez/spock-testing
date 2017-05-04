package com.synacy.lesson03.demo.domain;

public class BatchShippingException extends Exception {

	public BatchShippingException(Exception e) {
		super("Failed to process batch shipment", e);
	}

	public BatchShippingException(String message, Exception e) {
		super(message, e);
	}

	public BatchShippingException() {

	}
}
