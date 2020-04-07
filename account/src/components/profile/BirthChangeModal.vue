<template>
  <el-dialog
    class="birth-change-modal"
    :title="$t('profile.birthChangeModal.title')"
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
          :label="$t('profile.birthChangeModal.birth')"
        >
          <el-date-picker
            class="year"
            type="year"
            format="yyyy"
            v-model="birth"
            :placeholder="$t('profile.birthChangeModal.year')"
          />
          <el-date-picker
            class="month"
            type="month"
            format="MM"
            v-model="birth"
            :placeholder="$t('profile.birthChangeModal.month')"
          />
          <el-date-picker
            class="day"
            type="date"
            format="dd"
            v-model="birth"
            :placeholder="$t('profile.birthChangeModal.day')"
          />
        </el-form-item>
      </el-form>
    </div>

    <div slot="footer" class="dialog-footer">
      <el-button type="confirm" @click="submit">
        {{ $t('profile.birthChangeModal.submit') }}
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
      birth: '',
    }
  },
  watch: {
    visible() {
      this.birth = this.me.birth
    },
  },
  methods: {
    async submit() {
      try {
        await profileService.changeMyBirth({
          me: this.me,
          birth: this.birth,
        })
        this.$notify.success({
          message: this.$t('profile.birthChangeModal.message.success'),
          position: 'bottom-left',
        })
        this.$emit('changedBirth', this.birth)
      } catch (e) {
        this.$notify.error({
          message: this.$t('profile.birthChangeModal.message.fail'),
          position: 'bottom-left',
        })
      }
    },
  },
}
</script>

<style lang="scss">
.birth-change-modal {
  .el-input__inner {
    text-align: right;
  }
  .year .el-input__inner {
    width: 136px;
  }
  .el-input__icon {
    display: none;
  }
}
</style>
