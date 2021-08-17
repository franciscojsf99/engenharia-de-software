package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;

import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Question;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.QuestionDetails;
import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.WordAssignmentQuestion;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class WordAssignmentQuestionDto extends QuestionDetailsDto {

    private List<PhraseDto> phrases = new ArrayList<>();

    public WordAssignmentQuestionDto() {
    }

    public WordAssignmentQuestionDto(WordAssignmentQuestion question) {
        this.phrases = question.getPhrases().stream().map(PhraseDto::new).collect(Collectors.toList());
    }

    public List<PhraseDto> getPhrases() {
        return phrases;
    }

    public void setPhrases(List<PhraseDto> phrases) {
        this.phrases = phrases;
    }

    @Override
    public QuestionDetails getQuestionDetails(Question question) {
        return new WordAssignmentQuestion(question, this);
    }

    @Override
    public void update(WordAssignmentQuestion question) {
        question.update(this);
    }


}
