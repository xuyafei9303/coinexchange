package com.ixyf.service.impl;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.ixyf.model.WebLog;
import com.ixyf.service.TestService;
import org.springframework.stereotype.Service;

@Service
public class TestServiceImpl implements TestService {

    /**
     * 通过用户name查询weblog
     * @param username
     * @return
     */
    @Override
    @Cached(name = "com.ixyf.service.impl.TestServiceImpl:", key = "#username", cacheType = CacheType.BOTH)
    public WebLog queryByUsername(String username) {
        final WebLog webLog = new WebLog();
        webLog.setUsername(username);
        webLog.setIp("localhost");
        webLog.setResult("success");
        return webLog;
    }
}
