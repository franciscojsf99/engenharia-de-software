<template>
  <ul id="phrases">
    <li v-for="phrase in questionDetails.phrases" :key="phrase.id">
      <span
        v-html="
          convertMarkDown(
            phrase.content + ' -> **Word**: ' + phrase.word
          )
        "
        v-bind:class="['font-weight-bold']"
      />
    </li>
  </ul>
</template>

<script lang="ts">
import { Component, Vue, Prop } from 'vue-property-decorator';
import { convertMarkDown } from '@/services/ConvertMarkdownService';
import Question from '@/models/management/Question';
import Image from '@/models/management/Image';
import WordAssignmentQuestionDetails from '@/models/management/questions/WordAssignmentQuestionDetails';
import WordAssignmentAnswerDetails from '@/models/management/questions/WordAssignmentAnswerDetails';

@Component
export default class WordAssignmentView extends Vue {
  @Prop() readonly questionDetails!: WordAssignmentQuestionDetails;
  @Prop() readonly answerDetails?: WordAssignmentAnswerDetails;

  convertMarkDown(text: string, image: Image | null = null): string {
    return convertMarkDown(text, image);
  }
}
</script>
