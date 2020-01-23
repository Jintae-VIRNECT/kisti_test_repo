<template lang="pug">
  .process-new-modal(@click="e => e.stopPropagation()")
    el-dialog(
      title="Tips"
      :visible.sync="processModal"
      width="540px"
      height="50vh"
      @open="handleOpen"
      @close="handleCancel"
      :destroy-on-close="true")
      div(slot="title")
        span.process-new-modal__header-title {{modalType === "create" ? '신규 공정 등록' : '공정 편집'}}
      .process-new-modal__body
        .section.border-divider
          label 공정 이름
          .value
            span {{target.processName}}
        .section-body
          .section
            label.label-vertical-center.necessary 공정 일정
            .value.flex-container
              el-date-picker.date-picker(
                v-model="form.dateTime.startDate"
                type="date"
                :clearable="false"
                prefix-icon="null"
                format="yyyy-MM-dd")
              el-time-select.time-picker(
                v-model='form.dateTime.startTime' 
                :picker-options="{step: '01:00'}"
                prefix-icon="null"
                :clearable="false")
                //- @change="checkMinMaxTime"
              span.time-divider - 
              el-date-picker.date-picker(
                v-model="form.dateTime.endDate"
                type="date"
                :clearable="false"
                prefix-icon="null"
                format="yyyy-MM-dd")
              el-time-select.time-picker(
                v-model='form.dateTime.endTime' 
                :picker-options="{\
                  step: '01:00',\
                  minTime: form.dateTime.startTime == '24:00' ? '23:59' : form.dateTime.startTime\
                }"
                prefix-icon="null"
                :clearable="false")
                //- @change="checkMinMaxTime"
          .section
            label.label-vertical-center 공정 담당자
            .value
              el-select.auth-select(v-model='form.auth' placeholder='Select')
                el-option(v-for='item in usersData' :key='item.id' :label='item.username' :value='item.id')
              span.description 공정 담당자 설정 시 공정 내 전체 세부공정의 담당자로 지정됩니다
          .section
            label.label-vertical-center 공정 위치
            .value
              el-input(placeholder='공정 위치를 입력해주세요' v-model='form.location' )
              span.description 담당자에게 공정 진행 위치를 안내합니다.
        .section.border-divider
          label 세부공정 목록
          .value
            .number-label {{total}}
        .detail-process-list.border-divider
          .detail-process-item
            .section
              label.title 01.
              .value.title
                span Scene Group's name
            .section
              label.necessary 세부공정 일정
              .value.flex-container
                el-date-picker.date-picker(
                  v-model="startDate"
                  type="date"
                  :clearable="false"
                  prefix-icon="null"
                  format="yyyy-MM-dd")
                el-time-select.time-picker(
                  v-model='startTime' 
                  :picker-options="{ start: startTime, step: '01:00', end: endTime }"
                  prefix-icon="null"
                  :clearable="false")
                  //- @change="checkMinMaxTime"
                span.time-divider - 
                el-date-picker.date-picker(
                  v-model="endDate"
                  type="date"
                  :clearable="false"
                  prefix-icon="null"
                  format="yyyy-MM-dd")
                el-time-select.time-picker(
                  v-model='endTime' 
                  :picker-options="{\
                    start: startTime,\
                    step: '01:00',\
                    end: endTime,\
                    minTime: startTime == '24:00' ? '23:59' : startTime\
                  }"
                  prefix-icon="null"
                  :clearable="false")
                  //- @change="checkMinMaxTime"
            .section
              label.necessary 담당자
              .value
                el-select.auth-select(v-model='form.auth' placeholder='Select')
                  el-option(v-for='item in usersData' :key='item.id' :label='item.username' :value='item.id')
              
      span.dialog-footer.section(slot='footer')
        el-button(type='primary' @click='handleConfirm') 완료

</template>
<style lang="scss">
$label-width: 100px;

.process-new-modal {
  .el-dialog__header {
    box-shadow: 0 1px 0 0 #eaedf3;
  }
  .el-dialog__body {
    padding: 0px !important;
    overflow-y: auto;
    max-height: 50vh;
  }
  .label-vertical-center {
    margin-top: 7px;
  }
  &__body {
    overflow-y: auto;
  }
  &__header-title {
    font-size: 20px;
    color: #0d2a58;
  }
  .section {
    padding: 17px 30px;
    cursor: initial;
    & > * {
      vertical-align: middle;
    }
  }
  .detail-process-list {
    .detail-process-item {
      .section label {
        font-size: 12px;
        font-weight: 500;
        font-stretch: normal;
        font-style: normal;
        line-height: 1.5;
        letter-spacing: normal;
        color: #0d2a58;
      }
      .title {
        font-size: 14px;
        font-weight: 500;
        font-stretch: normal;
        font-style: normal;
        line-height: 1.57;
        letter-spacing: normal;
        color: #0d2a58;
      }
    }
  }
  .section-body {
    padding: 10px 0px;
    border-bottom: 1px solid #d3dbec;
    label {
      font-size: 14px;
      font-weight: 500;
      font-stretch: normal;
      font-style: normal;
      line-height: 2;
      letter-spacing: normal;
      color: #0d2a58;
    }
  }
  label {
    width: $label-width;
    float: left;
  }
  label.necessary {
    line-height: 1.5;
    color: #0d2a58;
    &:after {
      content: '*';
      margin-left: 2px;
      color: #ee5c57;
    }
  }
  .section__detail-process {
    label {
      font-size: 12px;
      font-weight: 500;
      font-stretch: normal;
      font-style: normal;
      line-height: 1.5;
      letter-spacing: normal;
      color: #0d2a58;
    }
  }
  .value {
    margin-left: $label-width;
  }
  input,
  .auth-select {
    height: 38px;
    border-radius: 3px;
    background-color: #f5f7fa;
    border: none;
    font-size: 14px;
    font-weight: 500;
    font-stretch: normal;
    font-style: normal;
    line-height: 1.71;
    letter-spacing: normal;
    color: #7a869a;
  }
  .border-divider {
    box-shadow: 0 1px 0 0 #eaedf3;
  }
  .auth-select {
    display: flex;
    justify-content: center;
    flex-direction: column;
  }
  & .el-select:hover .el-input__inner {
    background-color: unset !important;
  }
  span.description {
    font-size: 12px;
    font-weight: normal;
    font-stretch: normal;
    font-style: normal;
    line-height: 1.5;
    letter-spacing: normal;
    color: #566173;
  }
  .time-picker input,
  .date-picker input {
    padding: 0px !important;
    text-align: center;
  }
  .time-picker {
    width: 65px !important;
  }
  .date-picker {
    width: 100px !important;
  }
  .time-divider,
  .date-picker,
  .time-picker {
    margin-right: 5px;
  }
  .auth-select {
    width: 100%;
  }
  .number-label {
    height: 28px;
    border-radius: 4px;
    border: solid 1px #eaedf3;
    background-color: #fbfbfd;
    font-size: 14px;
    font-weight: 500;
    font-stretch: normal;
    font-style: normal;
    line-height: 1.57;
    letter-spacing: normal;
    color: #114997;
    padding: 2px 12px;
    display: inline-block;
  }
}
</style>
<script>
import users from '@/data/users'
import dayjs from 'dayjs'

