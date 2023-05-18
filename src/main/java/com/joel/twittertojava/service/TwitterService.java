package com.joel.twittertojava.service;

import cn.hutool.http.HttpRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Joel
 * @since 2023/5/16
 **/
public interface TwitterService {

    /**
     * 获取Oauth2授权请求地址
     * 官网接口文档 https://developer.twitter.com/en/docs/authentication/oauth-2-0/authorization-code
     *
     * @return 授权地址
     */
    String oauth2Url();

    /**
     * 处理Twitter授权回调
     * 官网接口文档 https://developer.twitter.com/en/docs/authentication/oauth-2-0/authorization-code
     *
     */
    void oauth2CallBack(HttpServletRequest request);

    /**
     * 发送推文
     * 官网接口文档 https://developer.twitter.com/en/docs/twitter-api/tweets/manage-tweets/api-reference/post-tweets
     *
     * @param content     内容
     * @param bearerToken Twitter BearerToken
     */
    void sendTweet(String content, String bearerToken);

}
