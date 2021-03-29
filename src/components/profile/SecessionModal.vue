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
      <p class="caution" v-html="$t('profile.secession.caution')"></p>
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
            show-password
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
            />
            <el-option :label="$t('profile.secession.etc')" value="etc" />
          </el-select>
          <el-input
            v-if="form.secessionReason === 'etc'"
            v-model="form.etcReason"
            :placeholder="$t('profile.secession.secessionReasonEtc')"
          />
        </el-form-item>
      </el-form>
      <div class="secession-notice">
        <vue-markdown :source="$t('secession-notice.md')" />
      </div>

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
import VueMarkdown from 'vue-markdown'

export default {
  mixins: [dialogMixin],
  components: {
    VueMarkdown,
  },
  props: {
    me: Object,
  },
  data() {
    return {
      form: {
        password: '',
        secessionReason: '',
        etcReason: '',
      },
      agree: false,
    }
  },
  methods: {
    opened() {
      this.form = {
        password: '',
        secessionReason: '',
        etcReason: '',
      }
      this.agree = false
    },
    async submit() {
      const form = {
        email: this.form.email,
        policyAssigned: this.agree,
        password: this.form.password,
        reason:
          this.form.secessionReason === 'etc'
            ? this.form.etcReason
            : this.form.secessionReason,
      }
      try {
        await profileService.secession(form)
      } catch (e) {
        const message = /^Error: (5001)/.test(e)
          ? this.$t('profile.secession.message.fail')
          : e
        this.$notify.error({
          message,
          position: 'bottom-left',
          duration: 2000,
        })
      }
    },
  },
}
</script>

<style lang="scss">
#__nuxt .secession-modal .el-dialog__body {
  .caution {
    margin-top: 4px;
    color: $color-danger;
    font-size: 11px;
  }
  .secession-form {
    margin: 24px 0;
    .el-select + .el-input {
      margin-top: 8px;
    }
  }
  .secession-notice {
    width: 360px;
    height: 200px;
    margin: 16px 0;
    padding: 6px;
    border: solid 1px #e6e9ee;
    border-radius: 3px;
  }
  .secession-notice > div {
    width: 100%;
    height: 100%;
    padding: 6px 10px;
    overflow: auto;
    color: $font-color-desc;
    font-size: 12px;

    h2 {
      margin-bottom: 12px;
      color: $font-color-content;
      font-size: 13px;
    }
    h3 {
      margin: 8px 0 4px;
      color: $font-color-content;
    }
    ol {
      counter-reset: unit;
      & > li:before {
        content: counter(unit, decimal) '. ';
        counter-increment: unit;
      }
    }
    ul li:before {
      content: '- ';
    }
  }
}
</style>
