export default class StatementPhrase {
  phraseId!: number;
  content!: string;
  word!: string;

  constructor(jsonObj?: StatementPhrase) {
    if (jsonObj) {
      this.phraseId = jsonObj.phraseId;
      this.content = jsonObj.content;
      this.word = jsonObj.word;
    }
  }
}
