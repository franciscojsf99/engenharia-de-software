package pt.ulisboa.tecnico.socialsoftware.tutor.pap.webservice

import com.fasterxml.jackson.databind.ObjectMapper
import groovyx.net.http.HttpResponseException
import groovyx.net.http.RESTClient
import org.apache.http.HttpStatus
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import pt.ulisboa.tecnico.socialsoftware.tutor.SpockTest
import pt.ulisboa.tecnico.socialsoftware.tutor.auth.domain.AuthUser
import pt.ulisboa.tecnico.socialsoftware.tutor.execution.domain.CourseExecution
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Course
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Phrase
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.WordAssignmentQuestion
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.PhraseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.WordAssignmentQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UpdateQuestionWebServiceIT extends SpockTest {
    @LocalServerPort
    private int port

    def course
    def courseExecution
    def teacher
    def student
    def question
    def phrase1
    def phrase2

    def setup() {
        given: "a client, a course and a teacher"
        restClient = new RESTClient("http://localhost:" + port)

        course = new Course(COURSE_1_NAME, Course.Type.EXTERNAL)
        courseRepository.save(course)
        courseExecution = new CourseExecution(course, COURSE_1_ACRONYM, COURSE_1_ACADEMIC_TERM, Course.Type.EXTERNAL, LOCAL_DATE_TOMORROW)
        courseExecutionRepository.save(courseExecution)

        teacher = new User(USER_1_NAME, USER_1_EMAIL, USER_1_EMAIL,
                User.Role.TEACHER, false, AuthUser.Type.TECNICO)
        teacher.authUser.setPassword(passwordEncoder.encode(USER_1_PASSWORD))
        teacher.addCourse(courseExecution)
        courseExecution.addUser(teacher)
        userRepository.save(teacher)


        student = new User(USER_2_NAME, USER_2_EMAIL, USER_2_EMAIL,
                User.Role.STUDENT, false, AuthUser.Type.TECNICO)
        student.authUser.setPassword(passwordEncoder.encode(USER_2_PASSWORD))
        student.addCourse(courseExecution)
        courseExecution.addUser(student)
        userRepository.save(student)

        given: "create a question"
        question = new Question()
        question.setCourse(course)
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

    def "update word assignment question"() {
        given: "login as teacher"
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        and: "a changed question"
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
        def ow = new ObjectMapper().writer().withDefaultPrettyPrinter()
        def response = restClient.put(
                path: '/questions/' + question.getId(),
                body: ow.writeValueAsString(questionDto),
                requestContentType: 'application/json'
        )

        then:
        response != null
        response.status == 200

        response.data != null
        response.data.id != null
        response.data.status == Question.Status.AVAILABLE.name()
        response.data.title == QUESTION_1_TITLE
        response.data.content == QUESTION_1_CONTENT
        response.data.image == null
        response.data.questionDetailsDto.type == "word_assignment"

        def result = response.data.questionDetailsDto.phrases

        result.size() == 3L

        result.get(0).content == PHRASE_1
        result.get(0).word == WORD_1
        result.get(1).content == PHRASE_2
        result.get(1).word == WORD_2
        result.get(2).content == PHRASE_3
        result.get(2).word == WORD_3
    }

    def "update word assignment question without login"() {
        given: "login as student"
        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)

        and: "a changed question"
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
        def ow = new ObjectMapper().writer().withDefaultPrettyPrinter()
        restClient.put(
                path: '/questions/' + question.getId() ,
                body: ow.writeValueAsString(questionDto),
                requestContentType: 'application/json',
        )

        then: "the request returns 403"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN

    }


    def cleanup() {
        questionService.removeQuestion(question.getId())
        userRepository.deleteById(teacher.getId())
        userRepository.deleteById(student.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }
}
