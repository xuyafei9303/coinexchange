package com.ixyf.controller;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.ixyf.constant.Constants;
import com.ixyf.model.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;

@RestController
@Api(tags = "文件上传控制器")
public class FileController {

    @Resource
    private OSS ossClient;

    @Value("${oss.bucket.name:coin-exchange-xyf}")
    private String bucketName;

    @Value("${spring.cloud.alicloud.oss.endpoint}")
    private String endPoint;

    @Value("${spring.cloud.alicloud.access-key}")
    private String accessKeyId;

    @Value("${spring.cloud.alicloud.secret-key}")
    private String accessKeySecret;

    @ApiOperation(value = "文件上传")
    @PostMapping("/image/AliYunImgUpload")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "要上传的文件")
    })
    public R<String> fileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        /**
         * bucketName
         * 文件名称 日期 + 原始文件名称 2021/02/01/文件原始名称.jpg
         * 输入流
         * https://mall-bucket-xyf.oss-cn-guangzhou.aliyuncs.com/2020-10-16/27351fbd-656c-4a70-8dc3-2123d43b35c1_WechatIMG15.jpeg
         * https://signin.aliyun.com/1418532166959814.onaliyun.com/login.htm
         */
        String fileName = DateUtil.today().replaceAll("-", "/") + "/" + file.getOriginalFilename();
        ossClient.putObject(bucketName, fileName, file.getInputStream());

        return R.ok(Constants.HTTPS + bucketName + Constants.POINT + endPoint + Constants.SLASH + fileName);
    }
}
