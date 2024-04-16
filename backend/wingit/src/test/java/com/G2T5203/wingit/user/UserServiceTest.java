package com.G2T5203.wingit.user;

import com.G2T5203.wingit.TestUtils;
import com.G2T5203.wingit.plane.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder encoder;

    private final TestUtils testUtils = new TestUtils(-1, encoder);

    @Test
    void getAllUsers_Success() {
        List<WingitUser> users = new ArrayList<>();
        users.add(testUtils.createSampleUser1(false));

        when(userRepository.findAll()).thenReturn(users);

        List<WingitUser> result = userService.getAllUsers();

        assertEquals(users, result);
        verify(userRepository).findAll();
    }

    @Test
    void getById_UserExists_Success() {
        WingitUser user = testUtils.createSampleUser1(false);

        when(userRepository.findById(any(String.class))).thenReturn(Optional.of(user));

        WingitUser result = userService.getById(user.getUsername());
        assertNotNull(result);
        assertEquals(user, result);
        verify(userRepository).findById(user.getUsername());
    }

    @Test
    void getById_PlaneNotFound_Failure() {
        String nonExistentUsername = "NonExistentUsername";
        when(userRepository.findById(any(String.class))).thenReturn(Optional.empty());

        WingitUser result = userService.getById(nonExistentUsername);

        assertNull(result);
        verify(userRepository).findById(nonExistentUsername);
    }

    @Test
    void createNormalUser_Success() {
        WingitUser newUser = testUtils.createSampleUser1(false);
        newUser.setAuthorityRole("ROLE_ADMIN"); // We test that the authority is forced to ROLE_USER.

        when(userRepository.existsById(any(String.class))).thenReturn(false);
        when(userRepository.existsByEmail(any(String.class))).thenReturn(false);
        when(userRepository.save(any(WingitUser.class))).thenAnswer((i) -> i.getArguments()[0] );
        final String mockedHashedPassword = "MOCKED_HASED_PASSWORD";
        when(encoder.encode(newUser.getPassword())).thenReturn(mockedHashedPassword);

        WingitUser result = userService.createUser(newUser);
        assertNotNull(result);
        assertEquals("ROLE_USER", result.getAuthorityRole());
        assertEquals(mockedHashedPassword, result.getPassword());
        verify(userRepository).existsById(any(String.class));
        verify(userRepository).existsByEmail(any(String.class));
        verify(userRepository).save(any(WingitUser.class));
    }

    @Test
    void createNormalUser_UserIdExists_Failure() {
        when(userRepository.existsById(any(String.class))).thenReturn(true);
        WingitUser newUser = testUtils.createSampleUser1(false);

        UserBadRequestException exception = assertThrows(UserBadRequestException.class, () -> {
            userService.createUser(newUser);
        });

        verify(userRepository).existsById(any(String.class));
        assertEquals("BAD REQUEST: Username already exists", exception.getMessage());
    }

    @Test
    void createNormalUser_EmailExists_Failure() {
        when(userRepository.existsById(any(String.class))).thenReturn(false);
        when(userRepository.existsByEmail(any(String.class))).thenReturn(true);
        WingitUser newUser = testUtils.createSampleUser1(false);

        UserBadRequestException exception = assertThrows(UserBadRequestException.class, () -> {
            userService.createUser(newUser);
        });

        verify(userRepository).existsById(any(String.class));
        verify(userRepository).existsByEmail(any(String.class));
        assertEquals("BAD REQUEST: Email already used for existing account.", exception.getMessage());
    }

    @Test
    void createAdminUser_Success() {
        WingitUser newUser = testUtils.createAdminUser(false);
        newUser.setAuthorityRole("ROLE_USER"); // We test that the authority is forced to ROLE_USER.

        when(userRepository.existsById(any(String.class))).thenReturn(false);
        when(userRepository.existsByEmail(any(String.class))).thenReturn(false);
        when(userRepository.save(any(WingitUser.class))).thenAnswer((i) -> i.getArguments()[0] );
        final String mockedHashedPassword = "MOCKED_HASED_PASSWORD";
        when(encoder.encode(newUser.getPassword())).thenReturn(mockedHashedPassword);

        WingitUser result = userService.createAdmin(newUser);
        assertNotNull(result);
        assertEquals("ROLE_ADMIN", result.getAuthorityRole());
        assertEquals(mockedHashedPassword, result.getPassword());
        verify(userRepository).existsById(any(String.class));
        verify(userRepository).existsByEmail(any(String.class));
        verify(userRepository).save(any(WingitUser.class));
    }

    @Test
    void createAdminUser_UserIdExists_Failure() {
        when(userRepository.existsById(any(String.class))).thenReturn(true);
        WingitUser newUser = testUtils.createAdminUser(false);

        UserBadRequestException exception = assertThrows(UserBadRequestException.class, () -> {
            userService.createAdmin(newUser);
        });

        verify(userRepository).existsById(any(String.class));
        assertEquals("BAD REQUEST: Username already exists", exception.getMessage());
    }

    @Test
    void createAdminUser_EmailExists_Failure() {
        when(userRepository.existsById(any(String.class))).thenReturn(false);
        when(userRepository.existsByEmail(any(String.class))).thenReturn(true);
        WingitUser newUser = testUtils.createAdminUser(false);

        UserBadRequestException exception = assertThrows(UserBadRequestException.class, () -> {
            userService.createAdmin(newUser);
        });

        verify(userRepository).existsById(any(String.class));
        verify(userRepository).existsByEmail(any(String.class));
        assertEquals("BAD REQUEST: Email already used for existing account.", exception.getMessage());
    }


    @Test
    void deleteUserById_UserExists_Success() {
        String mockUsername = "MOCK_USERNAME";
        when(userRepository.existsById(mockUsername)).thenReturn(true);
        assertDoesNotThrow(() -> userService.deleteUserById(mockUsername));
        verify(userRepository).existsById(mockUsername);
        verify(userRepository).deleteById(mockUsername);
    }

    @Test
    void deleteUserById_UserNotFound_Failure() {
        String mockNonExistentUsername = "MOCK_NON_EXISTENT_USERNAME";
        when(userRepository.existsById(mockNonExistentUsername)).thenReturn(false);
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUserById(mockNonExistentUsername);
        });
        assertEquals("Could not find user " + mockNonExistentUsername, exception.getMessage());
    }

    @Test
    void updateUser_UserExists_Success() {
        WingitUser originalUser = testUtils.createSampleUser2(false);
        WingitUser updatedUser = testUtils.createSampleUser2(false);
        updatedUser.setPhone("NEW_PHONE");
        // Making sure that the password and authority role forced to not be changed.
        updatedUser.setPassword("SOMETHING ELSE");
        updatedUser.setAuthorityRole("ROLE_ADMIN");

        when(userRepository.findById(any(String.class))).thenReturn(Optional.of(originalUser));
        when(userRepository.save(any(WingitUser.class))).thenAnswer(i -> i.getArguments()[0]);

        WingitUser result = userService.updateUser(updatedUser);
        assertNotNull(result);
        assertEquals("NEW_PHONE", result.getPhone());
        assertEquals(originalUser.getPassword(), result.getPassword());
        assertEquals("ROLE_USER", result.getAuthorityRole());
        verify(userRepository).findById(updatedUser.getUsername());
        verify(userRepository).save(any(WingitUser.class));
    }

    @Test
    void updateUser_UserNotFound_Failure() {
        WingitUser nonExistentUser = testUtils.createSampleUser1(false);
        when(userRepository.findById(any(String.class))).thenReturn(Optional.empty());
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.updateUser(nonExistentUser);
        });
        assertEquals("Could not find user " + nonExistentUser.getUsername(), exception.getMessage());
    }

    @Test
    void updatePassword_UserExists_Success() {
        WingitUser originalUser = testUtils.createSampleUser2(false);
        final String newPassword = "NEW_PASSWORD";
        final String newHashedPassword = "NEW_HASHED_PASSWORD";
        when(userRepository.findById(any(String.class))).thenReturn(Optional.of(originalUser));
        when(encoder.encode(newPassword)).thenReturn(newHashedPassword);
        when(userRepository.save(any(WingitUser.class))).thenAnswer(i -> i.getArguments()[0]);

        WingitUser result = userService.updatePassword(originalUser.getUsername(), newPassword);
        assertNotNull(result);
        assertEquals(newHashedPassword, result.getPassword());
        verify(userRepository).findById(originalUser.getUsername());
        verify(userRepository).save(any(WingitUser.class));
    }

    @Test
    void updatePassword_UserNotFound_Failure() {
        WingitUser nonExistentUser = testUtils.createSampleUser1(false);
        when(userRepository.findById(any(String.class))).thenReturn(Optional.empty());
        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.updatePassword(nonExistentUser.getUsername(), "NEW_PASSWORD");
        });
        assertEquals("Could not find user " + nonExistentUser.getUsername(), exception.getMessage());
    }
}

