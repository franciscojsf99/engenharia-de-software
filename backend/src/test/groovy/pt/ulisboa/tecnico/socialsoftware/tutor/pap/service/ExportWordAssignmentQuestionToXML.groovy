package pt.ulisboa.tecnico.socialsoftware.tutor.pap.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Phrase
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.WordAssignmentQuestion

@DataJpaTest
class ExportWordAssignmentQuestionToXML extends SpockTest {

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

    def "export a word assignment question to xml"() {
        when:
        def questionsXML = questionService.exportQuestionsToXml()

        then:
        questionsXML != null
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
