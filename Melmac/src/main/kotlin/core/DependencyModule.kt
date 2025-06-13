package core

import android.*
import config.Config
import controllers.*
import controllers.IControllers.*
import core.runners.*
import ios.*
import java.sql.DriverManager
import mappers.*
import org.koin.core.qualifier.named
import org.koin.dsl.module
import repos.*
import repos.IRepos.*
import services.*
import services.IServices.*

/**
 * Dependency injection module for managing platform-specific dependencies. Provides Android and iOS
 * implementations for device management, app management, and test runners.
 */
val appModule = module {

    // --- Database Connection ---
    single<java.sql.Connection> {
        val dbConfig = Config.getDatabaseConfig()
        val database =
                dbConfig["database"]?.asText()
                        ?: throw IllegalArgumentException("Database name not found")
        val host =
                dbConfig["host"]?.asText()
                        ?: throw IllegalArgumentException("Database host not found")
        val port =
                dbConfig["port"]?.asInt()
                        ?: throw IllegalArgumentException("Database port not found")
        val user =
                dbConfig["user"]?.asText()
                        ?: throw IllegalArgumentException("Database user not found")
        val password =
                dbConfig["password"]?.asText()
                        ?: throw IllegalArgumentException("Database password not found")
        val encrypt = dbConfig["encrypt"]?.asBoolean() ?: true
        val trustServerCertificate = dbConfig["trustServerCertificate"]?.asBoolean() ?: true
        val driver = dbConfig["driver"]?.asText() ?: "com.microsoft.sqlserver.jdbc.SQLServerDriver"

        val dbUrl =
                "jdbc:sqlserver://$host:$port;databaseName=$database;encrypt=$encrypt;trustServerCertificate=$trustServerCertificate"
        Class.forName(driver)
        DriverManager.getConnection(dbUrl, user, password)
    }

    single<String>(qualifier = named("metricsConfigPath")) { "data.json" }

    single { AppMapper }
    single { AppVersionMapper }
    single { AvailableDeviceMapper }
    single { DeviceMapper }
    single { ExecutionTypeMapper }
    single { ExecutionTypeMetricMapper }
    single { ExecutionTypeParameterMapper }
    single { MetricMapper }
    single { MetricOutputMapper }
    single { MetricParameterMapper }
    single { OperSysMapper }
    single { OSVersionMapper }
    single { SuiteExecutionMapper }
    single { TestExecutionMapper }
    single { TestExecutionTypeParameterMapper }
    single { TestMetricOutputResultMapper }
    single { TestMetricParameterMapper }
    single { TestPlanMapper }
    single { TestPlanVersionMapper }
    single { TestSuiteMapper }
    single { TestThresholdMapper }
    single { ThresholdTypeMapper }

    // --- Repositories ---

    // Bootstrap Update
    single<IBootstrapUpdateRepository> { BootstrapUpdateRepository(get()) }

    // Metrics
    single<IMetricRepository> { MetricRepository(get()) }
    single<IMetricOutputRepository> { MetricOutputRepository(get()) }
    single<IMetricParameterRepository> { MetricParameterRepository(get()) }

    // Execution Types
    single<IExecutionTypeRepository> { ExecutionTypeRepository(get()) }
    single<IExecutionTypeParameterRepository> { ExecutionTypeParameterRepository(get()) }
    single<IExecutionTypeMetricRepository> { ExecutionTypeMetricRepository(get()) }

    // TestPlan and Versions
    single<ITestPlanRepository> { TestPlanRepository(get()) }
    single<ITestPlanVersionRepository> { TestPlanVersionRepository(get()) }

    // Parameters and Thresholds
    single<ITestPlanExecutionTypeParameterValueRepository> {
        TestPlanExecutionTypeParameterValueRepository(get())
    }
    single<ITestPlanMetricParameterValueRepository> {
        TestPlanMetricParameterValueRepository(get())
    }
    single<IThresholdRepository> { ThresholdRepository(get()) }
    single<IThresholdTypeRepository> { ThresholdTypeRepository(get()) }

    // Test Execution
    single<ITestExecutionRepository> { TestExecutionRepository(get()) }
    single<ITestMetricOutputResultRepository> { TestMetricOutputResultRepository(get()) }

    // Test Suite
    single<ITestSuiteRepository> { TestSuiteRepository(get()) }
    single<ITestSuiteVersionRepository> { TestSuiteVersionRepository(get()) }
    single<ITestSuiteVersionPlanRepository> { TestSuiteVersionPlanRepository(get()) }
    single<ITestSuiteExecutionRepository> { TestSuiteExecutionRepository(get()) }

    // App & Device
    single<IAppRepository> { AppRepository(get()) }
    single<IAppVersionRepository> { AppVersionRepository(get()) }
    single<IDeviceRepository> { DeviceRepository(get()) }
    single<IOperSysRepository> { OperSysRepository(get()) }
    single<IOperSysVersionRepository> { OperSysVersionRepository(get()) }
    single<IDeviceService> { DeviceService(get(), get(), get(), get()) }
    single<IMetricService> {
        MetricService(get(), get(), get(), get(), get(), get(), get(), get(), get(), get())
    }
    single<IAppService> { AppService(get(), get(), get(), get()) }
    single<IDeviceService> { DeviceService(get(), get(), get(), get()) }
    single<IMetricService> {
        MetricService(get(), get(), get(), get(), get(), get(), get(), get(), get(), get())
    }
    single<ILoaderService> {
        LoaderService(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(named("metricsConfigPath"))
        )
    }
    single<ITestExecutionService> {
        TestExecutionService(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get()
        )
    }
    single<IThresholdService> { ThresholdService(get(), get(), get()) }
    single<IThresholdTypeService> { ThresholdTypeService(get(), get()) }
    single<ITestPlanService> {
        TestPlanService(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get()
        )
    }
    single<ITestMetricOutputResultService> { TestMetricOutputResultService(get(), get()) }
    single<ITestSuiteService> {
        TestSuiteService(
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get(),
                get()
        )
    }
    single<ITestPlanVersionService> {
        TestPlanVersionService(get(), get(), get(), get(), get(), get(), get(), get(), get())
    }

    // --- Controllers ---
    single<IAppController> { AppController(get()) }
    single<IDeviceController> { DeviceController(get()) }
    single<IMetricController> { MetricController(get()) }
    single<ITestExecutionController> { TestExecutionController(get(), get()) }
    single<IThresholdController> { ThresholdController(get()) }
    single<IThresholdTypeController> { ThresholdTypeController(get()) }
    single<ITestSuiteController> { TestSuiteController(get(), get()) }
    single<ITestPlanController> { TestPlanController(get()) }
    single<ITestPlanVersionController> { TestPlanVersionController(get()) }

    single<ITestRunner> { TestRunner(get(), get()) }

    // --- Platform-specific dependencies ---
    single<DeviceManager>(named("android")) { AndroidDeviceManager }
    single<AppManager>(named("android")) { AndroidAppManager }
    single<AndroidTestRunner> { AndroidTestRunner(get(named("android")), get(named("android"))) }

    single<DeviceManager>(named("ios")) { IOSDeviceManager }
    single<AppManager>(named("ios")) { IOSAppManager }
    single<IOSTestRunner> { IOSTestRunner(get(named("ios")), get(named("ios"))) }
}
