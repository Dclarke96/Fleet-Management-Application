package com.dylanclarke.FleetManagementApp;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.dylanclarke.FleetManagementApp.data.Vehicle;
import com.dylanclarke.FleetManagementApp.data.VehicleRepository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class VehicleRepositoryTest {

    private VehicleRepository vehicleRepo;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        vehicleRepo = new VehicleRepository(context);

        // Clean DB before test
        List<Vehicle> allVehicles = vehicleRepo.getAllVehicles();
        for (Vehicle v : allVehicles) {
            vehicleRepo.deleteVehicle(v);
        }
    }

    @After
    public void tearDown() {
        // Clean DB after test
        List<Vehicle> allVehicles = vehicleRepo.getAllVehicles();
        for (Vehicle v : allVehicles) {
            vehicleRepo.deleteVehicle(v);
        }
    }

    // -------------------------------
    // Test 1: Add Vehicle
    // -------------------------------
    @Test
    public void testAddVehicle() {

        Vehicle vehicle = new Vehicle();
        vehicle.setTitle("Blue Subi");
        vehicle.setMake("Subaru");
        vehicle.setModel("Impreza");
        vehicle.setLocation("Garage A");
        vehicle.setYear(2026);
        vehicle.setStartDate("2026-01-01");
        vehicle.setEndDate("2026-12-31");
        vehicle.setMaintenanceAlertsEnabled(true);

        int id = vehicleRepo.addVehicle(vehicle);
        assertTrue(id > 0);

        Vehicle savedVehicle = vehicleRepo.getVehicleById(id);

        assertNotNull(savedVehicle);
        assertEquals("Blue Subi", savedVehicle.getTitle());
        assertEquals("Subaru", savedVehicle.getMake());
        assertEquals("Impreza", savedVehicle.getModel());
        assertEquals("Garage A", savedVehicle.getLocation());
        assertEquals(2026, savedVehicle.getYear());
        assertTrue(savedVehicle.isMaintenanceAlertsEnabled());
    }

    // -------------------------------
    // Test 2: Edit Vehicle
    // -------------------------------
    @Test
    public void testEditVehicle() {

        Vehicle vehicle = new Vehicle(
                "Blue Subi",
                "Subaru",
                "Impreza",
                2026,
                "Garage A",
                true,
                "2026-01-01",
                "2026-12-31"
        );

        int id = vehicleRepo.addVehicle(vehicle);

        Vehicle savedVehicle = vehicleRepo.getVehicleById(id);
        assertNotNull(savedVehicle);

        savedVehicle.setLocation("Garage B");
        savedVehicle.setYear(2026);
        savedVehicle.setMaintenanceAlertsEnabled(false);

        boolean updated = vehicleRepo.updateVehicle(savedVehicle);
        assertTrue(updated);

        Vehicle updatedVehicle = vehicleRepo.getVehicleById(id);

        assertEquals("Garage B", updatedVehicle.getLocation());
        assertEquals(2026, updatedVehicle.getYear());
        assertFalse(updatedVehicle.isMaintenanceAlertsEnabled());
    }

    // -------------------------------
    // Test 3: Delete Vehicle
    // -------------------------------
    @Test
    public void testDeleteVehicle() {

        Vehicle vehicle = new Vehicle(
                "Red Car",
                "Toyota",
                "Corolla",
                2025,
                "Garage C",
                true,
                "2025-01-01",
                "2025-12-31"
        );

        int id = vehicleRepo.addVehicle(vehicle);

        Vehicle toDelete = vehicleRepo.getVehicleById(id);
        assertNotNull(toDelete);

        boolean deleted = vehicleRepo.deleteVehicle(toDelete);
        assertTrue(deleted);

        Vehicle deletedVehicle = vehicleRepo.getVehicleById(id);
        assertNull(deletedVehicle);
    }

    // -------------------------------
    // Test 4: Validation
    // -------------------------------
    @Test
    public void testValidation() {

        Vehicle vehicle = new Vehicle();

        vehicle.setTitle("");
        vehicle.setMake("Honda");
        vehicle.setModel("");
        vehicle.setLocation("");
        vehicle.setYear(2025);
        vehicle.setStartDate("2026-01-01");
        vehicle.setEndDate("2025-12-31");

        int id = vehicleRepo.addVehicle(vehicle);

        assertEquals(-1, id);
    }

    // -------------------------------
    // Test 5: Search Function
    // -------------------------------
    @Test
    public void testSearchVehicle() {

        Vehicle v1 = new Vehicle("Blue Subi","Subaru","Impreza",2025,"Garage A",true,"2026-01-01","2026-12-31");
        Vehicle v2 = new Vehicle("Red Car","Toyota","Corolla",2025,"Garage B",true,"2025-01-01","2025-12-31");
        Vehicle v3 = new Vehicle("Green Truck","Ford","F-150",2024,"Garage C",false,"2024-01-01","2024-12-31");

        vehicleRepo.addVehicle(v1);
        vehicleRepo.addVehicle(v2);
        vehicleRepo.addVehicle(v3);

        List<Vehicle> results = vehicleRepo.searchVehicles("Subi");

        assertEquals(1, results.size());
        assertEquals("Blue Subi", results.get(0).getTitle());

        results = vehicleRepo.searchVehicles("Garage");
        assertEquals(3, results.size());
    }
}
