package com.Ali.Store.App.user.security;

import com.Ali.Store.App.entities.userAndProfileUser.Device;
import com.Ali.Store.App.entities.userAndProfileUser.Users;
import com.Ali.Store.App.exceptions.security.DeviceNotAllowedException;
import com.Ali.Store.App.repository.userAndProfileUser.UserRepository;
import com.Ali.Store.App.security.customizationAuthentication.CustomAuthenticationProvider;
import com.Ali.Store.App.security.customizationAuthentication.CustomAuthenticationToken;
import com.Ali.Store.App.security.userDetails.UserDetailsImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.UUID;

import static com.Ali.Store.App.testHelpers.WhenHelper.whenHelper;
import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthenticationProviderServiceTest {

    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomAuthenticationProvider authenticationProvider;

    private Device device;
    private Authentication authentication;
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        Users user = Users.builder()
                .username("09876543210")
                .password("fake.password.12345")
                .status(true)
                .build();
        device = Device.builder()
                .deviceUuid(randomUUID())
                .deviceInfo("acer315-55kg")
                .isAvailable(true)
                .build();

        user.addDevice(device);

        authentication = new CustomAuthenticationToken(user.getUsername(), user.getPassword(), device.getDeviceUuid());
        userDetails = UserDetailsImpl.builder()
                .username("09876543210")
                .password("fake.encoded.password")
                .deviceUuid(device.getDeviceUuid())
                .build();
    }

    @Test
    void shouldThrowException_whenDeviceIsUnavailable() {
        Device unavailableDevice = device.toBuilder()
                .isAvailable(false)
                .build();
        whenHelper(userDetailsService.loadUserByUsername(anyString()), userDetails);
        whenHelper(passwordEncoder.matches(anyString(), anyString()), true);
        when(userRepository.findByUsernameAndDeviceUuidAndIsAvailable(anyString(), any(UUID.class)))
                .thenThrow(new DeviceNotAllowedException(unavailableDevice.getDeviceUuid().toString()));

        assertThatThrownBy(() -> authenticationProvider.authenticate(authentication))
                .isInstanceOf(DeviceNotAllowedException.class)
                .hasMessageContaining("This device [" + unavailableDevice.getDeviceUuid() + "] is not exist  or it is blocked !");
    }
}
