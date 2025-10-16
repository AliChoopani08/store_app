package com.Ali.Store.App.repository.userAndProfileUser;

import com.Ali.Store.App.entities.userAndProfileUser.ProfileUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositoryProfileUser extends JpaRepository<ProfileUser, Long> {

}
