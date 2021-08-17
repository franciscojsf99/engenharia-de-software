import Phrase from '@/models/management/Phrase';
import QuestionDetails from '@/models/management/questions/QuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';

export default class WordAssignmentQuestionDetails extends QuestionDetails {
  phrases: Phrase[] = [new Phrase()];

  constructor(jsonObj?: WordAssignmentQuestionDetails) {
    super(QuestionTypes.WordAssignment);
    if (jsonObj) {
      this.phrases = jsonObj.phrases.map(
        (phrase: Phrase) => new Phrase(phrase)
      );
    }
  }

  setAsNew(): void {
    this.phrases.forEach((phrase) => {
      phrase.id = null;
    });
  }
}
