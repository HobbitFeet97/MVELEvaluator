package com.MVELService.evaluator.services;

import com.MVELService.evaluator.dao.ConstantDao;
import com.MVELService.evaluator.models.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConstantService {

    private final ConstantDao constantDao;

    @Autowired
    public ConstantService(ConstantDao constantDao) {
        this.constantDao = constantDao;
    }

    public List<Constant> getAllConstants(){
        return constantDao.getAllConstants();
    }
}
