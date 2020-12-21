package com.notebook.swan;

import com.notebook.config.SwanProperties;
import com.notebook.domain.dto.SwanJscode2SessionDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Project: notebook
 * File: SwanUtil
 *
 * @author evan
 * @date 2020/11/7
 */
@Component
@SuppressWarnings("all")
public class SwanRequestService {
    public static final String SWAN_JSCODE2SESSIONEKY_URL = "https://spapi.baidu.com/oauth/jscode2sessionkey ";

    private final SwanProperties swanProperties;
    private final RestTemplate restTemplate;

    public SwanRequestService(SwanProperties swanProperties, RestTemplate restTemplate) {
        this.swanProperties = swanProperties;
        this.restTemplate = restTemplate;
    }

    protected Map<String, Object> requetLogin(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("code", code);
        map.add("client_id", swanProperties.getAppKey());
        map.add("sk", swanProperties.getAppSecret());
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);
        return restTemplate.postForObject(SWAN_JSCODE2SESSIONEKY_URL, request, Map.class);
    }

    public SwanJscode2SessionDto jscode2SessionKey(String code) {
        Map<String, Object> resultMap = requetLogin(code);
        if (resultMap.containsKey("openid") && resultMap.containsKey("session_key")) {
            return new SwanJscode2SessionDto(true, (String) resultMap.get("openid"),
                    (String) resultMap.get("session_key"), null, null, null);
        } else {
            throw new RuntimeException(resultMap.toString());
        }
    }
}
