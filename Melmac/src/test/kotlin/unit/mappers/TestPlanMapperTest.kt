import mappers.TestPlanMapper
import domain.TestPlan
import dtos.TestPlanResponseDTO
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TestPlanMapperTest {
    @Test
    fun `toDto maps TestPlan to TestPlanResponseDTO correctly`() {
        val testPlan = TestPlan(1, "MyTest", 2)
        val dto = TestPlanMapper.toDto(testPlan)
        assertEquals(1, dto.testPlanId)
        assertEquals("MyTest", dto.testName)
        assertEquals(2, dto.metricMetricId)
    }

    @Test
    fun `toDto throws if testPlanId is null`() {
        val testPlan = TestPlan(null, "MyTest", 2)
        assertThrows(IllegalStateException::class.java) {
            TestPlanMapper.toDto(testPlan)
        }
    }

    @Test
    fun `toDomain maps TestPlanResponseDTO to TestPlan correctly`() {
        val dto = TestPlanResponseDTO(3, "OtherTest", 4)
        val testPlan = TestPlanMapper.toDomain(dto)
        assertEquals(3, testPlan.testPlanId)
        assertEquals("OtherTest", testPlan.testName)
        assertEquals(4, testPlan.metricMetricId)
    }
}