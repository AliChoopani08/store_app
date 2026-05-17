package com.Ali.Store.App.repository;

import com.Ali.Store.App.entities.userAndProfileUser.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, Long> {


    Optional<Device> findByDeviceUuidAndUserId(UUID deviceUuid, Long userId);
}