export default {
  props: {
    toggleProcessModal: {
      type: Boolean,
    },
    target: {
      type: Object,
    },
    modalType: {
      type: String,
    },
  },
  watch: {
    toggleProcessModal() {
      this.processModal = this.$props.toggleProcessModal
    },
  },
  computed: {
    startDate: {
      set(value) {
        this.form.dateTime.startDate = value.split(' ')[0]
      },
      get() {
        return this.form.dateTime.startDate
      },
    },
    startTime: {
      set(value) {
        this.form.dateTime.startTime = value.split(':')[0]
      },
      get() {
        return this.form.dateTime.startTime
      },
    },
    endDate: {
      set(value) {
        this.form.dateTime.endDate = value.split(' ')[0]
      },
      get() {
        return this.form.dateTime.endDate
      },
    },
    endTime: {
      set(value) {
        this.form.dateTime.endTime = value.split(' ')[0]
      },
      get() {
        return this.form.dateTime.endTime
      },
    },
  },
  data() {
    return {
      processModal: null,
      form: {
        auth: null,
        location: null,
        dateTime: {
          startDate: null,
          startTime: null,
          endDate: null,
          endTime: null,
        },
      },
      total: null,
      usersData: users,
    }
  },
  mounted() {
    this.processModal = this.$props.toggleProcessModal
    const { auth, location, total } = this.$props.target
    let { startAt, endAt } = this.$props.target
    startAt = new Date(startAt)
    endAt = new Date(endAt)
    this.form.dateTime.auth = auth
    this.form.dateTime.location = location
    this.form.dateTime.startDate = startAt
    this.form.dateTime.startTime = dayjs(startAt).format('HH:00')
    this.form.dateTime.endDate = endAt
    this.form.dateTime.endTime = dayjs(endAt).format('HH:00')
    this.total = total
    console.log('this.form.dateTime : ', this.form.dateTime)
  },
  methods: {
    handleConfirm() {
      if (this.$props.modalType === 'edit') {
        this.handleEditConfirm()
      } else {
        this.handleCreateConfirm()
      }
    },
    handleCreateConfirm() {
      this.$confirm(
        `입력하신 정보로 공정을 추가 생성되었습니다. \n
            추가된 공정으로 새로운 보고를 받습니다.`,
        '공정 추가 생성 완료',
        {
          confirmButtonText: '확인',
        },
      )
        .then(() => {
          let createdData = this.$props.target
          for (let prop in this.form) {
            createdData[prop] = this.form[prop]
          }
          const { dateTime } = this.form
          createdData.id = String(Math.random() * (99999999 - 0) + 0)
          createdData.startAt = dayjs(dateTime.startDate).set(
            'hour',
            Number(dateTime.startTime.split(':')[0]),
          )
          createdData.endAt = dayjs(dateTime.endDate).set(
            'hour',
            Number(dateTime.endTime.split(':')[0]),
          )
          createdData.issue = true
          delete createdData.dateTime
          this.$emit('onCreateData', createdData)
          this.handleCancel()
        })
        .catch(() => {})
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
          let updatedData = this.$props.target
          for (let prop in this.form) {
            updatedData[prop] = this.form[prop]
          }
          const { dateTime } = this.form
          updatedData.id = String(Math.random() * (99999999 - 0) + 0)
          updatedData.startAt = dayjs(dateTime.startDate).set(
            'hour',
            Number(dateTime.startTime.split(':')[0]),
          )
          updatedData.endAt = dayjs(dateTime.endDate).set(
            'hour',
            Number(dateTime.endTime.split(':')[0]),
          )
          updatedData.issue = true
          delete updatedData.dateTime
          this.$emit('onChangeData', updatedData)
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
    // checkMinMaxTime() {
    //   const startTime = Number(this.form.dateTime.startTime.split(':')[0])
    //   const endTime = Number(this.form.dateTime.endTime.split(':')[0])
    //   console.log(`${startTime} > ${endTime}`)
    //   if (startTime > endTime) this.form.dateTime.endTime = this.form.dateTime.startTime
    // },
  },
}
</script>
