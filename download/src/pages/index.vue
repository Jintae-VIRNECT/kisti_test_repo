<template>
  <div id="home">
    <div class="ribbon">
      {{ $t('home.needPlan') }}
    </div>
    <div class="visual">
      <h3 v-html="$t('home.visual.title')" />
      <p v-html="$t('home.visual.desc')" />
    </div>
    <el-divider />
    <el-tabs v-model="activeTab">
      <!-- remote -->
      <el-tab-pane name="remote" :label="$t('home.remote')" disabled>
      </el-tab-pane>
      <!-- make -->
      <el-tab-pane name="make" :label="$t('home.make')">
        <el-card>
          <h6 v-html="$t('home.pc')" />
          <h5 v-html="$t('home.windowsInstall')" />
          <img src="~assets/images/img-make-pc.svg" />
          <span class="release">
            {{ `${$t('home.release')}: ${make.release}` }}
          </span>
          <span class="version">{{ make.version }}</span>
          <el-button type="primary" @click="download(make.appId)">
            {{ $t('home.installFileDownload') }}
          </el-button>
          <!-- <el-button type="text" @click="link(make.guideUrl)">
            {{ $t('home.guideDownload') }}
          </el-button> -->
        </el-card>
      </el-tab-pane>
      <!-- view -->
      <el-tab-pane name="view" :label="$t('home.view')">
        <el-card>
          <h6 v-html="$t('home.realwear')" />
          <h5 v-html="$t('home.hmt-1')" />
          <img src="~assets/images/img-view-realwear.png" />
          <span class="release">
            {{ `${$t('home.release')}: ${viewHmt1.release}` }}
          </span>
          <span class="version">{{ viewHmt1.version }}</span>
          <el-button type="primary" @click="download(viewHmt1.appId)">
            {{ $t('home.installFileDownload') }}
          </el-button>
          <!-- <el-button type="text" @click="link(viewHmt1.guideUrl)">
            {{ $t('home.guideDownload') }}
          </el-button> -->
        </el-card>
        <el-card>
          <h6 v-html="$t('home.googlePlay')" />
          <h5 v-html="$t('home.mobile')" />
          <img src="~assets/images/img-view-google-play.png" />
          <span class="release">
            {{ `${$t('home.release')}: ${viewApp.release}` }}
          </span>
          <span class="version">{{ viewApp.version }}</span>
          <el-button type="primary" @click="link(viewApp.link)" disabled>
            {{ $t('home.downloadLink') }}
          </el-button>
          <!-- <el-button type="text" @click="link(viewApp.link)">
            {{ $t('home.guideDownload') }}
          </el-button> -->
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import fileDownload from 'js-file-download'

export default {
  data() {
    return {
      activeTab: 'make',
      make: {
        release: '2020.02.02',
        version: 'v.1.0.1',
        appId: `8`,
        guideUrl: '',
      },
      viewHmt1: {
        release: '2020.02.02',
        version: 'v.1.0.1',
        appId: `1`,
        guideUrl: '',
      },
      viewApp: {
        release: '2020.02.02',
        version: 'v.1.0.1',
        link: '',
        guideUrl: '',
      },
    }
  },
  methods: {
    async download(id) {
      const data = await this.$api('DOWNLOAD', {
        headers: { 'Content-Type': 'application/octet-stream' },
        route: { id },
      })
      console.log(data)
      // fileDownload(data)
    },
    link(url) {
      window.open(url)
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
    width: 100%;
    height: 50px;
    line-height: 50px;
    background: #e6f0ff;
  }
  .visual {
    height: 340px;
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
    width: 900px;
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
  .el-tabs__header {
    margin-left: 140px;
  }
  .el-tabs__nav-wrap::after {
    display: none;
  }
  & > .el-divider {
    top: 64px;
    width: 100%;
    height: 1px;
    margin: 0;
    background: #f3f6f9;
  }

  .el-tabs__content {
    margin: 120px auto 160px;
  }
  .el-tabs__content .el-card {
    display: inline-block;
    width: 372px;
    margin: 0 18px;
    box-shadow: none;
  }
  .el-card .el-card__body {
    padding: 36px 44px 30px;
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
      height: 36px;
      margin: 5px auto;
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
