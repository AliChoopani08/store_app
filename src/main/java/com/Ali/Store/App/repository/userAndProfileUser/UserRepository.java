package com.Ali.Store.App.repository.userAndProfileUser;

import com.Ali.Store.App.entities.userAndProfileUser.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<Users, Long>, JpaSpecificationExecutor<Users> {
    @Query("""
            SELECT u
            FROM Users u
            JOIN FETCH u.devices d
            WHERE (u.username = :username AND d.deviceUuid = :deviceUuid) AND u.status = true AND d.isAvailable = true
           """)
    Optional<Users> findByUsernameAndDeviceUuidAndIsAvailable(@Param("username") String username, @Param("deviceUuid") UUID deviceUuid);

    Optional<Users> findByUsername(String username);
}
