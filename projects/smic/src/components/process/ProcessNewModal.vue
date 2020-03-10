<template lang="pug">
  .process-new-modal
    el-dialog(
      :visible.sync="processModal"
      width="540px"
      height="50vh"
      @open="handleOpen"
      @close="handleCancel"
      :destroy-on-close="true")
      template(slot="title")
        span.process-new-modal__header-title 신규 공정 등록
      .process-new-modal__body(v-if="target")
        .section.border-divider
          label 등록될 공정 이름
          .value
            span {{target.info.contentName}}
        .section.border-divider
          label 세부공정 목록
          .value
            .number-label {{target.info.sceneGroupTotal}}
        .detail-process-list.border-divider
          .detail-process-item
            .section.title(v-for="(scene, index) in target.sceneGroupList")
              label {{ scene.priority | leftZeroPad }}.
              .value
                span {{ scene.name }}
      span.dialog-footer.section(slot='footer')
        el-button(type='primary' @click='handleConfirm') 다음
</template>

<script>
import filters from '@/mixins/filters'
export default {
  mixins: [filters],
  props: {
    toggleProcessModal: Boolean,
    target: Object,
  },
  data() {
    return {
      processModal: false,
    }
  },
  methods: {
    handleCancel() {
      this.processModal = false
      this.$emit('onToggleNewModal', false)
    },
    handleOpen() {},
    handleConfirm() {
      this.$emit('handleConfirm')
    },
  },
  watch: {
    $props: {
      handler() {
        this.processModal = this.$props.toggleProcessModal
      },
      deep: true,
    },
  },
}
</script>

<style lang="scss">
$label-width: 97px;
$sub-label-width: 80px;
$value-width: 374px;

.process-new-modal {
  .el-dialog {
    max-width: 100vw;
    cursor: default;
  }
  .el-dialog__header {
    box-shadow: 0 1px 0 0 #eaedf3;
  }
  .el-dialog__body {
    min-height: 30vh;
    max-height: 60vh;
    padding: 0px !important;
    overflow-y: auto;
  }
  .label-vertical-center {
    margin-top: 7px;
  }
  &__body {
    overflow-y: auto;
  }
  &__header-title {
    color: #0d2a58;
    font-weight: 500;
    font-size: 20px;
  }
  .section {
    padding: 12px 12px 12px 30px;
    cursor: initial;
    & > * {
      vertical-align: middle;
    }
  }
  .detail-process-list {
    .detail-process-item {
      margin: 12px 0;
      .section {
        padding-top: 8px;
        padding-bottom: 8px;
      }
      .section label {
        width: $sub-label-width;
        color: #0d2a58;
        font-weight: 500;
        font-size: 12px;
        font-style: normal;
        font-stretch: normal;
        line-height: 3;
        letter-spacing: normal;
      }
      .title {
        color: #0d2a58;
        font-weight: 500;
        font-size: 14px;
        font-style: normal;
        font-stretch: normal;
        line-height: 1.57;
        letter-spacing: normal;
        & label {
          line-height: inherit;
        }
        & .value span {
          margin-left: -70px;
        }
      }
      .el-divider {
        width: calc(100% - 42px);
        margin: 12px auto 12px 30px;
        background-color: #eaedf3;
      }
      &:last-child > .el-divider {
        display: none;
      }
    }
  }
  .section-body {
    padding: 10px 0px;
    border-bottom: 1px solid #d3dbec;
    label {
      color: #0d2a58;
      font-weight: 500;
      font-size: 14px;
      font-style: normal;
      font-stretch: normal;
      line-height: 2;
      letter-spacing: normal;
    }
  }
  label {
    display: inline-block;
    min-width: $label-width;
  }
  label.necessary {
    color: #0d2a58;
    line-height: 1.5;
    &:after {
      margin-left: 2px;
      color: #ee5c57;
      content: '*';
    }
  }
  .section__detail-process {
    label {
      color: #0d2a58;
      font-weight: 500;
      font-size: 12px;
      font-style: normal;
      font-stretch: normal;
      line-height: 1.5;
      letter-spacing: normal;
    }
  }
  .value,
  .el-form-item__content {
    display: inline-block;
    & .el-select,
    & .el-input {
      display: block;
      width: $value-width;
      margin-bottom: -8px;
    }
  }
  .value {
    margin-left: 10px;
  }
  .el-form-item.is-required:not(.is-no-asterisk) > .el-form-item__label:before {
    content: none;
  }
  input,
  .auth-select {
    height: 38px;
    color: #7a869a;
    font-weight: 500;
    font-size: 14px;
    font-style: normal;
    font-stretch: normal;
    line-height: 1.71;
    letter-spacing: normal;
    background-color: #f5f7fa;
    border: none;
    border-radius: 3px;
  }
  .border-divider {
    padding: 18px 20px 18px 30px;
    box-shadow: 0 1px 0 0 #eaedf3;
  }
  .auth-select {
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
  & .el-select:hover .el-input__inner {
    background-color: unset !important;
  }
  span.description {
    color: #566173;
    font-weight: normal;
    font-size: 12px;
    font-style: normal;
    font-stretch: normal;
    line-height: 1.5;
    letter-spacing: normal;
  }
  .auth-select {
    width: 100%;
    & input {
      width: 100%;
      padding: 8px 20px;
      transition: 0s;
    }
    .el-input:hover:not(.is-focus):not(.is-disabled) {
      background: #e6e9ee;
    }
  }
  .number-label {
    display: inline-block;
    height: 28px;
    padding: 2px 12px;
    color: #114997;
    font-weight: 500;
    font-size: 14px;
    font-style: normal;
    font-stretch: normal;
    line-height: 1.57;
    letter-spacing: normal;
    background-color: #fbfbfd;
    border: solid 1px #eaedf3;
    border-radius: 4px;
  }
  .el-dialog__footer {
    box-shadow: 0 -1px 0 0 #eaedf3;
    .section {
      padding: 0;
    }
  }
  .el-date-editor--datetimerange.el-input__inner {
    width: $value-width;
    height: 38px;
    padding: 0;
    border: none;

    & > i {
      display: none;
    }
    .el-range-input {
      width: 48%;
    }
  }
}
</style>
