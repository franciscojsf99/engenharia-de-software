package pt.ulisboa.tecnico.socialsoftware.tutor.pap.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Phrase
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.WordAssignmentQuestion

@DataJpaTest
class ImportWordAssignmentQuestionFromXML extends SpockTest {

    def question
    def phrase1
    def phrase2

    def setup() {
        createExternalCourseAndExecution()

        given: "create a question"
        question = new Question()
        question.setCourse(externalCourse)
        question.setKey(1)
        question.setTitle(QUESTION_1_TITLE)
        question.setContent(QUESTION_1_CONTENT)
        question.setStatus(Question.Status.AVAILABLE)
        def questionDetails = new WordAssignmentQuestion()
        question.setQuestionDetails(questionDetails)
        questionDetailsRepository.save(questionDetails)
        questionRepository.save(question)

        and: '2 phrases'
        phrase1 = new Phrase()
        phrase1.setContent(PHRASE_1)
        phrase1.setWord(WORD_1)
        phrase1.setQuestionDetails(questionDetails)
        phraseRepository.save(phrase1)

        phrase2 = new Phrase()
        phrase2.setContent(PHRASE_2)
        phrase2.setWord(WORD_2)
        phrase2.setQuestionDetails(questionDetails)
        phraseRepository.save(phrase2)
    }

    def "export and import a word assignment question from xml"() {
        def questionsXML = questionService.exportQuestionsToXml()
        print questionsXML
        questionService.removeQuestion(question.getId())

        when:
        questionService.importQuestionsFromXml(questionsXML)

        then:
        questionRepository.findQuestions(externalCourse.getId()).size() == 1
        def result = questionService.findQuestions(externalCourse.getId()).get(0)
        result.getId() != null
        result.getKey() == null
        result.getStatus() == Question.Status.AVAILABLE.name()
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null

        result.getQuestionDetailsDto().getPhrases().size() == 2

        result.getQuestionDetailsDto().getPhrases().get(0).getContent() == PHRASE_1
        result.getQuestionDetailsDto().getPhrases().get(0).getWord() == WORD_1
        result.getQuestionDetailsDto().getPhrases().get(1).getContent() == PHRASE_2
        result.getQuestionDetailsDto().getPhrases().get(1).getWord() == WORD_2
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
