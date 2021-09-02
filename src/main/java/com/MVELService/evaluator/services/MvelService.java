package com.MVELService.evaluator.services;

import com.MVELService.evaluator.components.JsonHandler;
import com.MVELService.evaluator.models.Answer;
import com.MVELService.evaluator.models.Argument;
import com.MVELService.evaluator.models.Constant;
import com.MVELService.evaluator.models.Question;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.mvel2.MVEL;
import org.mvel2.integration.VariableResolverFactory;
import org.mvel2.integration.impl.MapVariableResolverFactory;
import org.springframework.stereotype.Service;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class MvelService {

    //Constant Service
    private final ConstantService constantService;
    //Json Handler
    private final JsonHandler jsonHandler = new JsonHandler();
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
    private static final String PREDEFINED_FUNCTION_GREATER_THAN = "def greaterThan(value1, value2){return value1 > value2;};";
    private static final String PREDEFINED_FUNCTION_DISJOINT = "import java.util.*; def disjoint(list1, list2){ if(list1 == null || list2 == null){return false;} return Collections.disjoint(Arrays.asList(list1), Arrays.asList(list2)); };";
    private static final String PREDEFINED_FUNCTION_FILTER_LIST = "import java.util.ArrayList; def filterList(originalList, filter){ list = []; foreach(element : originalList){ if(filter(element)){ list.add(element); }; }; return list; };";
    private static final String PREDEFINED_FUNCTION_VALIDATE_CHECK_DIGIT_1 = "def validateScenarioOne(inputValue){ characterWeighting = [\"A\": 10,\"B\": 11,\"C\": 12,\"D\": 13,\"E\": 14,\"F\": 15,\"G\": 16,\"H\": 17,\"I\": 18,\"J\": 19,\"K\": 20,\"L\": 21,\"M\": 22,\"N\": 23,\"O\": 24,\"P\": 25,\"Q\": 26,\"R\": 27,\"S\": 28,\"T\": 29,\"U\": 30,\"V\": 31,\"W\": 32,\"X\": 33,\"Y\": 34,\"Z\": 35]; positionWeighting = [9,8,7,6,5,4,3,2]; sum = 324; match = \"^[A-Z][0-9]{6}(\\\\([A,0-9]\\\\))\"; chars = inputValue.split(''); if(inputValue.matches(match)){ for(index = 0; index < 7; index++){ if(index == 0){ sum += (characterWeighting.get(chars[index]) * positionWeighting[index + 1]); }else{ sum += (Integer.valueOf(chars[index]) * positionWeighting[index + 1]); }; }; expectedDigit = (11 - (sum % 11)); if(expectedDigit == 11){return false;}else if(expectedDigit == 10){return chars[8] == 'A';}else{return chars[8] == String.valueOf(expectedDigit);}}else{return false;};};";
    private static final String PREDEFINED_FUNCTION_VALIDATE_CHECK_DIGIT_2 = "def validateScenarioTwo(inputValue){ characterWeighting = [\"A\": 10,\"B\": 11,\"C\": 12,\"D\": 13,\"E\": 14,\"F\": 15,\"G\": 16,\"H\": 17,\"I\": 18,\"J\": 19,\"K\": 20,\"L\": 21,\"M\": 22,\"N\": 23,\"O\": 24,\"P\": 25,\"Q\": 26,\"R\": 27,\"S\": 28,\"T\": 29,\"U\": 30,\"V\": 31,\"W\": 32,\"X\": 33,\"Y\": 34,\"Z\": 35]; positionWeighting = [9,8,7,6,5,4,3,2]; sum = 0; match = \"^[A-Z]{2}[0-9]{6}(\\\\([A,0-9]\\\\))\"; chars = inputValue.split(''); if(inputValue.matches(match)){ for(index = 0; index < 8; index++){ if(index == 0 || index == 1){ sum += (characterWeighting.get(chars[index]) * positionWeighting[index]); }else{ sum += (Integer.valueOf(chars[index]) * positionWeighting[index]); }; }; expectedDigit = (11 - (sum % 11)); if(expectedDigit == 11){return false;}else if(expectedDigit == 10){return chars[9] == 'A';}else{return chars[9] == String.valueOf(expectedDigit);}}else{return false;};};";
    private static final String PREDIFINED_FUNCTION_VALIDATE_CHECK_DIGIT = "def validateCheckDigit(inputValue){ if(inputValue.length() > 11 || inputValue.length() < 10){ return false; }else if(inputValue.length() == 10){ return validateScenarioOne(inputValue); }else{ return validateScenarioTwo(inputValue); }; };";
    //Variable factory to hold list of predefined constants for MVEL expressions
    VariableResolverFactory variableFactory = new MapVariableResolverFactory();

    public MvelService(ConstantService constantService) {
        this.constantService = constantService;
    }

    public List<Question> executeMvel(List<Question> questions) {

        if (!loaded){
            //Pre-load mvel service with all constants in the system, to be used in MVEL expressions
            constantService.getAllConstants()
                            .forEach(constant -> {
                                try {
                                    JsonNode node = jsonHandler.readJson(constant.getData());
                                    List<HashMap<String, Object>> dictionaries = new ArrayList<>();
                                    jsonHandler.fromJsonDictList(node, dictionaries);
                                    variableFactory.createVariable(constant.getCode(), dictionaries);
                                } catch (JsonProcessingException e) {
                                    e.printStackTrace();
                                }
                            });
            MVEL.eval(
                    PREDEFINED_FUNCTION_GREATER_THAN
                            + PREDEFINED_FUNCTION_DISJOINT
                            + PREDEFINED_FUNCTION_FILTER_LIST
                            + PREDEFINED_FUNCTION_VALIDATE_CHECK_DIGIT_1
                            + PREDEFINED_FUNCTION_VALIDATE_CHECK_DIGIT_2
                            + PREDIFINED_FUNCTION_VALIDATE_CHECK_DIGIT,
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
                                .orElse(new Question(null, null, null, new String[]{""}, null, null, null, null, null, null, null, null))
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
                    }
                });
            }
        });

        return args;
    }

    private String[] returnExpressions(Question question){

        return new String[]{question.getVisibleExpression(), question.getReadOnlyExpression(), question.getClearValueExpression(), question.getAnswerExpression()};
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

        return COLLAPSED_SENTENCE_1 + sb +COLLAPSED_SENTENCE_4;
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
        if (results.get(3) != null && results.get(3) instanceof List){
            question.setAnswers(formatQuestionAnswers((List<HashMap<String, String>>) results.get(3)));
        }
    }

    private String formatFilterExpressions(List<Question> questions){

        StringBuilder sb = new StringBuilder();

        for (Question question : questions){
            Argument[] args = question.getArgs();
            for (Argument arg : args){
                if (arg.getType().equals(ARG_TYPE_FILTER)){
                    sb.append(createFilterFunction(arg));
                }
            }
        }

        return sb.toString();
    }

    private String createFilterFunction(Argument arg){
        String function = "\r\n def %s($){ return %s; };";
        return function.formatted(arg.getArgumentName(), arg.getValue());
    }

    private Answer[] formatQuestionAnswers(List<HashMap<String, String>> inputAnswers){

        List<Answer> answers = new ArrayList<>();

        for (HashMap<String, String> answer : inputAnswers){
            answers.add(mapAnswer(answer));
        }

        return answers.toArray(Answer[]::new);
    }

    private Answer mapAnswer(HashMap<String, String> inputAnswer){
        return new Answer(
                inputAnswer.get("code"),
                inputAnswer.get("description")
        );
    }
}
