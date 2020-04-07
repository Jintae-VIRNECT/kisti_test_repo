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
          <el-select class="country-code" v-model="form.countryCode">
            <el-option
              v-for="code in countryCodes"
              :key="code.code"
              :value="code.code"
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
        countryCode: 'KR',
        phone: '',
      },
    }
  },
  watch: {
    visible() {
      this.form.phone = this.me.contact
    },
  },
  methods: {
    async submit() {
      try {
        await profileService.changeMyContact({
          me: this.me,
          contact: this.form.phone,
        })
        this.$notify.success({
          message: this.$t('profile.contactChangeModal.message.success'),
          position: 'bottom-left',
        })
        this.$emit('changedContact', this.form.phone)
      } catch (e) {
        this.$notify.error({
          message: this.$t('profile.contactChangeModal.message.fail'),
          position: 'bottom-left',
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
