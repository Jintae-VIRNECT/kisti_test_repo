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
        span.process-new-modal__header-title {{modalType === "create" ? '신규 공정 등록' : '공정 편집'}}
      .process-new-modal__body
        .section.border-divider
          label 공정 이름
          .value
            span {{target.info.contentName}}
        .section-body
          .section
            label.label-vertical-center.necessary 공정 일정
            .value.flex-container
              el-date-picker(
                v-model="processDate"
                type="datetimerange"
                start-placeholder="시작일"
                end-placeholder="마감일"
                format="yyyy. MM. dd.  HH:mm"
                :picker-options="pickerOptions"
              )
          .section
            label.label-vertical-center 공정 담당자
            .value
              el-select.auth-select(v-model='form.ownerUUID' placeholder='Select')
                el-option(v-for='item in memberList' :key='item.uuid' :label='item.name' :value='item.uuid')
              span.description 공정 담당자 설정 시 공정 내 전체 세부공정의 담당자로 지정됩니다
          .section
            label.label-vertical-center 공정 위치
            .value
              el-input(placeholder='공정 위치를 입력해주세요' v-model='form.position' )
              span.description 담당자에게 공정 진행 위치를 안내합니다.
        .section.border-divider
          label 세부공정 목록
          .value
            .number-label {{ target.info.sceneGroupTotal }}
        .detail-process-list
          .detail-process-item(v-for="scene in form.subProcessList")
            .section.title
              label {{ scene.priority | leftZeroPad }}.
              .value
                span {{ scene.name }}
            .section
              label.necessary 세부공정 일정
              .value.flex-container
                el-date-picker(
                  v-model="processDate"
                  type="datetimerange"
                  start-placeholder="시작일"
                  end-placeholder="마감일"
                  format="yyyy. MM. dd.  HH:mm"
                  :picker-options="pickerOptions"
                )
            .section
              label.necessary 담당자
              .value
                el-select.auth-select(v-model='form.ownerUUID' placeholder='Select')
                  el-option(v-for='item in memberList' :key='item.uuid' :label='item.name' :value='item.uuid')
            el-divider
              
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
    target: Object,
    modalType: String,
  },
  data() {
    return {
      processModal: this.toggleProcessModal,
      form: {
        contentUUID: null,
        name: null,
        ownerUUID: null,
        startDate: null,
        endDate: null,
        position: null,
        subProcessList: [],
      },
      processDate: [],
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
    processDate(val) {
      this.form.startDate = dayjs.filters.dayJS_ConvertUTCTime(val[0])
      this.form.endDate = dayjs.filters.dayJS_ConvertUTCTime(val[1])
      // temp
      this.form.subProcessList.forEach(sub => {
        sub.startDate = dayjs.filters.dayJS_ConvertUTCTime(val[0])
        sub.endDate = dayjs.filters.dayJS_ConvertUTCTime(val[1])
      })
    },
    'form.ownerUUID'(val) {
      // temp
      this.form.subProcessList.forEach(sub => {
        sub.workerUUID = val
      })
    },
  },
  computed: {
    memberList() {
      return this.$store.getters.getMemberList
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
    async handleCreateConfirm() {
      try {
        await this.$store.dispatch('CREATE_PROCESS', this.form)
        await this.$confirm(
          `입력하신 정보로 공정을 추가 생성되었습니다. \n
            추가된 공정으로 새로운 보고를 받습니다.`,
          '공정 추가 생성 완료',
          {
            confirmButtonText: '확인',
          },
        )
        this.handleCancel()
      } catch (e) {
        this.$confirm(`서버에러`, {
          confirmButtonText: '확인',
        })
      }
    },
    handleEditConfirm() {
      this.$confirm(
        `공정 정보를 편집하시겠습니까? 변경된 정보로 공정 보고를 받습니다.`,
        '공정 편집 완료',
        {
          confirmButtonText: '확인',
        },
      )
        .then(() => {
          this.handleCancel()
        })
        .catch(() => {})
    },
    handleCancel() {
      this.$emit('onToggleProcessModal', false)
      document.querySelector('body').style = ''
    },
    handleOpen() {
      document.querySelector('body').style = 'overflow-y: hidden;'
    },
  },
  created() {
    if (!this.target) return false
    this.form.contentUUID = this.target.info.contentUUID
    this.form.name = this.target.info.contentName
    this.form.subProcessList = this.target.sceneGroupList.map(scene => ({
      id: scene.id,
      name: scene.name,
      priority: scene.priority,
      startDate: null,
      endDate: null,
      workerUUID: null,
    }))
  },
}
</script>
