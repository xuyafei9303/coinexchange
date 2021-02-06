package com.ixyf.controller;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.ixyf.constant.Constants;
import com.ixyf.model.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * http://localhost:8081/#/usercenter/modify-id-senior
 */
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

    /**
     * oss的回调地址
     */
    // TODO 内网穿透地址
    @Value("${oss.callback.url:http://kvgprm.natappfree.cc}")
    private String ossCallBack;

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
        // https://coin-exchange-xyf.oss-cn-guangzhou.aliyuncs.com/2021/02/06/1612621615554.jpg
        // https://coin-exchange-xyf.oss-cn-guangzhou.aliyuncs.com2021/02/06/1612622433758.jpg
        return R.ok(Constants.HTTPS + bucketName + Constants.POINT + endPoint + Constants.SLASH + fileName);
    }

    @GetMapping("/image/pre/upload")
    @ApiOperation(value = "高级认证中的获取票据")
    public R<Map<String, String>> preUploadPolicy() {
        String dir = DateUtil.today().replaceAll("-", "/") + "/";
        // 2021/02/06/
        System.out.println("dir = " + dir);
        Map<String, String> policy = getPolicy(30L, 3 * 1024 * 1024L, dir);
        return R.ok(policy);
    }

    private Map<String, String> getPolicy(long expireTime, long maxFileSize, String dir) {
        try {
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            // PostObject请求最大可支持的文件大小为5 GB，即CONTENT_LENGTH_RANGE为5*1024*1024*1024。
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, maxFileSize);
            // 设置上传到哪个文件夹
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds); // 设置该policy有效时间
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            // 返回值
            Map<String, String> respMap = new LinkedHashMap<String, String>();
            respMap.put("accessid", accessKeyId); // accessKey
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            // https://coin-exchange-xyf.oss-cn-guangzhou.aliyuncs.com
            respMap.put("host", Constants.HTTPS + bucketName + "." + endPoint); // 上传文件的host
            respMap.put("expire", String.valueOf(expireEndTime / 1000));
            // respMap.put("expire", formatISO8601Date(expiration));

            JSONObject jasonCallback = new JSONObject();
            jasonCallback.put("callbackUrl", ossCallBack);// 当前端把文件上传给oss后，oss会朝这个地址(公网地址)发一个post请求来告知后端服务器用户上传文件的情况
            jasonCallback.put("callbackBody",
                    "filename=${object}&size=${size}&mimeType=${mimeType}&height=${imageInfo.height}&width=${imageInfo.width}");
            jasonCallback.put("callbackBodyType", "application/x-www-form-urlencoded");
            String base64CallbackBody = BinaryUtil.toBase64String(jasonCallback.toString().getBytes());
            respMap.put("callback", base64CallbackBody);

            return respMap;

//            JSONObject ja1 = JSONObject.fromObject(respMap);
//            // System.out.println(ja1.toString());
//            response.setHeader("Access-Control-Allow-Origin", "*");
//            response.setHeader("Access-Control-Allow-Methods", "GET, POST");
//            response(request, response, ja1.toString());

        } catch (Exception e) {
            // Assert.fail(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
