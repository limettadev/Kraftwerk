package pink.mino.kraftwerk.utils

class HealthChatColorer {
    companion object {
        fun returnHealth(health: Double): String {
            var c = ""
            when {
                health >= 90 -> {
                    c = "<dark_green>"
                }
                health >= 80 -> {
                    c = "<green>"
                }
                health >= 70 -> {
                    c = "<gold>"
                }
                health >= 35 -> {
                    c = "<yellow>"
                }
                health >= 0 -> {
                    c = "<red>"
                }
                else -> {
                    c = "<dark_gray>"
                }
            }
            return c
        }
    }
}