package com.acmen.controller;

import com.acmen.service.FlowableService;
import com.acmen.utils.R;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@RequestMapping("/flowable")
public class HolidayFlowableController {

    @Autowired
    private FlowableService flowableService;

//    @RequestMapping("/startFlowable")
    public R startFlowable(@PathVariable String userId){

        return R.ok("Process start !!!");
    }
}
