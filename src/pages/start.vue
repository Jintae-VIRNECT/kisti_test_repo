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
          :maxlength="30"
        />
        <span>{{ $t('workspace.setting.nameComment') }}</span>
      </el-form-item>
      <el-form-item class="horizon" :label="$t('workspace.setting.desc')">
        <el-input
          v-model="form.description"
          :placeholder="$t('workspace.setting.descPlaceholder')"
          :maxlength="40"
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
            <VirnectThumbnail
              :size="80"
              :image="file"
              :defaultImage="$defaultWorkspaceProfile"
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
  middleware({ store, redirect }) {
    //마스터 워크스페이스가 존재할 경우 튕겨냄
    if (store.getters['auth/myWorkspaces'].some(w => w.role === 'MASTER')) {
      redirect('/')
    }
  },
  data() {
    return {
      file: null,
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
      if (!form.name) {
        form.name = this.$t('workspace.setting.namePlaceholder', {
          nickname: this.myProfile.nickname,
        })
      }
      if (!form.description) {
        form.description = form.name
      }

      //  API 요청
      try {
        await workspaceSerivce.startWorkspace(form)
      } catch (e) {
        this.$message.error({
          message: this.$t('workspace.setting.message.updateFail') + `\n(${e})`,
          duration: 2000,
          showClose: true,
        })
        return false
      }
      // 시작 완료창
      try {
        await this.$confirm(
          this.$t('workspace.started.desc'),
          this.$t('workspace.started.title'),
          {
            confirmButtonText: this.$t('workspace.info.addMember'),
            cancelButtonText: this.$t('common.confirm'),
          },
        )
        location.href = '/members?modal=memberAdd'
      } catch (e) {
        location.href = '/'
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
