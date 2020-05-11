<template>
  <el-dialog
    class="contents-info-modal"
    :visible.sync="showMe"
    :title="$t('contents.info.title')"
    width="860px"
    top="11vh"
    @close="close"
  >
    <el-row>
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
            <dd>{{ content.contentSize }}</dd>
            <dt>{{ $t('contents.info.type') }}</dt>
            <dd></dd>
          </div>
          <div>
            <dt>{{ $t('contents.info.createdDate') }}</dt>
            <dd>{{ content.createdDate }}</dd>
            <dt>{{ $t('contents.info.device') }}</dt>
            <dd></dd>
          </div>
        </dl>
        <el-divider />
        <dl>
          <dt>{{ $t('contents.info.sharedStatus') }}</dt>
          <dd>{{ content.shared }}</dd>
          <dt>{{ $t('contents.info.target') }}</dt>
          <dd v-for="target in content.targets" :key="target.id">
            {{ target.type }}
          </dd>
        </dl>
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
      showMe: true,
    }
  },
  data() {
    return {
      propertiesProps: {
        label: 'label',
        childern: 'childern',
      },
    }
  },
  methods: {
    close() {
      this.showMe = false
      this.$router.push('/contents')
    },
  },
  beforeMount() {
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

  .el-col {
    height: 100%;
  }
  .el-col:nth-child(2) {
    padding-left: 20px;
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
  }
  .properties {
    height: calc(700px - 100px);
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
  }
}
</style>
