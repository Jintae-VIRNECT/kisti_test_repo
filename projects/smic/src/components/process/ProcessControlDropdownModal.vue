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
        span.process-new-modal__header-title {{modalType !== "edit" ? '신규 공정 등록' : '공정 편집'}}
      .process-new-modal__body
        el-form(ref="form" :model="form" :rules="rules" label-position="left" lable-width="120px")
          .section.border-divider
            label 공정 이름
            .value
              span {{form.name}}
          .section.border-divider
            el-form-item(label="공정 일정" prop="date")
              el-date-picker(
                v-model="form.date"
                type="datetimerange"
                start-placeholder="시작일"
                end-placeholder="마감일"
                format="yyyy. MM. dd.  HH:mm"
                :picker-options="pickerOptions"
                @focus="onProcessDateClick"
              )
            el-form-item(label="공정 담당자")
              el-select.auth-select(v-model='form.ownerUUID' placeholder='Select')
                el-option(v-for='item in memberList' :key='item.uuid' :label='item.name' :value='item.uuid')
              span.description 공정 담당자 설정 시 공정 내 전체 세부공정의 담당자로 지정됩니다
            el-form-item(label="공정 위치")
              el-input(placeholder='공정 위치를 입력해주세요' v-model='form.position')
              span.description 담당자에게 공정 진행 위치를 안내합니다.
          .section.border-divider
            label 세부공정 목록
            .value
              .number-label {{ form.subProcessList.length }}
          .detail-process-list
            .detail-process-item(v-for="(sub, index) in form.subProcessList")
              .section.title
                label {{ sub.priority | leftZeroPad }}.
                .value
                  span {{ sub.name }}
              .section
                el-form-item.is-required(label="세부공정 일정")
                  el-date-picker(
                    v-model="sub.date"
                    type="datetimerange"
                    start-placeholder="시작일"
                    end-placeholder="마감일"
                    format="yyyy. MM. dd.  HH:mm"
                    :picker-options="pickerOptions"
                  )
                el-form-item.is-required(label="담당자")
                  el-select.auth-select(v-model='sub.workerUUID' placeholder='Select')
                    el-option(v-for='item in memberList' :key='item.uuid' :label='item.name' :value='item.uuid')
              el-divider
              
      span.dialog-footer.section(slot='footer')
        el-button(type='primary' @click='handleConfirm') 완료

</template>

<script>
import filters from '@/mixins/filters'
import dayjs from '@/plugins/dayjs'
import cloneDeep from 'lodash.clonedeep'

