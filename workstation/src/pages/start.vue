<template>
  <div id="start" class="container">
    <h3 v-html="$t('workspace.welcome', { nickname: myProfile.nickname })" />
    <el-form
      class="virnect-login-form"
      ref="form"
      :model="form"
      @submit.native.prevent="submit"
    >
      <el-form-item class="horizon" :label="$t('workspace.setting.name')">
        <el-input
          v-model="form.name"
          :placeholder="
            $t('workspace.setting.namePlaceholder', {
              nickname: myProfile.nickname,
            })
          "
        />
        <span>{{ $t('workspace.setting.nameComment') }}</span>
      </el-form-item>
      <el-form-item class="horizon" :label="$t('workspace.setting.desc')">
        <el-input
          v-model="form.description"
          :placeholder="$t('workspace.setting.descPlaceholder')"
        />
        <span>{{ $t('workspace.setting.descComment') }}</span>
      </el-form-item>
      <el-form-item class="horizon" :label="$t('workspace.setting.image')">
        <el-upload
          ref="upload"
          action="#"
          :auto-upload="false"
          :on-change="imageSelected"
          :show-file-list="false"
          drag
        >
          <div class="avatar">
            <div
              class="image"
              :style="`background-image: url('${file || defaultFile}')`"
            />
            <i>
              <img src="~assets/images/icon/ic-camera-alt.svg" />
            </i>
          </div>
          <div class="el-upload__tip" slot="tip">
            <ul v-html="$t('workspace.setting.imageComment')" />
          </div>
        </el-upload>
      </el-form-item>
      <el-button type="info" @click="submit">
        {{ $t('workspace.setting.start') }}
      </el-button>
    </el-form>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import workspaceSerivce from '@/services/workspace'

export default {
  layout: 'noSidebar',
  data() {
    return {
      file: null,
      defaultFile: require('assets/images/workspace-profile.png'),
      form: {
        name: '',
        description: '',
      },
    }
  },
  computed: {
    ...mapGetters({
      myProfile: 'auth/myProfile',
    }),
  },
  methods: {
    imageSelected(file) {
      const reader = new FileReader()
      reader.readAsDataURL(file.raw)
      reader.onload = () => {
        this.file = reader.result
      }
    },
    async submit() {
      const { uploadFiles } = this.$refs.upload
      const form = {
        ...this.form,
        profile: uploadFiles.length
          ? uploadFiles[uploadFiles.length - 1].raw
          : null,
        userId: this.myProfile.uuid,
      }

      try {
        await workspaceSerivce.startWorkspace(form)
        location.href = '/'
      } catch (e) {
        this.$message.error({
          message: this.$t('workspace.setting.message.updateFail') + `\n(${e})`,
          duration: 2000,
          showClose: true,
        })
      }
    },
  },
}
</script>

<style lang="scss">
main {
  min-width: auto;
  background: #fff;
}

#start {
  max-width: 380px;
  margin: 0 auto;
  padding: 72px 0;

  h3 {
    font-size: 28px;
    line-height: 37px;
    text-align: center;
  }
  .el-form {
    margin: 64px 0;
    span {
      color: $font-color-desc;
      font-size: 13px;
    }
    .el-button {
      width: 100%;
      height: 48px;
    }
    .el-button > span {
      color: #fff;
      font-size: 15px;
    }
  }
  .virnect-login-form .el-form-item {
    margin-bottom: 40px;
  }
}
</style>
