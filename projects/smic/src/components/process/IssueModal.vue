<template lang="pug">
  .process-new-modal.issue-modal
    el-dialog(
      :visible.sync="issueModal"
      width="540px"
      height="50vh"
      @open="handleOpen"
      @close="handleCancel"
    )
      template(slot="title")
        span.process-new-modal__header-title 이슈 관리
      .process-new-modal__body
        .section.border-divider.section--image(v-if="issueDetail.photoFilePath")
          el-image(:src="issueDetail.photoFilePath" :preview-src-list="[issueDetail.photoFilePath]")
          i.el-icon-full-screen
        .section.issue
          label 이슈 유형
          .value
            span.issue-type {{ isWorkIssue ? '작업 이슈' : '이슈' }}
        .section
          label 보고자
          .value
            span {{ uuidToMember(issueDetail.workerUUID).name }}
        .section
          label 보고일시
          .value
            span {{ issueDetail.reportedDate }}
        .section
          label 이슈내용
          .value(v-if="issueDetail.photoFilePath")
            span {{ issueDetail.photoFilePath }}
            a(:href="issueDetail.photoFilePath" download)
              el-button
                img(src="~@/assets/image/ic-download.svg")
                span 다운로드
          .value(v-else)
            span {{ issueDetail.caption }}
        .section(v-if="isWorkIssue")
          label 공정 이름
          .value
            span {{ issueDetail.processName }}
        .section(v-if="isWorkIssue")
          label 세부공정 이름
          .value
            span {{ issueDetail.subProcessName }}
        .section(v-if="isWorkIssue")
          label 작업 이름
          .value
            span {{ issueDetail.jobName }}
            a(v-if="/issue/.test($route.path)" :href="jobUrl")
              el-button
                img(src="~@/assets/image/ic-shortcut.svg")
                span 작업 바로가기
</template>

<script>
import { mapGetters } from 'vuex'
import dayjs from '@/plugins/dayjs'
import members from '@/mixins/members'

export default {
  mixins: [members],
  props: {
    toggleIssueModal: Boolean,
    issueId: Number,
  },
  data() {
    return {
      issueModal: false,
      imgSrc: require('@/assets/image/issue-sample.jpg'),
    }
  },
  computed: {
    ...mapGetters(['memberList', 'issueList', 'issueDetail']),
    isWorkIssue() {
      return this.issueDetail.processId
    },
    worker() {
      const worker = this.memberList.find(
        member => member.uuid === this.issueDetail.workerUUID,
      )
      return worker && worker.name
    },
    reportedDate() {
      return (
        this.issueDetail.reportedDate &&
        dayjs.filters.dayJs_FilterDateTimeFormat(this.issueDetail.reportedDate)
      )
    },
    jobUrl() {
      const { processId, subProcessId } = this.issueDetail
      return `/process/${processId}/${subProcessId}`
    },
  },
  methods: {
    handleCancel() {
      this.issueModal = false
      this.$emit('handleCancel')
    },
    handleOpen() {
      let issueId = this.issueId
      if (!issueId) {
        issueId = this.issueList.find(report => {
          return report.jobId == this.$route.query.jobId
        }).issueId
      }
      this.$store.dispatch('getIssueDetail', issueId)
    },
    handleConfirm() {},
  },
  watch: {
    $props: {
      handler() {
        this.issueModal = this.$props.toggleIssueModal
      },
      deep: true,
    },
  },
}
</script>

<style lang="scss">
.issue-modal .el-dialog {
  .el-dialog__body {
    height: 68vh;
  }
  .section {
    position: relative;
    margin: 20px 30px;
    padding: 0;

    & > * {
      vertical-align: top;
    }
    &.border-divider {
      margin: 0;
      padding: 30px;
    }
    &.issue {
      margin: 30px;
    }
    &:last-child {
      margin-bottom: 36px;
    }
    & > .el-icon-full-screen {
      position: absolute;
      right: 30px;
      margin: 12px;
      padding: 6px;
      color: #fff;
      font-size: 19px;
      background: rgba(0, 0, 0, 0.5);
      border-radius: 50%;
      pointer-events: none;
    }

    &--image {
      display: inline-block;
    }
  }
  .el-image img {
    pointer-events: inherit;
  }
  i.el-icon-circle-close {
    color: #fff;
  }
  .el-button {
    display: block;
    margin-top: 12px;
    padding: 2px 10px;
    color: #0d2a58;
    font-weight: 500;
    background-color: #eaedf3;
    border: none;
    border-radius: 4px;
    box-shadow: none;
    &:hover {
      background: #e6e9ee;
    }
    &:active {
      color: #fff;
      background: #455163;
      span > img {
        filter: brightness(20);
      }
    }
    span > img {
      display: inline-block;
      margin: 0 4px 0 -2px;
      vertical-align: middle;
    }
    span > span {
      display: inline-block;
      vertical-align: middle;
    }
  }
  .value {
    margin-left: 0;
  }
  .value a {
    display: block;
  }
}
</style>
