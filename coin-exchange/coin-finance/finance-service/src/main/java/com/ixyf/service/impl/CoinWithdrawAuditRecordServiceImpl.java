package com.ixyf.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ixyf.domain.CoinWithdrawAuditRecord;
import com.ixyf.mapper.CoinWithdrawAuditRecordMapper;
import com.ixyf.service.CoinWithdrawAuditRecordService;
@Service
public class CoinWithdrawAuditRecordServiceImpl extends ServiceImpl<CoinWithdrawAuditRecordMapper, CoinWithdrawAuditRecord> implements CoinWithdrawAuditRecordService{

}
