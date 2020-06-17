<template>
  <el-dialog
    class="nickname-change-modal"
    :title="$t('profile.nicknameChangeModal.title')"
    :visible.sync="visible"
    width="420px"
    :before-close="handleClose"
  >
    <div>
      <p v-html="$t('profile.nicknameChangeModal.desc')"></p>
      <el-form
        class="virnect-login-form"
        ref="form"
        :model="form"
        @submit.native.prevent="submit"
      >
        <el-form-item :label="$t('profile.nicknameChangeModal.nickname')">
          <el-input v-model="form.nickname" />
        </el-form-item>
      </el-form>
    </div>

    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="submit">
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
