<template>
  <el-dialog
    class="password-change-modal"
    :title="$t('profile.passwordChangeModal.title')"
    :visible.sync="visible"
    width="420px"
    :before-close="handleClose"
  >
    <div>
      <p v-html="$t('profile.passwordChangeModal.desc')"></p>
      <el-form
        class="virnect-login-form"
        ref="form"
        :model="form"
        @submit.native.prevent="submit"
      >
        <el-form-item :label="$t('profile.passwordChangeModal.newPassword')">
          <el-input
            show-password
            v-model="form.password1"
            :placeholder="
              $t('profile.passwordChangeModal.newPasswordPlaceholder')
            "
          />
          <el-input
            show-password
            v-model="form.password2"
            :placeholder="
              $t('profile.passwordChangeModal.newPasswordRepeatPlaceholder')
            "
          />
          <p class="caution">{{ $t('profile.passwordChangeModal.caution') }}</p>
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
        password1: '',
        password2: '',
      },
    }
  },
  watch: {
    visible() {
      this.form.password1 = ''
      this.form.password2 = ''
    },
  },
  methods: {
    validate(password) {
      let typeCount = 0
      if (/[0-9]/.test(password)) typeCount++
      if (/[a-z]/.test(password)) typeCount++
      if (/[A-Z]/.test(password)) typeCount++
      if (/[$.$,$!$@$#$$$%]/.test(password)) typeCount++

      if (typeCount < 3) return false
      if (!/^.{8,20}$/.test(password)) return false
      if (/(.)\1\1\1/.test(password)) return false
      if (/(0123|1234|2345|3456|4567|5678|6789|7890)/.test(password))
        return false
      if (/(0987|9876|8765|7654|6543|5432|4321|3210)/.test(password))
        return false
      return true
    },
    async submit() {
      if (this.form.password1 !== this.form.password2) {
        this.$notify.error({
          message: this.$t('profile.passwordChangeModal.message.wrong'),
          position: 'bottom-left',
          duration: 2000,
        })
      } else if (!this.validate(this.form.password1)) {
        this.$notify.error({
          message: this.$t('profile.passwordChangeModal.message.invalid'),
          position: 'bottom-left',
          duration: 2000,
        })
      } else {
        try {
          const form = {
            password: this.form.password1,
          }
          await profileService.updateMyProfile(form)
          this.$notify.success({
            message: this.$t('profile.passwordChangeModal.message.success'),
            position: 'bottom-left',
            duration: 2000,
          })
          this.$emit('changedPassword')
        } catch (e) {
          this.$notify.error({
            message:
              this.$t('profile.passwordChangeModal.message.fail') + `\n(${e})`,
            position: 'bottom-left',
            duration: 2000,
          })
        }
      }
    },
  },
}
</script>

<style lang="scss">
.password-change-modal {
  .caution {
    margin-top: 4px;
    color: $font-color-desc;
    font-size: 12.6px;
  }
}
</style>
