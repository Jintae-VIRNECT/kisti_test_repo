<template>
  <el-card class="plans-used">
    <div slot="header">
      <h3>
        <span>{{ $t(`${i18nGroup}.title`) }}</span>
        <el-tooltip :content="$t(`${i18nGroup}.desc`)" placement="right">
          <img src="~assets/images/icon/ic-error.svg" />
        </el-tooltip>
      </h3>
      <!-- <a
        v-if="activeWorkspace.role === 'MASTER'"
        :href="$url.pay"
        target="_blank"
      >
        {{ $t('home.plansInfo.buy') }}
      </a> -->
    </div>
    <div>
      <div class="max">
        <span>{{ $t(`${i18nGroup}.max`) }}</span>
        <span class="count">
          {{ info.max | toLocaleString }} {{ $t(`${i18nGroup}.unit`) }}
        </span>
      </div>
      <div class="detail">
        <span>{{ $t(`${i18nGroup}.used`) }}</span>
        <el-progress
          :percentage="info.percent"
          :show-text="false"
          :stroke-width="8"
        />
        <div class="used">
          <span class="count">
            {{ info.current | toLocaleString }} {{ $t(`${i18nGroup}.unit`) }}
          </span>
          <span>{{ $t('home.plansInfo.used') }}</span>
        </div>
        <div class="remain">
          <span>
            {{ info.remain | toLocaleString }} {{ $t(`${i18nGroup}.unit`) }}
          </span>
          <span>{{ $t('home.plansInfo.remain') }}</span>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  props: {
    i18nGroup: String,
    info: Object,
  },
  computed: {
    ...mapGetters({
      activeWorkspace: 'auth/activeWorkspace',
    }),
  },
  filters: {
    toLocaleString(num) {
      return num ? num.toLocaleString() : 0
    },
  },
}
</script>

<style lang="scss">
.plans-used .el-card__body {
  span {
    color: $font-color-desc;

    line-height: 20px;
  }
  .max {
    margin-bottom: 16px;
    span {
      color: $font-color-content;
      font-size: 13px;
    }
    .count {
      float: right;
      font-size: 14px;
    }
  }
  .el-progress {
    margin: 8px 0 12px;
    &-bar__inner {
      background: #007cfe;
    }
  }
  .detail > span {
    font-size: 12px;
  }
  .used {
    display: inline-block;
    font-size: 14px;
    .count {
      color: $font-color-content;
    }
  }
  .remain {
    float: right;
    font-size: 13px;
  }
}
</style>
