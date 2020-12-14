package com.example.ajiranet.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.ajiranet.model.Request;
import com.example.ajiranet.service.OperationService;

@RestController
public class OperationRestController {

	@Autowired
	private OperationService operationService;

	@PostMapping(value = "/devicesss")
	public ResponseEntity<String> addDevice(@RequestBody Request request) {

		try {
			System.err.println("called");

			operationService.validate(request.getName(), request.getType(), 5l);
			return new ResponseEntity("Successfully added", HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

}
