<template>
  <div class="word-assignment-phrases">
    <v-row>
      <v-col cols="1" offset="9"> Correct Word </v-col>
    </v-row>

    <v-row
      v-for="(phrase, index) in sQuestionDetails.phrases"
      :key="index"
      data-cy="questionPhrasesInput"
    >
      <v-col cols="9">
        <v-textarea
          v-model="phrase.content"
          :label="`Phrase ${index + 1}`"
          :data-cy="`Phrase${index + 1}`"
          rows="1"
          auto-grow
        ></v-textarea>
      </v-col>
      <v-col cols="2">
        <v-textarea
          v-model="phrase.word"
          :label="`Word ${index + 1}`"
          :data-cy="`Word${index + 1}`"
          rows="1"
          auto-grow
        ></v-textarea>
      </v-col>
      <v-col v-if="sQuestionDetails.phrases.length > 1">
        <v-tooltip bottom>
          <template v-slot:activator="{ on }">
            <v-icon
              :data-cy="`Delete${index + 1}`"
              small
              class="ma-1 action-button"
              v-on="on"
              @click="removePhrase(index)"
              color="red"
              >close</v-icon
            >
          </template>
          <span>Remove Phrase</span>
        </v-tooltip>
      </v-col>
    </v-row>

    <v-row>
      <v-btn
        class="ma-auto"
        color="blue darken-1"
        @click="addPhrase"
        data-cy="addPhraseWordAssignment"
        >Add Phrase</v-btn
      >
    </v-row>
  </div>
</template>

<script lang="ts">
import { Component, Model, PropSync, Vue, Watch } from 'vue-property-decorator';
import WordAssignmentQuestionDetails from '@/models/management/questions/WordAssignmentQuestionDetails';
import Phrase from '@/models/management/Phrase';

@Component
export default class WordAssignmentCreate extends Vue {
  @PropSync('questionDetails', { type: WordAssignmentQuestionDetails })
  sQuestionDetails!: WordAssignmentQuestionDetails;

  addPhrase() {
    this.sQuestionDetails.phrases.push(new Phrase());
  }

  removePhrase(index: number) {
    this.sQuestionDetails.phrases.splice(index, 1);
  }
}
</script>
