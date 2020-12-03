package com.piesat.sso.client.config;


import com.piesat.sso.client.shiro.*;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @program: sod
 * @描述
 * @创建人 zzj
 * @创建时间 2019/12/5 14:28
 */
@Order(0)
@Configuration
public class ShiroConfig {
 /*   @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.timeout}")
    private int timeout;
    @Value("${spring.redis.password}")
    private String password;*/

    @Value("${session.timeout}")
    private int sessionTimeout;


    @Bean
    public ShiroFilterFactoryBean shirFilter(SecurityManager securityManager) {
        System.out.println("ShiroConfiguration.shirFilter()");
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        //注意过滤器配置顺序 不能颠倒
        //配置退出 过滤器,其中的具体的退出代码Shiro已经替我们实现了，登出后跳转配置的loginUrl
        filterChainDefinitionMap.put("/logout", "logout");
        filterChainDefinitionMap.put("/logout", "anon");

        // 配置不会被拦截的链接 顺序判断
        filterChainDefinitionMap.put("/static/**", "anon");
        filterChainDefinitionMap.put("/ajaxLogin", "anon");
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/thirdLogin", "anon");
        filterChainDefinitionMap.put("/checkToken/*", "anon");
        filterChainDefinitionMap.put("/captchaImage", "anon");
        filterChainDefinitionMap.put("/getInfo", "anon");
        filterChainDefinitionMap.put("/unauth", "anon");
        filterChainDefinitionMap.put("/swagger-ui.html", "anon");
        filterChainDefinitionMap.put("/webjars/**", "anon");
        filterChainDefinitionMap.put("/v2/**", "anon");
        filterChainDefinitionMap.put("/swagger-resources/**", "anon");
        filterChainDefinitionMap.put("/doc.html", "anon");
        filterChainDefinitionMap.put("/api/**", "anon");
        filterChainDefinitionMap.put("/upload/**", "anon");
        filterChainDefinitionMap.put("/**", "anon");
        filterChainDefinitionMap.put("/**", "authc");
        LinkedHashMap<String, Filter> filtsMap = new LinkedHashMap<>();
        // 这里使用自定义的filter
        filtsMap.put("authc", new HtAuthenticationFilter());
        filtsMap.put("roles", new HtRolesAuthorizationFilter());

        shiroFilterFactoryBean.setFilters(filtsMap);
        //配置shiro默认登录界面地址，前后端分离中登录界面跳转应由前端路由控制，后台仅返回json数据
        //shiroFilterFactoryBean.setLoginUrl("/unauth");
        //shiroFilterFactoryBean.setUnauthorizedUrl("/unauthorized");
        // 登录成功后要跳转的链接
//        shiroFilterFactoryBean.setSuccessUrl("/index");
        //未授权界面;
//        shiroFilterFactoryBean.setUnauthorizedUrl("/403");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(htShiroRealm());
        // 自定义session管理 使用redis
        securityManager.setSessionManager(sessionManager());
        // 自定义缓存实现 使用redis
        securityManager.setCacheManager(cacheManager());
        return securityManager;
    }

    @Bean
    public HtShiroRealm htShiroRealm() {
        HtShiroRealm htShiroRealm = new HtShiroRealm();
        htShiroRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return htShiroRealm;
    }


    /**
     * cacheManager 缓存 redis实现
     * <p>
     * 使用的是shiro-redis开源插件
     *
     * @return
     */
    @Bean
    public HtRedisCacheManager cacheManager() {
        HtRedisCacheManager redisCacheManager = new HtRedisCacheManager();
        redisCacheManager.setHtRedisManager(redisManager());
        redisCacheManager.setExpire(sessionTimeout * 1000);
        return redisCacheManager;
    }

    /**
     * 配置shiro redisManager
     * <p>
     * 使用的是shiro-redis开源插件
     *
     * @return
     */
    @Bean
    public HtRedisManager redisManager() {
        HtRedisManager redisManager = new HtRedisManager();
        return redisManager;
    }

    //自定义sessionManager
    @Bean
    public DefaultWebSessionManager sessionManager() {
        HtSessionManager htSessionManager = new HtSessionManager();
        htSessionManager.setSessionDAO(redisSessionDAO());

        htSessionManager.setSessionIdCookie(cookie());            // 设置JSESSIONID
        //全局会话超时时间（单位毫秒），默认30分钟  暂时设置为10秒钟 用来测试
        htSessionManager.setGlobalSessionTimeout(sessionTimeout * 1000);
        //是否开启删除无效的session对象  默认为true
        htSessionManager.setDeleteInvalidSessions(true);
        //是否开启定时调度器进行检测过期session 默认为true
        htSessionManager.setSessionValidationSchedulerEnabled(true);
        //设置session失效的扫描时间, 清理用户直接关闭浏览器造成的孤立会话 默认为 1个小时
        //设置该属性 就不需要设置 ExecutorServiceSessionValidationScheduler 底层也是默认自动调用ExecutorServiceSessionValidationScheduler
        //暂时设置为 5秒 用来测试
        htSessionManager.setSessionValidationInterval(sessionTimeout * 1000 * 2);
        //取消url 后面的 JSESSIONID
        htSessionManager.setSessionIdUrlRewritingEnabled(false);
        return htSessionManager;
    }


    /**
     * RedisSessionDAO shiro sessionDao层的实现 通过redis
     * <p>
     * 使用的是shiro-redis开源插件
     */
    @Bean
    public HtRedisSessionDAO redisSessionDAO() {
        HtRedisSessionDAO redisSessionDAO = new HtRedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        redisSessionDAO.setExpire(sessionTimeout * 1000);
        return redisSessionDAO;
    }

    /**
     * 凭证匹配器
     * （由于我们的密码校验交给Shiro的SimpleAuthenticationInfo进行处理了
     * ）
     *
     * @return
     */
    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5");//散列算法:这里使用MD5算法;
        hashedCredentialsMatcher.setHashIterations(2);//散列的次数，比如散列两次，相当于 md5(md5(""));
        return hashedCredentialsMatcher;
    }


    @Bean
    public SimpleCookie cookie() {
        SimpleCookie cookie = new SimpleCookie("SHAREJSESSIONID"); //  cookie的name,对应的默认是 JSESSIONID
        cookie.setHttpOnly(true);
        cookie.setPath("/");        //  path为 / 用于多个系统共享JSESSIONID
        return cookie;
    }


    @Bean
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }


    /**
     * 开启shiro aop注解支持.
     * 使用代理方式;所以需要开启代码支持;
     *
     * @param securityManager
     * @return
     */
    /**
     * 开启shiro aop注解支持.
     * 使用代理方式;所以需要开启代码支持;
     *
     * @param securityManager
     * @return
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    /*@Bean
    public  LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }*/

    @Bean(name = "exceptionHandler")
    public HtExceptionHandler handlerExceptionResolver(){
        return new HtExceptionHandler();
    }


}
