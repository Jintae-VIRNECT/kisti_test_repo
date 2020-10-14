<template>
  <el-dialog
    class="workspace-favicon-modal onpremise-setting-modal"
    :visible.sync="showMe"
    :title="$t('workspace.onpremiseSetting.favicon.title')"
    width="440px"
    top="11vh"
  >
    <div>
      <p>{{ $t('workspace.onpremiseSetting.favicon.desc') }}</p>
      <div class="preview">
        <img src="~assets/images/workstation-title-example.png" />
        <div class="area">
          <span class="editable">
            <img :src="file" />
          </span>
        </div>
        <div class="tooltip">
          {{ $t('workspace.onpremiseSetting.favicon.tooltip') }}
        </div>
      </div>
      <p
        class="caution"
        v-html="$t('workspace.onpremiseSetting.favicon.caution')"
      />
    </div>
    <div slot="footer">
      <el-upload
        ref="upload"
        action="#"
        :auto-upload="false"
        :on-change="imageSelected"
        :show-file-list="false"
      >
        <el-button type="info">
          {{ $t('workspace.onpremiseSetting.favicon.upload') }}
        </el-button>
      </el-upload>
      <el-button type="primary" @click="submit">
        {{ $t('common.update') }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import modalMixin from '@/mixins/modal'

const defaultFavicon = require('assets/images/logo/favicon.png')

export default {
  mixins: [modalMixin],
  data() {
    return {
      file: defaultFavicon,
    }
  },
  methods: {
    opened() {
      this.file = defaultFavicon
    },
    imageSelected(file) {
      const reader = new FileReader()
      reader.readAsDataURL(file.raw)
      reader.onload = () => {
        this.file = reader.result
      }
    },
    async submit() {},
  },
}
</script>

<style lang="scss">
#__nuxt .workspace-favicon-modal {
  .preview {
    .area {
      top: 11px;
      left: 15px;
      width: auto;
    }
    .tooltip {
      top: -8px;
      left: 15px;
    }
    .area > span {
      top: 0;
    }
    .area > .editable {
      display: block;
      width: 26px;
      height: 26px;
    }
    .area::after {
      display: none;
    }
    .editable {
      padding: 3px;
    }
  }

  .caution {
    margin-top: 8px;
    margin-bottom: 76px;
  }

  .el-upload {
    display: inline;
    .el-button {
      float: left;
    }
  }
}
</style>
