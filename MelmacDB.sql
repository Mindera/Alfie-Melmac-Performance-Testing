DROP TRIGGER Ensure_MetricConsistency;
DROP TRIGGER Ensure_ParameterConsistency;
DROP TABLE Device CASCADE CONSTRAINTS;
DROP TABLE OperativeSystem CASCADE CONSTRAINTS;
DROP TABLE OSVersion CASCADE CONSTRAINTS;
DROP TABLE App CASCADE CONSTRAINTS;
DROP TABLE AppVersion CASCADE CONSTRAINTS;
DROP TABLE Metric CASCADE CONSTRAINTS;
DROP TABLE TestExecution CASCADE CONSTRAINTS;
DROP TABLE TestMetric CASCADE CONSTRAINTS;
DROP TABLE TestSuite CASCADE CONSTRAINTS;
DROP TABLE ExecutionType CASCADE CONSTRAINTS;
DROP TABLE MetricOutput CASCADE CONSTRAINTS;
DROP TABLE ThresholdType CASCADE CONSTRAINTS;
DROP TABLE TestMetricThreshold CASCADE CONSTRAINTS;
DROP TABLE TestExecutionTypeParameter CASCADE CONSTRAINTS;
DROP TABLE ExecutionType_Metric CASCADE CONSTRAINTS;
DROP TABLE ExecutionTypeParameter CASCADE CONSTRAINTS;
DROP TABLE MetricParameter CASCADE CONSTRAINTS;
DROP TABLE TestMetricParameter CASCADE CONSTRAINTS;
CREATE TABLE Device (DeviceID number(10) GENERATED AS IDENTITY, DeviceName varchar2(255) NOT NULL, DeviceSerialNumber varchar2(255) NOT NULL UNIQUE, OSVersionOSVersionID number(10) NOT NULL, PRIMARY KEY (DeviceID));
CREATE TABLE OperativeSystem (OperSysID number(10) GENERATED AS IDENTITY, OperSysName varchar2(255) NOT NULL UNIQUE, PRIMARY KEY (OperSysID));
CREATE TABLE OSVersion (OSVersionID number(10) GENERATED AS IDENTITY, Version varchar2(255) NOT NULL, OperativeSystemOperSysID number(10) NOT NULL, PRIMARY KEY (OSVersionID), CONSTRAINT UQ_VERSION_PER_OS UNIQUE (Version, OperativeSystemOperSysID));
CREATE TABLE App (AppID number(10) GENERATED AS IDENTITY, AppName varchar2(255) NOT NULL, PRIMARY KEY (AppID));
CREATE TABLE AppVersion (AppVersionID number(10) GENERATED AS IDENTITY, Version varchar2(255) NOT NULL, AppAppID number(10) NOT NULL, PRIMARY KEY (AppVersionID), CONSTRAINT UN_VERSION_PER_APP UNIQUE (Version, AppAppID));
CREATE TABLE Metric (MetricID number(10) GENERATED AS IDENTITY, MetricName varchar2(255) NOT NULL, PRIMARY KEY (MetricID));
CREATE TABLE TestExecution (TestExecutionID number(10) GENERATED AS IDENTITY, TestExecutionDescription varchar2(255), InitialTimestamp timestamp(0) NOT NULL, EndTimestamp timestamp(0) NOT NULL, AppVersionAppVersionID number(10) NOT NULL, DeviceDeviceID number(10) NOT NULL, TestSuiteTestSuiteID number(10) NOT NULL, ExecutionTypeExecutionTypeID number(10) NOT NULL, MetricMetricID number(10) NOT NULL, PRIMARY KEY (TestExecutionID), CONSTRAINT CK_Execution_Duration CHECK (InitialTimestamp <= EndTimestamp));
CREATE TABLE TestMetric (TestMetricID number(10) GENERATED AS IDENTITY, Value number(10) NOT NULL, MetricOutputMetricOutputID number(10) NOT NULL, TestExecutionTestExecutionID number(10) NOT NULL, PRIMARY KEY (TestMetricID), CONSTRAINT UQ_SUBMETRIC_PER_EXECUTION UNIQUE (MetricOutputMetricOutputID, TestExecutionTestExecutionID), CONSTRAINT CK_TestMetric_PositiveValue CHECK (Value >= 0 ));
CREATE TABLE TestSuite (TestSuiteID number(10) GENERATED AS IDENTITY, TestSuiteName varchar2(255) NOT NULL, TestSuiteDescription varchar2(255), InitialTimestamp timestamp(0) NOT NULL, EndTimestamp timestamp(0) NOT NULL, PRIMARY KEY (TestSuiteID));
CREATE TABLE ExecutionType (ExecutionTypeID number(10) GENERATED AS IDENTITY, ExecutionTypeName varchar2(255) NOT NULL UNIQUE, ExecutionTypeDescription varchar2(255), PRIMARY KEY (ExecutionTypeID));
CREATE TABLE MetricOutput (MetricOutputID number(10) GENERATED AS IDENTITY, OutputName varchar2(255), Unit varchar2(255) NOT NULL, MetricMetricID number(10) NOT NULL, PRIMARY KEY (MetricOutputID), CONSTRAINT UQ_OUTPUT_PER_METRIC UNIQUE (MetricMetricID, OutputName));
CREATE TABLE ThresholdType (ThresholdTypeID number(10) GENERATED AS IDENTITY, ThresholdTypeName varchar2(255) NOT NULL UNIQUE, ThresholdTypeDescription varchar2(255), PRIMARY KEY (ThresholdTypeID));
CREATE TABLE TestMetricThreshold (TestMetricThresholdID number(10) GENERATED AS IDENTITY, MinValue number(10), MaxValue number(10), TargetValue number(10), Tolerance number(10), Severity number(10) NOT NULL, TestMetricTestMetricID number(10) NOT NULL, ThresholdTypeThresholdTypeID number(10) NOT NULL, PRIMARY KEY (TestMetricThresholdID), CONSTRAINT CK_Threshold_MinLEMax CHECK (MinValue <= MaxValue ), CONSTRAINT CK_Threshold_Tolerance_Positive CHECK (Tolerance >= 0 ));
CREATE TABLE TestExecutionTypeParameter (TestExecutionTypeParameterID number(10) GENERATED AS IDENTITY, ParameterValue varchar2(255) NOT NULL, TestExecutionTestExecutionID number(10) NOT NULL, ExecutionTypeParameterExecutionTypeParameterID number(10) NOT NULL, PRIMARY KEY (TestExecutionTypeParameterID), CONSTRAINT UQ_TYPE_PARAMETER_PER_TEST UNIQUE (TestExecutionTestExecutionID, ExecutionTypeParameterExecutionTypeParameterID), CONSTRAINT CK_Param_Value_Not_Empty CHECK (LENGTH(ParameterValue) > 0 ));
CREATE TABLE ExecutionType_Metric (ExecutionTypeExecutionTypeID number(10) NOT NULL, MetricMetricID number(10) NOT NULL, PRIMARY KEY (ExecutionTypeExecutionTypeID, MetricMetricID));
CREATE TABLE ExecutionTypeParameter (ExecutionTypeParameterID number(10) GENERATED AS IDENTITY, ParameterName varchar2(255) NOT NULL, ParameterType varchar2(255) NOT NULL, ExecutionTypeExecutionTypeID number(10) NOT NULL, PRIMARY KEY (ExecutionTypeParameterID), CONSTRAINT UQ_PARAMETER_PER_EXECUTION_TYPE UNIQUE (ParameterName, ExecutionTypeExecutionTypeID));
CREATE TABLE MetricParameter (MetricParameterID number(10) GENERATED AS IDENTITY, ParameterName varchar2(255) NOT NULL, ParameterType varchar2(255) NOT NULL, MetricMetricID number(10) NOT NULL, PRIMARY KEY (MetricParameterID), CONSTRAINT UQ_PARAMETER_PER_METRIC UNIQUE (ParameterName, MetricMetricID));
CREATE TABLE TestMetricParameter (TestMetricParameterID number(10) GENERATED AS IDENTITY, ParameterValue varchar2(255) NOT NULL, TestExecutionTestExecutionID number(10) NOT NULL, MetricParameterMetricParameterID number(10) NOT NULL, PRIMARY KEY (TestMetricParameterID), CONSTRAINT UQ_METRIC_PARAMETER_PER_TEST UNIQUE (TestExecutionTestExecutionID, MetricParameterMetricParameterID), CONSTRAINT CK_Param_Value_Not_Empty CHECK (LENGTH(ParameterValue) > 0 ));
ALTER TABLE OSVersion ADD CONSTRAINT FKOSVersion102342 FOREIGN KEY (OperativeSystemOperSysID) REFERENCES OperativeSystem (OperSysID);
ALTER TABLE Device ADD CONSTRAINT FKDevice97329 FOREIGN KEY (OSVersionOSVersionID) REFERENCES OSVersion (OSVersionID);
ALTER TABLE AppVersion ADD CONSTRAINT FKAppVersion811825 FOREIGN KEY (AppAppID) REFERENCES App (AppID);
ALTER TABLE TestExecution ADD CONSTRAINT FKTestExecut61692 FOREIGN KEY (AppVersionAppVersionID) REFERENCES AppVersion (AppVersionID);
ALTER TABLE TestExecution ADD CONSTRAINT FKTestExecut825206 FOREIGN KEY (DeviceDeviceID) REFERENCES Device (DeviceID);
ALTER TABLE TestExecution ADD CONSTRAINT FKTestExecut464817 FOREIGN KEY (TestSuiteTestSuiteID) REFERENCES TestSuite (TestSuiteID);
ALTER TABLE MetricOutput ADD CONSTRAINT FKMetricOutp511449 FOREIGN KEY (MetricMetricID) REFERENCES Metric (MetricID);
ALTER TABLE TestMetricThreshold ADD CONSTRAINT FKTestMetric834249 FOREIGN KEY (TestMetricTestMetricID) REFERENCES TestMetric (TestMetricID);
ALTER TABLE TestMetricThreshold ADD CONSTRAINT FKTestMetric521506 FOREIGN KEY (ThresholdTypeThresholdTypeID) REFERENCES ThresholdType (ThresholdTypeID);
ALTER TABLE TestMetric ADD CONSTRAINT FKTestMetric208441 FOREIGN KEY (MetricOutputMetricOutputID) REFERENCES MetricOutput (MetricOutputID);
ALTER TABLE TestExecutionTypeParameter ADD CONSTRAINT FKTestExecut848437 FOREIGN KEY (TestExecutionTestExecutionID) REFERENCES TestExecution (TestExecutionID);
ALTER TABLE TestExecution ADD CONSTRAINT FKTestExecut693828 FOREIGN KEY (ExecutionTypeExecutionTypeID) REFERENCES ExecutionType (ExecutionTypeID);
ALTER TABLE ExecutionType_Metric ADD CONSTRAINT FKExecutionT548563 FOREIGN KEY (ExecutionTypeExecutionTypeID) REFERENCES ExecutionType (ExecutionTypeID);
ALTER TABLE ExecutionType_Metric ADD CONSTRAINT FKExecutionT851064 FOREIGN KEY (MetricMetricID) REFERENCES Metric (MetricID);
ALTER TABLE TestExecution ADD CONSTRAINT FKTestExecut996329 FOREIGN KEY (MetricMetricID) REFERENCES Metric (MetricID);
ALTER TABLE TestMetric ADD CONSTRAINT FKTestMetric855011 FOREIGN KEY (TestExecutionTestExecutionID) REFERENCES TestExecution (TestExecutionID);
ALTER TABLE ExecutionTypeParameter ADD CONSTRAINT FKExecutionT409404 FOREIGN KEY (ExecutionTypeExecutionTypeID) REFERENCES ExecutionType (ExecutionTypeID);
ALTER TABLE MetricParameter ADD CONSTRAINT FKMetricPara448890 FOREIGN KEY (MetricMetricID) REFERENCES Metric (MetricID);
ALTER TABLE TestMetricParameter ADD CONSTRAINT FKTestMetric816684 FOREIGN KEY (TestExecutionTestExecutionID) REFERENCES TestExecution (TestExecutionID);
ALTER TABLE TestMetricParameter ADD CONSTRAINT FKTestMetric123270 FOREIGN KEY (MetricParameterMetricParameterID) REFERENCES MetricParameter (MetricParameterID);
ALTER TABLE TestExecutionTypeParameter ADD CONSTRAINT FKTestExecut631451 FOREIGN KEY (ExecutionTypeParameterExecutionTypeParameterID) REFERENCES ExecutionTypeParameter (ExecutionTypeParameterID);
CREATE OR REPLACE TRIGGER ENSURE_METRICCONSISTENCY
BEFORE INSERT OR UPDATE ON TESTMETRIC
FOR EACH ROW
DECLARE
    V_EXPECTED_METRIC_ID  NUMBER;
    V_OUTPUT_METRIC_ID NUMBER;
