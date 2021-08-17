import StatementCorrectAnswerDetails from '@/models/statement/questions/StatementCorrectAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import CorrectWords from '@/models/management/questions/WordAssignmentCorrectWords';

export default class WordAssignmentStatementCorrectAnswerDetails extends StatementCorrectAnswerDetails {
  public correctWords!: CorrectWords[];

  constructor(jsonObj?: WordAssignmentStatementCorrectAnswerDetails) {
    super(QuestionTypes.WordAssignment);
    if (jsonObj) {
      this.correctWords = jsonObj.correctWords || [];
    }
  }
}
