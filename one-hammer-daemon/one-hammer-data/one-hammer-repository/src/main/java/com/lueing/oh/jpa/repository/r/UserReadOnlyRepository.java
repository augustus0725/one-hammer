package com.lueing.oh.jpa.repository.r;

import com.lueing.oh.jpa.entity.User;
import com.lueing.oh.jpa.entity.projections.UserFieldsAge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author yesido0725@gmail.com
 * @date 2022/5/30 17:06
 */
public interface UserReadOnlyRepository extends JpaRepository<User, String> {
    /**
     * Query user by username.
     *
     * @param name    is query parameter.
     * @param request is page request parameter.
     * @return A page of UserFieldsAge Objects.
     */
    Page<UserFieldsAge> findByName(String name, PageRequest request);
}
