//package com.cmze.configuration;
//
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class RabbitConfiguration {
//    public static final String EXCHANGE = "contest.bus";
//
//    public static final String SURVEY_CMD_QUEUE = "survey-service.commands";
//    public static final String QUIZ_CMD_QUEUE   = "quiz-service.commands";
//    public static final String CONTEST_EVT_QUEUE= "contest-service.events";
//
//    @Bean TopicExchange contestExchange() { return new TopicExchange(EXCHANGE, true, false); }
//
//    @Bean Queue surveyCmdQueue() { return QueueBuilder.durable(SURVEY_CMD_QUEUE).build(); }
//    @Bean Queue quizCmdQueue()   { return QueueBuilder.durable(QUIZ_CMD_QUEUE).build(); }
//    @Bean Queue contestEvtQueue(){ return QueueBuilder.durable(CONTEST_EVT_QUEUE).build(); }
//
//    @Bean Binding bindSurveyCmd(Queue surveyCmdQueue, TopicExchange contestExchange) {
//        return BindingBuilder.bind(surveyCmdQueue).to(contestExchange).with("contest.command.survey.*");
//    }
//    @Bean Binding bindQuizCmd(Queue quizCmdQueue, TopicExchange contestExchange) {
//        return BindingBuilder.bind(quizCmdQueue).to(contestExchange).with("contest.command.quiz.*");
//    }
//    @Bean Binding bindContestEvtSurvey(Queue contestEvtQueue, TopicExchange contestExchange) {
//        return BindingBuilder.bind(contestEvtQueue).to(contestExchange).with("survey.event.#");
//    }
//    @Bean Binding bindContestEvtQuiz(Queue contestEvtQueue, TopicExchange contestExchange) {
//        return BindingBuilder.bind(contestEvtQueue).to(contestExchange).with("quiz.event.#");
//    }
//}
