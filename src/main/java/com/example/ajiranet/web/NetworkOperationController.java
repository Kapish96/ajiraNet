package com.example.ajiranet.web;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.ajiranet.model.DeviceVO;
import com.example.ajiranet.model.Request;
import com.example.ajiranet.service.OperationService;

@RestController("/test")
public class NetworkOperationController {

	@Autowired
	private OperationService operationService;

	@PostMapping(value = "/ajiranet/process")
	public String executeOperation(@RequestBody Request request, @RequestHeader Map<String, String> headers,
			RedirectAttributes redirectAttrs) {

		redirectAttrs.getFlashAttributes();
		try {

			String message = null;

//			System.err.println(headers);
			if (headers.containsKey("create")) {
				if (headers.get("create").equals("/devices")) { // add devices

//					operationService.validate(request.getName(), request.getType(), 5l);
//					message = "Successfully added";
					return "forward:" + headers.get("create");
				}

				else { // add connection
					operationService.validateDeviceConnectionRequest(request.getSource(), request.getTargets());
					message = "Successfully Connected";
				}

			} else if (headers.containsKey("modify")) {
				String ar[] = headers.get("modify").split("/");
				operationService.validateModifyRequest(ar[2], request.getValue());
				message = "Successfully defined strength";
			} else if (headers.containsKey("fetch")) {
				String ar[] = headers.get("modify").split("/");
				operationService.validateModifyRequest(ar[2], request.getValue());
				message = "Successfully defined strength";
			}
			return null;
//			return new ResponseEntity(message, HttpStatus.OK);

		} catch (Exception e) {
			return e.getMessage();
//			return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value = "/devices")
	public ResponseEntity<String> addDevice(@RequestBody Request request) {

		try {

			operationService.validate(request.getName(), request.getType(), 5l);
			return new ResponseEntity("Successfully added", HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value = "/connections")
	public ResponseEntity<String> addConnection(@RequestBody Request request) {

		try {

			operationService.validateDeviceConnectionRequest(request.getSource(), request.getTargets());
			return new ResponseEntity("Successfully Connected", HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value = "/devices/{name}/strength")
	public ResponseEntity<String> modigyStrength(@RequestBody Request request, @PathVariable("name") String name) {

		try {

			operationService.validateModifyRequest(name, request.getValue());
			return new ResponseEntity("Successfully defined strength", HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value = "/fetch/info-routes")
	public ResponseEntity<String> fetchRoute(@RequestParam(name = "from") String from,
			@RequestParam(name = "to") String to) {

		try {
			return new ResponseEntity("Successfully added", HttpStatus.OK);

		} catch (Exception e) {
			return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping(value = "/fetch/devices")
	public List<DeviceVO> fetchDevices() {

		return operationService.fetchDevices();
	}

}
