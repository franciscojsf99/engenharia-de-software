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
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.PhraseDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.WordAssignmentQuestionDto
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CreateQuestionWebServiceIT extends SpockTest{

    @LocalServerPort
    private int port

    def course
    def courseExecution
    def teacher

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

    }

    def "create a question"() {
        given: "login as teacher"
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        and: "a questionDto"
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
        def ow = new ObjectMapper().writer().withDefaultPrettyPrinter()
        def response = restClient.post(
                path: '/questions/courses/' + course.getId(),
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

        result.size() == 2L

        result.get(0).content == PHRASE_1
        result.get(0).word == WORD_1
        result.get(1).content == PHRASE_2
        result.get(1).word == WORD_2

        cleanup:
        questionService.removeQuestion(response.data.id)
    }

    def "create a question without login"() {
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
        def ow = new ObjectMapper().writer().withDefaultPrettyPrinter()
        restClient.post(
                path: '/questions/courses/' + course.getId(),
                body: ow.writeValueAsString(questionDto),
                requestContentType: 'application/json',
        )

        then: "the request returns 403"
        def error = thrown(HttpResponseException)
        error.response.status == HttpStatus.SC_FORBIDDEN

    }

    def cleanup() {
        userRepository.deleteById(teacher.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }

}
