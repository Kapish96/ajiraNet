package com.example.ajiranet.web;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.ajiranet.model.DeviceVO;
import com.example.ajiranet.model.Request;
import com.example.ajiranet.service.OperationService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public class NetworkOperationController {

	@Autowired
	private OperationService operationService;

	@PostMapping(value = "/ajiranet/process", consumes = MediaType.TEXT_PLAIN_VALUE)
	public String executeOperation(@RequestBody String body, HttpServletRequest request) {

		try {

			String requestArray[] = body.split("\n");

			String command[] = requestArray[0].split(" ");

			if ((command[0].equalsIgnoreCase("CREATE") || command[0].equalsIgnoreCase("MODIFY"))) {
				Request requestBody = null;
				if (requestArray.length == 3) {
					ObjectMapper objectMapper = new ObjectMapper();
					requestBody = objectMapper.readValue(requestArray[2], Request.class);
				}
				request.setAttribute("body", requestBody);
			}

			if(command[0].equalsIgnoreCase("FETCH") && command[1].equalsIgnoreCase("/devices"))
				command[1] = "/fetch"+command[1];
			
			return "forward:" + command[1];

		} catch (Exception e) {

			return e.getMessage();
		}
	}

	@PostMapping(value = "/devices", consumes = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public ResponseEntity<String> addDevice(HttpServletRequest servletRequest) {

		try {
			
			Request request = (Request) servletRequest.getAttribute("body");

			operationService.validate(request);
			return new ResponseEntity(String.format("Successfully added '%s'", request.getName()), HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value = "/connections")
	@ResponseBody
	public ResponseEntity<String> addConnection(HttpServletRequest servletRequest) {

		try {
			Request request = (Request) servletRequest.getAttribute("body");
//			System.err.println("request object "+request);
			operationService.validateDeviceConnectionRequest(request);
			return new ResponseEntity("Successfully Connected", HttpStatus.OK);

		} catch (Exception e) {
//			e.printStackTrace();
			return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value = "/devices/{name}/strength")
	@ResponseBody
	public ResponseEntity<String> modigyStrength(HttpServletRequest servletRequest, @PathVariable("name") String name) {

		try {
			Request request = (Request) servletRequest.getAttribute("body");

			operationService.validateModifyRequest(name, request.getValue());
			return new ResponseEntity("Successfully defined strength", HttpStatus.OK);

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value = "/info-routes")
	@ResponseBody
	public ResponseEntity<String> fetchRoute(@RequestParam(name = "from") String from,
			@RequestParam(name = "to") String to) {

		try {
			String route = operationService.fetchRoute(from, to);
			return new ResponseEntity(route, HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value = "/fetch/devices")
	@ResponseBody
	public List<DeviceVO> fetchDevices() {

		return operationService.fetchDevices();
	}

}
