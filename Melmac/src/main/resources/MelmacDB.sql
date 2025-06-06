ALTER TABLE OSVersion DROP CONSTRAINT FKOSVersion102342;
ALTER TABLE Device DROP CONSTRAINT FKDevice97329;
ALTER TABLE AppVersion DROP CONSTRAINT FKAppVersion811825;
ALTER TABLE MetricOutput DROP CONSTRAINT FKMetricOutp511449;
ALTER TABLE TestThreshold DROP CONSTRAINT FKTestThresh749575;
ALTER TABLE TestMetricOutputResult DROP CONSTRAINT FKTestMetric345452;
ALTER TABLE ExecutionType_Metric DROP CONSTRAINT FKExecutionT548563;
ALTER TABLE ExecutionType_Metric DROP CONSTRAINT FKExecutionT851064;
ALTER TABLE TestMetricOutputResult DROP CONSTRAINT FKTestMetric698881;
ALTER TABLE ExecutionTypeParameter DROP CONSTRAINT FKExecutionT409404;
ALTER TABLE MetricParameter DROP CONSTRAINT FKMetricPara448890;
ALTER TABLE TestMetricParameter DROP CONSTRAINT FKTestMetric123270;
ALTER TABLE TestExecutionTypeParameter DROP CONSTRAINT FKTestExecut631451;
ALTER TABLE TestSuiteVersion DROP CONSTRAINT FKTestSuiteV944199;
ALTER TABLE SuiteExecution DROP CONSTRAINT FKSuiteExecu709546;
ALTER TABLE TestPlan DROP CONSTRAINT FKTestPlan25374;
ALTER TABLE TestPlanVersion DROP CONSTRAINT FKTestPlanVe174033;
ALTER TABLE TestPlanVersion DROP CONSTRAINT FKTestPlanVe274291;
ALTER TABLE TestPlanVersion DROP CONSTRAINT FKTestPlanVe612607;
ALTER TABLE TestPlanVersion DROP CONSTRAINT FKTestPlanVe142913;
ALTER TABLE TestMetricParameter DROP CONSTRAINT FKTestMetric601777;
ALTER TABLE TestExecutionTypeParameter DROP CONSTRAINT FKTestExecut704690;
ALTER TABLE TestExecution DROP CONSTRAINT FKTestExecut703161;
ALTER TABLE TestSuiteVersionPlan DROP CONSTRAINT FKTestSuiteV825371;
ALTER TABLE TestSuiteVersionPlan DROP CONSTRAINT FKTestSuiteV358153;
ALTER TABLE TestThreshold DROP CONSTRAINT FKTestThresh200646;
ALTER TABLE TestThreshold DROP CONSTRAINT FKTestThresh705911;
DROP TABLE Device;
DROP TABLE OperativeSystem;
DROP TABLE OSVersion;
DROP TABLE App;
DROP TABLE AppVersion;
DROP TABLE Metric;
DROP TABLE TestExecution;
DROP TABLE TestMetricOutputResult;
DROP TABLE TestSuite;
DROP TABLE ExecutionType;
DROP TABLE MetricOutput;
DROP TABLE ThresholdType;
DROP TABLE TestThreshold;
DROP TABLE TestExecutionTypeParameter;
DROP TABLE ExecutionType_Metric;
DROP TABLE ExecutionTypeParameter;
DROP TABLE MetricParameter;
DROP TABLE TestMetricParameter;
DROP TABLE SuiteExecution;
DROP TABLE TestSuiteVersion;
DROP TABLE TestPlan;
DROP TABLE TestPlanVersion;
DROP TABLE TestSuiteVersionPlan;
DROP TABLE BootstrapUpdate;
CREATE TABLE Device (DeviceID int IDENTITY NOT NULL, DeviceName varchar(255) NOT NULL, DeviceSerialNumber varchar(255) NULL, OSVersionOSVersionID int NOT NULL, PRIMARY KEY (DeviceID)); CREATE UNIQUE INDEX UQ_Device_Serial ON Device (DeviceSerialNumber) WHERE DeviceSerialNumber IS NOT NULL;
CREATE TABLE OperativeSystem (OperSysID int IDENTITY NOT NULL, OperSysName varchar(255) NOT NULL UNIQUE, PRIMARY KEY (OperSysID));
CREATE TABLE OSVersion (OSVersionID int IDENTITY NOT NULL, Version varchar(255) NOT NULL, OperativeSystemOperSysID int NOT NULL, PRIMARY KEY (OSVersionID), CONSTRAINT UQ_VERSION_PER_OS UNIQUE (Version, OperativeSystemOperSysID));
CREATE TABLE App (AppID int IDENTITY NOT NULL, AppName varchar(255) NOT NULL, PRIMARY KEY (AppID));
CREATE TABLE AppVersion (AppVersionID int IDENTITY NOT NULL, Version varchar(255) NOT NULL, AppAppID int NOT NULL, PRIMARY KEY (AppVersionID), CONSTRAINT UN_VERSION_PER_APP UNIQUE (Version, AppAppID));
CREATE TABLE Metric (MetricID int IDENTITY NOT NULL, MetricName varchar(255) NOT NULL, PRIMARY KEY (MetricID));
CREATE TABLE TestExecution (TestExecutionID int IDENTITY NOT NULL, InitialTimestamp datetime NOT NULL, EndTimestamp datetime NOT NULL, Passed varchar(255) NOT NULL, TestPlanVersionTestPlanVersionID int NOT NULL, PRIMARY KEY (TestExecutionID), CONSTRAINT CK_Execution_Duration CHECK (InitialTimestamp <= EndTimestamp));
CREATE TABLE TestMetricOutputResult (TestMetricOutputResultID int IDENTITY NOT NULL, Value varchar(255) NOT NULL, MetricOutputMetricOutputID int NOT NULL, TestExecutionTestExecutionID int NOT NULL, PRIMARY KEY (TestMetricOutputResultID), CONSTRAINT UQ_SUBMETRIC_PER_EXECUTION UNIQUE (MetricOutputMetricOutputID, TestExecutionTestExecutionID));
CREATE TABLE TestSuite (TestSuiteID int IDENTITY NOT NULL, TestSuiteName varchar(255) NOT NULL, TestSuiteDescription varchar(255) NULL, CreationTimestamp datetime NOT NULL, isActive varchar(255) NOT NULL, PRIMARY KEY (TestSuiteID));
CREATE TABLE ExecutionType (ExecutionTypeID int IDENTITY NOT NULL, ExecutionTypeName varchar(255) NOT NULL UNIQUE, ExecutionTypeDescription varchar(255) NULL, PRIMARY KEY (ExecutionTypeID));
CREATE TABLE MetricOutput (MetricOutputID int IDENTITY NOT NULL, OutputName varchar(255) NULL, Unit varchar(255) NOT NULL, MetricMetricID int NOT NULL, PRIMARY KEY (MetricOutputID), CONSTRAINT UQ_OUTPUT_PER_METRIC UNIQUE (MetricMetricID, OutputName));
CREATE TABLE ThresholdType (ThresholdTypeID int IDENTITY NOT NULL, ThresholdTypeName varchar(255) NOT NULL UNIQUE, ThresholdTypeDescription varchar(255) NULL, PRIMARY KEY (ThresholdTypeID));
CREATE TABLE TestThreshold (TestThresholdID int IDENTITY NOT NULL, TargetValue int NOT NULL, ThresholdTypeThresholdTypeID int NOT NULL, TestPlanVersionTestPlanVersionID int NOT NULL, MetricOutputMetricOutputID int NOT NULL, PRIMARY KEY (TestThresholdID));
CREATE TABLE TestExecutionTypeParameter (TestExecutionTypeParameterID int IDENTITY NOT NULL, ParameterValue varchar(255) NOT NULL, ExecutionTypeParameterExecutionTypeParameterID int NOT NULL, TestPlanVersionTestPlanVersionID int NOT NULL, PRIMARY KEY (TestExecutionTypeParameterID), CONSTRAINT UQ_TYPE_PARAMETER_PER_VERSION UNIQUE (ExecutionTypeParameterExecutionTypeParameterID, TestPlanVersionTestPlanVersionID));
CREATE TABLE ExecutionType_Metric (ExecutionTypeExecutionTypeID int NOT NULL, MetricMetricID int NOT NULL, PRIMARY KEY (ExecutionTypeExecutionTypeID, MetricMetricID));
CREATE TABLE ExecutionTypeParameter (ExecutionTypeParameterID int IDENTITY NOT NULL, ParameterName varchar(255) NOT NULL, ParameterType varchar(255) NOT NULL, ExecutionTypeExecutionTypeID int NOT NULL, PRIMARY KEY (ExecutionTypeParameterID), CONSTRAINT UQ_PARAMETER_PER_EXECUTION_TYPE UNIQUE (ParameterName, ExecutionTypeExecutionTypeID));
CREATE TABLE MetricParameter (MetricParameterID int IDENTITY NOT NULL, ParameterName varchar(255) NOT NULL, ParameterType varchar(255) NOT NULL, MetricMetricID int NOT NULL, PRIMARY KEY (MetricParameterID), CONSTRAINT UQ_PARAMETER_PER_METRIC UNIQUE (ParameterName, MetricMetricID));
CREATE TABLE TestMetricParameter (TestMetricParameterID int IDENTITY NOT NULL, ParameterValue varchar(255) NOT NULL, MetricParameterMetricParameterID int NOT NULL, TestPlanVersionTestPlanVersionID int NOT NULL, PRIMARY KEY (TestMetricParameterID), CONSTRAINT UQ_PARAMETER_PER_VERSION UNIQUE (MetricParameterMetricParameterID, TestPlanVersionTestPlanVersionID));
CREATE TABLE SuiteExecution (SuiteExecutionID int IDENTITY NOT NULL, InitialTimestamp datetime NOT NULL, EndTimestamp datetime NOT NULL, TestSuiteVersionTestSuiteVersionID int NOT NULL, PRIMARY KEY (SuiteExecutionID));
CREATE TABLE TestSuiteVersion (TestSuiteVersionID int IDENTITY NOT NULL, Version varchar(255) NOT NULL, CreationTimestamp datetime NOT NULL, Notes varchar(255) NULL, TestSuiteTestSuiteID int NOT NULL, PRIMARY KEY (TestSuiteVersionID), CONSTRAINT UQ_VERSION_PER_TEST_SUITE UNIQUE (Version, TestSuiteTestSuiteID));
CREATE TABLE TestPlan (TestPlanID int IDENTITY NOT NULL, TestName varchar(255) NOT NULL, MetricMetricID int NOT NULL, PRIMARY KEY (TestPlanID));
CREATE TABLE TestPlanVersion (TestPlanVersionID int IDENTITY NOT NULL, Version varchar(255) NOT NULL, CreationTimestamp datetime NOT NULL, Notes varchar(255) NULL, AppPackage varchar(255) NOT NULL, AppMainActivity varchar(255) NULL, TestPlanTestPlanID int NOT NULL, DeviceDeviceID int NOT NULL, AppVersionAppVersionID int NOT NULL, ExecutionTypeExecutionTypeID int NOT NULL, PRIMARY KEY (TestPlanVersionID), CONSTRAINT UQ_VERSION_PER_TEST_PLAN UNIQUE (Version, TestPlanTestPlanID));
CREATE TABLE TestSuiteVersionPlan (TestSuiteVersionTestSuiteVersionID int NOT NULL, TestPlanVersionTestPlanVersionID int NOT NULL, [Order] int NOT NULL, PRIMARY KEY (TestSuiteVersionTestSuiteVersionID, TestPlanVersionTestPlanVersionID), CONSTRAINT UQ_SUITE_VERSION_ORDER UNIQUE (TestSuiteVersionTestSuiteVersionID, [Order]));
CREATE TABLE BootstrapUpdate (BootstrapUpdateID int IDENTITY NOT NULL, UpdateDate datetime NOT NULL UNIQUE, PRIMARY KEY (BootstrapUpdateID));
ALTER TABLE OSVersion ADD CONSTRAINT FKOSVersion102342 FOREIGN KEY (OperativeSystemOperSysID) REFERENCES OperativeSystem (OperSysID);
ALTER TABLE Device ADD CONSTRAINT FKDevice97329 FOREIGN KEY (OSVersionOSVersionID) REFERENCES OSVersion (OSVersionID);
ALTER TABLE AppVersion ADD CONSTRAINT FKAppVersion811825 FOREIGN KEY (AppAppID) REFERENCES App (AppID);
ALTER TABLE MetricOutput ADD CONSTRAINT FKMetricOutp511449 FOREIGN KEY (MetricMetricID) REFERENCES Metric (MetricID);
ALTER TABLE TestThreshold ADD CONSTRAINT FKTestThresh749575 FOREIGN KEY (ThresholdTypeThresholdTypeID) REFERENCES ThresholdType (ThresholdTypeID);
ALTER TABLE TestMetricOutputResult ADD CONSTRAINT FKTestMetric345452 FOREIGN KEY (MetricOutputMetricOutputID) REFERENCES MetricOutput (MetricOutputID);
ALTER TABLE ExecutionType_Metric ADD CONSTRAINT FKExecutionT548563 FOREIGN KEY (ExecutionTypeExecutionTypeID) REFERENCES ExecutionType (ExecutionTypeID);
ALTER TABLE ExecutionType_Metric ADD CONSTRAINT FKExecutionT851064 FOREIGN KEY (MetricMetricID) REFERENCES Metric (MetricID);
ALTER TABLE TestMetricOutputResult ADD CONSTRAINT FKTestMetric698881 FOREIGN KEY (TestExecutionTestExecutionID) REFERENCES TestExecution (TestExecutionID);
ALTER TABLE ExecutionTypeParameter ADD CONSTRAINT FKExecutionT409404 FOREIGN KEY (ExecutionTypeExecutionTypeID) REFERENCES ExecutionType (ExecutionTypeID);
ALTER TABLE MetricParameter ADD CONSTRAINT FKMetricPara448890 FOREIGN KEY (MetricMetricID) REFERENCES Metric (MetricID);
ALTER TABLE TestMetricParameter ADD CONSTRAINT FKTestMetric123270 FOREIGN KEY (MetricParameterMetricParameterID) REFERENCES MetricParameter (MetricParameterID);
ALTER TABLE TestExecutionTypeParameter ADD CONSTRAINT FKTestExecut631451 FOREIGN KEY (ExecutionTypeParameterExecutionTypeParameterID) REFERENCES ExecutionTypeParameter (ExecutionTypeParameterID);
ALTER TABLE TestSuiteVersion ADD CONSTRAINT FKTestSuiteV944199 FOREIGN KEY (TestSuiteTestSuiteID) REFERENCES TestSuite (TestSuiteID);
ALTER TABLE SuiteExecution ADD CONSTRAINT FKSuiteExecu709546 FOREIGN KEY (TestSuiteVersionTestSuiteVersionID) REFERENCES TestSuiteVersion (TestSuiteVersionID);
ALTER TABLE TestPlan ADD CONSTRAINT FKTestPlan25374 FOREIGN KEY (MetricMetricID) REFERENCES Metric (MetricID);
ALTER TABLE TestPlanVersion ADD CONSTRAINT FKTestPlanVe174033 FOREIGN KEY (TestPlanTestPlanID) REFERENCES TestPlan (TestPlanID);
ALTER TABLE TestPlanVersion ADD CONSTRAINT FKTestPlanVe274291 FOREIGN KEY (DeviceDeviceID) REFERENCES Device (DeviceID);
ALTER TABLE TestPlanVersion ADD CONSTRAINT FKTestPlanVe612607 FOREIGN KEY (AppVersionAppVersionID) REFERENCES AppVersion (AppVersionID);
ALTER TABLE TestPlanVersion ADD CONSTRAINT FKTestPlanVe142913 FOREIGN KEY (ExecutionTypeExecutionTypeID) REFERENCES ExecutionType (ExecutionTypeID);
ALTER TABLE TestMetricParameter ADD CONSTRAINT FKTestMetric601777 FOREIGN KEY (TestPlanVersionTestPlanVersionID) REFERENCES TestPlanVersion (TestPlanVersionID);
ALTER TABLE TestExecutionTypeParameter ADD CONSTRAINT FKTestExecut704690 FOREIGN KEY (TestPlanVersionTestPlanVersionID) REFERENCES TestPlanVersion (TestPlanVersionID);
ALTER TABLE TestExecution ADD CONSTRAINT FKTestExecut703161 FOREIGN KEY (TestPlanVersionTestPlanVersionID) REFERENCES TestPlanVersion (TestPlanVersionID);
ALTER TABLE TestSuiteVersionPlan ADD CONSTRAINT FKTestSuiteV825371 FOREIGN KEY (TestSuiteVersionTestSuiteVersionID) REFERENCES TestSuiteVersion (TestSuiteVersionID);
ALTER TABLE TestSuiteVersionPlan ADD CONSTRAINT FKTestSuiteV358153 FOREIGN KEY (TestPlanVersionTestPlanVersionID) REFERENCES TestPlanVersion (TestPlanVersionID);
ALTER TABLE TestThreshold ADD CONSTRAINT FKTestThresh200646 FOREIGN KEY (TestPlanVersionTestPlanVersionID) REFERENCES TestPlanVersion (TestPlanVersionID);
ALTER TABLE TestThreshold ADD CONSTRAINT FKTestThresh705911 FOREIGN KEY (MetricOutputMetricOutputID) REFERENCES MetricOutput (MetricOutputID);
