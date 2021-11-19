<template>
  <el-card id="install-file-upload" class="link-list-card">
    <div slot="header">
      <h3>
        <img src="~assets/images/icon/ic-file-upload.svg" />
        <span>{{ $t('workspace.onpremiseSetting.upload.title') }}</span>
      </h3>
    </div>

    <div>
      <a
        v-for="item in upload"
        ref="button"
        class="install-file-upload__button"
        :class="{
          'install-file-upload__button--clicked': item.key === selected,
        }"
        :key="item.key"
        @click="click(item.key)"
      >
        <img v-if="item.image" :src="item.image" />
        <span>{{ item.label }}</span>
      </a>
    </div>
  </el-card>
</template>

<script>
import { upload } from '@/models/settings/onpremise/upload'
export default {
  data() {
    return {
      upload,
      selected: 'remote',
    }
  },
  methods: {
    click(key) {
      this.selected = key
      this.$emit('selectUploadPart', key)
    },
  },
}
</script>
<style lang="scss">
#install-file-upload {
  .el-card__body {
    padding: 8px 0;
  }
  .install-file-upload__button {
    position: relative;
    display: flex;
    align-items: center;
    height: 44px;
    padding: 0 24px;
    transition: background-color 0.25s ease;
    cursor: pointer;
    &:hover {
      background-color: #f5f7fa;
    }
    &--clicked {
      background-color: #f5f7fa;
    }
    & > img:first-child {
      margin-right: 4px;
      margin-left: -8px;
    }
    & > span {
      color: $font-color-content;
    }
    & > img:last-child {
      position: absolute;
      right: 24px;
    }
  }
}
</style>
