import StatementAnswerDetails from '@/models/statement/questions/StatementAnswerDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import WordAssignmentStatementCorrectAnswerDetails from '@/models/statement/questions/WordAssignmentStatementCorrectAnswerDetails';

export default class WordAssignmentStatementAnswerDetails extends StatementAnswerDetails {


  constructor(jsonObj?: WordAssignmentStatementAnswerDetails) {
    super(QuestionTypes.WordAssignment);
    if (jsonObj) {

    }
  }

  isQuestionAnswered(): boolean {
    return true;
  }

  isAnswerCorrect(
    correctAnswerDetails: WordAssignmentStatementCorrectAnswerDetails
  ): boolean {
    return true;
  }
}
