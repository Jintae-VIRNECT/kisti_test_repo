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
        v-for="(product, idx) of productList"
        :key="idx"
        ref="button"
        class="install-file-upload__button"
        :class="{
          'install-file-upload__button--clicked': product === selected,
        }"
        @click="click(product)"
      >
        <img :src="imageSet(product)" />
        <span>VIRNECT {{ lowerCase(product) }}</span>
      </a>
    </div>
  </el-card>
</template>

<script>
export default {
  props: {
    selected: String,
  },
  data() {
    return {
      productList: ['remote', 'make', 'view'],
    }
  },
  methods: {
    click(key) {
      this.$emit('isSelected', key)
      this.$emit('selectUploadPart', key)
    },
    lowerCase(str) {
      return str[0].toUpperCase() + str.slice(1)
    },
    imageSet(product) {
      return require(`@virnect/ui-assets/images/products/logo-${this.lowerCase(
        product,
      )}.svg`)
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
    cursor: pointer;
    transition: background-color 0.25s ease;
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
