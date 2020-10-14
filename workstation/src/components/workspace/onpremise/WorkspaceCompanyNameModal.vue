<template>
  <el-dialog
    class="workspace-company-name-modal onpremise-setting-modal"
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
        <p
          class="caution"
          v-html="$t('workspace.onpremiseSetting.company.caution')"
        />
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
  .editable {
    padding: 0 6px 2px;
  }
  .editable::after {
    position: absolute;
    top: 20%;
    right: 0px;
    width: 1px;
    height: 60%;
    background: $font-color-desc;
    content: '';
  }
}
</style>
