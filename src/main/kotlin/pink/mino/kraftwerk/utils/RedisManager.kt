package pink.mino.kraftwerk.utils

import org.bukkit.Bukkit
import pink.mino.kraftwerk.features.ConfigFeature
import redis.clients.jedis.JedisPool
import redis.clients.jedis.JedisPoolConfig

class RedisManager {
    private val redisClient: JedisPool

    init {
        val username = ConfigFeature.instance.config!!.getString("database.redis.user")
        val password = ConfigFeature.instance.config!!.getString("database.redis.password")
        val host = ConfigFeature.instance.config!!.getString("database.redis.host")
        val port = ConfigFeature.instance.config!!.getInt("database.redis.port")
        val timeout = 2000 // Or any other timeout in milliseconds

        val poolConfig = JedisPoolConfig()

        redisClient = when {
            !username.isNullOrBlank() && !password.isNullOrBlank() -> {
                // Use full AUTH with username (ACL)
                Bukkit.getLogger().info("Using full AUTH with username")
                JedisPool(poolConfig, host, port, timeout, username, password)
            }
            !password.isNullOrBlank() -> {
                // Use AUTH with default user
                Bukkit.getLogger().info("Using AUTH with default user")
                JedisPool(poolConfig, host, port, timeout, password)
            }
            else -> {
                // No authentication
                Bukkit.getLogger().info("No AUTH")
                JedisPool(poolConfig, host, port, timeout)
            }
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