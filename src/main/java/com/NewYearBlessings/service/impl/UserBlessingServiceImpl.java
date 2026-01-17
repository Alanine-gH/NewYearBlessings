package com.NewYearBlessings.service.impl;

import com.NewYearBlessings.entity.UserBlessing;
import com.NewYearBlessings.entity.AuditLog;
import com.NewYearBlessings.enums.ErrorType;
import com.NewYearBlessings.exception.BlessingException;
import com.NewYearBlessings.exception.SystemException;
import com.NewYearBlessings.mapper.UserBlessingMapper;
import com.NewYearBlessings.mapper.AuditLogMapper;
import com.NewYearBlessings.service.UserBlessingService;
import com.NewYearBlessings.service.DeepSeekService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.NewYearBlessings.dto.BlessingSubmitDTO;
import com.NewYearBlessings.vo.BlessingVO;
import com.NewYearBlessings.vo.AuditResultVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户祝福表 服务实现类
 */
@Service
public class UserBlessingServiceImpl extends ServiceImpl<UserBlessingMapper, UserBlessing> implements UserBlessingService {

    @Autowired
    private DeepSeekService deepSeekService;

    @Autowired
    private AuditLogMapper auditLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void submitBlessing(BlessingSubmitDTO submitDTO, String ip) {
        // 1. 审核祝福内容
        AuditResultVO auditResult = deepSeekService.auditBlessingContent(submitDTO.getBlessingContent());

        // 保存祝福记录
        UserBlessing blessing = new UserBlessing();
        BeanUtils.copyProperties(submitDTO, blessing);

        // XSS防护：对祝福内容进行HTML转义
        blessing.setBlessingContent(org.springframework.web.util.HtmlUtils.htmlEscape(blessing.getBlessingContent()));
        // 对昵称也进行HTML转义
        blessing.setNickname(org.springframework.web.util.HtmlUtils.htmlEscape(blessing.getNickname()));
        // 对城市名称进行HTML转义
        if (blessing.getCity() != null) {
            blessing.setCity(org.springframework.web.util.HtmlUtils.htmlEscape(blessing.getCity()));
        }

        // 设置审核状态和情感得分
        blessing.setAuditStatus(auditResult.isPass() ? 1 : 2);
        blessing.setAuditReason(auditResult.isPass() ? null : auditResult.getReason());
        blessing.setEmotionScore(auditResult.getEmotionScore());
        blessing.setSubmitIp(ip);
        blessing.setSubmitTime(LocalDateTime.now());
        blessing.setIsDeleted(false);

        // 保存到数据库
        boolean saved = save(blessing);
        if (!saved) {
            throw new RuntimeException("祝福保存失败");
        }

        // 3. 记录审核日志
        AuditLog auditLog = new AuditLog();
        auditLog.setBlessingId(blessing.getId());
        auditLog.setAuditType(1);
        auditLog.setAuditResult(auditResult.isPass() ? 1 : 2);
        auditLog.setAuditReason(auditResult.getReason());
        auditLog.setEmotionScore(auditResult.getEmotionScore());
        auditLog.setAuditTime(LocalDateTime.now());
        auditLogMapper.insert(auditLog);
    }

    @Override
    public List<BlessingVO> getBlessings(int page, int size) {
        // 确保页码和尺寸为正数
        if (page < 1) page = 1;
        if (size < 1) size = 10;

        // 查询审核通过且未删除的祝福
        List<UserBlessing> blessings = list(new QueryWrapper<UserBlessing>()
                .eq("audit_status", 1)
                .eq("is_deleted", false)
                .orderByDesc("submit_time")
                .last("LIMIT " + (page - 1) * size + ", " + size));

        // 转换为VO
        return blessings.stream().map(blessing -> {
            BlessingVO vo = new BlessingVO();
            BeanUtils.copyProperties(blessing, vo);
            // 这里可以添加图片URL的处理
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public long getBlessingCount() {
        return count(new QueryWrapper<UserBlessing>()
                .eq("audit_status", 1)
                .eq("is_deleted", false));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void auditBlessing(Long id, Integer status, String reason, Double emotionScore) {
        UserBlessing blessing = getById(id);
        if (blessing == null) {
            throw new RuntimeException("祝福信息不存在");
        }

        // 更新审核状态
        blessing.setAuditStatus(status);
        blessing.setAuditReason(reason);
        blessing.setEmotionScore(emotionScore);
        blessing.setAuditTime(LocalDateTime.now());

        boolean updated = updateById(blessing);
        if (!updated) {
            throw new RuntimeException("祝福审核状态更新失败");
        }

        // 记录审核日志
        AuditLog auditLog = new AuditLog();
        auditLog.setBlessingId(id);
        auditLog.setAuditType(1);
        auditLog.setAuditResult(status);
        auditLog.setAuditReason(reason);
        auditLog.setEmotionScore(emotionScore);
        auditLog.setAuditTime(LocalDateTime.now());
        auditLogMapper.insert(auditLog);
    }


    @Override
    public List<java.util.Map<String, Object>> getCityStats() {
        // 查询各城市的祝福统计数据
        return baseMapper.selectCityStats();
    }

    @Override
    public List<BlessingVO> getCityBlessings(String city, int page, int size) {
        // 确保页码和尺寸为正数
        if (page < 1) page = 1;
        if (size < 1) size = 5;

        // 查询指定城市的审核通过且未删除的祝福
        List<UserBlessing> blessings = list(new QueryWrapper<UserBlessing>()
                .eq("city", city)
                .eq("audit_status", 1)
                .eq("is_deleted", false)
                .orderByDesc("submit_time")
                .last("LIMIT " + (page - 1) * size + ", " + size));

        // 转换为VO
        return blessings.stream().map(blessing -> {
            BlessingVO vo = new BlessingVO();
            BeanUtils.copyProperties(blessing, vo);
            return vo;
        }).collect(Collectors.toList());
    }
}
