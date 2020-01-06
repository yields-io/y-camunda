package io.yields.bpm.bnp;

public interface ProcessVariables {

    // ingest ids map
    String DATASET_IDS = "DATASET_IDS";

    String processError = "processError"; // error message, String

    String uploadSuccess = "uploadSuccess"; // true/false

    String dataCheckSuccess = "dataCheckSuccessful"; // true/false

    String dataCheckReportUrl = "dataCheckReportUrl";

    String performanceCheckSuccess = "performanceCheckSuccessful";
    String performanceCheckReportUrl = "performanceCheckReportUrl";
}
