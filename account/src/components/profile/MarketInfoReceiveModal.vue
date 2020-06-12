<template>
  <el-dialog
    class="market-receive-modal"
    :title="$t('profile.marketInfoReceive.title')"
    :visible.sync="visible"
    width="420px"
    :before-close="handleClose"
  >
    <div>
      <el-form ref="form" :model="form" @submit.native.prevent="submit">
        <el-checkbox v-model="agree">{{
          $t('profile.marketInfoReceive.title')
        }}</el-checkbox>
        <p class="contents">{{ $t('profile.marketInfoReceive.contents') }}</p>
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
        marketInfoReceive: '',
      },
      agree: null,
    }
  },
  watch: {
    visible() {
      this.$props.me.marketInfoReceive === 'ACCEPT'
        ? (this.agree = true)
        : (this.agree = false)
      this.form.marketInfoReceive = this.$props.me.marketInfoReceive
    },
    agree() {
      this.agree === true
        ? (this.form.marketInfoReceive = 'ACCEPT')
        : (this.form.marketInfoReceive = 'REJECT')
    },
  },
  methods: {
    async submit() {
      try {
        await profileService.updateMyProfile(this.form)
        this.$notify.success({
          message: this.$t('profile.marketInfoReceive.message.success'),
          position: 'bottom-left',
          duration: 2000,
        })
        this.$emit('changedMarketInfoReceive', this.form.marketInfoReceive)
      } catch (e) {
        console.error(e)
        this.$notify.error({
          message: this.$t('profile.marketInfoReceive.message.fail'),
          position: 'bottom-left',
          duration: 2000,
        })
      }
    },
  },
}
</script>

<style lang="scss">
#__nuxt .market-receive-modal .el-dialog__body {
  .el-checkbox__label {
    color: #0b1f48;
  }
  .el-form {
    margin: 0;
  }
  .contents {
    padding-left: 27px;
    color: #5e6b81;
    font-size: 12.6px;
  }
}
</style>
