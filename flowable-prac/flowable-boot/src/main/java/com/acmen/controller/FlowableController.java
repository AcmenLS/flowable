package com.acmen.controller;

import com.acmen.utils.R;
import org.flowable.engine.*;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/flowable")
public class FlowableController {

    @Autowired
    private ProcessEngine processEngine;

    @Autowired
    RuntimeService runtimeService;

    @Autowired
    RepositoryService repositoryService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private HistoryService historyService;

    @RequestMapping("/startFlowable")
    public R startFlowable(){
        /**
         * 通过 RepositoryService 去创建一个新的部署 Deployment，传入xml 文件地址，调用 deploy()方法完成部署
         *          act_re_deployment(部署记录),
         *          act_ge_bytearray(两条记录，对应bpmn文件和图片),
         *          act_re_procdef(记录 bpmn 文件包含的基本信息)
         * */
        Deployment deployment = repositoryService.createDeployment().addClasspathResource("processes/holidayRequest.bpmn20.xml")
                .deploy();
        /*
         * 搜索此次部署所对应的流程定义信息，然后启动这个流程
         *  首先向 act_ru_execution 表中插入一条记录，记录流程定义的执行实例，
         *  act_ru_task插入一条记录，记录第一个任务的信息
         *  向 act_hi_procinst 表和 act_hi_taskinst 表中各插入一条记录，记录本次执行实例和任务的历史记录
         *
         * */
        ProcessDefinition processDefinition=repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).singleResult();
        ProcessInstance processInstance=runtimeService.startProcessInstanceById(processDefinition.getId());

        Task task1 = taskService.createTaskQuery().processInstanceId(processInstance.getId()).taskDefinitionKey("approveTask").singleResult();
//        Map<String, Object> variables = new HashMap<>();
//        variables.put("approved",true);
        taskService.setVariable(task1.getId(),"approved",false);
        taskService.complete(task1.getId());

//        Task task2 = taskService.createTaskQuery().processInstanceId(processInstance.getId()).taskDefinitionKey("task2").singleResult();
//        taskService.complete(task2.getId());

        List<HistoricVariableInstance> vars = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstance.getId()).list();
        for (HistoricVariableInstance var:vars) {
            System.out.println(var.getVariableName());
            System.out.println(var.getValue());
        }


        return R.ok("Process start !!!");
    }
}
