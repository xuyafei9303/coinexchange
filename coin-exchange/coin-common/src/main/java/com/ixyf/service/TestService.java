package com.ixyf.service;

import com.ixyf.model.WebLog;

public interface TestService {
    /**
     * 通过一个username 查询weblog
     */
    WebLog queryByUsername(String username);
}
