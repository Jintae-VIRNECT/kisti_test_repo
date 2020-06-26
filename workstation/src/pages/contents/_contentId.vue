<template>
  <el-dialog
    id="contents-info-modal"
    class="info-modal"
    :visible.sync="showMe"
    :title="$t('contents.info.title')"
    width="860px"
    top="11vh"
    @close="closed"
  >
    <el-row type="flex">
      <el-col :span="9">
        <h4>{{ $t('contents.info.title') }}</h4>
        <el-divider />
        <dl>
          <dt>{{ $t('contents.info.id') }}</dt>
          <dd class="content-uuid">{{ content.contentUUID }}</dd>
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
          <dd v-if="content.target">
            <span>{{ content.target.type }}</span>
            <img
              v-if="content.target.imgPath"
              src="~assets/images/icon/ic-print.svg"
              @click="print(content.target.imgPath)"
            />
            <img
              v-if="content.target.imgPath"
              src="~assets/images/icon/ic-file-download.svg"
              @click="download(content.target.imgPath)"
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
import filters from '@/mixins/filters'

export default {
  mixins: [filters],
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
  methods: {
    closed() {
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
      setTimeout(() => popup.print(), 1)
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
          duration: 2000,
          showClose: true,
        })
        this.$emit('updated')
        this.$router.replace('/contents')
      } catch (e) {
        this.$message.error({
          message: this.$t('contents.info.message.deleteFail') + `\n(${e})`,
          duration: 2000,
          showClose: true,
        })
      }
    },
    async update() {
      try {
        await contentService.updateContent(this.content.contentUUID, this.form)
        this.$message.success({
          message: this.$t('contents.info.message.updateSuccess'),
          duration: 2000,
          showClose: true,
        })
        this.content.shared = this.form.shared
        this.$emit('updated')
      } catch (e) {
        this.$message.error({
          message: this.$t('contents.info.message.updateFail') + `\n(${e})`,
          duration: 2000,
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
#__nuxt #contents-info-modal .el-dialog__body {
  height: 700px;
}
</style>
