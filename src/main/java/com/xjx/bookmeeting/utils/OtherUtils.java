package com.xjx.bookmeeting.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.concurrent.TimeUnit;

/**
 * there is introduction
 *
 * @author xjx
 * @date 2021/10/7 15:26
 */
@Slf4j
public class OtherUtils {
    public static String urlEncodeUtf8(String s) {
        if (s == null) {
            return null;
        }
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

    public static void sleep(long milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            log.info(e.getMessage(), e);
        }
    }

    public static <FROM, TO> TO convert(FROM from, Class<TO> toClass) {
        if (from == null) {
            return null;
        }
        return JSON.parseObject(JSON.toJSONString(from), toClass);
    }

    public static String encrypt(String s) {
        if (StringUtils.isBlank(s)) {
            return s;
        }

        byte[] encode = Base64.getEncoder().encode(s.getBytes(StandardCharsets.UTF_8));
        return new String(encode);
    }

    public static String decrypt(String s) {
        if (StringUtils.isBlank(s)) {
            return s;
        }
        byte[] decode = Base64.getDecoder().decode(s);
        return new String(decode);
    }
}
