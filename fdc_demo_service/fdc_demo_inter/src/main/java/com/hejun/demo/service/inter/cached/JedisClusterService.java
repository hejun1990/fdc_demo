package com.hejun.demo.service.inter.cached;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisSentinelPool;
import redis.clients.jedis.exceptions.JedisException;

import javax.annotation.PostConstruct;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by hejun-FDC on 2017/5/12.
 */
@Service("jedisClusterService")
public class JedisClusterService {
    private static final Logger logger = LoggerFactory.getLogger(JedisClusterService.class);

    @Value("${sentinelAddress}")
    private String sentinelAddress;
    private JedisSentinelPool jedisSentinelPool;
    private String sentinelName = "mymaster";


    @PostConstruct
    private void initMethod() {
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxTotal(200);
        genericObjectPoolConfig.setMaxWaitMillis(5000L);
        genericObjectPoolConfig.setMaxIdle(100);
        genericObjectPoolConfig.setMinIdle(5);
        genericObjectPoolConfig.setMinEvictableIdleTimeMillis(600000L);
        genericObjectPoolConfig.setTimeBetweenEvictionRunsMillis(300000L);
        genericObjectPoolConfig.setTestOnBorrow(true);
        Set<String> addressSet = new HashSet<>();
        String[] addresses = this.sentinelAddress.split(";");
        for (String address : addresses) {
            addressSet.add(address);
        }
        this.jedisSentinelPool = new JedisSentinelPool(this.sentinelName, addressSet, genericObjectPoolConfig);
    }