BEGIN
    -- GET THE METRICID FROM THE PARENT TESTEXECUTION
    BEGIN
        SELECT METRICMETRICID
          INTO V_EXPECTED_METRIC_ID
          FROM TESTEXECUTION
         WHERE TESTEXECUTIONID = :NEW.TESTEXECUTIONTESTEXECUTIONID;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(
                -20002,
                'TESTEXECUTIONID NOT FOUND: ' || :NEW.TESTEXECUTIONTESTEXECUTIONID
            );
    END;

    -- GET THE METRICID FROM THE METRICOUTPUT
    SELECT METRICMETRICID
      INTO V_OUTPUT_METRIC_ID
      FROM METRICOUTPUT
     WHERE METRICOUTPUTID = :NEW.METRICOUTPUTMETRICOUTPUTID;

    -- COMPARE THE METRICIDS
    IF V_EXPECTED_METRIC_ID != V_OUTPUT_METRIC_ID THEN
        RAISE_APPLICATION_ERROR(
            -20001,
            'MISMATCH BETWEEN TESTEXECUTION.METRICID AND TESTMETRIC.METRICOUTPUT'
        );
    END IF;
END;
/
 CREATE OR REPLACE TRIGGER Ensure_ExecutionTypeParameter_Consistency
BEFORE INSERT OR UPDATE ON TestExecutionParameter
FOR EACH ROW
DECLARE
    v_execution_type_id         NUMBER;
    v_param_execution_type_id   NUMBER;
BEGIN
    -- Get the ExecutionTypeID from the parent TestExecution
    BEGIN
        SELECT ExecutionTypeExecutionTypeID
        INTO v_execution_type_id
        FROM TestExecution
        WHERE TestExecutionID = :NEW.TestExecutionTestExecutionID;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(
                -20003,
                'TestExecution not found: ' || :NEW.TestExecutionTestExecutionID
            );
    END;

    -- Get the ExecutionTypeID from the ExecutionTypeParameter
    BEGIN
        SELECT ExecutionTypeExecutionTypeID
        INTO v_param_execution_type_id
        FROM ExecutionTypeParameter
        WHERE ExecutionTypeParameterID = :NEW.ExecutionTypeParameterExecutionTypeParameterID;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(
                -20004,
                'ExecutionTypeParameter not found: ' || :NEW.ExecutionTypeParameterExecutionTypeParameterID
            );
    END;

    -- Ensure both ExecutionTypeIDs match
    IF v_execution_type_id != v_param_execution_type_id THEN
        RAISE_APPLICATION_ERROR(
            -20005,
            'ExecutionType mismatch: parameter does not belong to this execution''s type'
        );
    END IF;
END;
