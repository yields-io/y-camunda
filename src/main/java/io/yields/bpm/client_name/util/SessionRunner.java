package io.yields.bpm.client_name.util;

import io.yields.bpm.client_name.chiron.ChironApi;
import io.yields.bpm.client_name.chiron.ReportDTO;
import io.yields.bpm.client_name.chiron.StageDTO;
import io.yields.bpm.client_name.chiron.StartSessionResponse;
import io.yields.bpm.client_name.config.CheckProps;
import lombok.Value;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.variable.Variables;
import org.camunda.bpm.engine.variable.value.ObjectValue;

import static org.camunda.bpm.engine.variable.Variables.objectValue;


@UtilityClass
@Slf4j
public class SessionRunner {

    @Value
    public static class SessionRunResult {
        private final boolean success;
        private final ObjectValue report;
    }

    public SessionRunResult runSessionAndGetReport(CheckProps checkProps, DelegateExecution execution) {
        log.info("Starting session for props: {}", checkProps);
        StageDTO stage = ChironApi.getStage(checkProps.getStageType(), checkProps.getDataSet());
        StartSessionResponse startSessionResponse = ChironApi.startSession(stage.getId());

        boolean success = SessionsCheck.allSessionsCompletedWithSuccess(execution, startSessionResponse.getIds());
        String sessionReport = ChironApi.getSessionReport(startSessionResponse.getIds().get(0));
        ObjectValue reportVar = objectValue(new ReportDTO(sessionReport))
                .serializationDataFormat(Variables.SerializationDataFormats.JSON)
                .create();

        return new SessionRunResult(success, reportVar);
    }
}
