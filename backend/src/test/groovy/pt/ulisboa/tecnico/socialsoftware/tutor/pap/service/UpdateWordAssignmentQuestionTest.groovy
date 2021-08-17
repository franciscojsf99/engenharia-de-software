package pt.ulisboa.tecnico.socialsoftware.tutor.pap.service

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.TestConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.BeanConfiguration
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Phrase
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.WordAssignmentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.PhraseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.WordAssignmentQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User


@DataJpaTest
class UpdateWordAssignmentQuestionTest extends SpockTest {

    def question
    def phrase1
    def phrase2
    def user

    def setup() {
        createExternalCourseAndExecution()

        user = new User(USER_1_NAME, USER_1_USERNAME, USER_1_EMAIL, User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        user.addCourse(externalCourseExecution)
        userRepository.save(user)

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

    def "add 1 phrase"() {
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setQuestionDetailsDto(new WordAssignmentQuestionDto())

        and: '1 new phrase'
        def phrases = new ArrayList<PhraseDto>()
        def phraseDto = new PhraseDto(phrase1)
        phrases.add(phraseDto)
        phraseDto = new PhraseDto(phrase2)
        phrases.add(phraseDto)

        phraseDto = new PhraseDto()
        phraseDto.setContent(PHRASE_3)
        phraseDto.setWord(WORD_3)
        phrases.add(phraseDto)

        questionDto.getQuestionDetailsDto().setPhrases(phrases)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "the question is inside the repository"
        questionRepository.count() == 1L
        def result = questionRepository.findAll().get(0)
        result.getId() != null
        result.getKey() == 1
        result.getStatus() == Question.Status.AVAILABLE
        result.getTitle() == QUESTION_1_TITLE
        result.getContent() == QUESTION_1_CONTENT
        result.getImage() == null
        result.getQuestionDetails().getPhrases().size() == 3
        result.getCourse().getName() == COURSE_1_NAME
        externalCourse.getQuestions().contains(result)

        result.getQuestionDetails().getPhrases().get(0).getContent() == PHRASE_1
        result.getQuestionDetails().getPhrases().get(0).getWord() == WORD_1
        result.getQuestionDetails().getPhrases().get(1).getContent() == PHRASE_2
        result.getQuestionDetails().getPhrases().get(1).getWord() == WORD_2
        result.getQuestionDetails().getPhrases().get(2).getContent() == PHRASE_3
        result.getQuestionDetails().getPhrases().get(2).getWord() == WORD_3

    }

    def "remove 1 phrase"() {
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setQuestionDetailsDto(new WordAssignmentQuestionDto())

        and: '1 phrase'
        def phrases = new ArrayList<PhraseDto>()
        def phraseDto = new PhraseDto(phrase1)
        phrases.add(phraseDto)

        questionDto.getQuestionDetailsDto().setPhrases(phrases)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

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

    def "update 1 phrase content"() {
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setQuestionDetailsDto(new WordAssignmentQuestionDto())

        and: 'a changed phrase'
        def phrases = new ArrayList<PhraseDto>()
        def phraseDto = new PhraseDto(phrase1)
        phraseDto.setContent(PHRASE_3)
        phrases.add(phraseDto)
        phraseDto = new PhraseDto(phrase2)
        phrases.add(phraseDto)

        questionDto.getQuestionDetailsDto().setPhrases(phrases)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

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

        result.getQuestionDetails().getPhrases().get(0).getContent() == PHRASE_3
        result.getQuestionDetails().getPhrases().get(0).getWord() == WORD_1
        result.getQuestionDetails().getPhrases().get(1).getContent() == PHRASE_2
        result.getQuestionDetails().getPhrases().get(1).getWord() == WORD_2
    }

    def "update 2 phrases content"() {
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setQuestionDetailsDto(new WordAssignmentQuestionDto())

        and: '2 changed phrases'
        def phrases = new ArrayList<PhraseDto>()
        def phraseDto = new PhraseDto(phrase1)
        phraseDto.setContent(PHRASE_2)
        phrases.add(phraseDto)
        phraseDto = new PhraseDto(phrase2)
        phraseDto.setContent(PHRASE_3)
        phrases.add(phraseDto)

        questionDto.getQuestionDetailsDto().setPhrases(phrases)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

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

        result.getQuestionDetails().getPhrases().get(0).getContent() == PHRASE_2
        result.getQuestionDetails().getPhrases().get(0).getWord() == WORD_1
        result.getQuestionDetails().getPhrases().get(1).getContent() == PHRASE_3
        result.getQuestionDetails().getPhrases().get(1).getWord() == WORD_2
    }

    def "update 1 word"() {
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setQuestionDetailsDto(new WordAssignmentQuestionDto())

        and: 'a changed phrase'
        def phrases = new ArrayList<PhraseDto>()
        def phraseDto = new PhraseDto(phrase1)
        phraseDto.setWord(WORD_3)
        phrases.add(phraseDto)
        phraseDto = new PhraseDto(phrase2)
        phrases.add(phraseDto)

        questionDto.getQuestionDetailsDto().setPhrases(phrases)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

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
        result.getQuestionDetails().getPhrases().get(0).getWord() == WORD_3
        result.getQuestionDetails().getPhrases().get(1).getContent() == PHRASE_2
        result.getQuestionDetails().getPhrases().get(1).getWord() == WORD_2
    }

    def "update 2 words"() {
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setQuestionDetailsDto(new WordAssignmentQuestionDto())

        and: 'a changed phrase'
        def phrases = new ArrayList<PhraseDto>()
        def phraseDto = new PhraseDto(phrase1)
        phraseDto.setWord(WORD_2)
        phrases.add(phraseDto)
        phraseDto = new PhraseDto(phrase2)
        phraseDto.setWord(WORD_3)
        phrases.add(phraseDto)

        questionDto.getQuestionDetailsDto().setPhrases(phrases)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

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
        result.getQuestionDetails().getPhrases().get(0).getWord() == WORD_2
        result.getQuestionDetails().getPhrases().get(1).getContent() == PHRASE_2
        result.getQuestionDetails().getPhrases().get(1).getWord() == WORD_3
    }

    def "update phrase content and word"() {
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setQuestionDetailsDto(new WordAssignmentQuestionDto())

        and: 'a changed phrase'
        def phrases = new ArrayList<PhraseDto>()
        def phraseDto = new PhraseDto(phrase1)
        phraseDto.setContent(PHRASE_3)
        phraseDto.setWord(WORD_3)
        phrases.add(phraseDto)
        phraseDto = new PhraseDto(phrase2)
        phrases.add(phraseDto)

        questionDto.getQuestionDetailsDto().setPhrases(phrases)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

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

        result.getQuestionDetails().getPhrases().get(0).getContent() == PHRASE_3
        result.getQuestionDetails().getPhrases().get(0).getWord() == WORD_3
        result.getQuestionDetails().getPhrases().get(1).getContent() == PHRASE_2
        result.getQuestionDetails().getPhrases().get(1).getWord() == WORD_2
    }

    def "cannot update phrase content with blank"() {
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setQuestionDetailsDto(new WordAssignmentQuestionDto())

        and: 'a changed phrase'
        def phrases = new ArrayList<PhraseDto>()
        def phraseDto = new PhraseDto(phrase1)
        phraseDto.setContent("   ")
        phrases.add(phraseDto)
        phraseDto = new PhraseDto(phrase2)
        phrases.add(phraseDto)

        questionDto.getQuestionDetailsDto().setPhrases(phrases)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.EMPTY_PHRASE_CONTENT
    }

    def "cannot update phrase content with empty"() {
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setQuestionDetailsDto(new WordAssignmentQuestionDto())

        and: 'a changed phrase'
        def phrases = new ArrayList<PhraseDto>()
        def phraseDto = new PhraseDto(phrase1)
        phraseDto.setContent(null)
        phrases.add(phraseDto)
        phraseDto = new PhraseDto(phrase2)
        phrases.add(phraseDto)

        questionDto.getQuestionDetailsDto().setPhrases(phrases)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.EMPTY_PHRASE_CONTENT
    }

    def "cannot update word with blank"() {
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setQuestionDetailsDto(new WordAssignmentQuestionDto())

        and: 'a changed phrase'
        def phrases = new ArrayList<PhraseDto>()
        def phraseDto = new PhraseDto(phrase1)
        phraseDto.setWord("   ")
        phrases.add(phraseDto)
        phraseDto = new PhraseDto(phrase2)
        phrases.add(phraseDto)

        questionDto.getQuestionDetailsDto().setPhrases(phrases)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.EMPTY_WORD
    }

    def "cannot update word with empty"() {
        given: "a changed question"
        def questionDto = new QuestionDto(question)
        questionDto.setKey(1)
        questionDto.setTitle(QUESTION_1_TITLE)
        questionDto.setContent(QUESTION_1_CONTENT)
        questionDto.setQuestionDetailsDto(new WordAssignmentQuestionDto())

        and: 'a changed phrase'
        def phrases = new ArrayList<PhraseDto>()
        def phraseDto = new PhraseDto(phrase1)
        phraseDto.setWord(null)
        phrases.add(phraseDto)
        phraseDto = new PhraseDto(phrase2)
        phrases.add(phraseDto)

        questionDto.getQuestionDetailsDto().setPhrases(phrases)

        when:
        questionService.updateQuestion(question.getId(), questionDto)

        then: "exception is thrown"
        def exception = thrown(TutorException)
        exception.getErrorMessage() == ErrorMessage.EMPTY_WORD
    }

    @TestConfiguration
    static class LocalBeanConfiguration extends BeanConfiguration {}
}
