<template>
  <el-dialog
    class="secession-modal"
    :title="$t('profile.secession.title')"
    :visible.sync="visible"
    width="420px"
    :before-close="handleClose"
  >
    <div>
      <p class="mail-box" v-html="me.email"></p>
      <el-form
        class="virnect-login-form secession-form"
        ref="form"
        :model="form"
        @submit.native.prevent="submit"
      >
        <el-form-item
          class="must-check"
          :label="$t('profile.secession.password')"
        >
          <el-input
            :placeholder="$t('profile.secession.passwordDesk')"
            v-model="form.password"
          />
        </el-form-item>
        <el-form-item
          class="must-check"
          :label="$t('profile.secession.secessionReason')"
        >
          <el-select
            :placeholder="$t('profile.secession.secessionReasonDesk')"
            v-model="form.secessionReason"
          >
            <el-option
              v-for="item in $t('profile.secession.secessionSelect')"
              :key="item"
              :label="item"
              :value="item"
            ></el-option>
          </el-select>
          <!-- <el-input
            :placeholder="$t('profile.secession.passwordDesk')"
            v-model="form.secessionReason"
          /> -->
        </el-form-item>
      </el-form>
      <dl class="secession-notice">
        <dt>{{ $t('profile.secession.notice') }}</dt>
        <dd>
          <ul>
            <li
              v-for="(notice, idx) of $t('profile.secession.noticeLists')"
              :key="idx"
            >
              <p>{{ notice }}</p>
            </li>
          </ul>
        </dd>
      </dl>

      <el-checkbox v-model="agree">{{
        $t('profile.secession.done')
      }}</el-checkbox>
    </div>

    <div slot="footer" class="dialog-footer">
      <el-button type="primary" :disabled="!agree" @click="submit">
        {{ $t('profile.secession.title') }}
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
        password: '',
        secessionReason: '',
      },
      agree: false,
    }
  },
  watch: {
    // visible() {
    //   this.$props.me.marketInfoReceive === 'ACCEPT'
    //     ? (this.agree = true)
    //     : (this.agree = false)
    //   this.form.marketInfoReceive = this.$props.me.marketInfoReceive
    // },
    // agree() {
    //   this.agree === true
    //     ? (this.form.marketInfoReceive = 'ACCEPT')
    //     : (this.form.marketInfoReceive = 'REJECT')
    // },
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
        this.$notify.error({
          message:
            this.$t('profile.marketInfoReceive.message.fail') + `\n(${e})`,
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
  .mail-box {
    padding: 28px 0;
    text-align: center;
    background-color: #f5f7fa;
  }
  .el-checkbox__label {
    color: #0b1f48;
  }
  .el-form {
    margin: 0;
  }
  .contents {
    padding-left: 27px;
    color: #5e6b81;
    font-size: 13px;
  }
}
</style>
