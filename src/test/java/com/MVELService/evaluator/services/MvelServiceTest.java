package com.MVELService.evaluator.services;

import com.MVELService.evaluator.models.Argument;
import com.MVELService.evaluator.models.Constant;
import com.MVELService.evaluator.models.Question;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MvelServiceTest {

    @InjectMocks
    private MvelService mvelService;

    @Mock
    private ConstantService mockConstantService;

    @BeforeEach
    void setUp() {
        mvelService = new MvelService(mockConstantService);
    }

    @Test
    void executeMvelForOneQuestion(){

        List<Question> questionList = new ArrayList<>();

        //Argument list for the question
        Argument[] arguments = new Argument[]{new Argument("arg1", "value", "BDP", "party.legalName")};

        //Dummy question
        Question question1 = new Question("test1",
                "Label 1",
                "party.legalName",
                null,
                new String[]{"Yes"},
                false,
                "value == 'Yes'",
                false,
                "value == 'No'",
                "",
                arguments,
                null,
                null
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
                        "party.isHighRisk"
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
                null,
                new String[]{"Yes"},
                false,
                "legalName == 'No'",
                false,
                "taxNumber > 8",
                "",
                argumentsForQuetsion1,
                null,
                null
        );

        //Dummy question2
        Question question2 = new Question("test2",
                "Tax Number",
                "party.taxNumber",
                null,
                new String[]{"9"},
                false,
                "legalName == 'Yes'",
                false,
                "dummyArgument == 'Yes'",
                "",
                argumentsForQuetsion2,
                null,
                null
        );

        //Dummy question2
        Question question3 = new Question("test3",
                "Question 3",
                "party.isHighRisk",
                null,
                new String[]{"Yes"},
                true,
                "",
                false,
                "",
                "",
                null,
                null,
                null
        );

        questionList.add(question1);
        questionList.add(question2);
        questionList.add(question3);

        List<Question> resultQuestions = mvelService.executeMvel(questionList);

        assertFalse(resultQuestions.get(0).getVisible(), "Question 1 should not be visible.");
        assertTrue(resultQuestions.get(0).getReadOnly(), "Question 1 should be read only.");
        assertTrue(resultQuestions.get(1).getVisible(), "Question 2 should be visible.");
        assertTrue(resultQuestions.get(1).getReadOnly(), "Question 2 should be read only.");
        assertTrue(resultQuestions.get(2).getVisible(), "Question 3 should be visible.");
        assertFalse(resultQuestions.get(2).getReadOnly(), "Question 3 should not be read only.");
    }

    @Test
    void executeClearValueExpression(){

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
                        "party.isHighRisk"
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
                null,
                new String[]{"Yes"},
                false,
                "legalName == 'No'",
                false,
                "greaterThan(taxNumber, 8)",
                "taxNumber != 9",
                argumentsForQuetsion1,
                null,
                null
        );

        //Dummy question2
        Question question2 = new Question("test2",
                "Tax Number",
                "party.taxNumber",
                null,
                new String[]{"9"},
                false,
                "legalName == 'Yes'",
                false,
                "dummyArgument == 'Yes'",
                "legalName == 'Yes'",
                argumentsForQuetsion2,
                null,
                null
        );

        questionList.add(question1);
        questionList.add(question2);

        List<Question> resultQuestions = mvelService.executeMvel(questionList);

        assertNotNull(resultQuestions.get(0).getValue(), "Question 1 should not have it's value cleared.");
        assertNull(resultQuestions.get(1).getValue(), "Question 2 should have it's value cleared.");
    }

    @Test
    void executeDisjointFunction(){

        List<Question> questionList = new ArrayList<>();

        //Argument list for the question
        Argument[] argumentsForQuetsion1 = new Argument[]{
                new Argument("arg1",
                        "legalName",
                        "BDP",
                        "party.legalName"
                ),
                new Argument("arg2",
                        "testInfo",
                        "BDP",
                        "party.additionalInfo"
                )
        };

        //Dummy question1
        Question question1 = new Question("test1",
                "Label 1",
                "party.legalName",
                null,
                new String[]{"1", "2", "3"},
                false,
                "disjoint(legalName, testInfo)",
                false,
                "!disjoint(legalName, testInfo)",
                "",
                argumentsForQuetsion1,
                null,
                null
        );

        //Dummy question2
        Question question2 = new Question("test2",
                "Label 2",
                "party.additionalInfo",
                null,
                new String[]{"3", "4", "5"},
                false,
                "",
                false,
                "",
                "",
                null,
                null,
                null
        );

        questionList.add(question1);
        questionList.add(question2);

        List<Question> updatedQuestions = mvelService.executeMvel(questionList);

        assertTrue(updatedQuestions.get(0).getReadOnly(), "Question 1 should be read only.");
        assertFalse(updatedQuestions.get(0).getVisible(), "Question 1 should not be visible.");
    }

}