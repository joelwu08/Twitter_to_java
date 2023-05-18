package com.joel.twittertojava.web;

import com.joel.twittertojava.service.TwitterService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * @author Joel
 * @since 2023/5/16
 **/
@RestController
@RequestMapping("/twitter")
public class TwitterController {

    @Resource
    private HttpServletRequest httpServletRequest;
    @Resource
    private TwitterService twitterService;

    @GetMapping("/oauth2_url")
    public String oauth2Url(){
        return twitterService.oauth2Url();
    }

    @GetMapping("/callback")
    public String callback(){
        twitterService.oauth2CallBack(httpServletRequest);
        return "ok";
    }
}
