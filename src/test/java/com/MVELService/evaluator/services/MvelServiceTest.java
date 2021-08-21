package com.MVELService.evaluator.services;

import com.MVELService.evaluator.models.Argument;
import com.MVELService.evaluator.models.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MvelServiceTest {

    private MvelService mvelService;

    @BeforeEach
    void setUp() {
        mvelService = new MvelService();
    }

    @Test
    void executeMvelForOneQuestion() {

        List<Question> questionList = new ArrayList<>();

        //Argument list for the question
        Argument[] arguments = new Argument[]{new Argument("arg1", "value", "BDP", "party.legalName")};

        //Dummy question
        Question question1 = new Question("test1",
                "Label 1",
                "party.legalName",
                new String[]{"Yes"},
                false,
                "value == 'Yes'",
                false,
                "value == 'No'",
                arguments
        );

        questionList.add(question1);

        //Execute the service
        List<Question> resultQuestions = mvelService.executeMvel(questionList);

        assertTrue(resultQuestions.get(0).getVisible(), "Question should be visible.");
        assertFalse(resultQuestions.get(0).getReadOnly(), "Question should not be read only.");
    }

    @Test
    void executeMvelForMultipleQuestions(){

        List<Question> questionList = new ArrayList<>();

        //Argument list for the question
        Argument[] argumentsForQuetsion1 = new Argument[]{
                new Argument("arg1",
                        "legalName",
                        "BDP",
                        "party.legalName"
                ),
                new Argument(
                        "arg2",
                        "taxNumber",
                        "BDP",
                        "party.taxNumber"
                )
        };

        //Arguments for question 2
        Argument[] argumentsForQuetsion2 = new Argument[]{
                new Argument("arg3",
                        "dummyArgument",
                        "BDP",
                        ""
                ),
                new Argument(
                        "arg4",
                        "legalName",
                        "BDP",
                        "party.legalName"
                )
        };

        //Dummy question1
        Question question1 = new Question("test1",
                "Label 1",
                "party.legalName",
                new String[]{"Yes"},
                false,
                "legalName == 'No'",
                false,
                "greaterThan(taxNumber, 8)",
                argumentsForQuetsion1
        );

        //Dummy question2
        Question question2 = new Question("test1",
                "Tax Number",
                "party.taxNumber",
                new String[]{"9"},
                false,
                "legalName == 'Yes'",
                false,
                "",
                argumentsForQuetsion2
        );

        questionList.add(question1);
        questionList.add(question2);

        List<Question> resultQuestions = mvelService.executeMvel(questionList);

        assertFalse(resultQuestions.get(0).getVisible(), "Question 1 should not be visible.");
        assertTrue(resultQuestions.get(0).getReadOnly(), "Question 1 should be read only.");
        assertTrue(resultQuestions.get(1).getVisible(), "Question 2 should be visible.");
        assertFalse(resultQuestions.get(1).getReadOnly(), "Question 2 should be read only.");
    }
}