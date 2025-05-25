package pink.mino.kraftwerk.utils

import java.util.*

data class PlayerJoinMessage (
    val playerUniqueId: UUID,
    val sessionId: UUID
)