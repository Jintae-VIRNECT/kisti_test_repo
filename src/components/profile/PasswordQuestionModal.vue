<template>
  <el-dialog
    class="password-question-modal"
    :title="$t('profile_op.passwordQuestionModal.title')"
    :visible.sync="visible"
    width="420px"
    :before-close="handleClose"
    :close-on-click-modal="false"
  >
    <div>
      <p v-html="$t('profile_op.passwordQuestionModal.desc')" />
      <el-form
        class="virnect-login-form secession-form"
        ref="form"
        :model="form"
        @submit.native.prevent="submit"
      >
        <el-form-item
          class="must-check"
          :label="$t('profile_op.passwordQuestionModal.question')"
        >
          <el-select
            v-model="form.question"
            :placeholder="
              $t('profile_op.passwordQuestionModal.questionPlaceholder')
            "
          >
            <el-option
              v-for="question in $t(
                'profile_op.passwordQuestionModal.questionList',
              )"
              :key="question"
              :label="question"
              :value="question"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-input
            v-model="form.answer"
            :placeholder="
              $t('profile_op.passwordQuestionModal.answerPlaceholder')
            "
          />
        </el-form-item>
      </el-form>
    </div>

    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="submit">
        {{ $t('profile.passwordChangeModal.submit') }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import dialogMixin from '@/mixins/dialog'
import profileService from '@/services/profile'

export default {
  mixins: [dialogMixin],
  props: {
    me: Object,
  },
  data() {
    return {
      form: {
        question: '',
        answer: '',
      },
    }
  },
  methods: {
    opened() {
      this.form.question = this.me.question
      this.form.answer = this.me.answer
    },
    async submit() {
      try {
        await profileService.updateMyProfile(this.form)

        this.$notify.success({
          message: this.$t('profile_op.passwordQuestionModal.message.success'),
          position: 'bottom-left',
          duration: 2000,
        })
        this.$emit('changedPasswordQuestion', this.form)
      } catch (e) {
        this.$notify.error({
          message:
            this.$t('profile_op.passwordQuestionModal.message.fail') +
            `\n[ERROR CODE: ${e}]`,
          position: 'bottom-left',
          duration: 2000,
        })
      }
    },
  },
}
</script>

<style lang="scss">
.password-question-modal {
  .caution {
    margin-top: 4px;
    color: $font-color-desc;
    font-size: 13px;
  }
}
</style>
