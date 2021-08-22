package com.MVELService.evaluator.services;

import com.MVELService.evaluator.models.Question;
import lombok.extern.slf4j.Slf4j;
import org.mvel2.MVEL;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class MvelService {

    //Logging strings
    private static final String ARGUMENT_LOGGING = "Arguments generated are: ";
    private static final String EXPRESSION_LOGGING = "Expressions generated for question id %s are: ";
    private static final String MVEL_RESULT_LOGGING = "Results for question id %s are: ";
    //List of constants used to format MVEL sentences
    private static final String COLLAPSED_SENTENCE_1 = "results = [];";
    private static final String COLLAPSED_SENTENCE_2 = "results.add(";
    private static final String COLLAPSED_SENTENCE_3 = ");";
    private static final String COLLAPSED_SENTENCE_4 = "return results;";
    //Variable which will control when predefined functions are loaded into MVEL
    private static boolean loaded = false;
    //Constants which represent argument types
    private static final String ARG_TYPE_BDP = "BDP";
    private static final String ARG_TYPE_FILTER = "FILTER";
    //Predefined functions to be loaded into the MVEL evaluator
    private static String PREDEFINED_FUNCTION_GREATER_THAN = "def greaterThan(value1, value2){return value1 > value2;};";
    private static String PREDEFINED_FUNCTION_DISJOINT = "import java.util.*; def disjoint(list1, list2){ if(list1 == null || list2 == null){return false;} return Collections.disjoint(Arrays.asList(list1), Arrays.asList(list2)); };";
    //Variable factory to hold list of predefined constants for MVEL expressions
    VariableResolverFactory variableFactory = new MapVariableResolverFactory();

    public List<Question> executeMvel(List<Question> questions){

        if (!loaded){
            variableFactory.createVariable("CONSTANT_1", new String[]{"1","2"});
            MVEL.eval(
                    PREDEFINED_FUNCTION_GREATER_THAN + PREDEFINED_FUNCTION_DISJOINT,
                    variableFactory
            );
            loaded = true;
        }

        //Generate global list of arguments to be used in the MVEL evaluator
        HashMap<String, Object> arguments = generateArguments(questions);
        log.info(ARGUMENT_LOGGING+arguments);

        for (Question question : questions){
            //Format the expressions to be evaluated
            String expressions = formatQuestionExpressions(question);
            log.info(String.format(EXPRESSION_LOGGING, question.getId())+expressions);
            ArrayList<Object> results = (ArrayList<Object>) MVEL.eval(expressions, arguments, variableFactory);
            log.info(String.format(MVEL_RESULT_LOGGING, question.getId())+results);
            //Update the question with the results of MVEL
            updateQuestion(results, question);
        }

        return questions;
    }

    private HashMap<String, Object> generateArguments(List<Question> questions){

        HashMap<String, Object> args = new HashMap<>();

        questions.forEach(question -> {
            if (question.getArgs() != null){
                Arrays.stream(question.getArgs()).forEach(argument -> {
                    if (argument.getType().equals(ARG_TYPE_BDP)){
                        String[] value = questions.stream().filter(q -> q.getBdp()
                                .equals(argument.getValue()))
                                .findFirst()
                                .orElse(new Question(null, null, null, new String[]{""}, null, null, null, null, null, null))
                                .getValue();
                        if (value != null){
                            if (value.length == 1) {
                                args.put(
                                        argument.getArgumentName(),
                                        value[0]
                                );
                            }else{
                                args.put(
                                        argument.getArgumentName(),
                                        value
                                );
                            }
                        }else{
                            args.put(
                                    argument.getArgumentName(),
                                    null
                            );
                        }
                    }else if (argument.getType().equals(ARG_TYPE_FILTER)){
                        args.put(
                                argument.getArgumentName(),
                                argument.getValue()
                        );
                    }
                });
            }
        });

        return args;
    }

    private String[] returnExpressions(Question question){

        return new String[]{question.getVisibleExpression(), question.getReadOnlyExpression(), question.getClearValueExpression()};
    }

    private String formatQuestionExpressions(Question question){

        StringBuilder sb = new StringBuilder();

        for (String expression : returnExpressions(question)){
            if (!expression.isEmpty()) {
                sb.append(COLLAPSED_SENTENCE_2 + expression + COLLAPSED_SENTENCE_3);
            }else{
                sb.append(COLLAPSED_SENTENCE_2 + "''" + COLLAPSED_SENTENCE_3);
            }
        }

        return COLLAPSED_SENTENCE_1+ sb +COLLAPSED_SENTENCE_4;
    }

    private void updateQuestion(ArrayList<Object> results, Question question){
        if (results.get(0) instanceof Boolean){
            question.setVisible((Boolean) results.get(0));
        }
        if (results.get(1) instanceof Boolean){
            question.setReadOnly((Boolean) results.get(1));
        }
        if (results.get(2) instanceof Boolean && (Boolean) results.get(2)){
            question.setValue(null);
        }
    }
}
