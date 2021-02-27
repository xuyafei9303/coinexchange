package com.ixyf.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.CashRecharge;
import com.ixyf.domain.CashWithdrawAuditRecord;
import com.ixyf.domain.CashWithdrawals;
import com.ixyf.model.R;
import com.ixyf.service.CashWithdrawalsService;
import com.ixyf.utils.ReportCsvUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.supercsv.cellprocessor.CellProcessorAdaptor;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.util.CsvContext;
import springfox.documentation.annotations.ApiIgnore;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@Api(tags = "提现记录")
@RequestMapping("/cashWithdrawals")
public class CashWithdrawalsController {

    @Resource
    private CashWithdrawalsService cashWithdrawalsService;

    @GetMapping("/records")
    @ApiOperation(value = "分页查询提现记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示条数"),
            @ApiImplicitParam(name = "userId", value = "用户id"),
            @ApiImplicitParam(name = "userName", value = "用户名"),
            @ApiImplicitParam(name = "mobile", value = "用户手机号"),
            @ApiImplicitParam(name = "status", value = "充值状态"),
            @ApiImplicitParam(name = "numMin", value = "最小充值金额"),
            @ApiImplicitParam(name = "numMax", value = "最大充值金额"),
            @ApiImplicitParam(name = "startTime", value = "开始时间"),
            @ApiImplicitParam(name = "endTime", value = "结束时间"),
    })
    public R<Page<CashWithdrawals>> findByPage(@ApiIgnore Page<CashWithdrawals> page,
                                               Long userId, String userName, String mobile,
                                               Byte status, String numMin, String numMax, String startTime, String endTime

    ) {
        Page<CashWithdrawals> cashWithdrawalsPage = cashWithdrawalsService.findByPage(page, userId, userName, mobile, status, numMin, numMax, startTime, endTime);
        return R.ok(cashWithdrawalsPage);
    }


    @GetMapping("/records/export")
    @ApiOperation(value = "数字货币提现记录导出csv")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户id"),
            @ApiImplicitParam(name = "userName", value = "用户名"),
            @ApiImplicitParam(name = "mobile", value = "用户手机号"),
            @ApiImplicitParam(name = "status", value = "充值状态"),
            @ApiImplicitParam(name = "numMin", value = "最小充值金额"),
            @ApiImplicitParam(name = "numMax", value = "最大充值金额"),
            @ApiImplicitParam(name = "startTime", value = "开始时间"),
            @ApiImplicitParam(name = "endTime", value = "结束时间"),
    })
    public void export(Long userId, String userName, String mobile,
                       Byte status, String numMin, String numMax, String startTime, String endTime) {
        Page<CashWithdrawals> cashWithdrawalsPage = new Page<>(1, 10000);
        Page<CashWithdrawals> pageData = cashWithdrawalsService.findByPage(cashWithdrawalsPage, userId, userName, mobile, status, numMin, numMax, startTime, endTime);
        List<CashWithdrawals> records = pageData.getRecords();
        if (!CollectionUtils.isEmpty(records)) {
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            String[] header = {"ID", "用户ID", "用户名", "提现金额(USDT)", "手续费", "到账金额", "提现开户名", "银行名称", "账户", "申请时间", "完成时间", "状态", "备注", "审核级数"};
            String[] properties = {"id", "userId", "username", "num", "fee", "mum", "truename", "bank", "bankCard", "created", "lastTime", "status", "remark", "step"};

            CellProcessorAdaptor long2StringAdaptor = new CellProcessorAdaptor() {
                @Override
                public <T> T execute(Object o, CsvContext csvContext) {
                    return (T) String.valueOf(o);
                }
            };

            // 金额需要保留8位有效数字
            DecimalFormat decimalFormat = new DecimalFormat("0.00000000");
            CellProcessorAdaptor bigDecimalAdaptor = new CellProcessorAdaptor() {
                @Override
                public <T> T execute(Object o, CsvContext csvContext) {
                    BigDecimal bigDecimal = (BigDecimal) o;
                    return (T) decimalFormat.format(bigDecimal);
                }
            };

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            CellProcessorAdaptor timeAdaptor = new CellProcessorAdaptor() {
                @Override
                public <T> T execute(Object o, CsvContext csvContext) {
                    Date date = (Date) o;
                    return (T) simpleDateFormat.format(date);
                }
            };

            // 状态：0-待审核；1-审核通过；2-拒绝；3-提现成功；
            CellProcessorAdaptor statusAdaptor = new CellProcessorAdaptor() {
                @Override
                public <T> T execute(Object o, CsvContext csvContext) {
                    Integer status = (Integer) o;
                    String statusStr = "";
                    switch (status) {
                        case 0 :
                            statusStr = "待审核";
                            break;
                        case 1:
                            statusStr = "审核通过";
                            break;
                        case 2:
                            statusStr = "拒绝";
                            break;
                        case 3:
                            statusStr = "提现成功";
                            break;
                        default:
                            statusStr = "未知";
                            break;

                    }
                    return (T) statusStr;
                }
            };

            CellProcessor[] PROCESSOR = new CellProcessor[]{
                long2StringAdaptor, long2StringAdaptor, null, bigDecimalAdaptor, bigDecimalAdaptor, bigDecimalAdaptor, null, null, timeAdaptor, timeAdaptor, statusAdaptor, null, null
            };
            try {
                ReportCsvUtils.reportList(requestAttributes.getResponse(), header, properties, "场外交易数字货币提现记录.csv", records, PROCESSOR);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @PostMapping("/updateWithdrawalsStatus")
    public R updateCashWithdrawalsStatus(@RequestBody @Validated CashWithdrawAuditRecord cashWithdrawAuditRecord) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        boolean update = cashWithdrawalsService.updateWithdrawalsStatus(userId, cashWithdrawAuditRecord);
        if (update) {
            return R.ok();
        }
        return R.fail("审核失败");
    }

    @GetMapping("/user/records")
    @ApiOperation(value = "查询当前用户的充值记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示条数"),
            @ApiImplicitParam(name = "status", value = "充值状态")
    })
    public R<Page<CashWithdrawals>> findUserCashWithdrawals(@ApiIgnore Page<CashWithdrawals> page, Byte status) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        Page<CashWithdrawals> withdrawalsPage = cashWithdrawalsService.findUserCashWithdrawals(page, userId, status);
        return R.ok(withdrawalsPage);
    }
}
