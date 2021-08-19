package com.acmen.demo;

import org.flowable.engine.*;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.impl.cfg.StandaloneProcessEngineConfiguration;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * @author Administrator
 */
public class FlowableApplication {

    public static void main(String[] args) {
        // 初始化一个 ProcessEngineConfiguration 配置对象
        ProcessEngineConfiguration processEngineConfiguration = new StandaloneProcessEngineConfiguration()
                .setJdbcUrl("jdbc:mysql://192.168.7.18:3306/flowable-db?useUnicode=true&characterEncoding=utf-8&useSSL=true&nullCatalogMeansCurrent=true&serverTimezone=UTC")
                .setJdbcUsername("root")
                .setJdbcPassword("root")
                .setJdbcDriver("com.mysql.jdbc.Driver")
                .setDatabaseSchemaUpdate(ProcessEngineConfiguration.DB_SCHEMA_UPDATE_TRUE);
        // 使用 ProcessEngineConfiguration 配置对象去实例化一个 ProcessEngine 对象
        ProcessEngine processEngine = processEngineConfiguration.buildProcessEngine();

        // 将流程定义部署到 Flowable 引擎中，通过 RepositoryService
        RepositoryService repositoryService = processEngine.getRepositoryService();
        // 通过 RepositoryService 去创建一个新的部署 Deployment，传入xml 文件地址，调用 deploy()方法完成部署
        Deployment deployment = repositoryService.createDeployment().addClasspathResource("processes/holiday-request.bpmn20.xml")
                .deploy();

        // 查询流程定义
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult();
        System.out.println("Found process definition : " + processDefinition.getName());
        System.out.println("Found process definition : " + processDefinition.getId());
        System.out.println("Found process definition : " + processDefinition.getVersion());
        System.out.println("Found process definition : " + processDefinition.getKey());
        System.out.println("Found process definition : " + processDefinition.getDeploymentId());


        Scanner scanner = new Scanner(System.in);

        System.out.println("Who are you?");
        String employee = scanner.nextLine();

        System.out.println("How many holidays do you want to request?");
        Integer nrOfHolidays = Integer.valueOf(scanner.nextLine());

        System.out.println("Why do you need them?");
        String description = scanner.nextLine();

        // 初始化流程实例 ProcessInstance 对象
        RuntimeService runtimeService = processEngine.getRuntimeService();
        Map<String, Object> variables = new HashMap<>();
        variables.put("employee", employee);
        variables.put("nrOfHolidays", nrOfHolidays);
        variables.put("description", description);
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("holidayRequest", variables);

        // 获得实际的任务列表，获得的是 “managers” 这个组中的任务列表
        TaskService taskService = processEngine.getTaskService();
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("managers").list();
        System.out.println("You have " + tasks.size() + " tasks:");
        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ") " + tasks.get(i).getName());
        }

        // 通过任务 Id 获取特定流程实例的变量
        System.out.println("Which task would you like to complete?");
        Integer taskIndex = Integer.valueOf(scanner.nextLine());
        Task task = tasks.get(taskIndex - 1);
        System.out.println(task);
        Map<String, Object> processVariables = taskService.getVariables(task.getId());
        System.out.println(processVariables.get("employee") + " wants " + processVariables.get("nrOfHolidays") + " of holidays. Do you approve this?");
        System.out.println("Please input your operation ? (y/n)");

        String res = scanner.nextLine();
        boolean approved = res.toLowerCase().equals("y");
        variables = new HashMap<String, Object>();
        variables.put("approved", approved);
        taskService.complete(task.getId(), variables);

        // 使用历史数据
        HistoryService historyService = processEngine.getHistoryService();
        List<HistoricActivityInstance> activityInstances = historyService.createHistoricActivityInstanceQuery()
                .finished()
                .orderByHistoricActivityInstanceEndTime().asc()
                .list();
        for (HistoricActivityInstance activity : activityInstances) {
            System.out.println(activity.getActivityId() + " took " + activity.getDurationInMillis() + " milliseconds");
        }


    }
}
