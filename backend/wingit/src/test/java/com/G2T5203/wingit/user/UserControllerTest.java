package com.G2T5203.wingit.user;

import com.G2T5203.wingit.TestUtils;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerTest {
    @LocalServerPort
    private int port;

    @Autowired
    private BCryptPasswordEncoder encoder;

    @Autowired
    TestRestTemplate testRestTemplate;

    TestUtils testUtils;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        testUtils = new TestUtils(port, encoder);
        userRepository.save(testUtils.createAdminUser());
        userRepository.save(testUtils.createSampleUser1());
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }


    @Test
    void getAllUsers_TwoUsers_Success() throws Exception {
        WingitUser[] sampleUsers = {
                testUtils.createAdminUser(),
                testUtils.createSampleUser1()
        };

        URI uri = testUtils.constructUri("users");
        ResponseEntity<WingitUser[]> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.ADMIN_USERNAME, testUtils.ADMIN_PASSWORD)
                .getForEntity(uri, WingitUser[].class);
        WingitUser[] retrievedUsers = responseEntity.getBody();
        // Logger.getLogger("UserControllerTest").log(Level.INFO, "QQQ: Length = " + retrievedUsers.length + "\n" + Arrays.toString(retrievedUsers));

        assertEquals(200, responseEntity.getStatusCode().value());
        assertNotNull(retrievedUsers);
        assertEquals(2, retrievedUsers.length);

        for (int i = 0; i < retrievedUsers.length; i++) {
            String id = sampleUsers[i].getUsername();
            WingitUser sampleControl = sampleUsers[i];
            WingitUser retrievedUser = retrievedUsers[i];

            assertEquals(id, retrievedUser.getUsername());
            assertEquals(sampleControl.getFirstName(), retrievedUser.getFirstName());
            assertEquals(sampleControl.getLastName(), retrievedUser.getLastName());
            assertEquals(sampleControl.getDob(), retrievedUser.getDob());
            assertEquals(sampleControl.getEmail(), retrievedUser.getEmail());
            assertEquals(sampleControl.getPhone(), retrievedUser.getPhone());
            assertEquals(sampleControl.getSalutation(), retrievedUser.getSalutation());
        }
    }

    @Test
    void getAllUsers_WrongAuth_Failure() throws Exception {
        URI uri = testUtils.constructUri("users");
        ResponseEntity<WingitUser[]> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.SAMPLE_USERNAME_1, testUtils.SAMPLE_PASSWORD_1)
                .getForEntity(uri, WingitUser[].class);

        assertEquals(403, responseEntity.getStatusCode().value());
    }

    @Test
    void getUser_SampleUser1_Success() throws Exception {
        WingitUser sampleUser = testUtils.createSampleUser1();

        URI uri = testUtils.constructUri("users/" + sampleUser.getUsername());
        ResponseEntity<WingitUser> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.SAMPLE_USERNAME_1, testUtils.SAMPLE_PASSWORD_1)
                .getForEntity(uri, WingitUser.class);
        WingitUser retrievedUser = responseEntity.getBody();
        // Logger.getLogger("UserControllerTest").log(Level.INFO, "QQQ: " + retrievedUser);

        assertEquals(200, responseEntity.getStatusCode().value());
        assertNotNull(retrievedUser);

        assertEquals(sampleUser.getUsername(), retrievedUser.getUsername());
        assertEquals(sampleUser.getFirstName(), retrievedUser.getFirstName());
        assertEquals(sampleUser.getLastName(), retrievedUser.getLastName());
        assertEquals(sampleUser.getDob(), retrievedUser.getDob());
        assertEquals(sampleUser.getEmail(), retrievedUser.getEmail());
        assertEquals(sampleUser.getPhone(), retrievedUser.getPhone());
        assertEquals(sampleUser.getSalutation(), retrievedUser.getSalutation());
    }

    @Test
    void getUser_DifferentUsername_Failure() throws Exception {
        URI uri = testUtils.constructUri("users/admin");
        ResponseEntity<WingitUser> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.SAMPLE_USERNAME_1, testUtils.SAMPLE_PASSWORD_1)
                .getForEntity(uri, WingitUser.class);

        assertEquals(400, responseEntity.getStatusCode().value());
        // TODO: Assert that the passed message is "Not the same user."
    }

    @Test
    void getUser_NonExistentUser_Failure() throws Exception {
        URI uri = testUtils.constructUri("users/1");
        ResponseEntity<WingitUser> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.ADMIN_USERNAME, testUtils.ADMIN_PASSWORD)
                .getForEntity(uri, WingitUser.class);
        WingitUser retrievedUser = responseEntity.getBody();

        assertEquals(404, responseEntity.getStatusCode().value());
        assertNotNull(retrievedUser);
        assertEquals(new WingitUser().toString(), retrievedUser.toString());
    }

    @Test
    void createUser_Success() throws Exception {
        WingitUser sampleUser = testUtils.createSampleUser2();
        URI uri = testUtils.constructUri("users/new");
        ResponseEntity<WingitUser> responseEntity = testRestTemplate.postForEntity(uri, sampleUser, WingitUser.class);

        assertEquals(201, responseEntity.getStatusCode().value());
        WingitUser postedUser = userRepository.findByEmail(sampleUser.getEmail());
        assertNotNull(postedUser);
    }

    @Test
    void createUser_ExistingEmail_Failure() throws Exception {
        WingitUser existingUser = testUtils.createSampleUser1();
        userRepository.save(existingUser);

        WingitUser duplicateUserEmail = testUtils.createSampleUser2();
        duplicateUserEmail.setEmail(existingUser.getEmail());
        URI uri = testUtils.constructUri("users/new");
        ResponseEntity<WingitUser> responseEntity = testRestTemplate.postForEntity(uri, duplicateUserEmail, WingitUser.class);

        assertEquals(400, responseEntity.getStatusCode().value());
        // TODO: Assert that the passed message is also "Email already used for existing account."
    }

    @Test
    void createUser_ExistingUsername_Failure() throws Exception {
        WingitUser existingUser = testUtils.createSampleUser1();
        userRepository.save(existingUser);

        WingitUser duplicateUsename = testUtils.createSampleUser2();
        duplicateUsename.setUsername(existingUser.getUsername());
        URI uri = testUtils.constructUri("users/new");
        ResponseEntity<WingitUser> responseEntity = testRestTemplate.postForEntity(uri, duplicateUsename, WingitUser.class);

        assertEquals(400, responseEntity.getStatusCode().value());
        // TODO: Assert that the passed message is also "Username already exists."
    }

    // TODO: Testcase createUser with future Date of Birth
    // TODO: Testcase createUser with no name, empty parameters, etc.
    // TODO: Testcase createUser with non-valid parameters, Salutation, Role, etc.

    @Test
    void deleteUser_Success() throws Exception {
        WingitUser userToBeDeleted = testUtils.createSampleUser1();
        String username = userRepository.save(userToBeDeleted).getUsername();

        URI uri = testUtils.constructUri("users/delete/" + username);
        ResponseEntity<Void> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.ADMIN_USERNAME, testUtils.ADMIN_PASSWORD)
                .exchange(uri, HttpMethod.DELETE, null, Void.class);
        assertEquals(200, responseEntity.getStatusCode().value());

        Optional<WingitUser> retrievedUser = userRepository.findById(username);
        assertFalse(retrievedUser.isPresent());
    }

    @Test
    void deleteUser_DifferentUsername_Failure() throws Exception {
        URI uri = testUtils.constructUri("users/delete/admin");
        ResponseEntity<Void> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.SAMPLE_USERNAME_1, testUtils.SAMPLE_PASSWORD_1)
                .exchange(uri, HttpMethod.DELETE, null, Void.class);

        assertEquals(400, responseEntity.getStatusCode().value());
        // TODO: Assert that the passed message is "Not the same user."
    }

    @Test
    void deleteUser_NotFound_Failure() throws Exception {
        URI uri = testUtils.constructUri("users/delete/1");
        ResponseEntity<Void> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.ADMIN_USERNAME, testUtils.ADMIN_PASSWORD)
                .exchange(uri, HttpMethod.DELETE, null, Void.class);
        assertEquals(404, responseEntity.getStatusCode().value());
    }

    @Test
    void updateUser_Success() throws Exception {
        WingitUser sampleUser = testUtils.createSampleUser1();
        String sampleUsername = userRepository.save(sampleUser).getUsername();
        WingitUser updatedUser = testUtils.createSampleUser1();
        updatedUser.setFirstName("Updated");
        updatedUser.setLastName("User");
        // TODO: Exhaustive test all the other update fields

        URI uri = testUtils.constructUri("users/update/" + sampleUsername);
        HttpEntity<WingitUser> payloadEntity = new HttpEntity<>(updatedUser);
        ResponseEntity<Void> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.SAMPLE_USERNAME_1, testUtils.SAMPLE_PASSWORD_1)
                .exchange(uri, HttpMethod.PUT, payloadEntity, Void.class);
        assertEquals(200, responseEntity.getStatusCode().value());

        Optional<WingitUser> retrievedUser = userRepository.findById(sampleUsername);
        assertTrue(retrievedUser.isPresent());
        assertEquals("Updated", retrievedUser.get().getFirstName());
        assertEquals("User", retrievedUser.get().getLastName());
    }

    @Test
    void updateUser_DifferentUsername_Failure() throws Exception {
        URI uri = testUtils.constructUri("users/update/admin");
        HttpEntity<WingitUser> payloadEntity = new HttpEntity<>(testUtils.createSampleUser1());
        ResponseEntity<Void> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.SAMPLE_USERNAME_1, testUtils.SAMPLE_PASSWORD_1)
                .exchange(uri, HttpMethod.PUT, payloadEntity, Void.class);

        assertEquals(400, responseEntity.getStatusCode().value());
        // TODO: Assert that the passed message is "Not the same user."
    }

    // TODO: Update User fail test case (not found).
    // TODO: Update User tried to maliciously update password and role but verify not changed.


    @Test
    void updateUserPassword_Success() throws Exception {
        WingitUser sampleUser = testUtils.createSampleUser1();
        final String NEW_PASSWORD = "newPassword";

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("password", NEW_PASSWORD);
        HttpEntity<Map<String, Object>> payloadEntity = new HttpEntity<>(requestBody);

        URI uri = testUtils.constructUri("users/updatePass/" + sampleUser.getUsername());
        ResponseEntity<Void> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.SAMPLE_USERNAME_1, testUtils.SAMPLE_PASSWORD_1)
                .exchange(uri, HttpMethod.PUT, payloadEntity, Void.class);
        assertEquals(200, responseEntity.getStatusCode().value());
        // TODO: Get this check replaced by using repo.ExistsByUsernameAndPassword
        //       Will need to figure out how to compare the password hashes correctly.
        ResponseEntity<Object> verificationEntity = testRestTemplate
                .withBasicAuth(testUtils.SAMPLE_USERNAME_1, NEW_PASSWORD)
                .getForEntity(testUtils.constructUri("users/authTest"), Object.class);
        assertEquals(200, verificationEntity.getStatusCode().value());
    }

    @Test
    void updateUserPassword_DifferentUsername_Failure() throws Exception {
        URI uri = testUtils.constructUri("users/updatePass/admin");
        HttpEntity<WingitUser> payloadEntity = new HttpEntity<>(testUtils.createSampleUser1());
        ResponseEntity<Void> responseEntity = testRestTemplate
                .withBasicAuth(testUtils.SAMPLE_USERNAME_1, testUtils.SAMPLE_PASSWORD_1)
                .exchange(uri, HttpMethod.PUT, payloadEntity, Void.class);

        assertEquals(400, responseEntity.getStatusCode().value());
        // TODO: Assert that the passed message is "Not the same user."
    }
}