import StatementQuestionDetails from '@/models/statement/questions/StatementQuestionDetails';
import { QuestionTypes } from '@/services/QuestionHelpers';
import StatementPhrase from '@/models/statement/StatementPhrase';
import { _ } from 'vue-underscore';

export default class WordAssignmentStatementQuestionDetails extends StatementQuestionDetails {
  phrases: StatementPhrase[] = [];

  constructor(jsonObj?: WordAssignmentStatementQuestionDetails) {
    super(QuestionTypes.WordAssignment);
    if (jsonObj) {
      if (jsonObj.phrases) {
        this.phrases = _.shuffle(
          jsonObj.phrases.map(
            (phrase: StatementPhrase) => new StatementPhrase(phrase)
          )
        );
      }
    }
  }
}
