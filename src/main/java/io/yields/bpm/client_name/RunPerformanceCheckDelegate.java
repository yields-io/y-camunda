package io.yields.bpm.client_name;

import io.yields.bpm.client_name.config.CheckProps;
import io.yields.bpm.client_name.config.YieldsProperties;
import io.yields.bpm.client_name.util.Models;
import io.yields.bpm.client_name.util.SessionRunner;
import io.yields.bpm.client_name.util.SessionRunner.SessionRunResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Component
@RequiredArgsConstructor
public class RunPerformanceCheckDelegate implements JavaDelegate {

    private final YieldsProperties yieldsProperties;

    public void execute(DelegateExecution execution) {
        log.info("STARTING RunPerformanceCheck STEP");
        boolean success = true;

        String localTeam = (String) execution.getVariable("localTeam");
        List<String> selectedModelNames = Models.getSelectedModels(execution);
        List<CheckProps> performanceCheckPropsList = yieldsProperties.getPerformanceChecks().get(localTeam).stream()
                .filter(
                    props ->
                        StringUtils.isBlank(props.getIfModel())
                        || selectedModelNames.contains(props.getIfModel())
                )
                .collect(Collectors.toList());

        log.debug("performanceCheckProps: {}", performanceCheckPropsList);
        for (CheckProps props: performanceCheckPropsList) {
            if (success) {
                try {
                    SessionRunResult sessionResult = SessionRunner.runSessionAndGetReport(props, execution);
                    success = success && sessionResult.isSuccess();

                    execution.setVariableLocal(ProcessVariables.performanceCheckReport, sessionResult.getReport());
                    execution.setVariable(ProcessVariables.performanceCheckReport + "_" + props.getDataSet(), sessionResult.getReport());
                } catch (Exception e) {
                    execution.setVariable(ProcessVariables.processError,
                            e.getMessage() + String.format("Stage: %s, dataset: %s", props.getStageType(), props.getDataSet())
                    );
                }
            }
        }
        execution.setVariable(ProcessVariables.performanceCheckSuccess, success);
        log.info("RunPerformanceCheck success: {}", success);
    }

}
