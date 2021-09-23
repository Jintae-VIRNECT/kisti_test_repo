<template>
  <el-dialog
    class="recovery-emgail-change-modal"
    :title="$t('profile.recoveryEmailChangeModal.title')"
    :visible.sync="visible"
    width="420px"
    :before-close="handleClose"
    :close-on-click-modal="false"
  >
    <div>
      <el-form
        class="virnect-login-form"
        ref="form"
        :model="form"
        :rules="rules"
        @submit.native.prevent="submit"
      >
        <el-form-item
          :label="$t('profile.recoveryEmailChangeModal.recoveryEmail')"
          prop="recoveryEmail"
        >
          <el-input v-model="form.recoveryEmail" :maxlength="50" />
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
      rules: {
        recoveryEmail: [
          {
            validator: (rule, value, callback) => {
              if (
                value.length &&
                !/^[^\s@]+@([^\s@.,]+\.)+[^\s@.,]{2,}$/.test(value)
              ) {
                callback(
                  new Error(
                    this.$t('invalid.format', [
                      this.$t('profile.recoveryEmailChangeModal.email'),
                    ]),
                  ),
                )
              } else {
                callback()
              }
            },
          },
        ],
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
      // 유효성 검사
      try {
        await this.$refs.form.validate()
      } catch (e) {
        return false
      }
      try {
        await profileService.updateMyProfile(this.form)
        this.$notify.success({
          message: this.$t('profile.recoveryEmailChangeModal.message.success'),
          position: 'bottom-left',
          duration: 2000,
        })
        this.$emit('changedRecoveryEmail', this.form.recoveryEmail)
      } catch (e) {
        this.$notify.error({
          message:
            this.$t('profile.recoveryEmailChangeModal.message.fail') +
            `\n(${e})`,
          position: 'bottom-left',
          duration: 2000,
        })
      }
    },
  },
}
</script>

<style lang="scss"></style>
