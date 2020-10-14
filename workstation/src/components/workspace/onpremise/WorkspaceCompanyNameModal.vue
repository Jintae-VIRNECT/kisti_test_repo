<template>
  <el-dialog
    class="workspace-company-name-modal"
    :visible.sync="showMe"
    :title="$t('workspace.onpremiseSetting.company.title')"
    width="440px"
    top="11vh"
  >
    <div>
      <p>{{ $t('workspace.onpremiseSetting.company.desc') }}</p>
      <div class="preview">
        <img src="~assets/images/workstation-title-example.png" />
        <div class="area">
          <span class="editable">{{ form.companyName }}</span>
          <span>{{ $t('common.workstation') }}</span>
        </div>
        <div class="tooltip">
          {{ $t('workspace.onpremiseSetting.company.tooltip') }}
        </div>
      </div>
      <el-form
        ref="form"
        class="virnect-workstation-form"
        :model="form"
        :show-message="false"
      >
        <el-form-item
          class="horizon"
          prop="companyName"
          required
          :label="$t('workspace.onpremiseSetting.company.companyName')"
        >
          <el-input v-model="form.companyName" />
        </el-form-item>
        <p>{{ $t('workspace.onpremiseSetting.company.caution') }}</p>
      </el-form>
    </div>
    <div slot="footer">
      <el-button type="primary" @click="submit">
        {{ $t('common.update') }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import modalMixin from '@/mixins/modal'

export default {
  mixins: [modalMixin],
  data() {
    return {
      form: {
        companyName: 'VIRNECT',
      },
    }
  },
  methods: {
    opened() {
      this.form = {
        companyName: 'VIRNECT',
      }
    },
    async submit() {},
  },
}
</script>

<style lang="scss">
#__nuxt .workspace-company-name-modal {
  .preview {
    position: relative;
    box-sizing: border-box;
    height: 78px;
    margin-top: 30px;

    img {
      display: block;
    }
    .tooltip {
      position: absolute;
      top: -6px;
      left: 40px;
      padding: 2px 8px;
      color: #fff;
      font-weight: normal;
      font-size: 11px;
      background: #1468e2;
    }
    .area {
      position: absolute;
      top: 13px;
      left: 40px;
      width: 180px;
      height: 26px;
      overflow: hidden;
      font-size: 13px;
      white-space: nowrap;
      background: #fff;

      &:after {
        position: absolute;
        top: 0;
        right: 0;
        display: block;
        width: 10%;
        height: 100%;
        background: linear-gradient(
          90deg,
          rgba(255, 255, 255, 0),
          rgba(255, 255, 255, 1)
        );
        content: '';
      }

      & > span {
        position: relative;
        top: 2px;
        height: 100%;
      }
      .editable {
        padding: 0 6px 2px;
        border: solid 2px #1468e2;

        &:after {
          position: absolute;
          top: 20%;
          right: 0px;
          width: 1px;
          height: 60%;
          background: $font-color-desc;
          content: '';
        }
      }
    }
  }
  .el-form {
    margin: 24px 0 40px;

    .el-form-item {
      margin-bottom: 8px;
    }
    p {
      color: $font-color-desc;
      font-size: 12px;
      line-height: 1.5;
      opacity: 0.8;
    }
  }
  .el-dialog__footer {
    border-top: solid 1px #edf0f7;
  }
}
</style>
