<template lang="pug">
  .process-new-modal.issue-modal.report-modal
    el-dialog(
      :visible.sync="reportModal"
      width="540px"
      height="50vh"
      @open="handleOpen"
      @close="handleCancel"
      )
      template(slot="title")
        span.process-new-modal__header-title 리포트
      .process-new-modal__body
        div(v-for="(item, index) in reportDetail.reportItems")
          .section(:class="item.type === 'REPORT' ? '' : 'border-divider'")
            label 항목 {{ index + 1 }}.
            .value
              span {{ item.title }}
              p(:class="item.type === 'TOGGLE' ? 'bool' : ''") {{ item.answer }}
              a(v-if="item.photoFilePath" :href="item.photoFilePath" download)
                el-button
                  img(src="~@/assets/image/ic-download.svg")
                  span 다운로드
          .section.section--image(v-if="item.photoFilePath")
            el-image(:src="item.photoFilePath" :preview-src-list="[item.photoFilePath]")
            i.el-icon-full-screen
</template>

<script>
import { mapGetters } from 'vuex'
export default {
  props: {
    toggleReportModal: Boolean,
    reportId: Number,
  },
  data() {
    return {
      reportModal: false,
      // imgSrc: require('@/assets/image/issue-sample.jpg'),
    }
  },
  computed: {
    ...mapGetters(['reportList', 'reportDetail']),
  },
  methods: {
    handleCancel() {
      this.reportModal = false
      this.$emit('handleCancel')
    },
    handleOpen() {
      let reportId = this.reportId
      if (!reportId) {
        reportId = this.reportList.find(report => {
          return report.jobId == this.$route.query.jobId
        }).reportId
      }
      this.$store.dispatch('getReportDetail', reportId)
    },
    handleConfirm() {},
  },
  watch: {
    $props: {
      handler() {
        this.reportModal = this.$props.toggleReportModal
      },
      deep: true,
    },
  },
}
</script>

<style lang="scss">
.report-modal .el-dialog .section {
  &.border-divider {
    margin: 18px 30px;
    padding: 0 0 18px;
    &:first-child {
      margin-top: 26px;
    }
  }
  label {
    width: 60px;
  }
  .value {
    width: 390px;
  }
  .bool {
    display: block;
    width: 63px;
    margin-top: 8px;
    padding: 2px 4px 1px;
    text-align: center;
    border: solid 1.2px #566173;
    border-radius: 16px;
    opacity: 0.94;
  }
  p {
    margin-bottom: 0;
    color: #566173;
  }
  & > .el-icon-full-screen {
    right: 0;
  }
}
</style>
