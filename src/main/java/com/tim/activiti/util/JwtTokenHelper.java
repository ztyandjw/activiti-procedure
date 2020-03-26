package com.tim.activiti.util;

import com.tim.activiti.exception.ActivitiServiceException;
import com.tim.activiti.service.activiti.ActivitiService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.Map;

/**
 * @author T1m Zhang(49244143@qq.com) 2020/3/24.
 */

public class JwtTokenHelper extends JwtAccessTokenConverter {

    public Map<String, Object> decode(String token){
        return super.decode(token);
    }

    public static String getGroupId(Map<String, Object> jwtToken) {
        if(!jwtToken.containsKey("groupId") ) {
            throw new ActivitiServiceException(400, "token中不含有groupId字段");
        }

        String groupId = (String)jwtToken.get("groupName");
//        if(StringUtils.isBlank(groupId)) {
//            throw new ActivitiServiceException(400, "token中groupId字段为空");
//        }
        return groupId;
    }

    public static String getUserId(Map<String, Object> jwtToken) {
        if(!jwtToken.containsKey("userId") ) {
            throw new ActivitiServiceException(400, "token中不含有userId字段");
        }

        String userId = (String)jwtToken.get("userId");
        if(StringUtils.isBlank(userId)) {
            throw new ActivitiServiceException(400, "token中userId字段为空");
        }
        return userId;
    }

    public static void authenticationGroup(String authGroupName, Map<String, Object> jwtToken) {
        String groupId = getGroupId(jwtToken);
        if(!StringUtils.equals(authGroupName, groupId)) {
            throw new ActivitiServiceException(401, "没有权限执行");
        }
    }
}
