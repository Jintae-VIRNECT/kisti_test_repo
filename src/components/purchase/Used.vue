<template>
  <el-card class="purchases-used">
    <el-tabs slot="header" v-model="activeTab">
      <el-tab-pane
        v-for="[name, tab] in Object.entries(tabs)"
        :key="name"
        :name="name"
        :label="$t(tab.label)"
      />
    </el-tabs>
    <el-row>
      <el-col :span="6">
        <span>{{ $t(`${usedI18n}.used`) }}</span>
        <el-tooltip :content="$t(`${usedI18n}.usedDesc`)" placement="right">
          <img src="~assets/images/icon/ic-error.svg" />
        </el-tooltip>
      </el-col>
      <el-col :span="18">
        <el-progress
          :percentage="tabs[activeTab].percent"
          :show-text="false"
          :stroke-width="12"
          stroke-linecap="square"
        />
        <div class="used-text">
          <span>
            {{ tabs[activeTab].current }}
            {{ $t(`${usedI18n}.unit`) }}
          </span>
          <span>{{ $t('purchases.used') }}</span>
        </div>
        <div class="remain-text">
          {{
            $t('purchases.remainOfMax', {
              max: tabs[activeTab].max,
              remain: tabs[activeTab].remain,
              unit: $t(`${usedI18n}.unit`),
            })
          }}
        </div>
      </el-col>
    </el-row>
    <el-row>
      <el-col :span="6">
        <span>{{ $t(`${usedI18n}.capacity`) }}</span>
        <el-tooltip :content="$t(`${usedI18n}.capacityDesc`)" placement="right">
          <img src="~assets/images/icon/ic-error.svg" />
        </el-tooltip>
      </el-col>
      <el-col :span="18">
        <el-progress
          :percentage="basisRate"
          type="circle"
          :show-text="false"
          :stroke-width="12"
          stroke-linecap="butt"
          :width="192"
        />
        <div class="progress-text">
          <span>{{ $t(`${usedI18n}.maxShort`) }}</span>
          <span>
            {{ tabs[activeTab].max }}
            {{ $t(`${usedI18n}.unit`) }}
          </span>
        </div>
        <dl>
          <dt>{{ $t(`${usedI18n}.max`) }}</dt>
          <dd>
            {{ tabs[activeTab].max }}
            {{ $t(`${usedI18n}.unit`) }}
          </dd>
          <el-divider />
          <dt class="default">{{ $t(`${usedI18n}.default`) }}</dt>
          <dd>
            {{ plansInfo[activeTab].default.toLocaleString() }}
            {{ $t(`${usedI18n}.unit`) }}
          </dd>
          <dt class="extend">{{ $t(`${usedI18n}.extend`) }}</dt>
          <dd>
            {{ plansInfo[activeTab].add.toLocaleString() }}
            {{ $t(`${usedI18n}.unit`) }}
          </dd>
          <a :href="$url.pay">
            <el-button type="simple">
              {{ $t('purchases.change') }}
            </el-button>
          </a>
        </dl>
      </el-col>
    </el-row>
  </el-card>
</template>

<script>
/**
 * 퍼센트 계산
 */
function rate(now, max) {
  const rate = now / max
  if (isNaN(rate)) return 0
  else if (rate === Infinity) return 100
  else return rate * 100
}
/**
 * 차트에 필요한 계산 처리
 */
function calc(used, max) {
  return {
    current: used.toLocaleString(),
    max: max.toLocaleString(),
    percent: rate(used, max),
    remain: (max - used).toLocaleString(),
  }
}

export default {
  props: {
    plansInfo: {
      type: Object,
      default: () => ({
        storage: {},
        viewCount: {},
        callTime: {},
      }),
    },
  },
  data() {
    return {
      activeTab: 'storage',
      tabs: {
        storage: {
          label: 'purchases.info.arStorageCapacity',
          i18nGroup: 'purchases.arStorage',
          ...calc(this.plansInfo.storage.current, this.plansInfo.storage.max),
        },
        viewCount: {
          label: 'purchases.info.arContentsViewCount',
          i18nGroup: 'purchases.arContent',
          ...calc(
            this.plansInfo.viewCount.current,
            this.plansInfo.viewCount.max,
          ),
        },
        // callTime: {
        //   label: 'purchases.info.callTime',
        //   i18nGroup: 'purchases.call',
        //   ...calc(this.plansInfo.callTime.current, this.plansInfo.callTime.max),
        // },
      },
    }
  },
  computed: {
    usedI18n() {
      return this.tabs[this.activeTab].i18nGroup
    },
    basisRate() {
      const basis = this.plansInfo[this.activeTab].default
      const extend = this.plansInfo[this.activeTab].add
      return rate(basis, basis + extend)
    },
  },
}
</script>

<style lang="scss">
.purchases-used {
  .el-card__header {
    padding-top: 0;
    padding-bottom: 0;
    .el-tabs .el-tabs__item {
      height: 64px;
      font-size: 16px;
      line-height: 64px;
    }
    .el-tabs__nav-wrap::after {
      display: none;
    }
  }
  .el-card__body {
    padding: 40px;
  }
  // 사용량
  .el-row:first-child {
    padding-bottom: 46px;
    border-bottom: solid 1px #eaedf3;
    .el-progress {
      margin: 7px 0 10px;
    }
    .used-text {
      float: left;
      color: $font-color-desc;
      & > span:first-child {
        color: $font-color-content;
      }
    }
    .remain-text {
      color: $font-color-desc;
      text-align: right;
    }
  }
  // 용량
  .el-row:nth-child(2) {
    padding-top: 38px;
    .el-progress-circle,
    .progress-text {
      margin-top: 20px;
      margin-left: -6px;
    }
    .el-progress--circle path:first-child {
      stroke: #a4c7ff;
    }
    .el-progress--circle path:last-child {
      stroke: #007cfe;
    }
    .el-progress--circle[aria-valuenow='0'] path:first-child {
      stroke: #e6e9ee;
    }
    .el-col {
      position: relative;
    }
    .progress-text {
      position: absolute;
      top: 33%;
      width: 192px;
      height: 192px;
      text-align: center;
      & > span {
        display: block;
        font-size: 12px;
      }
      & > span:last-child {
        font-size: 24px;
        line-height: 30px;
      }
    }
    dl {
      float: right;
      width: 300px;
      margin-top: 36px;
      margin-right: 18px;
      line-height: 20px;
      dt {
        float: left;
        font-size: 13px;
      }
      dd {
        margin-bottom: 10px;
        text-align: right;
      }
      .default:before,
      .extend:before {
        position: relative;
        top: 1px;
        display: inline-block;
        width: 10px;
        height: 10px;
        margin-right: 10px;
        background: #237df5;
        border-radius: 2px;
        content: '';
      }
      .extend:before {
        background: #a4c7ff;
      }
    }
    .el-divider {
      margin: 10px 0;
    }
    .el-button {
      width: 100%;
      height: 36px;
      margin-top: 20px;
    }
  }
  // 설명
  .el-col:first-child {
    & > * {
      display: inline-block;
      vertical-align: middle;
    }
  }
}
</style>
