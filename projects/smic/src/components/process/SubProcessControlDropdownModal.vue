<template lang="pug">
  .process-new-modal(@click="e => e.stopPropagation()")
    el-dialog(
      :visible.sync="processModal"
      width="540px"
      height="50vh"
      @open="handleOpen"
      @close="handleCancel"
      :destroy-on-close="true")
      template(slot="title")
        span.process-new-modal__header-title 세부공정 편집
      .process-new-modal__body
        el-form(ref="form" :model="form" :rules="rules" label-position="left" lable-width="120px")
          .section.border-divider
            label 세부공정 이름
            .value
              span {{form.name}}
          .detail-process-list
            .detail-process-item
              .section
                el-form-item.is-required(label="세부공정 일정")
                  el-date-picker(
                    v-model="date"
                    type="datetimerange"
                    start-placeholder="시작일"
                    end-placeholder="마감일"
                    format="yyyy. MM. dd.  HH:mm"
                    :picker-options="pickerOptions"
                  )
              .section
                el-form-item.is-required(label="담당자")
                  el-select.auth-select( v-model='form.workerUUID' placeholder='Select')
                    el-option(v-for='item in memberList' :key='item.uuid' :label='item.name' :value='item.uuid')
      span.dialog-footer.section(slot='footer')
        el-button(type='primary' @click='handleConfirm') 완료

</template>

<script>
import filters from '@/mixins/filters'
import dayjs from '@/plugins/dayjs'

export default {
  mixins: [filters],
  props: {
    toggleProcessModal: Boolean,
    processId: String,
    target: Object,
    modalType: String,
  },
  data() {
    return {
      processModal: this.toggleProcessModal,
      form: this.target,
      date: [],
      // 날짜
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() < dayjs._().subtract(1, 'day')
        },
      },
    }
  },
  watch: {
    toggleProcessModal() {
      this.processModal = this.$props.toggleProcessModal
    },
  },
  computed: {
    memberList() {
      return this.$store.getters.memberList
    },
  },
  methods: {
    handleConfirm() {
      if (this.$props.modalType === 'edit') {
        this.handleEditConfirm()
      } else {
        this.handleCreateConfirm()
      }
    },
    async handleEditConfirm() {
      if (!this.validate(this.form)) {
        return false
      }
      this.form.startDate = dayjs.filters.dayJS_ConvertUTCTimeFormat(
        this.date[0],
      )
      this.form.endDate = dayjs.filters.dayJS_ConvertUTCTimeFormat(this.date[1])

      await this.$confirm(
        `공정 정보를 편집하시겠습니까? 변경된 정보로 공정 보고를 받습니다.`,
        '공정 편집 완료',
        {
          confirmButtonText: '확인',
        },
      )
      try {
        await this.$store.dispatch('updateSubProcess', this.form)
        this.handleCancel()
        this.$store.commit('SET_SUB_PROCESS_LIST', [])
        this.$store.dispatch('getSubProcessList', { processId: this.processId })
      } catch (e) {
        console.log(e)
        this.$alert(`서버에러`, {
          confirmButtonText: '확인',
        })
      }
    },
    handleCancel() {
      this.$emit('onToggleProcessModal', false)
      document.querySelector('body').style = ''
    },
    async handleOpen() {
      document.querySelector('body').style = 'overflow-y: hidden;'
      this.date = [
        dayjs.filters.dayJS_ConvertLocalTime(this.target.startDate),
        dayjs.filters.dayJS_ConvertLocalTime(this.target.endDate),
      ]
    },
    validate(form) {
      if (!form.date.length) {
        this.$alert(`세부공정 일정을 지정하여야 합니다.`, {
          confirmButtonText: '확인',
        })
        return false
      } else if (!form.workerUUID) {
        this.$alert(`세부공정 담당자를 지정하여야 합니다.`, {
          confirmButtonText: '확인',
        })
        return false
      }
      return true
    },
  },
}
</script>
