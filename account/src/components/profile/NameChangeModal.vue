<template>
  <el-dialog
    class="name-change-modal"
    :title="$t('profile.nameChangeModal.title')"
    :visible.sync="visible"
    width="420px"
    :before-close="handleClose"
  >
    <div>
      <p v-html="$t('profile.nameChangeModal.desc')"></p>
      <p class="caution" v-html="$t('profile.nameChangeModal.caution')"></p>
      <el-form
        class="virnect-login-form"
        ref="form"
        :model="form"
        @submit.native.prevent="submit"
      >
        <el-form-item :label="$t('profile.nameChangeModal.lastName')">
          <el-input v-model="form.lastName" />
        </el-form-item>
        <el-form-item :label="$t('profile.nameChangeModal.firstName')">
          <el-input v-model="form.firstName" />
        </el-form-item>
      </el-form>
    </div>

    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="submit">
        {{ $t('profile.nameChangeModal.submit') }}
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
        lastName: '',
        firstName: '',
      },
    }
  },
  watch: {
    visible() {
      this.form.lastName = this.$props.me.lastName
      this.form.firstName = this.$props.me.firstName
    },
  },
  methods: {
    async submit() {
      try {
        await profileService.updateMyProfile(this.form)
        this.$notify.success({
          message: this.$t('profile.nameChangeModal.message.success'),
          position: 'bottom-left',
          duration: 2000,
        })
        this.$emit('changedName', this.form)
      } catch (e) {
        this.$notify.error({
          message: this.$t('profile.nameChangeModal.message.fail') + `\n(${e})`,
          position: 'bottom-left',
          duration: 2000,
        })
      }
    },
  },
}
</script>

<style lang="scss">
.name-change-modal {
  p {
    margin-bottom: 6px;
  }
  .caution {
    color: $font-color-desc;
    font-size: 13px;
  }
}
</style>
