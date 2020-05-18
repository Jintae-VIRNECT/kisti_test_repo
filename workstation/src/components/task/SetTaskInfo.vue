<template>
  <el-dialog
    id="set-task-info-modal"
    class="info-modal"
    :visible.sync="showMe"
    :title="$t('task.new.contentsInfo')"
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
            <el-input v-model="shared" disabled />
          </dd>
          <dt>{{ $t('contents.info.target') }}</dt>
          <dd v-for="target in content.targets" :key="target.id">
            <span>{{ target.type }}</span>
          </dd>
        </dl>
      </el-col>
      <el-col :span="15">
        <h4>{{ $t('task.new.sceneGroupInfo') }}</h4>
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
    <template slot="footer">
      <el-button @click="$router.push(`/contents/${content.contentUUID}`)">
        {{ $t('task.new.moveContentInfo') }}
      </el-button>
      <el-button @click="$emit('next', content, properties)" type="primary">
        {{ $t('task.new.next') }}
      </el-button>
    </template>
  </el-dialog>
</template>

<script>
import contentService from '@/services/content'
import { sharedStatus } from '@/models/content/Content'
import filters from '@/mixins/filters'

export default {
  mixins: [filters],
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
  computed: {
    shared() {
      return this.$t(
        sharedStatus.find(status => status.value === this.content.shared).label,
      )
    },
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
  methods: {},
}
</script>

<style lang="scss">
#__nuxt #set-task-info-modal .el-dialog__body {
  height: 640px;
}
</style>
