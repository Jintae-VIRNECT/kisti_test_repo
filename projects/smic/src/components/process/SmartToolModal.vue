<template lang="pug">
  .process-new-modal.smart-tool-modal
    el-dialog(
      :visible.sync="smartToolModal"
      width="540px"
      height="50vh"
      @open="handleOpen"
      @close="handleCancel"
      :destroy-on-close="true")
      template(slot="title")
        span.process-new-modal__header-title 스마트툴
      .process-new-modal__body
        .section
          label Job ID
          .value
            span Job ID no. {{ smartToolDetail.smartToolJobId }}
            el-divider(direction="vertical")
            span 목표 토크 값
            span.blue {{ smartToolDetail.normalToque }}
            el-divider(direction="vertical")
            span 체결 완료 수
            span.blue {{ done }}
            span / {{ smartTools.length }}
        .section(v-for="{ batchCount, workingToque } in smartTools")
          label 체결 {{ batchCount }}.
          .value
            span(v-if="workingToque") 토크 값: {{ workingToque }}
            el-progress(:percentage="workingToque == smartToolDetail.normalToque ? 100 : 0" :show-text="false")
      
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  props: {
    toggleSmartToolModal: Boolean,
    jobId: Number,
  },
  data() {
    return {
      smartToolModal: false,
      smartTools: [
        {
          batchCount: 0,
          workingToque: 1,
        },
      ],
    }
  },
  computed: {
    ...mapGetters(['smartToolDetail']),
    done() {
      return this.smartTools.filter(
        ({ workingToque }) => workingToque == this.smartToolDetail.normalToque,
      ).length
    },
  },
  filters: {
    toPercentNumber(val, max) {
      return ((val * 1) / (max * 1)) * 100
    },
    toPercentStr(val, max) {
      const percent = ((val * 1) / (max * 1)) * 100
      return Number(percent).toFixed(0)
    },
  },
  methods: {
    handleCancel() {
      this.smartToolModal = false
      this.$emit('handleCancel')
    },
    handleOpen() {
      this.$store.commit('SET_SMART_TOOL_DETAIL', this.jobId)
      this.smartTools = this.smartToolDetail.smartToolItems
    },
    handleConfirm() {},
  },
  watch: {
    $props: {
      handler() {
        this.smartToolModal = this.$props.toggleSmartToolModal
      },
      deep: true,
    },
  },
}
</script>

<style lang="scss">
.smart-tool-modal .el-dialog .section {
  padding: 24px 30px;

  .el-divider {
    margin: 0 18px;
  }
  label {
    width: 60px;
  }
  .value {
    & > span {
      &:first-child {
        color: #0d2a58;
        font-weight: 500;
      }
      &.blue {
        margin: 0 4px 0 16px;
        color: #186ae2;
      }
    }
    .el-progress {
      width: 400px;
      margin-top: 12px;
    }
  }
}
</style>
