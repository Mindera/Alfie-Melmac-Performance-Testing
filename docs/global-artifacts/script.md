# Alfie Melmac Performance Testing - API Usage

## 1. Create Test Suite

```bash
curl -X POST http://localhost:8080/test-suites \
  -H "Content-Type: application/json" \
  -d '{
    "testSuiteName": "My Test Suite",
    "testSuiteDescription": "Description of my test suite"
  }'
```

## 2. Create Android Test Plan

```bash
curl -X POST http://localhost:8080/test-plans \
  -H "Content-Type: application/json" \
  -d '{
    "notes": "Test plan for Android",
    "testName": "Android Startup Time",
    "metricName": "App Startup Time",
    "deviceName": "Medium_Phone_API_35",
    "appName": "Alfie.apk",
    "appVersion": "0.8.0",
    "appPackage": "au.com.alfie.ecomm.debug",
    "mainActivity": "au.com.alfie.ecomm.MainActivity",
    "executionType": "Cold Start",
    "thresholds": [
      {
        "targetValue": 20000,
        "thresholdType": "Max",
        "metricOutputMetricOutputId": 1
      }
    ],
    "metricParameters": [
      {
        "parameterValue": "home-tab",
        "metricParameter": "elementToWaitFor"
      },
      {
        "parameterValue": "200000",
        "metricParameter": "timeout"
      }
    ],
    "executionTypeParameters": [],
    "testSuiteVersionId": 1
  }'
```

## 3. Create iOS Test Plan

```bash
curl -X POST http://localhost:8080/test-plans \
  -H "Content-Type: application/json" \
  -d '{
    "notes": "Test plan for iOS",
    "testName": "iOS Startup Time",
    "metricName": "App Startup Time",
    "deviceName": "iPhone 16 Pro",
    "appName": "Alfie.app",
    "appVersion": "0.8.1",
    "appPackage": "com.mindera.alfie.debug",
    "executionType": "Cold Start",
    "thresholds": [
      {
        "targetValue": 5000,
        "thresholdType": "Max",
        "metricOutputMetricOutputId": 1
      }
    ],
    "metricParameters": [
      {
        "parameterValue": "account-btn",
        "metricParameter": "elementToWaitFor"
      },
      {
        "parameterValue": "200000",
        "metricParameter": "timeout"
      }
    ],
    "executionTypeParameters": [],
    "testSuiteVersionId": 1
  }'
```

## 4. Execute Test Suite

```bash
curl -X POST http://localhost:8080/test-suites/1/run
```

## 5. Execute Single Test Plan

```bash
curl -X POST "http://localhost:8080/test-executions/run?testPlanVersionId=2"
```