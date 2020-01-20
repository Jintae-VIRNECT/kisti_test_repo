<template lang="pug">
  .process-new-modal(@click="e => e.stopPropagation()")
    el-dialog(
      title="Tips"
      :visible.sync="getProcessNewModal"
      width="540px"
      height="50vh"
      @close="handleCancel")
      div(slot="title")
        span.process-new-modal__header-title 신규 공정 등록
      .process-new-modal__body
        .section.border-divider
          label 공정 이름
          .value
            span Scene Group's name
        .section-body
          .section
            label.label-vertical-center.necessary 공정 일정
            .value.flex-container
              el-date-picker.date-picker(
                v-model="form.dateTime.startDate"
                type="date"
                :clearable="false"
                prefix-icon="null"
                format="yyyy-MM-dd"
                :placeholder="form.dateTime.startDate")
              el-time-select.time-picker(
                :placeholder='form.dateTime.startTime' 
                v-model='form.dateTime.startTime' 
                :picker-options="{ start: form.dateTime.startTime, step: '01:00', end: form.dateTime.endTime }"
                @change="checkMinMaxTime"
                prefix-icon="null"
                :clearable="false"
              )
              span.time-divider - 
              el-date-picker.date-picker(
                v-model="form.dateTime.endDate"
                type="date"
                :clearable="false"
                prefix-icon="null"
                format="yyyy-MM-dd"
                :placeholder="form.dateTime.endDate")
              el-time-select.time-picker(
                :placeholder='form.dateTime.endTime' 
                v-model='form.dateTime.endTime' 
                :picker-options="{\
                  start: form.dateTime.startTime,\
                  step: '01:00',\
                  end: form.dateTime.endTime,\
                  minTime: form.dateTime.startTime == '24:00' ? '23:59' : form.dateTime.startTime\
                }"
                @change="checkMinMaxTime"
                prefix-icon="null"
                :clearable="false"
              )
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
            .number-label 8
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
                  v-model="form.dateTime.startDate"
                  type="date"
                  :clearable="false"
                  prefix-icon="null"
                  format="yyyy-MM-dd"
                  :placeholder="form.dateTime.startDate")
                el-time-select.time-picker(
                  :placeholder='form.dateTime.startTime' 
                  v-model='form.dateTime.startTime' 
                  :picker-options="{ start: form.dateTime.startTime, step: '01:00', end: form.dateTime.endTime }"
                  @change="checkMinMaxTime"
                  prefix-icon="null"
                  :clearable="false"
                )
                span.time-divider - 
                el-date-picker.date-picker(
                  v-model="form.dateTime.endDate"
                  type="date"
                  :clearable="false"
                  prefix-icon="null"
                  format="yyyy-MM-dd"
                  :placeholder="form.dateTime.endDate")
                el-time-select.time-picker(
                  :placeholder='form.dateTime.endTime' 
                  v-model='form.dateTime.endTime' 
                  :picker-options="{\
                    start: form.dateTime.startTime,\
                    step: '01:00',\
                    end: form.dateTime.endTime,\
                    minTime: form.dateTime.startTime == '24:00' ? '23:59' : form.dateTime.startTime\
                  }"
                  @change="checkMinMaxTime"
                  prefix-icon="null"
                  :clearable="false"
                )
            .section
              label.necessary 담당자
              .value
                el-select.auth-select(v-model='form.auth' placeholder='Select')
                  el-option(v-for='item in usersData' :key='item.id' :label='item.username' :value='item.id')
              
      span.dialog-footer.section.border-divider(slot='footer')
        el-button(type='primary' @click='handleConfirm') Confirm

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

export default {
	props: {
		toggleProcessNewModal: {
			type: Boolean,
		},
	},
	computed: {
		getProcessNewModal: {
			get() {
				return this.$props.toggleProcessNewModal
			},
		},
	},
	data() {
		return {
			form: {
				auth: null,
				location: null,
				dateTime: {
					startDate: String(new Date()),
					startTime: '01:00',
					endDate: String(new Date()),
					endTime: '24:00',
				},
			},
			usersData: users,
		}
	},
	methods: {
		handleConfirm() {
			this.$confirm(
				`입력하신 정보로 공정을 추가 생성되었습니다. \n
            추가된 공정으로 새로운 보고를 받습니다.`,
				'공정 추가 생성 완료',
				{
					confirmButtonText: '확인',
					cancelButtonText: '취소',
				},
			)
				.then(() => {
					this.handleCancel()
					// this.$emit('update', false)
					// document.querySelector('body').style='overflow: hidden;'
					// document.querySelector('body').style = ''
					// this.processNewModal = false
				})
				.catch(e => console.log('e : ', e))
		},
		handleCancel() {
			this.$emit('update', false)
			document.querySelector('body').style = ''
			// this.processNewModal = false
		},
		checkMinMaxTime() {
			const startTime = Number(this.form.dateTime.startTime.split(':')[0])
			const endTime = Number(this.form.dateTime.endTime.split(':')[0])
			if (startTime > endTime)
				this.form.dateTime.endTime = this.form.dateTime.startTime
		},
	},
}
</script>
