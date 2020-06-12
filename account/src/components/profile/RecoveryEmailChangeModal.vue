<template>
  <el-dialog
    class="recovery-emgail-change-modal"
    :title="$t('profile.recoveryEmailChangeModal.title')"
    :visible.sync="visible"
    width="420px"
    :before-close="handleClose"
  >
    <div>
      <el-form
        class="virnect-login-form"
        ref="form"
        :model="form"
        @submit.native.prevent="submit"
      >
        <el-form-item
          :label="$t('profile.recoveryEmailChangeModal.recoveryEmail')"
        >
          <el-input v-model="form.recoveryEmail" />
        </el-form-item>
      </el-form>
    </div>

    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="submit">
        {{ $t('profile.recoveryEmailChangeModal.submit') }}
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
        recoveryEmail: '',
      },
    }
  },
  watch: {
    visible() {
      this.form.recoveryEmail = this.$props.me.recoveryEmail
    },
  },
  methods: {
    async submit() {
      try {
        await profileService.updateMyProfile(this.form)
        this.$notify.success({
          message: this.$t('profile.recoveryEmailChangeModal.message.success'),
          position: 'bottom-left',
          duration: 2000,
        })
        this.$emit('changedRecoveryEmail', this.form.recoveryEmail)
      } catch (e) {
        console.error(e)
        this.$notify.error({
          message: this.$t('profile.recoveryEmailChangeModal.message.fail'),
          position: 'bottom-left',
          duration: 2000,
        })
      }
    },
  },
}
</script>

<style lang="scss"></style>
