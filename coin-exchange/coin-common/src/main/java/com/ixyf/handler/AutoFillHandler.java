package com.ixyf.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 自动填充属性的类 例如在新增或者修改的时候 需要填充一些更新时间这样的属性
 */
@Component
public class AutoFillHandler implements MetaObjectHandler {

    /**
     * 插入时属性填充 一般用于公共字段的填充
     * 新增人
     * 新增时间
     * lastUpdateTime
     * @param metaObject 元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        // 创建人
        this.strictInsertFill(metaObject, "createBy", Long.class, getCurrentUserId());
        // 创建时间
        this.strictInsertFill(metaObject, "created", Date.class, new Date());
        // 最后修改时间
        this.strictInsertFill(metaObject, "lastUpdateTime", Date.class, new Date());
    }

    /**
     * 更新时属性填充
     * 修改人
     * 修改时间
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        // 修改人
        this.strictUpdateFill(metaObject, "modifyBy", Long.class, getCurrentUserId());
        // 修改时间
        this.strictUpdateFill(metaObject, "created", Date.class, new Date());
        // 最后修改时间
        this.strictInsertFill(metaObject, "lastUpdateTime", Date.class, new Date());
    }


    /**
     * 获取当前操作的用户对象
     * @return
     */
    private Object getCurrentUserId() {
        // 上下文中取用户id
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            String auth = authentication.getPrincipal().toString();
            return Long.valueOf(auth);
        }
        return null;
    }
}
