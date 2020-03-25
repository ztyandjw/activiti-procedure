package com.tim.activiti.service.activiti;

import com.tim.activiti.exception.ActivitiServiceException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static java.awt.SystemColor.info;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/3/13.
 */


@Slf4j
public class DisabledUrlService extends  ActivitiService implements JavaDelegate {


    private static class SingleTonHoler{

        private static RestTemplate restTemplate = new RestTemplate();

    }

    @Override
    public void execute(DelegateExecution execution) {

        String urlPath = getApolloConfigValue("disableUrl", "xxx");
        logHead(execution, log, " 访问url地址" + urlPath);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> input = new HashMap();
        input.put("bussinessKey", execution.getProcessInstanceBusinessKey());
        input.put("token", (String)execution.getVariable("callbackToken"));

        try {
            ResponseEntity<DisableUrlResult> response = SingleTonHoler.restTemplate.exchange(urlPath, HttpMethod.POST,
                    new HttpEntity<>(input, headers), DisableUrlResult.class);
            DisableUrlResult result = response.getBody();

            if(result.getSuccess() == false) {
                logTailFail(execution, log);
                throw new ActivitiServiceException(500, "调用url地址: " + urlPath + "返回值为false");
            }
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
    private static final class DisableUrlResult {
        private Boolean success;
    }

}