export default {
  mixins: [filters],
  props: {
    toggleProcessModal: Boolean,
    target: Object,
    modalType: String,
  },
  data() {
    return {
      processModal: this.toggleProcessModal,
      form: {
        id: null,
        name: null,
        ownerUUID: null,
        position: null,
        date: [],
        subProcessList: [],
      },
      rules: {
        date: [
          {
            required: true,
            trigger: 'blur',
            message: '공정 일정을 지정하여야 합니다.',
          },
        ],
      },
      subWorkerSelectedText: '세부공정 별 담당자가 지정되었습니다.',
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
    'form.date'(val) {
      // 일괄수정
      this.form.subProcessList.forEach(sub => {
        if (!sub.date.length || !this.date) sub.date = val
      })
    },
    'form.ownerUUID'(val) {
      // 일괄수정
      this.form.subProcessList.forEach(sub => {
        if (val !== this.subWorkerSelectedText) sub.workerUUID = val
      })
    },
    'form.subProcessList': {
      deep: true,
      handler(list) {
        list.forEach(sub => {
          // 공정 일정 자동 조정
          if (this.form.date[0] > sub.date[0]) {
            this.form.date = [sub.date[0], this.form.date[1]]
          }
          if (this.form.date[1] < sub.date[1]) {
            this.form.date = [this.form.date[0], sub.date[1]]
          }
          // 세부공정 담당자 지정시 공정담당자 비활성화
          if (sub.workerUUID !== this.form.ownerUUID) {
            this.form.ownerUUID = this.subWorkerSelectedText
          }
        })
      },
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
    onProcessDateClick() {
      this.form.date = []
    },
    async handleCreateConfirm() {
      const form = cloneDeep(this.form)
      if (!this.validate(form)) {
        return false
      }
      // post form
      form.contentUUID = form.id
      delete form.id
      form.startDate = dayjs.filters.dayJS_ConvertUTCTimeFormat(form.date[0])
      form.endDate = dayjs.filters.dayJS_ConvertUTCTimeFormat(form.date[1])
      delete form.date
      form.subProcessList.forEach(sub => {
        sub.startDate = dayjs.filters.dayJS_ConvertUTCTimeFormat(sub.date[0])
        sub.endDate = dayjs.filters.dayJS_ConvertUTCTimeFormat(sub.date[1])
        delete sub.date
      })
      if (this.form.ownerUUID === this.subWorkerSelectedText) {
        form.ownerUUID = null
      }
      try {
        await this.$store.dispatch('createProcess', form)
        // done
        if (this.modalType === 'replace') {
          this.$alert(
            `입력하신 정보로 공정을 추가 생성되었습니다.<br>
          추가된 공정으로 새로운 보고를 받습니다.`,
            '공정 추가 생성 완료',
            {
              dangerouslyUseHTMLString: true,
            },
          )
        } else {
          await this.$alert(
            `입력하신 정보로 새로운 공정이 등록되었습니다.<br>
            담당자의 공정 진행상태가 보고됩니다.<br>
            보고된 정보는 공정 목록에서 확인할 수 있습니다.`,
            '공정 등록 완료',
            {
              dangerouslyUseHTMLString: true,
              confirmButtonText: '확인',
            },
          )
        }

        this.handleCancel()
        this.$router.push('/process')
      } catch (e) {
        this.$alert(`공정 등록에 실패하엿습니다.<br>(${e})`, {
          dangerouslyUseHTMLString: true,
          confirmButtonText: '확인',
        })
      }
    },
    async handleEditConfirm() {
      const form = cloneDeep(this.form)
      if (!this.validate(form)) {
        return false
      }

      form.ownerUUID = null
      form.processId = form.id
      delete form.id
      form.startDate = dayjs.filters.dayJS_ConvertUTCTimeFormat(form.date[0])
      form.endDate = dayjs.filters.dayJS_ConvertUTCTimeFormat(form.date[1])
      delete form.date
      form.subProcessList.forEach(sub => {
        sub.startDate = dayjs.filters.dayJS_ConvertUTCTimeFormat(sub.date[0])
        sub.endDate = dayjs.filters.dayJS_ConvertUTCTimeFormat(sub.date[1])
        delete sub.date
      })

      await this.$alert(
        `공정 정보 편집이 완료되었습니다. 변경된 정보로 공정 보고를 받습니다.`,
        '공정 편집 완료',
        {
          confirmButtonText: '확인',
        },
      )
      try {
        await this.$store.dispatch('updateProcess', form)
        this.handleCancel()
        this.$router.push(`/process/${form.processId}`)
      } catch (e) {
        this.$alert(`공정 편집에 실패하엿습니다.<br>(${e})`, {
          dangerouslyUseHTMLString: true,
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
      this.form.position = ''
      this.form.date = []
      this.form.ownerUUID = null

      // create and replace
      if (this.modalType !== 'edit') {
        this.form.id = this.target.info.contentUUID
        this.form.name = this.target.info.contentName
        this.form.subProcessList = this.target.sceneGroupList.map(scene => ({
          id: scene.id,
          name: scene.name,
          priority: scene.priority,
          date: [],
          workerUUID: null,
        }))
      }
      // edit
      else if (this.modalType === 'edit') {
        this.form.id = this.target.id
        this.form.name = this.target.name
        this.form.position = this.target.position
        this.form.date = [
          dayjs.filters.dayJS_ConvertLocalTime(this.target.startDate),
          dayjs.filters.dayJS_ConvertLocalTime(this.target.endDate),
        ]
        await this.$store.dispatch('getSubProcessList', {
          processId: this.target.id,
        })
        this.form.subProcessList = this.$store.getters.processDetail.subProcessList.map(
          sub => {
            this.$set(sub, 'date', [
              dayjs.filters.dayJS_ConvertLocalTime(sub.startDate),
              dayjs.filters.dayJS_ConvertLocalTime(sub.endDate),
            ])
            return sub
          },
        )
      }
    },
    validate(form) {
      if (!form.date.length) {
        this.$alert(`공정 일정을 지정하여야 합니다.`, {
          confirmButtonText: '확인',
        })
        return false
      }
      for (const sub of form.subProcessList) {
        if (!sub.date) {
          this.$alert(`세부공정 일정을 지정하여야 합니다.`, {
            confirmButtonText: '확인',
          })
          return false
        } else if (!sub.workerUUID) {
          this.$alert(`세부공정 담당자를 지정하여야 합니다.`, {
            confirmButtonText: '확인',
          })
          return false
        }
      }
      return true
    },
  },
}
</script>
