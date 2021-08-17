export default class Phrase {
  id: number | null = null;
  content: string = '';
  word: string = '';

  constructor(jsonObj?: Phrase) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.content = jsonObj.content;
      this.word = jsonObj.word;
    }
  }
}
