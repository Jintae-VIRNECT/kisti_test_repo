<template>
  <el-dialog
    class="set-process-info-modal"
    :visible.sync="showMe"
    :title="$t('process.new.contentsInfo')"
    width="860px"
    top="11vh"
  >
    <el-row type="flex" v-if="content">
      <el-col :span="9">
        <h4>{{ $t('contents.info.title') }}</h4>
        <el-divider />
        <dl>
          <dt>{{ $t('contents.info.id') }}</dt>
          <dd>{{ content.contentUUID }}</dd>
          <dt>{{ $t('contents.info.name') }}</dt>
          <dd>{{ content.contentName }}</dd>
          <dt>{{ $t('contents.info.uploader') }}</dt>
          <dd class="column-user">
            <div class="avatar">
              <div
                class="image"
                :style="`background-image: url(${content.uploaderProfile})`"
              />
            </div>
            <span>{{ content.uploaderName }}</span>
          </dd>
        </dl>
        <el-divider />
        <dl class="row">
          <div>
            <dt>{{ $t('contents.info.volume') }}</dt>
            <dd>{{ content.contentSize | toMegaBytes }}</dd>
            <dt>{{ $t('contents.info.type') }}</dt>
            <dd></dd>
          </div>
          <div>
            <dt>{{ $t('contents.info.createdDate') }}</dt>
            <dd>{{ content.createdDate | localDateFormat }}</dd>
            <dt>{{ $t('contents.info.device') }}</dt>
            <dd></dd>
          </div>
        </dl>
        <el-divider />
        <dl>
          <dt>{{ $t('contents.info.sharedStatus') }}</dt>
          <dd class="virnect-workstation-form">
            <el-select v-model="content.shared" popper-class="select-shared" />
          </dd>
          <dt>{{ $t('contents.info.target') }}</dt>
          <dd v-for="target in content.targets" :key="target.id">
            <span>{{ target.type }}</span>
            <img
              src="~assets/images/icon/ic-print.svg"
              @click="print(target.data)"
            />
            <img
              src="~assets/images/icon/ic-file-download.svg"
              @click="download(target.data)"
            />
          </dd>
        </dl>
        <div class="buttons-wrapper"></div>
      </el-col>
      <el-col :span="15">
        <h4>{{ $t('contents.info.properties') }}</h4>
        <div class="properties">
          <el-tree
            :data="properties"
            :props="propertiesProps"
            node-key="id"
            :default-expanded-keys="[content.contentUUID]"
          />
        </div>
      </el-col>
    </el-row>
  </el-dialog>
</template>

<script>
import contentService from '@/services/content'
import { sharedStatus } from '@/models/content/Content'
import { filters } from '@/plugins/dayjs'

export default {
  props: {
    visible: Boolean,
    contentId: String,
  },
  data() {
    return {
      showMe: false,
      content: null,
      properties: null,
      sharedStatus,
      propertiesProps: {
        label: 'label',
        childern: 'childern',
      },
    }
  },
  watch: {
    visible(bool) {
      this.showMe = bool
    },
    async showMe(bool) {
      if (!bool) {
        this.$emit('update:visible', bool)
        return false
      }
      const promise = {
        content: contentService.getContentInfo(this.contentId),
        properties: contentService.getContentProperties(
          this.contentId,
          this.$store.getters['auth/myProfile'].uuid,
        ),
      }
      this.content = await promise.content
      this.properties = await promise.properties
    },
  },
  filters: {
    ...filters,
    toMegaBytes(bytes) {
      const kb = bytes / 1024
      const mb = kb / 1024
      return mb < 1
        ? Math.round(kb * 100) / 100 + 'kB'
        : Math.round(mb * 100) / 100 + 'MB'
    },
  },
  methods: {},
}
</script>

<style lang="scss">
#__nuxt .set-process-info-modal .el-dialog__body {
  height: 700px;
  max-height: 700px;
  padding-bottom: 28px;

  .el-row {
    align-items: stretch;
    height: 100%;
  }
  .el-col {
    position: relative;

    &:first-child {
      padding-right: 30px;
    }
    &:last-child {
      padding-left: 20px;
    }
  }
  dl.row {
    display: flex;

    & > div {
      min-width: 110px;
    }
  }
  .el-divider {
    margin: 16px 0;
  }
  .buttons-wrapper {
    position: absolute;
    bottom: 0;
    width: calc(100% - 30px);

    .el-button {
      width: 92px;
    }
    .el-button:last-child {
      float: right;
    }
  }

  h4 {
    margin-bottom: 16px;
    color: #445168;
  }
  dt {
    margin-bottom: 4px;
    color: $font-color-desc;
    font-size: 12px;
  }
  dd {
    margin-bottom: 20px;
    white-space: nowrap;

    & > img {
      float: right;
      margin-left: 12px;
      cursor: pointer;
    }
    .el-select {
      width: 100%;
      margin-top: 5px;
    }
    .el-input__inner {
      font-size: 14px;
      line-height: 22px;
    }
  }

  // 콘텐츠 구성 정보
  .properties {
    height: calc(100% - 38px);
    overflow: scroll;
    border: solid 1px rgba(226, 231, 237, 0.8);
    border-radius: 3px;

    .el-tree-node__content {
      height: 36px;
      font-weight: 500;
    }
    [role='tree'] > [role='treeitem'] > :first-child {
      height: 40px;
    }
    .el-tree-node__content > .el-tree-node__expand-icon:not(.is-leaf) {
      margin-left: 8px;
      color: $font-color-content;
      font-size: 1em;
    }
    .el-tree-node__content:hover,
    .el-tree-node:focus > .el-tree-node__content {
      background-color: rgba(245, 247, 250, 0.75);
    }
  }
}

body .el-popper.select-shared .el-select-dropdown__item {
  font-size: 14px;
}
</style>
