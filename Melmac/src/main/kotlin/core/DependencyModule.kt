package core

import android.*
import controllers.*
import controllers.IControllers.*
import ios.*
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
    // Metric + execution setup
    single<IMetricRepository> { MetricRepository() }
    single<IOutputRepository> { OutputRepository() }
    single<IMetricParameterRepository> { MetricParameterRepository() }
    single<IExecutionTypeRepository> { ExecutionTypeRepository() }
    single<IExecutionTypeParameterRepository> { ExecutionTypeParameterRepository() }
    single<IExecutionTypeMetricRepository> { ExecutionTypeMetricRepository() }

    // Suite & Test Execution
    single<ITestSuiteRepository> { TestSuiteRepository() }
    single<ITestExecutionRepository> { TestExecutionRepository() }
    single<ITestExecutionTypeParameterRepository> { TestExecutionTypeParameterRepository() }
    single<ITestMetricParameterRepository> { TestMetricParameterRepository() }

    // App & Device
    single<IAppRepository> { AppRepository() }
    single<IAppVersionRepository> { AppVersionRepository() }
    single<IDeviceRepository> { DeviceRepository() }
    single<IOSRepository> { OSRepository() }
    single<IOSVersionRepository> { OSVersionRepository() }

    // Threshold and Threshold Type
    single<IThresholdTypeRepository> { ThresholdTypeRepository() }
    single<IThresholdRepository> { ThresholdRepository() }

    // Services
    single<ITestSuiteService> { TestSuiteService(get()) }
    single<IAppService> { AppService(get(), get()) }
    single<IDeviceService> { DeviceService(get(), get(), get()) }
    single<IMetricService> { MetricService(get(), get(), get(), get(), get(), get()) }
    single<ITestExecutionService> {
        TestExecutionService(
                get(), // ITestExecutionRepository
                get(), // IMetricRepository
                get(), // IExecutionTypeRepository
                get(), // ITestSuiteRepository
                get(), // IAppVersionRepository
                get(), // IExecutionTypeParameterRepository
                get(), // IMetricParameterRepository
                get(), // ITestExecutionParameterRepository
                get() // ITestMetricParameterRepository
        )
    }
    single<IThresholdTypeService> { ThresholdTypeService(get()) }
    single<IThresholdService> { ThresholdService(get()) }

    // Controllers
    single<ITestSuiteController> { TestSuiteController() }
    single<IAppController> { AppController() }
    single<IDeviceController> { DeviceController() }
    single<IMetricController> { MetricController() }
    single<ITestExecutionController> { TestExecutionController() }
    single<IThresholdController> { ThresholdController(get()) }
    single<IThresholdTypeController> { ThresholdTypeController(get()) }

    // Android dependencies
    single<DeviceManager>(qualifier = named("android")) { AndroidDeviceManager }
    single<AppManager>(qualifier = named("android")) { AndroidAppManager }
    single { AndroidTestRunner() }

    // iOS dependencies
    single<DeviceManager>(qualifier = named("ios")) { IOSDeviceManager }
    single<AppManager>(qualifier = named("ios")) { IOSAppManager }
    single { IOSTestRunner() }
}
