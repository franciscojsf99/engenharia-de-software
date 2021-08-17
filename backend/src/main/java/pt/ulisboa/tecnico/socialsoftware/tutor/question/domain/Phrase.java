package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.DomainEntity;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.PhraseDto;

import javax.persistence.*;

@Entity
@Table(name = "phrases")
public class Phrase implements DomainEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String word;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_details_id")
    private WordAssignmentQuestion questionDetails;

    public Phrase() {
        super();
    }

    public Phrase(PhraseDto phraseDto) {
        setContent(phraseDto.getContent());
        setWord(phraseDto.getWord());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return this.content;
    }

    public void setContent(String content) {
        checkContent(content);
        this.content = content;
    }

    private void checkContent(String content) {
        if (content == null || content.isBlank()) {
            throw new TutorException(ErrorMessage.EMPTY_PHRASE_CONTENT);
        }
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        checkWord(word);
        this.word = word;
    }

    private void checkWord(String word) {
        if (word == null || word.isBlank()) {
            throw new TutorException(ErrorMessage.EMPTY_WORD);
        }
    }

    public void setQuestionDetails(WordAssignmentQuestion question) {
        this.questionDetails = question;
        question.addPhrase(this);
    }

    public void remove() {
        this.content = null;
        this.word = null;
        this.questionDetails = null;
    }

    public WordAssignmentQuestion getQuestionDetails() {
        return questionDetails;
    }

    @Override
    public String toString() {
        return "Phrase{" +
                "id=" + id +
                ", content=" + content +
                ", word=" + word +
                '}';
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitPhrase(this);
    }
}
