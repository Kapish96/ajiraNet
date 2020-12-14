package com.example.ajiranet.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.example.ajiranet.model.Device;
import com.example.ajiranet.model.DeviceVO;

@Service
public class OperationService {

	private List<Device> devicesList = new ArrayList<>();

	public void validate(String name, String type, Long strength) {

		if (name == null && type == null) {
			throw new RuntimeException("Invalid command syntax");
		}

		if (isDeviceExist(name)) {
			throw new RuntimeException("Device already exist");
		}

		if (!"COMPUTER".equals(type) && !"REPEATER".equals(type)) {
			throw new RuntimeException("type is not supported");
		}
		addDevice(name, type, strength);
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
		System.err.println(devicesList.toString());
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

	public void validateDeviceConnectionRequest(String source, List<String> targets) {

		if (targets == null || source == null) {
			throw new RuntimeException("Invalid command syntax");
		}

		if (targets.contains(source)) {
			throw new RuntimeException("Cannot connect device to itself");
		}

		checkConnectionAndNode(source, targets);

		addDeviceConncection(source, targets);
	}

	private void checkConnectionAndNode(String source, List<String> targets) {

		boolean found = false;

		for (Device device : devicesList) {
			if (device.getName().equalsIgnoreCase(source)) {
				found = true;
				for (int i = 0; i < targets.size(); i++) {

					if (device.getConnections().contains(targets.get(i))) {
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

		modifyStrength(name, value);
	}

	@CachePut(cacheNames = "device")
	public Device modifyStrength(String name, String value) {

		Long strength = null;

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
		return devicesList.stream().collect(Collectors.toMap(Device::getName, Device::getConnections));
	}

	boolean find(String source, String destination, List<String> path, Map<String, List<String>> connectionMap) {
		if (!connectionMap.containsKey(source))
			return false;
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
