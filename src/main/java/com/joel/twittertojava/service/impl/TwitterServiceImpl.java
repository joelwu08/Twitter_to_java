package com.joel.twittertojava.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.joel.twittertojava.service.TwitterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Joel
 * @since 2023/5/16
 **/
@Service
@Slf4j
public class TwitterServiceImpl implements TwitterService {

    static final String clientId = "";
    static final String clientSecret = "";
    static final String callBackUrl = "http://127.0.0.1:8080/twitter/callback";
    static final String codeChallenge = Base64.getUrlEncoder().encodeToString(RandomUtil.randomString(48).getBytes(StandardCharsets.UTF_8));

    @Override
    public String oauth2Url() {
        String state = IdUtil.simpleUUID();//这里可以用业务的唯一ID
        return "https://twitter.com/i/oauth2/authorize?response_type=code&scope=tweet.read%20users.read%20follows.read%20" +
                "like.read%20offline.access&client_id=" + clientId + "&redirect_uri=" + callBackUrl + "&state=" + state + "&code_challenge=" + codeChallenge + "&code_challenge_method=plain";
    }

    @Override
    public void oauth2CallBack(HttpServletRequest request) {
        String code = request.getParameter("code");
        String state = request.getParameter("state");
        String url = "https://api.twitter.com/2/oauth2/token";
        String clientCredentials = clientId + ":" + clientSecret;
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("grant_type", "authorization_code");
        paramMap.put("code", code);
        paramMap.put("state", state);
        paramMap.put("code_verifier", codeChallenge);
        paramMap.put("redirect_uri", callBackUrl);
        HttpResponse response = HttpRequest.post(url).form(paramMap)
                .auth("Basic " + Base64.getUrlEncoder().encodeToString(clientCredentials.getBytes(StandardCharsets.UTF_8)))
                .execute();
        if (response.getStatus() == HttpStatus.HTTP_OK) {
            JSONObject body = JSONObject.parseObject(response.body());
            String tokenType = body.getString("token_type");
            String accessToken = body.getString("access_token");
            String refreshToken = body.getString("refresh_token");
            //通过accessToken获取用户信息
            HttpResponse rep = HttpRequest.get("https://api.twitter.com/2/users/me").bearerAuth(accessToken).execute();
            if (rep.getStatus() == HttpStatus.HTTP_OK) {
                JSONObject userBody = JSONObject.parseObject(rep.body());
                JSONObject userData = userBody.getJSONObject("data");
                if (userData != null) {
                    String id = userData.getString("id");
                    String name = userData.getString("name");
                    String username = userData.getString("username");
                    System.out.println("id:" + id);
                    System.out.println("name:" + name);
                    System.out.println("username:" + username);
                }
            } else {
                log.error(rep.body());
            }

        } else {
            log.error(response.body());
        }
    }

    @Override
    public void sendTweet(String content, String bearerToken) {
        String url = "https://api.twitter.com/2/tweets";
        HashMap<String, Object> param = new HashMap<>(1);
        param.put("text", content);
        HttpResponse execute = HttpRequest.post(url).bearerAuth(bearerToken).body(JSON.toJSONString(param)).execute();
        if (execute.getStatus() != HttpStatus.HTTP_CREATED) {
            log.error(execute.body());
        }
    }

}
