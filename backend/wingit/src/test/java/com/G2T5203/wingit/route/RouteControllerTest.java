package com.G2T5203.wingit.route;

import com.G2T5203.wingit.TestUtils;
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
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RouteControllerTest {
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
    RouteRepository routeRepository;

    @BeforeEach
    void setUp() {
        testUtils = new TestUtils(port, encoder);
        userRepository.save(testUtils.createAdminUser());
        userRepository.save(testUtils.createSampleUser1());
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        routeRepository.deleteAll();
    }


    @Test
    void getAllRoutes_TwoRoutes_Success() throws Exception {
        Route[] sampleRoutes = { testUtils.createSampleRoute1(), testUtils.createSampleRoute2() };
        List<Route> savedRoutes = new ArrayList<>();
        for (Route sampleRoute : sampleRoutes) { savedRoutes.add(routeRepository.save(sampleRoute)); }

        URI uri = testUtils.constructUri("routes");
        ResponseEntity<Route[]> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.ADMIN_USERNAME, testUtils.ADMIN_PASSWORD)
                .getForEntity(uri, Route[].class);
        Route[] retrievedRoutes = responseEntity.getBody();

        assertEquals(200, responseEntity.getStatusCode().value());
        assertNotNull(retrievedRoutes);
        assertEquals(2, retrievedRoutes.length);

        for (int i = 0; i < retrievedRoutes.length; i++) {
            Route sampleControl = savedRoutes.get(i);
            Route retrievedRoute = retrievedRoutes[i];
            assertEquals(sampleControl.getRouteId(), retrievedRoute.getRouteId());
            assertEquals(sampleControl.getDepartureDest(), retrievedRoute.getDepartureDest());
            assertEquals(sampleControl.getArrivalDest(), retrievedRoute.getArrivalDest());
            assertEquals(sampleControl.getFlightDuration(), retrievedRoute.getFlightDuration());
        }
    }

    @Test
    void getAllRoutes_WrongAuth_Failure() throws Exception {
        URI uri = testUtils.constructUri("routes");
        ResponseEntity<Route[]> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.SAMPLE_USERNAME_1, testUtils.SAMPLE_PASSWORD_1)
                .getForEntity(uri, Route[].class);

        assertEquals(403, responseEntity.getStatusCode().value());
    }

    @Test
    void getAllRoutesWithDepartureDest() throws Exception {
        Route[] sampleRoutes = { testUtils.createSampleRoute1(), testUtils.createSampleRoute2() };
        List<Route> savedRoutes = new ArrayList<>();
        for (Route sampleRoute : sampleRoutes) { savedRoutes.add(routeRepository.save(sampleRoute)); }
        final int sampleForTest = 0;

        URI uri = testUtils.constructUri("routes/departureDest/" + sampleRoutes[sampleForTest].getDepartureDest());
        ResponseEntity<Route[]> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.SAMPLE_USERNAME_1, testUtils.SAMPLE_PASSWORD_1)
                .getForEntity(uri, Route[].class);
        Route[] retrievedRoutes = responseEntity.getBody();

        assertEquals(200, responseEntity.getStatusCode().value());
        assertNotNull(retrievedRoutes);
        assertEquals(1, retrievedRoutes.length);

        Route sampleControl = savedRoutes.get(sampleForTest);
        Route retrievedRoute = retrievedRoutes[0];
        assertEquals(sampleControl.getRouteId(), retrievedRoute.getRouteId());
        assertEquals(sampleControl.getDepartureDest(), retrievedRoute.getDepartureDest());
        assertEquals(sampleControl.getArrivalDest(), retrievedRoute.getArrivalDest());
        assertEquals(sampleControl.getFlightDuration(), retrievedRoute.getFlightDuration());
    }

    @Test
    void getRoute_SampleRoute_Success() throws Exception {
        Route sampleRoute = testUtils.createSampleRoute1();
        int savedRouteId = routeRepository.save(sampleRoute).getRouteId();

        URI uri = testUtils.constructUri("routes/" + savedRouteId);
        ResponseEntity<Route> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.SAMPLE_USERNAME_1, testUtils.SAMPLE_PASSWORD_1)
                .getForEntity(uri, Route.class);
        Route retrievedRoute = responseEntity.getBody();

        assertEquals(200, responseEntity.getStatusCode().value());
        assertNotNull(retrievedRoute);

        assertEquals(savedRouteId, retrievedRoute.getRouteId());
        assertEquals(sampleRoute.getDepartureDest(), retrievedRoute.getDepartureDest());
        assertEquals(sampleRoute.getArrivalDest(), retrievedRoute.getArrivalDest());
        assertEquals(sampleRoute.getFlightDuration(), retrievedRoute.getFlightDuration());
    }

    @Test
    void getRoute_NonExistentRoute_Failure() throws Exception {
        URI uri = testUtils.constructUri("routes/1");
        ResponseEntity<Route> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.SAMPLE_USERNAME_1, testUtils.SAMPLE_PASSWORD_1)
                .getForEntity(uri, Route.class);
        Route retrievedRoute = responseEntity.getBody();

        assertEquals(404, responseEntity.getStatusCode().value());
        assertNotNull(retrievedRoute);
        assertEquals(new Route().toString(), retrievedRoute.toString());
    }

    @Test
    void createRoute_Success() throws Exception {
        Route sampleRoute = testUtils.createSampleRoute1();
        URI uri = testUtils.constructUri("routes/new");
        ResponseEntity<Route> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.ADMIN_USERNAME, testUtils.ADMIN_PASSWORD)
                .postForEntity(uri, sampleRoute, Route.class);

        assertEquals(201, responseEntity.getStatusCode().value());
        Route returnedSavedRoute = responseEntity.getBody();
        assertNotNull(returnedSavedRoute);
        Optional<Route> retrievedRoute = routeRepository.findById(returnedSavedRoute.getRouteId());
        assertTrue(retrievedRoute.isPresent());
    }

    @Test
    void createRoute_ExistingRouteId_Failure() throws Exception {
        Route sampleRouteAdded = testUtils.createSampleRoute1();
        int savedRouteId = routeRepository.save(sampleRouteAdded).getRouteId();
        Route duplicateRoute = testUtils.createSampleRoute2();
        duplicateRoute.setRouteId(savedRouteId);

        URI uri = testUtils.constructUri("routes/new");
        ResponseEntity<Route> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.ADMIN_USERNAME, testUtils.ADMIN_PASSWORD)
                .postForEntity(uri, duplicateRoute, Route.class);

        assertEquals(400, responseEntity.getStatusCode().value());
        // TODO: Asset that the message is also "RouteId already exists."
    }

    @Test
    void createRoute_WrongAuth_Failure() throws Exception {
        Route sampleRoute = testUtils.createSampleRoute1();
        URI uri = testUtils.constructUri("routes/new");
        ResponseEntity<Route> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.SAMPLE_USERNAME_1, testUtils.SAMPLE_PASSWORD_1)
                .postForEntity(uri, sampleRoute, Route.class);

        assertEquals(403, responseEntity.getStatusCode().value());
    }

    @Test
    void deleteRoute_Success() throws Exception {
        Route routeToBeDeleted = testUtils.createSampleRoute1();
        int routeId = routeRepository.save(routeToBeDeleted).getRouteId();

        URI uri = testUtils.constructUri("routes/delete/" + routeId);
        ResponseEntity<Void> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.ADMIN_USERNAME, testUtils.ADMIN_PASSWORD)
                .exchange(uri, HttpMethod.DELETE, null, Void.class);
        assertEquals(200, responseEntity.getStatusCode().value());

        Optional<Route> retrievedRoute = routeRepository.findById(routeId);
        assertFalse(retrievedRoute.isPresent());
    }

    @Test
    void deleteRoute_WrongAuth_Failure() throws Exception {
        Route routeToBeDeleted = testUtils.createSampleRoute1();
        int routeId = routeRepository.save(routeToBeDeleted).getRouteId();

        URI uri = testUtils.constructUri("routes/delete/" + routeId);
        ResponseEntity<Void> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.SAMPLE_USERNAME_1, testUtils.SAMPLE_PASSWORD_1)
                .exchange(uri, HttpMethod.DELETE, null, Void.class);
        assertEquals(403, responseEntity.getStatusCode().value());

        Optional<Route> retrievedRoute = routeRepository.findById(routeId);
        assertTrue(retrievedRoute.isPresent());
    }

    @Test
    void updateRoute_Success() throws Exception{
        Route sampleRoute = testUtils.createSampleRoute1();
        int sampleRouteId = routeRepository.save(sampleRoute).getRouteId();
        Route updatedRoute = testUtils.createSampleRoute1();
        updatedRoute.setRouteId(sampleRouteId);
        updatedRoute.setDepartureDest("NEW_DEPART");
        updatedRoute.setArrivalDest("NEW_ARRIVE");
        updatedRoute.setFlightDuration(Duration.ofHours(5).plusMinutes(55));

        URI uri = testUtils.constructUri("routes/update/" + sampleRouteId);
        HttpEntity<Route> payloadEntity = new HttpEntity<>(updatedRoute);
        ResponseEntity<Void> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.ADMIN_USERNAME, testUtils.ADMIN_PASSWORD)
                .exchange(uri, HttpMethod.PUT, payloadEntity, Void.class);
        assertEquals(200, responseEntity.getStatusCode().value());

        Optional<Route> retrievedRoute = routeRepository.findById(sampleRouteId);
        assertTrue(retrievedRoute.isPresent());
        assertEquals("NEW_DEPART", retrievedRoute.get().getDepartureDest());
        assertEquals("NEW_ARRIVE", retrievedRoute.get().getArrivalDest());
        assertEquals(Duration.ofHours(5).plusMinutes(55), retrievedRoute.get().getFlightDuration());
    }

    @Test
    void updateRoute_DifferentRouteId_Failure() throws Exception{
        Route sampleRoute = testUtils.createSampleRoute1();
        int sampleRouteId = routeRepository.save(sampleRoute).getRouteId();
        Route updatedRoute = testUtils.createSampleRoute2();

        URI uri = testUtils.constructUri("routes/update/" + sampleRouteId);
        HttpEntity<Route> payloadEntity = new HttpEntity<>(updatedRoute);
        ResponseEntity<Void> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.ADMIN_USERNAME, testUtils.ADMIN_PASSWORD)
                .exchange(uri, HttpMethod.PUT, payloadEntity, Void.class);
        assertEquals(400, responseEntity.getStatusCode().value());
        // TODO: Asset that the passed message is "Not the same routeId."

        Optional<Route> retrievedRoute = routeRepository.findById(sampleRouteId);
        assertTrue(retrievedRoute.isPresent());
        assertEquals(sampleRoute.getDepartureDest(), retrievedRoute.get().getDepartureDest());
        assertEquals(sampleRoute.getArrivalDest(), retrievedRoute.get().getArrivalDest());
        assertEquals(sampleRoute.getFlightDuration(), retrievedRoute.get().getFlightDuration());
    }

    @Test
    void updateRoute_WrongAuth_Failure() throws Exception{
        Route sampleRoute = testUtils.createSampleRoute1();
        int sampleRouteId = routeRepository.save(sampleRoute).getRouteId();
        Route updatedRoute = testUtils.createSampleRoute1();
        updatedRoute.setDepartureDest("NEW_DEPART");
        updatedRoute.setArrivalDest("NEW_ARRIVE");
        updatedRoute.setFlightDuration(Duration.ofHours(5).plusMinutes(55));

        URI uri = testUtils.constructUri("routes/update/" + sampleRouteId);
        HttpEntity<Route> payloadEntity = new HttpEntity<>(updatedRoute);
        ResponseEntity<Void> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.SAMPLE_USERNAME_1, testUtils.SAMPLE_PASSWORD_1)
                .exchange(uri, HttpMethod.PUT, payloadEntity, Void.class);
        assertEquals(403, responseEntity.getStatusCode().value());

        Optional<Route> retrievedRoute = routeRepository.findById(sampleRouteId);
        assertTrue(retrievedRoute.isPresent());
        assertEquals(sampleRoute.getDepartureDest(), retrievedRoute.get().getDepartureDest());
        assertEquals(sampleRoute.getArrivalDest(), retrievedRoute.get().getArrivalDest());
        assertEquals(sampleRoute.getFlightDuration(), retrievedRoute.get().getFlightDuration());
    }

    @Test
    void updateRoute_NotFound_Failure() throws Exception {
        URI uri = testUtils.constructUri("routes/update/1");
        Route sampleRoute1 = testUtils.createSampleRoute1();
        sampleRoute1.setRouteId(1);

        HttpEntity<Route> payloadEntity = new HttpEntity<>(sampleRoute1);
        ResponseEntity<Void> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.ADMIN_USERNAME, testUtils.ADMIN_PASSWORD)
                .exchange(uri, HttpMethod.PUT, payloadEntity, Void.class);
        assertEquals(404, responseEntity.getStatusCode().value());
    }
}