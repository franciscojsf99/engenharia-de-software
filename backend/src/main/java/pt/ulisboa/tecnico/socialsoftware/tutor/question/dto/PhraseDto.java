package pt.ulisboa.tecnico.socialsoftware.tutor.question.dto;


import pt.ulisboa.tecnico.socialsoftware.tutor.question.domain.Phrase;

import java.io.Serializable;

public class PhraseDto implements Serializable {

    private Integer id;
    private String content;
    private String word;

    public PhraseDto() {
    }

    public PhraseDto(Phrase phrase) {
        setId(phrase.getId());
        setContent(phrase.getContent());
        setWord(phrase.getWord());
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    @Override
    public String toString() {
        return "Phrase{" +
                "id=" + id +
                ", content=" + content +
                ", word=" + word +
                '}';
    }

}
