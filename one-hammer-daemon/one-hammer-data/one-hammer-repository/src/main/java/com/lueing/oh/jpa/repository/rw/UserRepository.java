package com.lueing.oh.jpa.repository.rw;

import com.lueing.oh.jpa.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author yesido0725@gmail.com
 * @date 2022/5/30 17:06
 */
public interface UserRepository extends JpaRepository<User, String> {
}
