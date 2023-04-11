package com.lueing.oh.app.v1;

import lombok.RequiredArgsConstructor;
import com.lueing.oh.app.api.Users;
import com.lueing.oh.app.api.vo.i.UserVO;
import com.lueing.oh.commons.standard.RestResponse;
import com.lueing.oh.jpa.entity.User;
import com.lueing.oh.jpa.entity.base.Page;
import com.lueing.oh.jpa.entity.projections.UserFieldsAge;
import com.lueing.oh.jpa.repository.r.UserReadOnlyRepository;
import com.lueing.oh.jpa.repository.rw.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

/**
 * @author zhangcanbin@hongwangweb.com
 * @date 2022/5/30 17:06
 */
@Service
@RequiredArgsConstructor(onConstructor__ = {@Autowired})
public class UsersV1 implements Users {
    private final UserRepository userRepository;
    private final UserReadOnlyRepository userReadOnlyRepository;

    @Override
    public RestResponse<User> save(UserVO user) {
        return RestResponse.ok(userRepository.save(user));
    }


    @Override
    public RestResponse<Page<UserFieldsAge>> findByName(String name, int currentPage, int pageSize) {
        org.springframework.data.domain.Page<UserFieldsAge> pageData =
                userReadOnlyRepository.findByName(name, PageRequest.of(currentPage, pageSize));

        return RestResponse.ok(Page.of(pageData, currentPage, pageSize));
    }
}
