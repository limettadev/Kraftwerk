package pink.mino.kraftwerk.utils

import redis.clients.jedis.Jedis

fun interface RedisCommand<T> {
    fun execute(jedis: Jedis): T
}