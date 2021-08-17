package pt.ulisboa.tecnico.socialsoftware.tutor.pap.webservice


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
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExportQuestionWebServiceIT extends SpockTest {
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

    def "export word assignment question as teacher"() {
        given: "login as teacher"
        createdUserLogin(USER_1_EMAIL, USER_1_PASSWORD)

        and: 'prepare request response'
        restClient.handler.failure = {
            resp, reader -> [response:resp, reader:reader]
        }
        restClient.handler.success = {
            resp, reader -> [response:resp, reader:reader]
        }

        when:
        def map = restClient.get(
                path: '/questions/courses/' + course.getId() + '/export',
                requestContentType: "application/json"
        )

        then: "response status is OK"
        assert map['response'].status == 200
        assert map['reader'] != null
    }

    def "export word assignment question as student"() {
        given: "login as student"
        createdUserLogin(USER_2_EMAIL, USER_2_PASSWORD)

        and: 'prepare request response'
        restClient.handler.failure = {
            resp, reader -> [response:resp, reader:reader]
        }
        restClient.handler.success = {
            resp, reader -> [response:resp, reader:reader]
        }

        when:
        def map = restClient.get(
                path: '/questions/courses/' + course.getId() + '/export',
                requestContentType: "application/json"
        )

        then: "response status is 403"
        assert map['response'].status == HttpStatus.SC_FORBIDDEN
    }



    def cleanup() {
        questionService.removeQuestion(question.getId())
        userRepository.deleteById(teacher.getId())
        userRepository.deleteById(student.getId())
        courseExecutionRepository.deleteById(courseExecution.getId())
        courseRepository.deleteById(course.getId())
    }


}