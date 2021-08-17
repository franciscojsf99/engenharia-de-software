import Phrase from '@/models/management/Option';
import AnswerDetails from '@/models/management/questions/AnswerDetails';
import { QuestionTypes, convertToLetter } from '@/services/QuestionHelpers';

export default class WordAssignmentAnswerType extends AnswerDetails {
  phrase!: Phrase;

  constructor(jsonObj?: WordAssignmentAnswerType) {
    super(QuestionTypes.WordAssignment);
    if (jsonObj) {
      this.phrase = new Phrase(jsonObj.phrase);
    }
  }

  isCorrect(): boolean {
    return true;
  }
  answerRepresentation(): string {
    return '';
  }
}
