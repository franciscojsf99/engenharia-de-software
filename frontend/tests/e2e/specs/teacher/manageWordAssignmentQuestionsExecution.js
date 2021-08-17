describe('Manage Word Assignment Questions Walk-through', () => {
  function validateQuestion(
    title,
    content,
    phrases
  ) {
    cy.get('[data-cy="showQuestionDialog"]')
      .should('be.visible')
      .within(($ls) => {
        cy.get('.headline').should('contain', title);
        cy.get('span > p').should('contain', content);

        cy.get('#phrases').children().each(($el, index, $list) => {
            cy.get($el).should('contain', phrases[index][0] + ' -> Word: ' + phrases[index][1]);
        });

      });
  }

  function validateQuestionFull(
      title,
      content,
      phrases
    ) {
      cy.log('Validate question with show dialog. ');

      cy.get('[data-cy="questionTitleGrid"]').first().click();

      validateQuestion(title, content, phrases);

      cy.get('button').contains('close').click();
    }

  before(() => {
    cy.cleanMultipleChoiceQuestionsByName('Cypress Question Example');
    cy.cleanCodeFillInQuestionsByName('Cypress Question Example');
    cy.cleanWordAssignmentQuestionsByName('Cypress Question Example');
  });
  after(() => {
    cy.cleanWordAssignmentQuestionsByName('Cypress Question Example');
  });

  beforeEach(() => {
    cy.demoTeacherLogin();
    cy.server();
    cy.route('GET', '/questions/courses/*').as('getQuestions');
    cy.route('GET', '/topics/courses/*').as('getTopics');
    cy.get('[data-cy="managementMenuButton"]').click();
    cy.get('[data-cy="questionsTeacherMenuButton"]').click();

    cy.wait('@getQuestions').its('status').should('eq', 200);

    cy.wait('@getTopics').its('status').should('eq', 200);
  });

  afterEach(() => {
    cy.logout();
  });

  it('Creates a new word assignment question with 1 phrase', function () {
    cy.get('button').contains('New Question').click();

    cy.get('[data-cy="createOrEditQuestionDialog"]')
      .parent()
      .should('be.visible');

    cy.get('span.headline').should('contain', 'New Question');

    cy.get('[data-cy="questionTypeInput"]')
          .type('word_assignment', { force: true })
          .click({ force: true });

    cy.get(
      '[data-cy="questionTitleTextArea"]'
    ).type('Cypress Question Example - 01', { force: true });
    cy.get(
      '[data-cy="questionQuestionTextArea"]'
    ).type('Cypress Question Example - Content - 01', { force: true });

    cy.get('[data-cy="questionPhrasesInput"')
      .should('have.length', 1)
      .each(($el, index, $list) => {
        cy.get($el).within(($ls) => {
          cy.get(`[data-cy="Phrase${index + 1}"]`).type('Phrase ' + (index + 1));
          cy.get(`[data-cy="Word${index + 1}"]`).type('Word ' + (index + 1));
        });
      });

    cy.route('POST', '/questions/courses/*').as('postQuestion');

    cy.get('button').contains('Save').click();

    cy.wait('@postQuestion').its('status').should('eq', 200);

    cy.get('[data-cy="questionTitleGrid"]')
      .first()
      .should('contain', 'Cypress Question Example - 01');

    validateQuestionFull(
      'Cypress Question Example - 01',
      'Cypress Question Example - Content - 01',
      [['Phrase 1', 'Word 1']]
    );
  });
    it('Can view question (with button)', function () {
        cy.get('tbody tr')
          .first()
          .within(($list) => {
            cy.get('button').contains('visibility').click();
          });

        validateQuestion(
            'Cypress Question Example - 01',
            'Cypress Question Example - Content - 01',
            [['Phrase 1', 'Word 1']]
        );

        cy.get('button').contains('close').click();
    });

    it('Can view question (with click)', function () {
        cy.get('[data-cy="questionTitleGrid"]').first().click();

        validateQuestion(
            'Cypress Question Example - 01',
            'Cypress Question Example - Content - 01',
            [['Phrase 1', 'Word 1']]
        );

        cy.get('button').contains('close').click();
    });

    it('Can update title (with right-click)', function () {
          cy.route('PUT', '/questions/*').as('updateQuestion');

          cy.get('[data-cy="questionTitleGrid"]').first().rightclick();

          cy.get('[data-cy="createOrEditQuestionDialog"]')
            .parent()
            .should('be.visible')
            .within(($list) => {
              cy.get('span.headline').should('contain', 'Edit Question');

              cy.get('[data-cy="questionTitleTextArea"]')
                .clear({ force: true })
                .type('Cypress Question Example - 01 - Edited', { force: true });

              cy.get('button').contains('Save').click();
            });

          cy.wait('@updateQuestion').its('status').should('eq', 200);

          cy.get('[data-cy="questionTitleGrid"]')
            .first()
            .should('contain', 'Cypress Question Example - 01 - Edited');

            validateQuestionFull(
                'Cypress Question Example - 01 - Edited',
                'Cypress Question Example - Content - 01',
                [['Phrase 1', 'Word 1']]
            );
    });

    it('Can update content (with button)', function () {
          cy.route('PUT', '/questions/*').as('updateQuestion');

          cy.get('tbody tr')
            .first()
            .within(($list) => {
              cy.get('button').contains('edit').click();
            });

          cy.get('[data-cy="createOrEditQuestionDialog"]')
            .parent()
            .should('be.visible')
            .within(($list) => {
              cy.get('span.headline').should('contain', 'Edit Question');

              cy.get('[data-cy="questionQuestionTextArea"]')
                .clear({ force: true })
                .type('Cypress New Content For Question!', { force: true });

              cy.get('button').contains('Save').click();
            });

          cy.wait('@updateQuestion').its('status').should('eq', 200);

            validateQuestionFull(
                'Cypress Question Example - 01 - Edited',
                'Cypress New Content For Question!',
                [['Phrase 1', 'Word 1']]
            );
    });

    it('Can update phrase (with right-click)', function () {
          cy.route('PUT', '/questions/*').as('updateQuestion');

          cy.get('[data-cy="questionTitleGrid"]').first().rightclick();

          cy.get('[data-cy="createOrEditQuestionDialog"]')
            .parent()
            .should('be.visible')
            .within(($list) => {
              cy.get('span.headline').should('contain', 'Edit Question');

                cy.get(`[data-cy="Phrase1"]`).clear({ force: true }).type('New Phrase 1', { force: true });
                cy.get(`[data-cy="Word1"]`).clear({ force: true }).type('New Word 1', { force: true });

                cy.get('button').contains('Save').click();
            });

          cy.wait('@updateQuestion').its('status').should('eq', 200);

          cy.get('[data-cy="questionTitleGrid"]')
            .first()
            .should('contain', 'Cypress Question Example - 01 - Edited');

            validateQuestionFull(
                'Cypress Question Example - 01 - Edited',
                'Cypress New Content For Question!',
                [['New Phrase 1', 'New Word 1']]
            );

    });

    it('Can update phrase (with button)', function () {
          cy.route('PUT', '/questions/*').as('updateQuestion');

          cy.get('tbody tr')
            .first()
            .within(($list) => {
              cy.get('button').contains('edit').click();
            });

          cy.get('[data-cy="createOrEditQuestionDialog"]')
            .parent()
            .should('be.visible')
            .within(($list) => {
              cy.get('span.headline').should('contain', 'Edit Question');

                 cy.get(`[data-cy="Phrase1"]`).clear({ force: true }).type('Phrase 1', { force: true });
                 cy.get(`[data-cy="Word1"]`).clear({ force: true }).type('Word 1', { force: true });

              cy.get('button').contains('Save').click();
            });

          cy.wait('@updateQuestion').its('status').should('eq', 200);

            validateQuestionFull(
                'Cypress Question Example - 01 - Edited',
                'Cypress New Content For Question!',
                [['Phrase 1', 'Word 1']]
            );
    });

    it('Can duplicate question', function () {
        cy.get('tbody tr')
          .first()
          .within(($list) => {
            cy.get('button').contains('cached').click();
          });

        cy.get('[data-cy="createOrEditQuestionDialog"]')
          .parent()
          .should('be.visible');

        cy.get('span.headline').should('contain', 'New Question');

        cy.get('[data-cy="questionTitleTextArea"]')
          .should('have.value', 'Cypress Question Example - 01 - Edited')
          .type('{end} - DUP', { force: true });
        cy.get('[data-cy="questionQuestionTextArea"]').should('have.value',
          'Cypress New Content For Question!'
        );

        cy.get('[data-cy="questionPhrasesInput"')
              .should('have.length', 1)
              .each(($el, index, $list) => {
                cy.get($el).within(($ls) => {
                  cy.get(`[data-cy="Phrase${index + 1}"]`).should('have.value', 'Phrase ' + (index + 1));
                  cy.get(`[data-cy="Word${index + 1}"]`).should('have.value', 'Word ' + (index + 1));
              });
        });

        cy.route('POST', '/questions/courses/*').as('postQuestion');

        cy.get('button').contains('Save').click();

        cy.wait('@postQuestion').its('status').should('eq', 200);

        cy.get('[data-cy="questionTitleGrid"]')
          .first()
          .should('contain', 'Cypress Question Example - 01 - Edited - DUP');

        validateQuestionFull(
          'Cypress Question Example - 01 - Edited - DUP',
          'Cypress New Content For Question!',
          [['Phrase 1', 'Word 1']]
        );
    });

    it('Can delete created question', function () {
        cy.route('DELETE', '/questions/*').as('deleteQuestion');
        cy.get('tbody tr')
            .first()
            .within(($list) => {
              cy.get('button').contains('delete').click();
            });

        cy.wait('@deleteQuestion').its('status').should('eq', 200);
    });

    it('Creates a new word assignment question with 2 phrases', function () {
          cy.get('button').contains('New Question').click();

          cy.get('[data-cy="createOrEditQuestionDialog"]')
            .parent()
            .should('be.visible');

          cy.get('span.headline').should('contain', 'New Question');

          cy.get('[data-cy="questionTypeInput"]')
                .type('word_assignment', { force: true })
                .click({ force: true });

          cy.get(
            '[data-cy="questionTitleTextArea"]'
          ).type('Cypress Question Example - 02', { force: true });
          cy.get(
            '[data-cy="questionQuestionTextArea"]'
          ).type('Cypress Question Example - Content - 02', { force: true });

          cy.get('[data-cy="addPhraseWordAssignment"]').click({ force: true });

          cy.get('[data-cy="questionPhrasesInput"')
            .should('have.length', 2)
            .each(($el, index, $list) => {
              cy.get($el).within(($ls) => {
                cy.get(`[data-cy="Phrase${index + 1}"]`).type('Phrase ' + (index + 1));
                cy.get(`[data-cy="Word${index + 1}"]`).type('Word ' + (index + 1));
              });
            });

          cy.route('POST', '/questions/courses/*').as('postQuestion');

          cy.get('button').contains('Save').click();

          cy.wait('@postQuestion').its('status').should('eq', 200);

          cy.get('[data-cy="questionTitleGrid"]')
            .first()
            .should('contain', 'Cypress Question Example - 02');

          validateQuestionFull(
            'Cypress Question Example - 02',
            'Cypress Question Example - Content - 02',
            [['Phrase 1', 'Word 1'], ['Phrase 2', 'Word 2']]
          );
        });

});
