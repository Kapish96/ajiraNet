package com.example.ajiranet.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.ajiranet.model.Device;
import com.example.ajiranet.model.DeviceVO;
import com.example.ajiranet.model.Request;

@Service
public class OperationService {

	private List<Device> devicesList = new ArrayList<>();

	public void validate(Request request) {
		
		if (request==null  || request.getName() == null || request.getType() == null) {
			throw new RuntimeException("Invalid command syntax");
		}

		if (isDeviceExist(request.getName())) {
			throw new RuntimeException(String.format("Device '%s' already exist", request.getName()));
		}

		if (!"COMPUTER".equals(request.getType()) && !"REPEATER".equals(request.getType())) {
			throw new RuntimeException(String.format("Type '%s'is not supported", request.getType()));
		}
		addDevice(request.getName(), request.getType(), 5L);
	}

	@Cacheable(cacheNames = "device", key = "#name")
	public Device addDevice(String name, String type, Long strength) {

		try {
			System.out.println("Going to sleep for 5 Secs.. to simulate backend call.");
			Thread.sleep(1000 * 2);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		Device device = new Device();
		device.setName(name);
		device.setType(type);
		device.setStrength(strength);
		devicesList.add(device);
		return null;
	}

	@CachePut(cacheNames = "device")
	public Device addDeviceConncection(String source, List<String> targets) {

		for (Device device : devicesList) {
			if (device.getName().equalsIgnoreCase(source)) {
				List<String> connections = new ArrayList<>();
				for (int i = 0; i < targets.size(); i++) {
					connections.add(targets.get(i));
				}
				device.setConnections(connections);
			}
		}
		return null;
	}

	public void validateDeviceConnectionRequest(Request request) {

		if (request==null || request.getTargets() == null || request.getSource() == null) {
			throw new RuntimeException("Invalid command syntax");
		}

		if (request.getTargets().contains(request.getSource())) {
			throw new RuntimeException("Cannot connect device to itself");
		}

		checkConnectionAndNode(request.getSource(), request.getTargets());

		addDeviceConncection(request.getSource(), request.getTargets());
	}

	private void checkConnectionAndNode(String source, List<String> targets) {

		boolean found = false;

		for (Device device : devicesList) {
			if (device.getName().equalsIgnoreCase(source)) {
				found = true;
				for (int i = 0; i < targets.size(); i++) {

					if (device.getConnections()!=null && device.getConnections().contains(targets.get(i))) {
						throw new RuntimeException("Devices are already connected");
					}
				}
			}
		}

		if (!found) {
			throw new RuntimeException(String.format("Node '%s' not found", source));
		}
	}

	public void validateModifyRequest(String name, String value) {

		if (!isDeviceExist(name)) {
			throw new RuntimeException("Device not found");
		}
		Long strength = null;
		try {
			strength = Long.valueOf(value);
		} catch (Exception e) {
			throw new RuntimeException("Value should be an integer");
		}

		modifyStrength(name, strength);
	}

	@CachePut(cacheNames = "device")
	public Device modifyStrength(String name, Long strength) {

		for (Device device : devicesList) {
			if (device.getName().equalsIgnoreCase(name)) {
				device.setStrength(strength);
			}
		}
		return null;
	}

	public boolean isDeviceExist(String name) {
		for (Device device : devicesList) {
			if (device.getName().equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}

	public String fetchRoute(String from, String to) {
		
		validateFetchRouteRequest(from, to);
		
		Map<String, List<String>> connectionMap = createConnectionMap();

		List<String> path = new ArrayList<>();
		find(from, to, path, connectionMap);
		String route = "No route find";
		
		if (!path.isEmpty()) {
			route = "Route is ";
			for (int i = 0; i < path.size() - 1; i++) {
				route += path.get(i) + "->";
			}
			route += path.get(path.size() - 1);
		}
		return route;
	}

	private void validateFetchRouteRequest(String from, String to) {

		if (from == null || to == null)
			throw new RuntimeException("Invalid Reqest");

		Map<String, String> deviceNameAndTypeMap = createDeviceNameAndTypeMap();

		if (!deviceNameAndTypeMap.containsKey(from))
			throw new RuntimeException(String.format("Node '%s' not found", from));

		if (!deviceNameAndTypeMap.containsKey(to))
			throw new RuntimeException(String.format("Node '%s' not found", to));

		if (deviceNameAndTypeMap.get(from).equals("REPEATER") || deviceNameAndTypeMap.get(to).equals("REPEATER"))
			throw new RuntimeException("Route cannot be calculated with repeater");

	}

	private Map<String, String> createDeviceNameAndTypeMap() {
		return devicesList.stream().collect(Collectors.toMap(Device::getName, Device::getType));
	}

	private Map<String, List<String>> createConnectionMap() {
		Map<String, List<String>> map =  new HashMap<>();
		for (Device device : devicesList) {
			map.put(device.getName(), device.getConnections());
		}
		return map;
	}

	boolean find(String source, String destination, List<String> path, Map<String, List<String>> connectionMap) {
		if (!connectionMap.containsKey(source)) {
			return false;
		}
			
		else if(connectionMap.containsKey(source) && connectionMap.get(source)==null) {
			return false;
		}
			
		path.add(source);
		if (connectionMap.get(source).contains(destination)) {
			path.add(destination);
			return true;
		}

		List<String> connections = connectionMap.get(source);
		for (String ele : connections) {
			if (find(ele, destination, path, connectionMap))
				return true;
		}
		path.remove(source);

		return false;
	}

	public List<DeviceVO> fetchDevices() {

		List<DeviceVO> deviceVOList = new ArrayList<>();

		for (Device device : devicesList) {
			DeviceVO deviceVO = new DeviceVO().builder().name(device.getName()).type(device.getType()).build();
			deviceVOList.add(deviceVO);
		}

		return deviceVOList;
	}

}
