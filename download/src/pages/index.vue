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
      <el-tab-pane
        v-for="(product, name) in products"
        :label="$t(`home.${name}`)"
        :name="name"
        :key="name"
      >
        <el-card v-for="app in product" :key="app.id">
          <h6 v-html="app.os" />
          <h5 v-html="app.device" />
          <img :src="app.imageUrl" />
          <span class="release">
            {{ $t('home.release') }}: {{ app.releaseTime | dateFormat }}
          </span>
          <span class="version">{{ app.version }}</span>
          <el-button type="primary" @click="download(app.appUrl)">
            {{ $t('home.installFileDownload') }}
          </el-button>
          <el-button
            type="text"
            @click="download(app.guideUrl)"
            :disabled="!app.guideUrl"
          >
            {{ $t('home.guideDownload') }}
          </el-button>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import fileDownload from 'js-file-download'
import { filters } from '@/plugins/dayjs'

export default {
  data() {
    return {
      activeTab: null,
      products: {
        remote: [],
        make: [],
        view: [],
      },
    }
  },
  watch: {
    async activeTab(tab) {
      if (this.products[tab].length) return false

      const data = await this.$api('APP_LIST', {
        route: { productName: tab },
      })
      this.products[tab] = data.appInfoList
    },
  },
  filters,
  methods: {
    async download(url) {
      window.open(url)
      // const data = await this.$api('DOWNLOAD', {
      //   headers: { 'Content-Type': 'application/octet-stream' },
      //   route: { id },
      // })
      // fileDownload(data)
    },
    link(url) {
      window.open(url)
    },
  },
  mounted() {
    this.activeTab =
      {
        '/remote': 'remote',
        '/make': 'make',
        '/view': 'view',
      }[this.$route.path] || 'remote'
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
    margin: 0 18px 32px;
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
