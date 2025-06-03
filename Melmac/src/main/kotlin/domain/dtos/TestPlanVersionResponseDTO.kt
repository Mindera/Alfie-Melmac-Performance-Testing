package dtos

import java.time.LocalDateTime

data class TestPlanVersionResponseDTO(
    val testPlanVersionId: Int,              
    val version: String,                     
    val creationTimestamp: LocalDateTime,    
    val notes: String?,                      
    val testPlanTestPlanId: Int,             
    val deviceDeviceId: Int,                 
    val appVersionAppVersionId: Int,   
    val appPackage: String,
    val mainActivity: String? = null,      
    val executionTypeExecutionTypeId: Int,  
    val thresholds: List<TestThresholdResponseDTO>,
    val metricParameters: List<TestMetricParameterResponseDTO>,
    val executionTypeParameters: List<TestExecutionTypeParameterResponseDTO>,
    val testSuiteVersionId: Int
)


