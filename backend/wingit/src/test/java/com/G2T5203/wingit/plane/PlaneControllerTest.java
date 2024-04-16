package com.G2T5203.wingit.plane;

import com.G2T5203.wingit.TestUtils;
import com.G2T5203.wingit.seat.SeatRepository;
import com.G2T5203.wingit.seat.SeatService;
import com.G2T5203.wingit.seat.SeatSimpleJson;
import com.G2T5203.wingit.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PlaneControllerTest {
    @LocalServerPort
    private int port;
    @Autowired
    private BCryptPasswordEncoder encoder;
    @Autowired
    TestRestTemplate testRestTemplate;
    TestUtils testUtils;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PlaneRepository planeRepository;
    @Autowired
    SeatRepository seatRepository;
    @Autowired
    SeatService seatService;

    @BeforeEach
    void setUp() {
        testUtils = new TestUtils(port, encoder);
        userRepository.save(testUtils.createAdminUser());
        userRepository.save(testUtils.createSampleUser1());
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        seatRepository.deleteAll();
        planeRepository.deleteAll();
    }


    @Test
    void getAllPlanes_TwoPlanes_Success() throws Exception {
        Plane[] samplePlanes = { testUtils.createSamplePlane1(), testUtils.createSamplePlane2() };
        for (Plane samplePlane : samplePlanes) { planeRepository.save(samplePlane); }

        URI uri = testUtils.constructUri("planes");
        ResponseEntity<Plane[]> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.ADMIN_USERNAME, testUtils.ADMIN_PASSWORD)
                .getForEntity(uri, Plane[].class);
        Plane[] retrievedPlanes = responseEntity.getBody();

        assertEquals(200, responseEntity.getStatusCode().value());
        assertNotNull(retrievedPlanes);
        assertEquals(2, retrievedPlanes.length);

        for (int i = 0; i < retrievedPlanes.length; i++) {
            Plane sampleControl = samplePlanes[i];
            Plane retrievedPlane = retrievedPlanes[i];
            assertEquals(sampleControl.getPlaneId(), retrievedPlane.getPlaneId());
            assertEquals(sampleControl.getCapacity(), retrievedPlane.getCapacity());
            assertEquals(sampleControl.getModel(), retrievedPlane.getModel());
        }
    }

    @Test
    void getAllPlanes_WrongAuth_Failure() throws Exception {
        URI uri = testUtils.constructUri("planes");
        ResponseEntity<Plane[]> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.SAMPLE_USERNAME_1, testUtils.SAMPLE_PASSWORD_1)
                .getForEntity(uri, Plane[].class);

        assertEquals(403, responseEntity.getStatusCode().value());
    }

    @Test
    void getPlane_SamplePlane_Success() throws Exception {
        Plane samplePlane = testUtils.createSamplePlane1();
        planeRepository.save(samplePlane);

        URI uri = testUtils.constructUri("planes/" + samplePlane.getPlaneId());
        ResponseEntity<Plane> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.SAMPLE_USERNAME_1, testUtils.SAMPLE_PASSWORD_1)
                .getForEntity(uri, Plane.class);
        Plane retrievedPlane = responseEntity.getBody();

        assertEquals(200, responseEntity.getStatusCode().value());
        assertNotNull(retrievedPlane);

        assertEquals(samplePlane.getPlaneId(), retrievedPlane.getPlaneId());
        assertEquals(samplePlane.getCapacity(), retrievedPlane.getCapacity());
        assertEquals(samplePlane.getModel(), retrievedPlane.getModel());
    }

    @Test
    void getPlane_NonExistentPlane_Failure() throws Exception {
        URI uri = testUtils.constructUri("planes/SQ123");
        ResponseEntity<Plane> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.SAMPLE_USERNAME_1, testUtils.SAMPLE_PASSWORD_1)
                .getForEntity(uri, Plane.class);
        Plane retrievedPlane = responseEntity.getBody();

        assertEquals(404, responseEntity.getStatusCode().value());
        assertNotNull(retrievedPlane);
        assertEquals(new Plane().toString(), retrievedPlane.toString());
    }




    @Test
    void createPlaneNewWithSeats_Success() throws Exception {
        Plane samplePlane = testUtils.createSamplePlane1();
        URI uri = testUtils.constructUri("planes/newWithSeats");
        ResponseEntity<Plane> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.ADMIN_USERNAME, testUtils.ADMIN_PASSWORD)
                .postForEntity(uri, samplePlane, Plane.class);

        assertEquals(201, responseEntity.getStatusCode().value());
        Optional<Plane> postedPlane = planeRepository.findById(samplePlane.getPlaneId());
        assertTrue(postedPlane.isPresent());
        List<SeatSimpleJson> createdSeats = seatService.getAllSeatsForPlaneAsSimpleJson(postedPlane.get().getPlaneId());
        assertEquals(postedPlane.get().getCapacity(), createdSeats.size());
    }

    @Test
    void createPlane_ExistingPlaneId_Failure() throws Exception {
        Plane samplePlaneAdded = testUtils.createSamplePlane1();
        planeRepository.save(samplePlaneAdded);
        Plane duplicatePlane = testUtils.createSamplePlane2();
        duplicatePlane.setPlaneId(samplePlaneAdded.getPlaneId());

        URI uri = testUtils.constructUri("planes/newWithSeats");
        ResponseEntity<Plane> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.ADMIN_USERNAME, testUtils.ADMIN_PASSWORD)
                .postForEntity(uri, duplicatePlane, Plane.class);

        assertEquals(400, responseEntity.getStatusCode().value());
        // TODO: Asset that the message is also "PlaneId already exists."
    }

    @Test
    void createPlane_WrongAuth_Failure() throws Exception {
        Plane samplePlane = testUtils.createSamplePlane1();
        URI uri = testUtils.constructUri("planes/newWithSeats");
        ResponseEntity<Plane> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.SAMPLE_USERNAME_1, testUtils.SAMPLE_PASSWORD_1)
                .postForEntity(uri, samplePlane, Plane.class);

        assertEquals(403, responseEntity.getStatusCode().value());
    }

    @Test
    void deletePlane_Success() throws Exception {
        Plane planeToBeDeleted = testUtils.createSamplePlane1();
        String planeId = planeRepository.save(planeToBeDeleted).getPlaneId();

        URI uri = testUtils.constructUri("planes/delete/" + planeId);
        ResponseEntity<Void> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.ADMIN_USERNAME, testUtils.ADMIN_PASSWORD)
                .exchange(uri, HttpMethod.DELETE, null, Void.class);
        assertEquals(200, responseEntity.getStatusCode().value());

        Optional<Plane> retrievedPlane = planeRepository.findById(planeId);
        assertFalse(retrievedPlane.isPresent());
    }

    @Test
    void deletePlane_WrongAuth_Failure() throws Exception {
        Plane planeToBeDeleted = testUtils.createSamplePlane1();
        String planeId = planeRepository.save(planeToBeDeleted).getPlaneId();

        URI uri = testUtils.constructUri("planes/delete/" + planeId);
        ResponseEntity<Void> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.SAMPLE_USERNAME_1, testUtils.SAMPLE_PASSWORD_1)
                .exchange(uri, HttpMethod.DELETE, null, Void.class);
        assertEquals(403, responseEntity.getStatusCode().value());

        Optional<Plane> retrievedPlane = planeRepository.findById(planeId);
        assertTrue(retrievedPlane.isPresent());
    }

    @Test
    void updatePlane_Success() throws Exception{
        Plane samplePlane = testUtils.createSamplePlane1();
        String samplePlaneId = planeRepository.save(samplePlane).getPlaneId();
        Plane updatedPlane = testUtils.createSamplePlane1();
        updatedPlane.setModel("C790");
        updatedPlane.setCapacity(123);

        URI uri = testUtils.constructUri("planes/update/" + samplePlaneId);
        HttpEntity<Plane> payloadEntity = new HttpEntity<>(updatedPlane);
        ResponseEntity<Void> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.ADMIN_USERNAME, testUtils.ADMIN_PASSWORD)
                .exchange(uri, HttpMethod.PUT, payloadEntity, Void.class);
        assertEquals(200, responseEntity.getStatusCode().value());

        Optional<Plane> retrievedPlane = planeRepository.findById(samplePlaneId);
        assertTrue(retrievedPlane.isPresent());
        assertEquals("C790", retrievedPlane.get().getModel());
        assertEquals(123, retrievedPlane.get().getCapacity());
    }

    @Test
    void updatePlane_DifferentPlaneId_Failure() throws Exception{
        Plane samplePlane = testUtils.createSamplePlane1();
        String samplePlaneId = planeRepository.save(samplePlane).getPlaneId();
        Plane updatedPlane = testUtils.createSamplePlane2();

        URI uri = testUtils.constructUri("planes/update/" + samplePlaneId);
        HttpEntity<Plane> payloadEntity = new HttpEntity<>(updatedPlane);
        ResponseEntity<Void> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.ADMIN_USERNAME, testUtils.ADMIN_PASSWORD)
                .exchange(uri, HttpMethod.PUT, payloadEntity, Void.class);
        assertEquals(400, responseEntity.getStatusCode().value());
        // TODO: Asset that the passed message is "Not the same planeId."

        Optional<Plane> retrievedPlane = planeRepository.findById(samplePlaneId);
        assertTrue(retrievedPlane.isPresent());
        assertEquals(samplePlane.getModel(), retrievedPlane.get().getModel());
        assertEquals(samplePlane.getCapacity(), retrievedPlane.get().getCapacity());
    }

    @Test
    void updatePlane_WrongAuth_Failure() throws Exception{
        Plane samplePlane = testUtils.createSamplePlane1();
        String samplePlaneId = planeRepository.save(samplePlane).getPlaneId();
        Plane updatedPlane = testUtils.createSamplePlane1();
        updatedPlane.setModel("NEW_MODEL");
        updatedPlane.setCapacity(123);

        URI uri = testUtils.constructUri("planes/update/" + samplePlaneId);
        HttpEntity<Plane> payloadEntity = new HttpEntity<>(updatedPlane);
        ResponseEntity<Void> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.SAMPLE_USERNAME_1, testUtils.SAMPLE_PASSWORD_1)
                .exchange(uri, HttpMethod.PUT, payloadEntity, Void.class);
        assertEquals(403, responseEntity.getStatusCode().value());

        Optional<Plane> retrievedPlane = planeRepository.findById(samplePlaneId);
        assertTrue(retrievedPlane.isPresent());
        assertEquals(samplePlane.getModel(), retrievedPlane.get().getModel());
        assertEquals(samplePlane.getCapacity(), retrievedPlane.get().getCapacity());
    }

    @Test
    void updatePlane_NotFound_Failure() throws Exception {
        URI uri = testUtils.constructUri("planes/update/SQ123");
        HttpEntity<Plane> payloadEntity = new HttpEntity<>(testUtils.createSamplePlane1());
        ResponseEntity<Void> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.ADMIN_USERNAME, testUtils.ADMIN_PASSWORD)
                .exchange(uri, HttpMethod.PUT, payloadEntity, Void.class);
        assertEquals(404, responseEntity.getStatusCode().value());
    }
}