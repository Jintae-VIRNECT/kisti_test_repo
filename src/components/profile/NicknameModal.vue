<template>
  <el-dialog
    class="nickname-change-modal"
    :title="$t('profile.nicknameChangeModal.title')"
    :visible.sync="visible"
    width="420px"
    :before-close="handleClose"
  >
    <div>
      <p
        v-html="
          $isOnpremise
            ? $t('profile_op.nicknameChangeModal.desc')
            : $t('profile.nicknameChangeModal.desc')
        "
      />
      <el-form
        class="virnect-login-form"
        ref="form"
        :model="form"
        :rules="rules"
        @submit.native.prevent="submit"
      >
        <el-form-item
          :label="$t('profile.nicknameChangeModal.nickname')"
          prop="nickname"
        >
          <el-input v-model="form.nickname" />
        </el-form-item>
      </el-form>
    </div>

    <div slot="footer" class="dialog-footer">
      <el-button
        type="primary"
        @click="submit"
        :disabled="!form.confirmNickname"
      >
        {{ $t('profile.nicknameChangeModal.submit') }}
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
        nickname: '',
        confirmNickname: false,
      },
      rules: {
        nickname: [
          {
            validator: (rule, value, callback) => {
              if (value.length < 1 || value.length > 20) {
                this.form.confirmNickname = false
                callback(
                  new Error(
                    this.$t('profile.nicknameChangeModal.message.caution'),
                  ),
                )
              } else if (/[<>]/.test(value)) {
                this.form.confirmNickname = false
                callback(
                  new Error(
                    this.$t('profile.nicknameChangeModal.message.caution'),
                  ),
                )
              } else {
                this.form.confirmNickname = true
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
      this.form.nickname = this.$props.me.nickname
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
          message: this.$t('profile.nicknameChangeModal.message.success'),
          position: 'bottom-left',
          duration: 2000,
        })
        this.$emit('changedNickname', this.form.nickname)
      } catch (e) {
        this.$notify.error({
          message:
            this.$t('profile.nicknameChangeModal.message.fail') + `\n(${e})`,
          position: 'bottom-left',
          duration: 2000,
        })
      }
    },
  },
}
</script>

<style lang="scss"></style>
