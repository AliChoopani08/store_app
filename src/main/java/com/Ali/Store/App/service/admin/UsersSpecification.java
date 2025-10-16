package com.Ali.Store.App.service.admin;

import com.Ali.Store.App.entities.userAndProfileUser.Users;
import org.springframework.data.jpa.domain.Specification;

public class UsersSpecification {

    public static Specification<Users> withUsername(String username) {
        return ((root, _, criteriaBuilder) ->
                username == null ? null : criteriaBuilder
                        .like(criteriaBuilder
                                .lower(root.get("username"))
                                    , "%" + username.toLowerCase() + "%"));
    }
}
