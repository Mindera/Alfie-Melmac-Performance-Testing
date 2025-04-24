package domain

import java.time.Instant

data class TestSuite(
    val id: Int? = null,
    val name: String,
    val description: String? = null,
    val initialTimestamp: Instant? = null,
    val endTimestamp: Instant? = null
)

