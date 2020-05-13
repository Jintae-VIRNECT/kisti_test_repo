<template>
  <el-dialog
    class="contents-info-modal"
    :visible.sync="showMe"
    :title="$t('contents.info.title')"
    width="860px"
    top="11vh"
    @close="close"
  >
    <el-row type="flex">
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
            <el-select v-model="form.shared" popper-class="select-shared">
              <el-option
                v-for="status in sharedStatus"
                :key="status.value"
                :value="status.value"
                :label="$t(status.label)"
              />
            </el-select>
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
        <div class="buttons-wrapper">
          <el-button @click="remove">
            {{ $t('contents.info.delete') }}
          </el-button>
          <el-button @click="update" :disabled="!isDirty" type="primary">
            {{ $t('contents.info.update') }}
          </el-button>
        </div>
      </el-col>
      <!-- 콘텐츠 구성 정보 -->
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
  async asyncData({ params, store }) {
    const promise = {
      content: contentService.getContentInfo(params.contentId),
      properties: contentService.getContentProperties(
        params.contentId,
        store.getters['auth/myProfile'].uuid,
      ),
    }
    return {
      content: await promise.content,
      properties: await promise.properties,
    }
  },
  data() {
    return {
      showMe: true,
      sharedStatus,
      form: {
        shared: null,
      },
      propertiesProps: {
        label: 'label',
        childern: 'childern',
      },
    }
  },
  computed: {
    isDirty() {
      return this.form.shared !== this.content.shared
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
  methods: {
    close() {
      this.showMe = false
      this.$router.push('/contents')
    },
    download(url) {
      window.open(url)
    },
    print(url) {
      const popup = window.open('', '_blank')
      popup.document.write(`<img src="${url}" />`)
      popup.document.close()
      popup.print()
    },
    async remove() {
      try {
        await this.$confirm(
          this.$t('contents.info.message.deleteSure'),
          this.$t('contents.info.message.delete'),
        )
      } catch (e) {
        return false
      }
      try {
        await contentService.deleteContent([this.content.contentUUID])
        this.$message.success({
          message: this.$t('contents.info.message.deleteSuccess'),
          showClose: true,
        })
        this.$emit('updated')
        this.$router.replace('/contents')
      } catch (e) {
        this.$message.error({
          message: this.$t('contents.info.message.deleteFail'),
          showClose: true,
        })
      }
    },
    async update() {
      try {
        await contentService.updateContent(this.content.contentUUID, this.form)
        this.$message.success({
          message: this.$t('contents.info.message.updateSuccess'),
          showClose: true,
        })
        this.content.shared = this.form.shared
        this.$emit('updated')
      } catch (e) {
        this.$message.error({
          message: this.$t('contents.info.message.updateFail'),
          showClose: true,
        })
      }
    },
  },
  beforeMount() {
    this.form.shared = this.content.shared
    this.$store.commit(
      'workspace/SET_ACTIVE_WORKSPACE',
      this.content.workspaceUUID,
    )
  },
}
</script>

<style lang="scss">
#__nuxt .contents-info-modal .el-dialog__body {
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
