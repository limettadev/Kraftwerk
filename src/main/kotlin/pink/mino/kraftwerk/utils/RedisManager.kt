package pink.mino.kraftwerk.utils

import pink.mino.kraftwerk.features.ConfigFeature
import redis.clients.jedis.JedisPool
import redis.clients.jedis.util.JedisURIHelper
import java.net.URI

class RedisManager {
    private val redisClient: JedisPool

    init {
        val jedisUri = URI.create(ConfigFeature.instance.config!!.getString("database.redis.uri"))

        if (JedisURIHelper.isValid(jedisUri)) {
            this.redisClient = JedisPool(jedisUri)
        } else {
            throw RuntimeException("Invalid Redis URL")
        }
    }


    fun <T> executeCommand(command: RedisCommand<T?>): T? {
        try {
            this.redisClient.resource.use { jedis ->
                return command.execute(jedis)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
}