<template>
  <el-card class="onpremise-download-file">
    <div>
      <label>Category</label>
      <input type="text" value="Android OS" readonly />
    </div>
    <div>
      <label>File Name</label>
      <input type="text" value="{file_name}.plist" readonly />
    </div>
    <div>
      <label>Version</label>
      <input type="text" value="2.5.10" readonly />
    </div>
    <div>
      <label>Released</label>
      <input type="text" value="21.11.08 05:36" readonly />
    </div>
    <VirnectButton label="설치 파일 업로드" @onClick="click" />
  </el-card>
</template>

<script>
export default {
  methods: {
    click() {
      this.$emit('fileUploadClick', 'remote')
    },
  },
}
</script>

<style lang="scss">
.onpremise-download-file {
  .el-card__body {
    display: flex;
    flex-wrap: wrap;
    flex-direction: row;
    justify-content: space-evenly;
    align-items: center;
    div {
      padding: 34px 0;
    }
    div:first-child {
      width: 17.6%;
    }
    div:first-child input {
      @include fontLevel(400);
    }
    div:nth-child(2) {
      width: 31.5%;
    }
    div:nth-child(3) {
      width: 7.4%;
    }
    div:nth-child(4) {
      width: 11.8%;
    }
    label {
      margin-bottom: 4px;
      color: #5e6b81;
      @include fontLevel(50);
    }
    input {
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
}
</style>