    /**
     * 删除key
     *
     * @param key key值
     * @return 被删除 key 的数量
     */
    public Long del(String key) {
        Jedis jedis = null;
        long result = 0;
        try {
            jedis = this.jedisSentinelPool.getResource();
            result = jedis.del(key);
        } catch (JedisException e) {
            logger.error("jedis del key : {} ,exception occured: {}", key, e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return result;
    }

    /**
     * 删除key
     *
     * @param key      key值
     * @param dbNumber 分区
     * @return 被删除 key 的数量
     */
    public Long del(String key, int dbNumber) {
        Jedis jedis = null;
        long result = 0;
        try {
            jedis = this.jedisSentinelPool.getResource();
            jedis.select(dbNumber);
            result = jedis.del(key);
        } catch (JedisException e) {
            logger.error("jedis del key : {} ,exception occured: {}", key, e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return result;
    }

    /**
     * 检查给定 key 是否存在
     *
     * @param key key值
     * @return 若 key 存在返回 true ，否则返回 false
     */
    public boolean exists(String key) {
        Jedis jedis = null;
        boolean result = false;
        try {
            jedis = this.jedisSentinelPool.getResource();
            result = jedis.exists(key);
        } catch (JedisException e) {
            logger.error("jedis exists key : {} ,exception occured: {}", key, e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return result;
    }

    /**
     * 检查给定 key 是否存在
     *
     * @param key      key值
     * @param dbNumber 分区
     * @return 若 key 存在返回 true ，否则返回 false
     */
    public boolean exists(String key, int dbNumber) {
        Jedis jedis = null;
        boolean result = false;
        try {
            jedis = this.jedisSentinelPool.getResource();
            jedis.select(dbNumber);
            result = jedis.exists(key);
        } catch (JedisException e) {
            logger.error("jedis exists key : {} ,exception occured: {}", key, e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return result;
    }

    /**
     * 为给定 key 设置过期时间
     *
     * @param key     key值
     * @param timeout 过期时间，以秒为单位
     * @return 设置成功返回 1，否则返回0
     */
    public long expire(String key, int timeout) {
        Jedis jedis = null;
        long result = 0L;
        try {
            jedis = this.jedisSentinelPool.getResource();
            result = jedis.expire(key, timeout);
        } catch (Exception e) {
            logger.error("jedis settimeout: {} ,exception occured: {}", key, e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return result;
    }

    /**
     * 为给定 key 设置过期时间
     *
     * @param key      key值
     * @param timeout  过期时间，以秒为单位
     * @param dbNumber 分区
     * @return 设置成功返回 1，否则返回0
     */
    public long expire(String key, int timeout, Integer dbNumber) {
        Jedis jedis = null;
        long result = 0L;
        try {
            jedis = this.jedisSentinelPool.getResource();
            jedis.select(dbNumber.intValue());
            result = jedis.expire(key, timeout);
        } catch (Exception e) {
            logger.error("jedis settimeout: {} ,exception occured: {}", key, e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return result;
    }

    /**
     * 设置指定 key 的值
     *
     * @param key   key值
     * @param value value值
     * @return 设置成功返回 OK
     */
    public String set(String key, String value) {
        Jedis jedis = null;
        String code = null;
        try {
            jedis = this.jedisSentinelPool.getResource();
            code = jedis.set(key, value);
        } catch (JedisException e) {
            logger.error("jedis set key : {} ,exception occured: {}", key, e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return code;
    }

    /**
     * 设置指定 key 的值
     *
     * @param key      key值
     * @param value    value值
     * @param dbNumber 分区
     * @return 设置成功返回 OK
     */
    public String set(String key, String value, int dbNumber) {
        Jedis jedis = null;
        String code = null;
        try {
            jedis = this.jedisSentinelPool.getResource();
            jedis.select(dbNumber);
            code = jedis.set(key, value);
        } catch (JedisException e) {
            logger.error("jedis set key : {} ,exception occured: {}", key, e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return code;
    }

    /**
     * 获取指定 key 的值
     *
     * @param key key值
     * @return 返回 key 的值
     */
    public String get(String key) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = this.jedisSentinelPool.getResource();
            result = jedis.get(key);
        } catch (JedisException e) {
            logger.error("jedis get key : {} ,exception occured: {}", key, e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return result;
    }

    /**
     * 获取指定 key 的值
     *
     * @param key      key值
     * @param dbNumber 分区
     * @return 返回 key 的值
     */
    public String get(String key, int dbNumber) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = this.jedisSentinelPool.getResource();
            jedis.select(dbNumber);
            result = jedis.get(key);
        } catch (JedisException e) {
            logger.error("jedis get key : {} ,exception occured: {}", key, e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return result;
    }

    /**
     * 设置指定 key 的值，并返回 key 旧的值
     *
     * @param key   key值
     * @param value value值
     * @return key 旧的值
     */
    public String getSet(String key, String value) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = this.jedisSentinelPool.getResource();
            result = jedis.getSet(key, value);
        } catch (JedisException e) {
            logger.error("jedis getSet key : {} ,exception occured: {}", key, e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return result;
    }

    /**
     * 设置指定 key 的值，并返回 key 旧的值
     *
     * @param key      key值
     * @param value    value值
     * @param dbNumber 分区
     * @return key 旧的值
     */
    public String getSet(String key, String value, int dbNumber) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = this.jedisSentinelPool.getResource();
            jedis.select(dbNumber);
            result = jedis.getSet(key, value);
        } catch (JedisException e) {
            logger.error("jedis getSet key : {} ,exception occured: {}", key, e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return result;
    }

    /**
     * 将 key 中储存的数字值增一
     *
     * @param key key值
     * @return 数值增一后的值
     */
    public long incr(String key) {
        Jedis jedis = null;
        long result = 0L;
        try {
            jedis = this.jedisSentinelPool.getResource();
            result = jedis.incr(key);
        } catch (JedisException e) {
            logger.error("jedis incr key : {} ,exception occured: {}", key, e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return result;
    }

    /**
     * 将 key 中储存的数字值增一
     *
     * @param key      key值
     * @param dbNumber 分区
     * @return 数值增一后的值
     */
    public long incr(String key, int dbNumber) {
        Jedis jedis = null;
        long result = 0L;
        try {
            jedis = this.jedisSentinelPool.getResource();
            jedis.select(dbNumber);
            result = jedis.incr(key);
        } catch (JedisException e) {
            logger.error("jedis incr key : {} ,exception occured: {}", key, e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return result;
    }

    /**
     * 将 key 所储存的值加上给定的增量值
     *
     * @param key       key值
     * @param increment 增量
     * @return 加上指定的增量值之后，key 的值
     */
    public long incrBy(String key, int increment) {
        Jedis jedis = null;
        long result = 0L;
        try {
            jedis = this.jedisSentinelPool.getResource();
            result = jedis.incrBy(key, increment);
        } catch (JedisException e) {
            logger.error("jedis incrBy key : {} ,exception occured: {}", key, e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return result;
    }

    /**
     * 将 key 所储存的值加上给定的增量值
     *
     * @param key       key值
     * @param increment 增量
     * @param dbNumber  分区
     * @return 加上指定的增量值之后，key 的值
     */
    public long incrBy(String key, int increment, int dbNumber) {
        Jedis jedis = null;
        long result = 0L;
        try {
            jedis = this.jedisSentinelPool.getResource();
            jedis.select(dbNumber);
            result = jedis.incrBy(key, increment);
        } catch (JedisException e) {
            logger.error("jedis incrBy key : {} ,exception occured: {}", key, e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return result;
    }

    /**
     * 将 value 追加到 key 原来的值的末尾
     *
     * @param key   key值
     * @param value value值
     * @return 追加指定值后，key 中字符串的长度
     */
    public long append(String key, String value) {
        Jedis jedis = null;
        long result = 0L;
        try {
            jedis = this.jedisSentinelPool.getResource();
            result = jedis.append(key, value);
        } catch (JedisException e) {
            logger.error("jedis append key : {} ,exception occured: {}", key, e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return result;
    }

    /**
     * 将 value 追加到 key 原来的值的末尾
     *
     * @param key      key值
     * @param value    value值
     * @param dbNumber 分区
     * @return 追加指定值后，key 中字符串的长度
     */
    public long append(String key, String value, int dbNumber) {
        Jedis jedis = null;
        long result = 0L;
        try {
            jedis = this.jedisSentinelPool.getResource();
            jedis.select(dbNumber);
            result = jedis.append(key, value);
        } catch (JedisException e) {
            logger.error("jedis append key : {} ,exception occured: {}", key, e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return result;
    }

    /**
     * 同时将多个 field-value (域-值)对设置到哈希表 key 中
     *
     * @param key  key值
     * @param hash map值
     * @return 执行成功，返回 OK
     */
    public String hmset(String key, Map<String, String> hash) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = this.jedisSentinelPool.getResource();
            result = jedis.hmset(key, hash);
        } catch (JedisException e) {
            logger.error("jedis hmset key : {} ,exception occured: {}", key, e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    /**
     * 同时将多个 field-value (域-值)对设置到哈希表 key 中
     *
     * @param key      key值
     * @param hash     map值
     * @param dbNumber 分区
     * @return 执行成功，返回 OK
     */
    public String hmset(String key, Map<String, String> hash, int dbNumber) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = this.jedisSentinelPool.getResource();
            jedis.select(dbNumber);
            result = jedis.hmset(key, hash);
        } catch (JedisException e) {
            logger.error("jedis hmset key : {} ,exception occured: {}", key, e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    /**
     * 获取所有给定字段的值
     *
     * @param key    key值
     * @param fields 字段值
     * @return 包含多个给定字段关联值的List集合
     */
    public List<String> hmget(String key, String[] fields) {
        Jedis jedis = null;
        List<String> result = null;
        try {
            jedis = this.jedisSentinelPool.getResource();
            result = jedis.hmget(key, fields);
        } catch (JedisException e) {
            logger.error("jedis hmget key : {} ,exception occured: {}", key, e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    /**
     * 获取所有给定字段的值
     *
     * @param key      key值
     * @param fields   字段值
     * @param dbNumber 分区
     * @return 包含多个给定字段关联值的List集合
     */
    public List<String> hmget(String key, String[] fields, int dbNumber) {
        Jedis jedis = null;
        List<String> result = null;
        try {
            jedis = this.jedisSentinelPool.getResource();
            jedis.select(dbNumber);
            result = jedis.hmget(key, fields);
        } catch (JedisException e) {
            logger.error("jedis hmget key : {} ,exception occured: {}", key, e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    /**
     * 获取指定key的Map值
     *
     * @param key key值
     * @return 指定key的Map值
     */
    public Map<String, String> hgetAll(String key) {
        Jedis jedis = null;
        Map<String, String> result = null;
        try {
            jedis = this.jedisSentinelPool.getResource();
            result = jedis.hgetAll(key);
        } catch (JedisException e) {
            logger.error("jedis hgetAll key : {} ,exception occured: {}", key, e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }
        return result;
    }

    /**
     * 获取指定key的Map值
     *
     * @param key      key值
     * @param dbNumber 分区
     * @return 指定key的Map值
     */
    public Map<String, String> hgetAll(String key, int dbNumber) {
        Jedis jedis = null;
        Map<String, String> result = null;
        try {
            jedis = this.jedisSentinelPool.getResource();
            jedis.select(dbNumber);
            result = jedis.hgetAll(key);
        } catch (JedisException e) {
            logger.error("jedis hgetAll key : {} ,exception occured: {}", key, e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return result;
    }

    /**
     * 将一个或多个值插入到列表头部
     *
     * @param key  key值
     * @param list list值
     * @return 列表的长度
     */
    public long lpush(String key, List<String> list) {
        Jedis jedis = null;
        long result = 0L;
        try {
            jedis = this.jedisSentinelPool.getResource();
            result = jedis.lpush(key, list.toArray(new String[list.size()]));
        } catch (JedisException e) {
            logger.error("jedis lpush key : {} ,exception occured: {}", key, e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return result;
    }

    /**
     * 将一个或多个值插入到列表头部
     *
     * @param key      key值
     * @param list     list值
     * @param dbNumber 分区
     * @return 列表的长度
     */
    public long lpush(String key, List<String> list, int dbNumber) {
        Jedis jedis = null;
        long result = 0L;
        try {
            jedis = this.jedisSentinelPool.getResource();
            jedis.select(dbNumber);
            result = jedis.lpush(key, list.toArray(new String[list.size()]));
        } catch (JedisException e) {
            logger.error("jedis lpush key : {} ,exception occured: {}", key, e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return result;
    }

    /**
     * 返回列表中全部元素
     *
     * @param key key值
     * @return 返回列表中全部元素
     */
    public List<String> lrange(String key) {
        Jedis jedis = null;
        List<String> result = null;
        try {
            jedis = this.jedisSentinelPool.getResource();
            result = jedis.lrange(key, 0, -1);
        } catch (JedisException e) {
            logger.error("jedis lrange key : {} ,exception occured: {}", key, e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return result;
    }

    /**
     * 返回列表中全部元素
     *
     * @param key      key值
     * @param dbNumber 分区
     * @return 返回列表中全部元素
     */
    public List<String> lrange(String key, int dbNumber) {
        Jedis jedis = null;
        List<String> result = null;
        try {
            jedis = this.jedisSentinelPool.getResource();
            jedis.select(dbNumber);
            result = jedis.lrange(key, 0, -1);
        } catch (JedisException e) {
            logger.error("jedis lrange key : {} ,exception occured: {}", key, e.getMessage());
        } finally {
            if (jedis != null) {
                jedis.close();
            }
        }

        return result;
    }
}
