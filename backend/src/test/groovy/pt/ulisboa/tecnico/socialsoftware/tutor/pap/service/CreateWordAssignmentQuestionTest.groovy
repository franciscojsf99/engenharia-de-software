package pt.ulisboa.tecnico.socialsoftware.tutor.pap.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.PhraseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.WordAssignmentQuestionDto


@DataJpaTest
class CreateWordAssignmentQuestionTest extends SpockTest {

    def setup() {
        createExternalCourseAndExecution()
    }

    def "create a word assignment question with 1 phrase"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new WordAssignmentQuestionDto())

        and: 'a phraseDto'
        def phraseDto = new PhraseDto()
        phraseDto.setContent(PHRASE_1)
        phraseDto.setWord(WORD_1)
        def phrases = new ArrayList<PhraseDto>()
        phrases.add(phraseDto)
        questionDto.getQuestionDetailsDto().setPhrases(phrases)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getQuestionDetails().getPhrases().size() == 1
        result.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(result)

        result.getQuestionDetails().getPhrases().get(0).getContent() == PHRASE_1
        result.getQuestionDetails().getPhrases().get(0).getWord() == WORD_1
    }

    def "create a word assignment question with 2 phrases"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new WordAssignmentQuestionDto())

        and: 'phrasesDto'
        def phraseDto1 = new PhraseDto()
        phraseDto1.setContent(PHRASE_1)
        phraseDto1.setWord(WORD_1)
        def phraseDto2 = new PhraseDto()
        phraseDto2.setContent(PHRASE_2)
        phraseDto2.setWord(WORD_2)
        def phrases = new ArrayList<PhraseDto>()
        phrases.add(phraseDto1)
        phrases.add(phraseDto2)
        questionDto.getQuestionDetailsDto().setPhrases(phrases)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "the question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getQuestionDetails().getPhrases().size() == 2
        result.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(result)

        result.getQuestionDetails().getPhrases().get(0).getContent() == PHRASE_1
        result.getQuestionDetails().getPhrases().get(0).getWord() == WORD_1
        result.getQuestionDetails().getPhrases().get(1).getContent() == PHRASE_2
        result.getQuestionDetails().getPhrases().get(1).getWord() == WORD_2
    }

    def "create a word assignment question without phrases"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new WordAssignmentQuestionDto())

        questionDto.getQuestionDetailsDto().setPhrases(new ArrayList<PhraseDto>())

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.AT_LEAST_ONE_PHRASE_NEEDED
    }

    def "cannot create a word assignment question with phrase content empty"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new WordAssignmentQuestionDto())

        and: 'a phraseDto'
        def phraseDto = new PhraseDto()
        phraseDto.setWord(WORD_1)
        def phrases = new ArrayList<PhraseDto>()
        phrases.add(phraseDto)
        questionDto.getQuestionDetailsDto().setPhrases(phrases)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.EMPTY_PHRASE_CONTENT
    }

    def "cannot create a word assignment question with phrase content blank"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new WordAssignmentQuestionDto())

        and: 'a phraseDto'
        def phraseDto = new PhraseDto()
        phraseDto.setContent("   ")
        phraseDto.setWord(WORD_1)
        def phrases = new ArrayList<PhraseDto>()
        phrases.add(phraseDto)
        questionDto.getQuestionDetailsDto().setPhrases(phrases)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.EMPTY_PHRASE_CONTENT
    }

    def "cannot create a word assignment question with the word empty"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new WordAssignmentQuestionDto())

        and: 'a phraseDto'
        def phraseDto = new PhraseDto()
        phraseDto.setContent(PHRASE_1)
        def phrases = new ArrayList<PhraseDto>()
        phrases.add(phraseDto)
        questionDto.getQuestionDetailsDto().setPhrases(phrases)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.EMPTY_WORD
    }

    def "cannot create a word assignment question with the word blank"() {
        given: "a questionDto"
        def questionDto = new QuestionDto()
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setStatus(Question.Status.AVAILABLE.name())
        questionDto.setQuestionDetailsDto(new WordAssignmentQuestionDto())

        and: 'a phraseDto'
        def phraseDto = new PhraseDto()
        phraseDto.setContent(PHRASE_1)
        phraseDto.setWord("   ")
        def phrases = new ArrayList<PhraseDto>()
        phrases.add(phraseDto)
        questionDto.getQuestionDetailsDto().setPhrases(phrases)

        when:
        questionService.createQuestion(externalCourse.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.EMPTY_WORD
    }


    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}