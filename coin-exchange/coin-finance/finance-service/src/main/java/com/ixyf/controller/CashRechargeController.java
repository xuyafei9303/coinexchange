package com.ixyf.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ixyf.domain.CashRecharge;
import com.ixyf.domain.CashRechargeAuditRecord;
import com.ixyf.model.R;
import com.ixyf.service.CashRechargeService;
import com.ixyf.utils.ReportCsvUtils;
import io.swagger.annotations.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
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
@Api(tags = "GCN充值")
@RequestMapping(value = "/CashRecharge")
public class CashRechargeController {

    @Resource
    private CashRechargeService cashRechargeService;

    @GetMapping("/records")
    @ApiOperation(value = "分页查询现金充值数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "current", value = "当前页"),
            @ApiImplicitParam(name = "size", value = "每页显示条数"),
            @ApiImplicitParam(name = "coinId", value = "币种id"),
            @ApiImplicitParam(name = "userId", value = "用户id"),
            @ApiImplicitParam(name = "userName", value = "用户名"),
            @ApiImplicitParam(name = "mobile", value = "用户手机号"),
            @ApiImplicitParam(name = "status", value = "充值状态"),
            @ApiImplicitParam(name = "numMin", value = "最小充值金额"),
            @ApiImplicitParam(name = "numMax", value = "最大充值金额"),
            @ApiImplicitParam(name = "startTime", value = "开始时间"),
            @ApiImplicitParam(name = "endTime", value = "结束时间"),
    })
    public R<Page<CashRecharge>> findByPage(@ApiIgnore Page<CashRecharge> page,
                                            Long coinId, Long userId, String userName, String mobile,
                                            Byte status, String numMin, String numMax, String startTime, String endTime
    ) {
        Page<CashRecharge> cashRechargePage = cashRechargeService.findByPage(page, coinId, userId, userName, mobile, status, numMin, numMax, startTime, endTime);
        return R.ok(cashRechargePage);

    }

    @GetMapping(value = "/records/export")
    @ApiOperation(value = "充值记录导出为csv")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "coinId", value = "币种id"),
            @ApiImplicitParam(name = "userId", value = "用户id"),
            @ApiImplicitParam(name = "userName", value = "用户名"),
            @ApiImplicitParam(name = "mobile", value = "用户手机号"),
            @ApiImplicitParam(name = "status", value = "充值状态"),
            @ApiImplicitParam(name = "numMin", value = "最小充值金额"),
            @ApiImplicitParam(name = "numMax", value = "最大充值金额"),
            @ApiImplicitParam(name = "startTime", value = "开始时间"),
            @ApiImplicitParam(name = "endTime", value = "结束时间"),
    })
    public void recordsExport(Long coinId, Long userId, String userName, String mobile,
                              Byte status, String numMin, String numMax, String startTime, String endTime) {
        Page<CashRecharge> page = new Page<>(1, 10000);
        Page<CashRecharge> cashRechargePage = cashRechargeService.findByPage(page, coinId, userId, userName, mobile, status, numMin, numMax, startTime, endTime);
        List<CashRecharge> records = cashRechargePage.getRecords();
        if (!CollectionUtils.isEmpty(records)) {
            String[] header = {"ID", "用户ID", "用户名", "真实用户名", "充值币种", "充值金额(USDT)", "手续费", "到账金额(CNY)", "充值方式", "充值订单", "参考号", "充值时间", "完成时间", "状态", "审核备注", "审核级数"};
            String[] properties = {"id", "userId", "username", "realName", "coinName", "num", "fee", "mum", "type", "tradeno", "remark", "created", "lastTime", "status", "auditRemark", "step"};

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

            // 类型：alipay，支付宝；cai1pay，财易付；bank，银联;
            CellProcessorAdaptor typeAdaptor = new CellProcessorAdaptor() {
                @Override
                public <T> T execute(Object o, CsvContext csvContext) {
                    String type = String.valueOf(o);
                    String typeName = "";
                    switch (type) {
                        case "alipay":
                            typeName = "支付宝";
                            break;
                        case "cai1pay":
                            typeName = "财易付";
                            break;
                        case "bank":
                            typeName = "银联";
                            break;
                        case "linepay":
                            typeName = "在线支付";
                            break;
                        default:
                            typeName = "未知";
                            break;
                    }
                    return (T) typeName;
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

            // 状态：0-待审核；1-审核通过；2-拒绝；3-充值成功；
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
                            statusStr = "充值成功";
                            break;
                        default:
                            statusStr = "未知";
                            break;

                    }
                    return (T) statusStr;
                }
            };

            CellProcessor[] PROCESSOR = new CellProcessor[]{
                long2StringAdaptor, long2StringAdaptor, null, null, null, bigDecimalAdaptor, bigDecimalAdaptor, bigDecimalAdaptor, typeAdaptor, null, null, timeAdaptor, timeAdaptor, statusAdaptor, null, null
            };
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            try {
                assert requestAttributes != null;
                // 导出csv文件
                ReportCsvUtils.reportList(requestAttributes.getResponse(), header, properties, "场外交易审核记录.csv",records, PROCESSOR);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @PostMapping("/cashRechargeUpdateStatus")
    @ApiOperation(value = "现金充值审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "cashRechargeAuditRecord", value = "cashRechargeAuditRecord json")
    })
    public R cashRechargeUpdateStatus(@RequestBody @Validated CashRechargeAuditRecord cashRechargeAuditRecord) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        boolean update = cashRechargeService.cashRechargeAudit(userId, cashRechargeAuditRecord);
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
    public R<Page<CashRecharge>> findUserCashRecharge(@ApiIgnore Page<CashRecharge> page, Byte status) {
        Long userId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        Page<CashRecharge> rechargePage = cashRechargeService.findUserCashRecharge(page, userId, status);
        return R.ok(rechargePage);
    }
}
