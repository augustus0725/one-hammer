package com.lueing.oh.app.api;


import com.lueing.oh.app.api.vo.i.UserVO;
import com.lueing.oh.commons.standard.RestResponse;
import com.lueing.oh.jpa.entity.User;
import com.lueing.oh.jpa.entity.base.Page;
import com.lueing.oh.jpa.entity.projections.UserFieldsAge;

/**
 * @author yesido0725@gmail.com
 * @date 2022/5/30 17:06
 */
public interface Users {
    /**
     * Save a user object.
     *
     * @param user is the object needed to be saved.
     * @return saved User Object.
     */
    RestResponse<User> save(UserVO user);

    /**
     * Find user info by user name.
     *
     * @param name        We find info by name.
     * @param currentPage Current page number.
     * @param pageSize    Page size.
     * @return A page of UserFieldsAge objects.
     */
    RestResponse<Page<UserFieldsAge>> findByName(String name, int currentPage, int pageSize);
}
