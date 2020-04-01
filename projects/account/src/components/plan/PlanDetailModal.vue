<template>
  <el-dialog
    class="plan-detail-modal"
    title="플랜 사용 정보"
    :visible.sync="visible"
    width="420px"
    :before-close="handleClose"
  >
    <div>
      <dl class="vertical">
        <dt>워크스페이스 이름</dt>
        <dd>
          {{ data.workspace }}
          <el-button class="el-button--simple">바로가기</el-button>
        </dd>
        <dt>워크스페이스 마스터</dt>
        <dd>{{ data.workspaceMaster }}</dd>
      </dl>
      <el-divider />
      <dl class="horizon">
        <dt>사용 중인 플랜 이름</dt>
        <dd>{{ data.name }}</dd>
        <dt>플랜 할당일</dt>
        <dd>{{ data.startDate }}</dd>
      </dl>
    </div>
  </el-dialog>
</template>

<script>
import dialogMixin from '@/mixins/dialog'

export default {
  mixins: [dialogMixin],
  props: {
    data: Object,
  },
}
</script>

<style lang="scss">
.plan-detail-modal {
  dt,
  dd {
    line-height: 32px;
  }
  dt {
    color: $font-color-desc;
    font-size: 12px;
  }
  dd {
    color: $font-color-content;
    font-size: 14px;
  }
  .el-divider--horizontal {
    margin: 20px 0;
  }

  .vertical {
    .el-button {
      float: right;
    }
  }
  .horizon {
    display: flex;
    flex-wrap: wrap;
    dt,
    dd {
      width: 50%;
      padding-bottom: 10px;
    }
    dd {
      text-align: right;
    }
  }
}
</style>
