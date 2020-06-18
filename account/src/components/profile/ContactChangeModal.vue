<template>
  <el-dialog
    class="contact-change-modal"
    :title="$t('profile.contactChangeModal.title')"
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
          class="horizon"
          :label="$t('profile.contactChangeModal.contact')"
        >
          <el-select class="country-code" v-model="form.code">
            <el-option
              v-for="code in countryCodes"
              :key="code.dial_code"
              :value="code.dial_code"
              :label="code.dial_code"
            />
          </el-select>
          <el-input
            v-model="form.phone"
            :placeholder="$t('profile.contactChangeModal.contactPlaceholder')"
          />
        </el-form-item>
      </el-form>
    </div>

    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="submit">
        {{ $t('profile.contactChangeModal.submit') }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import dialogMixin from '@/mixins/dialog'
import profileService from '@/services/profile'
import countryCodes from '@/models/countryCodes'

export default {
  mixins: [dialogMixin],
  props: {
    me: Object,
  },
  data() {
    return {
      countryCodes,
      form: {
        code: '+82',
        phone: '',
      },
    }
  },
  watch: {
    visible() {
      const contact = this.me.contact || ''
      this.form.code = contact.replace(/-.*$/, '')
      this.form.phone = contact.replace(/^\+.*?-/, '')
    },
  },
  methods: {
    async submit() {
      const form = {
        mobile: this.form.phone ? `${this.form.code}-${this.form.phone}` : '',
      }
      try {
        await profileService.updateMyProfile(form)
        this.$notify.success({
          message: this.$t('profile.contactChangeModal.message.success'),
          position: 'bottom-left',
          duration: 2000,
        })
        this.$emit('changedContact', form.mobile)
      } catch (e) {
        this.$notify.error({
          message:
            this.$t('profile.contactChangeModal.message.fail') + `\n(${e})`,
          position: 'bottom-left',
          duration: 2000,
        })
      }
    },
  },
}
</script>

<style lang="scss">
#__nuxt .contact-change-modal {
  .country-code {
    width: 117px;
  }
}
</style>
