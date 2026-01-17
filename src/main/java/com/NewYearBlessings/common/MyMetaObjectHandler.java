package com.NewYearBlessings.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 自定义元对象处理器
 *
 * @author Alanine
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    //新增的自动填充逻辑
    @Override
    public void insertFill(MetaObject metaObject) {
        // 创建人 = 登录人  创建时间
        //从session中获取登录人
        // 无法从session中获取！
        // 使用线程空间 treadlocal
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("createBy", BaseContext.getUsername());
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateBy", BaseContext.getUsername());
    }

    //修改的自动填充逻辑
    @Override
    public void updateFill(MetaObject metaObject) {
        //修改人 = 登录人 修改时间
        metaObject.setValue("updateTime", LocalDateTime.now());
        // 从session中获取登录人
        metaObject.setValue("updateBy", BaseContext.getUsername());
    }
}
