package com.NewYearBlessings.service;

import com.NewYearBlessings.entity.UserBlessing;
import com.baomidou.mybatisplus.extension.service.IService;
import com.NewYearBlessings.dto.BlessingSubmitDTO;
import com.NewYearBlessings.vo.BlessingVO;
import java.util.List;

/**
 * 用户祝福表 服务类
 */
public interface UserBlessingService extends IService<UserBlessing> {
    /**
     * 提交祝福
     */
    void submitBlessing(BlessingSubmitDTO submitDTO, String ip);

    /**
     * 获取祝福列表
     */
    List<BlessingVO> getBlessings(int page, int size);

    /**
     * 获取祝福总数
     */
    long getBlessingCount();

    /**
     * 审核祝福
     */
    void auditBlessing(Long id, Integer status, String reason, Double emotionScore);

    /**
     * 获取各城市祝福统计数据
     */
    List<java.util.Map<String, Object>> getCityStats();

    /**
     * 获取指定城市的祝福列表
     */
    List<BlessingVO> getCityBlessings(String city, int page, int size);
}