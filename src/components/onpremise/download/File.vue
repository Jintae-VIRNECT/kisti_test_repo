<template>
  <article>
    <div class="file-cell" v-for="(val, idx) of files" :key="`cell-${idx}`">
      <p class="file-title" v-if="idx !== 'file'">
        {{ lowerCase(idx) || '-' }}
      </p>
      <p class="value" v-if="!/format|file/.test(idx)">{{ val || '-' }}</p>
      <p class="value" v-else-if="val === null && idx !== 'file'">-</p>
      <ul v-if="idx === 'format'">
        <li v-for="names in val" :key="names">
          {{ names }}
        </li>
      </ul>
      <VirnectButton
        v-if="idx === 'file'"
        :label="$t('workspace.onpremiseSetting.upload.title')"
        @onClick="click(files)"
      />
    </div>
  </article>
</template>

<script>
export default {
  props: {
    product: String,
    files: Object,
  },
  methods: {
    click(info) {
      this.$emit('fileUploadClick', this.product, info)
    },
    lowerCase(str) {
      return str[0].toUpperCase() + str.slice(1)
    },
  },
}
</script>

<style lang="scss">
.onpremise-download-file {
  article {
    display: flex;
    flex-wrap: wrap;
    flex-direction: row;
    justify-content: space-evenly;
    align-items: center;
    padding: 34px 0;
    .file-title {
      margin-bottom: 4px;
      color: #5e6b81;
      @include fontLevel(50);
    }
    .value {
      @include fontLevel(150);
      color: #0b1f48;
      width: 100%;
    }
    .el-button {
      @include fontLevel(100);
      width: 210px;
      color: #0b1f48;
    }
  }

  .file-cell {
    &:first-child {
      width: 17.6%;
      .value {
        @include fontLevel(400);
      }
    }
    &:nth-child(2) {
      width: 31.5%;
    }
    &:nth-child(3) {
      width: 7.4%;
    }
    &:nth-child(4) {
      width: 11.8%;
    }
  }
}
</style>
