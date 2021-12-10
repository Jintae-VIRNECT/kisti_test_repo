<template>
  <article>
    <div class="file-cell">
      <p class="file-title">Category</p>
      <p class="value">
        {{ file.category || '-' }}
      </p>
    </div>
    <div class="file-cell">
      <p class="file-title">Format</p>
      <ul v-if="typeof file.format === 'string'">
        <li>
          {{ file.format }}
        </li>
      </ul>
      <ul v-else>
        <li v-for="(value, index) in file.format" :key="index">{{ value }}</li>
      </ul>
    </div>
    <div class="file-cell">
      <p class="file-title">Version</p>
      <p class="value">{{ file.version }}</p>
    </div>
    <div class="file-cell">
      <p class="file-title">Released</p>
      <p class="value">{{ released(file.released) }}</p>
    </div>
    <div class="file-cell">
      <VirnectButton
        :label="$t('workspace.onpremiseSetting.upload.title')"
        @onClick="click(file)"
      />
    </div>
  </article>
</template>

<script>
import dayjs from '@/plugins/dayjs'
export default {
  props: {
    file: Object,
  },
  methods: {
    click(info) {
      this.$emit('fileUploadClick', info)
    },
    released(reletedTime) {
      const format = dayjs(this.file.released).format('YY.MM.DD hh:mm')
      return format === 'Invalid Date' ? reletedTime : format
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
