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
        @submit.native.prevent="submit"
      >
        <el-form-item
          class="horizon"
          :label="$t('profile.birthChangeModal.birth')"
        >
          <el-date-picker
            class="year"
            popper-class="year"
            type="year"
            format="yyyy"
            v-model="year"
          />
          <el-date-picker
            class="month"
            popper-class="month"
            type="month"
            format="MM"
            v-model="month"
          />
          <el-date-picker
            class="day"
            popper-class="day"
            type="date"
            format="dd"
            v-model="day"
          />
        </el-form-item>
      </el-form>
    </div>

    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="submit">
        {{ $t('profile.birthChangeModal.submit') }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import dialogMixin from '@/mixins/dialog'
import profileService from '@/services/profile'
import dayjs, { filters } from '@/plugins/dayjs'

export default {
  mixins: [dialogMixin],
  props: {
    me: Object,
  },
  data() {
    return {
      year: '',
      month: '',
      day: '',
    }
  },
  watch: {
    visible() {
      this.year = this.me.birth
      this.month = this.me.birth
      this.day = this.me.birth
    },
    year(newDate) {
      const newYear = dayjs(newDate).year()
      const newMonth = dayjs(newDate).month()
      this.month = dayjs(this.month).year(newYear)
      this.day = dayjs(this.day)
        .year(newYear)
        .month(newMonth)
    },
    month(newDate) {
      const newMonth = dayjs(newDate).month()
      this.day = dayjs(this.day).month(newMonth)
    },
  },
  methods: {
    async submit() {
      const birth = dayjs()
        .year(dayjs(this.year).year())
        .month(dayjs(this.month).month())
        .date(dayjs(this.day).date())
      const form = {
        birth: filters.dateFormat(birth).replace(/\./g, '-'),
      }
      try {
        await profileService.updateMyProfile(form)
        this.$notify.success({
          message: this.$t('profile.birthChangeModal.message.success'),
          position: 'bottom-left',
          duration: 2000,
        })
        this.$emit('changedBirth', birth)
      } catch (e) {
        console.error(e)
        this.$notify.error({
          message: this.$t('profile.birthChangeModal.message.fail'),
          position: 'bottom-left',
          duration: 2000,
        })
      }
    },
  },
}
</script>

<style lang="scss">
.birth-change-modal {
  .el-input {
    &:after {
      position: absolute;
      right: 0;
      color: #c7cfda;
      font-size: 16px;
      line-height: 44px;
    }
  }
  .el-input__icon {
    display: none;
  }
  .year {
    .el-input__inner {
      width: 136px;
    }
  }

  &:lang(ko) {
    .year:after {
      content: '년';
    }
    .month:after {
      content: '월';
    }
    .day:after {
      content: '일';
    }
  }
}
.el-popper {
  &.month,
  &.day {
    .el-date-picker__header-label {
      pointer-events: none;
    }
    .el-picker-panel__icon-btn {
      display: none;
    }
  }
}
</style>
