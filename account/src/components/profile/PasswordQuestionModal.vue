<template>
  <el-dialog
    class="password-question-modal"
    :title="$t('profile_op.passwordQuestionModal.title')"
    :visible.sync="visible"
    width="420px"
    :before-close="handleClose"
  >
    <div>
      <p v-html="$t('profile_op.passwordQuestionModal.desc')" />
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
      form: {},
    }
  },
  methods: {
    submit() {},
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
