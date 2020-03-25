package com.tim.activiti.service.activiti;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.tim.activiti.exception.ActivitiServiceException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static java.awt.Color.decode;
import static java.awt.SystemColor.info;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/3/13.
 */

@Slf4j
public class GenerateUrlService extends ActivitiService implements JavaDelegate {

    private static final class SingleTonHoler{
        private static RestTemplate restTemplate = new RestTemplate();
    }




    @Override
    public void execute(DelegateExecution execution) {
        String urlPath = getApolloConfigValue("generateUrl", "xxx");
        logHead(execution, log, " 访问url地址" + urlPath);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> input = new HashMap();
        input.put("bussinessKey", execution.getProcessInstanceBusinessKey());
        input.put("token", (String)execution.getVariable("callbackToken"));
        try {
            ResponseEntity<GenerateUrlResult> response = SingleTonHoler.restTemplate.exchange(urlPath, HttpMethod.POST,
                    new HttpEntity<>(input, headers), GenerateUrlResult.class);
            GenerateUrlResult generateUrlResult = response.getBody();
            String html = generateUrlResult.getHtml();
            String expiredDate = generateUrlResult.getExpiredDate();
            if(generateUrlResult.getSuccess() == false) {
                logTailFail(execution, log);
                throw new ActivitiServiceException(500, "调用url地址: " + urlPath + "返回值为false");
            }
            if(StringUtils.isBlank(html)) {
                logTailFail(execution, log);
                throw new ActivitiServiceException(500, "调用url地址: " + urlPath + "返回html为空");
            }
            if(StringUtils.isBlank(expiredDate)) {
                logTailFail(execution, log);
                throw new ActivitiServiceException(500, "调用url地址: " + urlPath + "返回expiredDate为空");
            }
//            LocalDateTime expire = LocalDateTime.parse(expiredDate);
//            ZoneId myZone = ZoneId.of("Asia/Shanghai");
//            LocalDateTime now = LocalDateTime.now(myZone);
//            if(!expire.isAfter(now)) {
//                logTailFail(execution, log);
//                throw new ActivitiServiceException(500, "过期时间: " + expiredDate + "早于现在时间");
//            }

            execution.setVariable("html",generateUrlResult.getHtml());
            execution.setVariable("expiredDate",generateUrlResult.getExpiredDate());
            logTailSuccess(execution, log);
        }
        catch (ActivitiServiceException e) {
            throw e;
        }
        catch (Throwable throwable) {
            logTailFail(execution, log);
            throw new ActivitiServiceException(500, "调用url地址: " + urlPath + "失败", throwable);
        }
    }

    @Data
    private static final class GenerateUrlResult {
        private String expiredDate;
        private Boolean success;
        private String html;
    }


}
