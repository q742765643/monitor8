package com.piesat.sso.client.shiro;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.crazycake.shiro.SessionInMemory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;

/**
 * @program: sod
 * @description:
 * @author: zzj
 * @create: 2019-12-18 11:31
 **/
public class HtRedisSessionDAO extends AbstractSessionDAO {
    private static final String DEFAULT_SESSION_KEY_PREFIX = "shiro:session:";
    private static final long DEFAULT_SESSION_IN_MEMORY_TIMEOUT = 1000L;
    private static final int DEFAULT_EXPIRE = -2;
    private static final int NO_EXPIRE = -1;
    private static final int MILLISECONDS_IN_A_SECOND = 1000;
    private static Logger logger = LoggerFactory.getLogger(HtRedisSessionDAO.class);
    private static ThreadLocal sessionsInThread = new ThreadLocal();
    private String keyPrefix = "shiro:session:";
    private long sessionInMemoryTimeout = 1000L;
    private int expire = -2;
    private HtRedisManager redisManager;

    public HtRedisSessionDAO() {
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        this.saveSession(session);
    }

    private void saveSession(Session session) throws UnknownSessionException {
        if (session != null && session.getId() != null) {
            String key;
            Object value;
            try {
                key = this.getRedisSessionKey(session.getId());
                value = session;
            } catch (Exception var5) {
                logger.error("serialize session error. session id=" + session.getId());
                throw new UnknownSessionException(var5);
            }

            if (this.expire == -2) {
                this.redisManager.set(key, value, (int) (session.getTimeout() / 1000L));
            } else {
                if (this.expire != -1 && (long) (this.expire * 1000) < session.getTimeout()) {
                    logger.warn("Redis session expire time: " + this.expire * 1000 + " is less than Session timeout: " + session.getTimeout() + " . It may cause some problems.");
                }

                this.redisManager.set(key, value, this.expire / 1000L);
            }
        } else {
            logger.error("session or session id is null");
            throw new UnknownSessionException("session or session id is null");
        }
    }

    @Override
    public void delete(Session session) {
        if (session != null && session.getId() != null) {
            try {
                this.redisManager.del(this.getRedisSessionKey(session.getId()));
            } catch (Exception var3) {
                logger.error("delete session error. session id=" + session.getId());
            }

        } else {
            logger.error("session or session id is null");
        }
    }

    @Override
    public Collection<Session> getActiveSessions() {
        HashSet sessions = new HashSet();

        try {
            Set<String> keys = this.redisManager.scan(this.keyPrefix + "*");
            if (keys != null && keys.size() > 0) {
                Iterator var3 = keys.iterator();

                while (var3.hasNext()) {
                    String key = (String) var3.next();
                    Session s = (Session) this.redisManager.get(key);
                    sessions.add(s);
                }
            }
        } catch (Exception var6) {
            logger.error("get active sessions error.");
        }

        return sessions;
    }

    @Override
    protected Serializable doCreate(Session session) {
        if (session == null) {
            logger.error("session is null");
            throw new UnknownSessionException("session is null");
        } else {
            Serializable sessionId = this.generateSessionId(session);
            this.assignSessionId(session, sessionId);
            this.saveSession(session);
            return sessionId;
        }
    }

    @Override
    protected Session doReadSession(Serializable sessionId) {
        if (sessionId == null) {
            logger.warn("session id is null");
            return null;
        } else {
            Session s = this.getSessionFromThreadLocal(sessionId);
            if (s != null) {
                return s;
            } else {
                logger.debug("read session from redis");

                try {
                    s = (Session) this.redisManager.get(this.getRedisSessionKey(sessionId));
                    this.setSessionToThreadLocal(sessionId, s);
                } catch (Exception var4) {
                    logger.error("read session error. settionId=" + sessionId);
                }

                return s;
            }
        }
    }

    private void setSessionToThreadLocal(Serializable sessionId, Session s) {
        Map<Serializable, SessionInMemory> sessionMap = (Map) sessionsInThread.get();
        if (sessionMap == null) {
            sessionMap = new HashMap();
            sessionsInThread.set(sessionMap);
        }

        SessionInMemory sessionInMemory = new SessionInMemory();
        sessionInMemory.setCreateTime(new Date());
        sessionInMemory.setSession(s);
        ((Map) sessionMap).put(sessionId, sessionInMemory);
    }

    private Session getSessionFromThreadLocal(Serializable sessionId) {
        Session s = null;
        if (sessionsInThread.get() == null) {
            return null;
        } else {
            Map<Serializable, SessionInMemory> sessionMap = (Map) sessionsInThread.get();
            SessionInMemory sessionInMemory = (SessionInMemory) sessionMap.get(sessionId);
            if (sessionInMemory == null) {
                return null;
            } else {
                Date now = new Date();
                long duration = now.getTime() - sessionInMemory.getCreateTime().getTime();
                if (duration < this.sessionInMemoryTimeout) {
                    s = sessionInMemory.getSession();
                    logger.debug("read session from memory");
                } else {
                    sessionMap.remove(sessionId);
                }

                return s;
            }
        }
    }

    private String getRedisSessionKey(Serializable sessionId) {
        return this.keyPrefix + sessionId;
    }

    public HtRedisManager getRedisManager() {
        return redisManager;
    }

    public void setRedisManager(HtRedisManager redisManager) {
        this.redisManager = redisManager;
    }

    public String getKeyPrefix() {
        return this.keyPrefix;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix;
    }

    public long getSessionInMemoryTimeout() {
        return this.sessionInMemoryTimeout;
    }

    public void setSessionInMemoryTimeout(long sessionInMemoryTimeout) {
        this.sessionInMemoryTimeout = sessionInMemoryTimeout;
    }

    public int getExpire() {
        return this.expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }
}

