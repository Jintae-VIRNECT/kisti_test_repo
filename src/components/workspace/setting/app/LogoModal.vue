<template>
  <el-dialog
    class="app-logo-modal onpremise-setting-modal"
    :visible.sync="showMe"
    :title="$t('workspace.onpremiseSetting.logo.title')"
    width="551px"
    top="11vh"
  >
    <div>
      <p>{{ $t('workspace.appSetting.remote.desc') }}</p>
      <WorkspaceSettingAppLogoUploader
        v-for="(logo, index) in logos"
        :key="index"
        :logo="logo"
        :clearFiles="logo.src === undefined"
        @logoSelected="logoSelected"
      />
    </div>
    <div slot="footer">
      <el-button type="text" @click="deleteImage" :disabled="disabledState">
        {{ $t('common.delete') }}
      </el-button>
      <el-button type="primary" @click="submit" :disabled="submitDisabled">
        {{ $t('workspace.onpremiseSetting.logo.submit') }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import { mapGetters } from 'vuex'
import modalMixin from '@/mixins/modal'
import workspaceService from '@/services/workspace'
import axios from 'axios'

export default {
  mixins: [modalMixin],
  data() {
    return {
      androidLogoType1File: null,
      androidLogoType2File: null,
      hololens2LogoTypeFile: null,
      logos: [
        {
          title: 'workspace.appSetting.remote.android.type1.title',
          caution: 'workspace.appSetting.remote.android.type1.caution',
          src: this.androidType1Logo,
          file: null,
          defaultLogo: require('assets/images/logo/android_logo_type1.svg'),
          type: 'androidType1Logo',
        },
        {
          title: 'workspace.appSetting.remote.android.type2.title',
          caution: 'workspace.appSetting.remote.android.type2.caution',
          src: this.androidType2Logo,
          file: null,
          defaultLogo: require('assets/images/logo/android_logo_type2.svg'),
          type: 'androidType2Logo',
        },
        {
          title: 'workspace.appSetting.remote.hololens2.title',
          caution: 'workspace.appSetting.remote.hololens2.caution',
          src: this.hololens2Logo,
          file: null,
          defaultLogo: require('assets/images/logo/hololens2_logo_type.png'),
          type: 'hololens2Logo',
        },
      ],
      isDeleted: false,
      submitDisabled: true,
    }
  },
  computed: {
    ...mapGetters({
      activeWorkspace: 'auth/activeWorkspace',
      androidType1Logo: 'layout/androidType1Logo',
      androidType2Logo: 'layout/androidType2Logo',
      hololens2Logo: 'layout/hololens2Logo',
    }),
    disabledState() {
      return this.logos.every(logo => logo.src === undefined)
    },
  },
  methods: {
    setLogoFiles() {
      this.logos.forEach(logo => {
        switch (logo.type) {
          case 'androidType1Logo':
            logo.src = this.androidType1Logo
            break
          case 'androidType2Logo':
            logo.src = this.androidType2Logo
            break
          case 'hololens2Logo':
            logo.src = this.hololens2Logo
            break
        }
      })
    },
    opened() {
      this.setLogoFiles()
      this.submitDisabled = true
    },
    logoSelected(file, base64, type) {
      this.logos.forEach(logo => {
        if (logo.type === type) {
          logo.src = base64
          logo.file = file
          this.submitDisabled = false
        }
      })
    },
    deleteImage() {
      this.logos.forEach(logo => {
        logo.src = undefined
        logo.file = null
      })
      this.isDeleted = true
      this.submitDisabled = false
    },
    async urlToFile(url) {
      const { data } = await axios({
        url,
        method: 'GET',
        responseType: 'blob',
      })
      return new File([data], 'img.png', { type: data.type })
    },
    async submit() {
      try {
        const uploadFiles = {}
        const uploadSrc = {}
        this.logos.forEach(async logo => {
          uploadFiles[logo.type] = logo.file
            ? logo.file
            : await this.urlToFile(logo.src)

          uploadSrc[logo.type] = logo.src
        })

        await workspaceService.setWorkspaceAppLogo(uploadFiles, this.isDeleted)
        this.$store.commit('layout/SET_APP_LOGO', uploadSrc)
        this.showMe = false
      } catch (e) {
        const message =
          {
            3000: this.$t('common.message.notAllowFileExtension'),
            3001: this.$t('common.message.notAllowFileSize'),
          }[e.code] || e
        this.$message.error({
          message,
          duration: 2000,
          showClose: true,
        })
      }
    },
  },
  mounted() {
    this.setLogoFiles()
  },
}
</script>

<style lang="scss">
#__nuxt .app-logo-modal {
  .el-dialog__body {
    max-height: unset;
  }
  .product {
    margin-top: 28px;
    text-align: right;
    h5 {
      float: left;
      font-weight: 500;
      font-size: 16px;
    }
    .el-upload {
      color: #0052cc;
      font-weight: 500;
      font-size: 12px;
    }
  }
  .preview {
    height: 140px;
    display: flex;
    flex-direction: row;
    align-content: center;
    justify-content: space-around;
    align-items: center;

    .area {
      position: unset;
      width: 212px;
      height: 120px;
      background-color: #242427;
    }
    .area:after {
      background: none;
    }
    .tooltip {
      top: -13px;
      left: 9px;
    }
    .area > span {
      top: 0;
      vertical-align: middle;
    }
    .area > .editable {
      display: inline-block;
      position: absolute;
      height: 100%;
      img {
        height: 100%;
        width: 100%;
      }
    }
    .sub-title .virnect-thumbnail {
      display: inline-block;
      margin-right: 4px;
      vertical-align: middle;
    }
  }
}
</style>
