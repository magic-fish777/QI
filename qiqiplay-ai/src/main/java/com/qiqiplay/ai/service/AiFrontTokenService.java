package com.qiqiplay.ai.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.servlet.http.HttpServletRequest;

import com.qiqiplay.ai.domain.model.AiFrontLoginUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.qiqiplay.common.constant.CacheConstants;
import com.qiqiplay.common.constant.Constants;
import com.qiqiplay.common.core.redis.RedisCache;
import com.qiqiplay.common.utils.ServletUtils;
import com.qiqiplay.common.utils.StringUtils;
import com.qiqiplay.common.utils.ip.AddressUtils;
import com.qiqiplay.common.utils.ip.IpUtils;
import com.qiqiplay.common.utils.uuid.IdUtils;
import eu.bitwalker.useragentutils.UserAgent;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * AI前端token验证处理
 *
 * @author qiqiplay
 */
@Component
public class AiFrontTokenService
{
    private static final Logger log = LoggerFactory.getLogger(AiFrontTokenService.class);

    // 令牌自定义标识
    @Value("${token.header}")
    private String header;

    // 令牌秘钥
    @Value("${token.secret}")
    private String secret;

    // 令牌有效期（默认30分钟）
    @Value("${token.expireTime}")
    private int expireTime;

    protected static final long MILLIS_SECOND = 1000;

    protected static final long MILLIS_MINUTE = 60 * MILLIS_SECOND;

    // 令牌有效期（默认30分钟）
    private static final long EXPIRE_TIME = 30 * MILLIS_MINUTE;

    @Autowired
    private RedisCache redisCache;

    /**
     * 获取用户身份信息
     *
     * @return 用户信息
     */
    public AiFrontLoginUser getLoginUser(HttpServletRequest request)
    {
        // 获取请求携带的令牌
        String token = getToken(request);
        System.out.println("==> [AI Token] 从请求中获取token: " + (token != null ? "成功" : "失败"));
        if (StringUtils.isNotEmpty(token))
        {
            try
            {
                System.out.println("==> [AI Token] 开始解析token: " + token.substring(0, Math.min(20, token.length())) + "...");
                Claims claims = parseToken(token);
                // 解析对应的权限以及用户信息
                String uuid = (String) claims.get(Constants.LOGIN_USER_KEY);
                System.out.println("==> [AI Token] 从claims中获取uuid: " + uuid);
                String userKey = getTokenKey(uuid);
                System.out.println("==> [AI Token] 构建用户缓存键: " + userKey);
                AiFrontLoginUser user = redisCache.getCacheObject(userKey);
                System.out.println("==> [AI Token] 从缓存获取用户: " + (user != null ? "成功" : "失败"));
                return user;
            }
            catch (Exception e)
            {
                System.out.println("==> [AI Token] 获取用户信息异常: " + e.getMessage());
                log.error("获取用户信息异常'{}'", e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("==> [AI Token] 请求中没有token");
        }
        return null;
    }

    /**
     * 设置用户身份信息
     */
    public void setLoginUser(AiFrontLoginUser loginUser)
    {
        if (StringUtils.isNotNull(loginUser) && StringUtils.isNotEmpty(loginUser.getToken()))
        {
            refreshToken(loginUser);
        }
    }

    /**
     * 删除用户身份信息
     */
    public void delLoginUser(String token)
    {
        if (StringUtils.isNotEmpty(token))
        {
            String userKey = getTokenKey(token);
            redisCache.deleteObject(userKey);
        }
    }

    /**
     * 创建令牌
     *
     * @param loginUser 用户信息
     * @return 令牌
     */
    public String createToken(AiFrontLoginUser loginUser)
    {
        String token = IdUtils.fastUUID();
        loginUser.setToken(token);
        setUserAgent(loginUser);
        refreshToken(loginUser);

        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.LOGIN_USER_KEY, token);
        return createToken(claims);
    }

    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     *
     * @param loginUser
     * @return 令牌
     */
    public void verifyToken(AiFrontLoginUser loginUser)
    {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= MILLIS_MINUTE * 20)
        {
            refreshToken(loginUser);
        }
    }

    /**
     * 刷新令牌有效期
     *
     * @param loginUser 登录信息
     */
    public void refreshToken(AiFrontLoginUser loginUser)
    {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + expireTime * MILLIS_MINUTE);
        // 根据uuid将loginUser缓存
        String userKey = getTokenKey(loginUser.getToken());
        redisCache.setCacheObject(userKey, loginUser, expireTime, TimeUnit.MINUTES);
    }

    /**
     * 设置用户代理信息
     *
     * @param loginUser 登录信息
     */
    public void setUserAgent(AiFrontLoginUser loginUser)
    {
        UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        String ip = IpUtils.getIpAddr();
        loginUser.setIpaddr(ip);
        loginUser.setLoginLocation(AddressUtils.getRealAddressByIP(ip));
        loginUser.setBrowser(userAgent.getBrowser().getName());
        loginUser.setOs(userAgent.getOperatingSystem().getName());
    }

    /**
     * 从数据声明生成令牌
     *
     * @param claims 数据声明
     * @return 令牌
     */
    private String createToken(Map<String, Object> claims)
    {
        String token = Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret).compact();
        return token;
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return 数据声明
     */
    private Claims parseToken(String token)
    {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token)
    {
        Claims claims = parseToken(token);
        return claims.getSubject();
    }

    /**
     * 获取请求token
     *
     * @param request
     * @return token
     */
    private String getToken(HttpServletRequest request)
    {
        String token = request.getHeader(header);
        System.out.println("==> [AI Token] Header名称: " + header + ", 原始token: " + token);
        if (StringUtils.isNotEmpty(token) && token.startsWith(Constants.TOKEN_PREFIX))
        {
            token = token.replace(Constants.TOKEN_PREFIX, "");
            System.out.println("==> [AI Token] 移除前缀后token: " + token);
        }
        return token;
    }

    private String getTokenKey(String uuid)
    {
        return CacheConstants.AI_LOGIN_TOKEN_KEY + uuid;
    }
}