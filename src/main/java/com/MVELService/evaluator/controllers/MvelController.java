package com.MVELService.evaluator.controllers;


import com.MVELService.evaluator.models.Question;
import com.MVELService.evaluator.services.MvelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/v1/section/validate")
@RestController
public class MvelController {

    private final MvelService mvelService;

    @Autowired
    public MvelController(MvelService mvelService) {
        this.mvelService = mvelService;
    }

    @PostMapping
    public List<Question> validateSection(@RequestBody List<Question> questions){
        return mvelService.executeMvel(questions);
    }
}
