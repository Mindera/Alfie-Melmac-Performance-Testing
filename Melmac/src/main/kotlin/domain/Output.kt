package domain

data class Output(
    val id: Int?= null,
    val metricId: Int,
    val name: String,
    val unit: String
)