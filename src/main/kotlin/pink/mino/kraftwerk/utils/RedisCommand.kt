package pink.mino.kraftwerk.utils;

import redis.clients.jedis.Jedis;

public interface RedisCommand<T> {
    T execute(Jedis paramJedis);
}