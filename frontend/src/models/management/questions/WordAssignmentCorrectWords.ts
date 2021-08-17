export default class WordAssignmentCorrectWords {
  id: number | null = null;
  content: string = '';
  word: string = '';

  constructor(jsonObj?: WordAssignmentCorrectWords) {
    if (jsonObj) {
      this.id = jsonObj.id;
      this.content = jsonObj.content;
      this.word = jsonObj.word;
    }
  }

  setAsNew(): void {
    this.id = null;
  }
}
