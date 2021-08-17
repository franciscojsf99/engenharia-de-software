package pt.ulisboa.tecnico.socialsoftware.tutor.question.domain;

import pt.ulisboa.tecnico.socialsoftware.tutor.answer.dto.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.ErrorMessage;
import pt.ulisboa.tecnico.socialsoftware.tutor.exceptions.TutorException;
import pt.ulisboa.tecnico.socialsoftware.tutor.impexp.domain.Visitor;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.Updator;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.PhraseDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.QuestionDetailsDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.dto.WordAssignmentQuestionDto;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@DiscriminatorValue(Question.QuestionTypes.WORD_ASSIGNMENT_QUESTION)
public class WordAssignmentQuestion extends QuestionDetails {

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "questionDetails", fetch = FetchType.LAZY, orphanRemoval = true)
    private List<Phrase> phrases = new ArrayList<>();

    public WordAssignmentQuestion() {
        super();
    }

    public WordAssignmentQuestion(Question question, WordAssignmentQuestionDto questionDto) {
        super(question);
        setPhrases(questionDto.getPhrases());
    }

    public List<Phrase> getPhrases() {
        return phrases;
    }

    public void setPhrases(List<PhraseDto> phrases) {
        checkPhrases(phrases);

        this.phrases.clear();

        for (PhraseDto phraseDto : phrases) {
            new Phrase(phraseDto).setQuestionDetails(this);
        }
    }

    private void checkPhrases(List<PhraseDto> phrases) {
        List<PhraseDto> nrPhrases = phrases.stream().collect(Collectors.toList());
        if (nrPhrases.size() < 1) {
            throw new TutorException(ErrorMessage.AT_LEAST_ONE_PHRASE_NEEDED);
        }
    }

    public void update(WordAssignmentQuestionDto questionDto) {
        setPhrases(questionDto.getPhrases());
    }



    public void addPhrase(Phrase phrase) {
        phrases.add(phrase);
    }

    @Override
    public void accept(Visitor visitor) {
        visitor.visitQuestionDetails(this);
    }

    @Override
    public void delete() {
        super.delete();
        for(Phrase phrase : this.phrases) {
            phrase.remove();
        }
        this.phrases.clear();
    }

    @Override
    public QuestionDetailsDto getQuestionDetailsDto() {
        return new WordAssignmentQuestionDto(this);
    }

    public void visitPhrases(Visitor visitor) {
        for (Phrase phrase : this.getPhrases()) {
            phrase.accept(visitor);
        }
    }


    @Override
    public String toString() {
        return "WordAssignmentQuestion{" +
                "phrases=" + phrases +
                '}';
    }

    @Override
    public CorrectAnswerDetailsDto getCorrectAnswerDetailsDto() {
        return null;
    }

    @Override
    public StatementQuestionDetailsDto getStatementQuestionDetailsDto() {
        return null;
    }

    @Override
    public StatementAnswerDetailsDto getEmptyStatementAnswerDetailsDto() {
        return null;
    }

    @Override
    public AnswerDetailsDto getEmptyAnswerDetailsDto() {
        return null;
    }

    @Override
    public void update(Updator updator) {
        updator.update(this);
    }

    @Override
    public String getCorrectAnswerRepresentation() {
        StringBuilder string = new StringBuilder();
        for (Phrase phrase : getPhrases()) {
            string.append(String.format("%s, %s\n", phrase.getContent(), phrase.getWord()));
        }
        return string.toString();
    }

    @Override
    public String getAnswerRepresentation(List<Integer> selectedIds) {
        return null;
    }
}
