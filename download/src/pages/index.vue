<template>
  <div id="home">
    <div class="ribbon">
      {{ $t('home.needPlan') }}
    </div>
    <div class="visual">
      <h3 v-html="$t('home.visual.title')" />
      <p v-html="$t('home.visual.desc')" />
    </div>
    <el-tabs>
      <el-tab-pane :label="$t('home.make')">
        <el-card>
          <h6 v-html="$t('home.pc')" />
          <h5 v-html="$t('home.windowsInstall')" />
          <img src="~assets/images/img-make-pc.svg" />
          <span class="release">
            {{ `${$t('home.release')}: ${make.release}` }}
          </span>
          <span class="version">{{ make.version }}</span>
          <el-button type="primary" @click="download(make.productName)">
            {{ $t('home.installFileDownload') }}
          </el-button>
          <el-button type="text">{{ $t('home.guideDownload') }}</el-button>
        </el-card>
      </el-tab-pane>
      <el-tab-pane :label="$t('home.view')" disabled> </el-tab-pane>
      <el-tab-pane :label="$t('home.remote')" disabled> </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import urls from 'WC-Modules/javascript/api/virnectPlatform/urls'
import api from '@/api/gateway'

export default {
  data() {
    return {
      make: {
        release: '2020.02.02',
        version: 'v.1.0.1',
        productName: 'make',
        guideUrl: '',
      },
    }
  },
  methods: {
    async download(productName) {
      const data = await api('DOWNLOAD', {
        route: { productName },
      })
      window.open(data)
    },
  },
}
</script>

<style lang="scss">
#home {
  color: $font-color-content;
  font-weight: 500;
  text-align: center;

  .ribbon {
    position: fixed;
    top: 64px;
    z-index: 5;
    width: 100vw;
    height: 50px;
    line-height: 50px;
    background: #e6f0ff;
  }
  .visual {
    height: 340px;
    margin-top: 114px;
    padding: 93px;
    color: #fff;
    background: url('~assets/images/img-top.jpg') center;
    background-size: cover;

    h3 {
      font-size: 30px;
    }
    p {
      margin-top: 10px;
      font-size: 15px;
    }
  }
  .el-tabs {
    width: 640px;
    margin: 0 auto;

    [role='tab'] {
      height: 64px;
      padding: 0 60px;
      font-size: 18px;
      line-height: 64px;

      &:nth-child(2) {
        padding-left: 0;
      }
      &:last-child {
        padding-right: 0;
      }
    }
  }
  .el-tabs__nav-wrap::after {
    display: none;
  }

  .el-tabs__content {
    margin: 94px auto 120px;
  }
  .el-tabs__content .el-card {
    width: 372px;
    margin: 0 auto;
    box-shadow: none;
  }
  .el-card .el-card__body {
    padding: 36px 44px 20px;
    h6 {
      font-size: 18px;
    }
    h5 {
      font-size: 26px;
      span {
        color: $color-primary;
      }
    }
    img {
      margin: 10px auto 16px;
    }
    & > span {
      display: block;
    }
    .release {
      color: #8e95a1;
      font-weight: normal;
    }
    .version {
      margin-bottom: 23px;
      color: #3f465a;
      font-size: 18px;
    }
    .el-button {
      display: block;
      margin: 6px auto;
    }
  }

  // transition
  .rise-enter {
    transform: translateY(10px);
    opacity: 0;
  }
  .rise-enter-to {
    transform: translateY(0);
    opacity: 1;
    transition: 0.5s 0.3s;
    &-2 {
      transition: 0.5s 0.5s;
    }
  }
}
</style>
