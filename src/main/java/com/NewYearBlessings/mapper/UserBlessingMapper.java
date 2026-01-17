package com.NewYearBlessings.mapper;

import com.NewYearBlessings.entity.UserBlessing;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

/**
 * 用户祝福表 Mapper 接口
 */
public interface UserBlessingMapper extends BaseMapper<UserBlessing> {
    /**
     * 获取各城市的祝福统计数据
     */
    @Select("SELECT city as name, COUNT(*) as value FROM user_blessing WHERE audit_status = 1 AND is_deleted = 0 GROUP BY city ORDER BY value DESC")
    List<Map<String, Object>> selectCityStats();
}