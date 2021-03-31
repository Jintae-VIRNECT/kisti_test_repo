<template>
  <!-- simple -->
  <div v-if="simple" class="purchase-info simple">
    <h5>{{ $t('purchases.info.proudctPlan') }}</h5>
    <dl>
      <dd
        class="plans"
        v-for="product in plansInfo.products"
        :key="product.value"
      >
        <div class="column-plan">
          <img :src="product.logo" />
          <span>{{ product.label }}</span>
        </div>
        <div class="count">
          <span>{{ product.amount }}</span>
        </div>
      </dd>
    </dl>
    <el-divider />
    <h5>{{ $t('home.payment.extend') }}</h5>
    <dl>
      <dt>{{ $t('purchases.info.arStorageCapacity') }}</dt>
      <dd>{{ plansInfo.storage.max.toLocaleString() }} GB</dd>
      <dt>{{ $t('purchases.info.arContentsViewCount') }}</dt>
      <dd>
        {{ plansInfo.viewCount.max.toLocaleString() }}
        {{ $t('purchases.countsUnit') }}
      </dd>
    </dl>
    <el-divider />
    <h6>
      <span>{{ $t('purchases.info.nextPaymentDate') }}</span>
    </h6>
    <span class="value">
      {{ paymentInfo.nextPayDate | fullYearDateFormat }}
    </span>
    <el-divider />
    <h6>{{ $t('purchases.info.way') }}</h6>
    <span class="value">{{ way }}</span>
    <a :href="$url.pay" target="_blank">
      <el-button type="simple">
        {{ $t('common.payCenter') }}
      </el-button>
    </a>
  </div>

  <!-- full info -->
  <div v-else class="purchase-info">
    <h5>{{ $t('purchases.info.productPurchasesInfo') }}</h5>
    <dl>
      <dt>{{ $t('purchases.info.proudctPlan') }}</dt>
      <dd
        class="plans"
        v-for="product in plansInfo.products"
        :key="product.value"
      >
        <el-progress
          :percentage="percent(product.usedAmount, product.amount)"
          :show-text="false"
        />
        <div class="column-plan">
          <img :src="product.logo" />
          <span>{{ product.label }}</span>
        </div>
        <div class="count">
          <span>{{ product.usedAmount }}</span>
          <span>/{{ product.amount }}</span>
        </div>
      </dd>
      <dt>{{ $t('purchases.info.arStorageCapacity') }}</dt>
      <dd>{{ plansInfo.storage.default.toLocaleString() }} GB</dd>
      <dt>{{ $t('purchases.info.arContentsViewCount') }}</dt>
      <dd>
        {{ plansInfo.viewCount.default.toLocaleString() }}
        {{ $t('purchases.countsUnit') }}
      </dd>
      <dt>{{ $t('purchases.info.callTime') }}</dt>
      <dd>
        <!-- {{ plansInfo.callTime.default.toLocaleString() }}
        {{ $t('purchases.hoursUnit') }} -->
        {{ $t('purchases.infinity') }}
      </dd>
    </dl>
    <el-divider />
    <h5>{{ $t('purchases.info.extendPurchasesInfo') }}</h5>
    <dl>
      <dt>{{ $t('purchases.info.arStorageCapacity') }}</dt>
      <dd>{{ plansInfo.storage.add.toLocaleString() }} GB</dd>
      <dt>{{ $t('purchases.info.arContentsViewCount') }}</dt>
      <dd>
        {{ plansInfo.viewCount.add.toLocaleString() }}
        {{ $t('purchases.countsUnit') }}
      </dd>
    </dl>
    <el-divider />
    <h6>
      <span>{{ $t('purchases.info.licenseExpireDate') }}</span>
      <el-tooltip
        :content="$t('purchases.info.licenseExpireDateDesc')"
        placement="right"
      >
        <img src="~assets/images/icon/ic-error.svg" />
      </el-tooltip>
    </h6>
    <span class="value">{{ plansInfo.endDate | fullYearDateFormat }}</span>
    <h6>
      <span>{{ $t('purchases.info.nextPaymentDate') }}</span>
      <el-tooltip
        :content="$t('purchases.info.nextPaymentDateDesc')"
        placement="right"
      >
        <img src="~assets/images/icon/ic-error.svg" />
      </el-tooltip>
    </h6>
    <span class="value">
      {{ paymentInfo.nextPayDate | fullYearDateFormat }}
    </span>
    <el-divider />
    <h6>{{ $t('purchases.info.way') }}</h6>
    <span class="value">{{ way }}</span>
    <el-divider />
    <a :href="$url.pay" target="_blank">
      <el-button type="simple">
        {{ $t('purchases.info.changePlans') }}
      </el-button>
    </a>
  </div>
</template>

<script>
import filters from '@/mixins/filters'

export default {
  mixins: [filters],
  props: {
    simple: Boolean,
    plansInfo: {
      type: Object,
      default: () => ({}),
    },
    paymentInfo: {
      type: Object,
      default: () => ({}),
    },
  },
  computed: {
    way() {
      const { payFlag, way } = this.paymentInfo
      const { planStatus } = this.plansInfo
      if (payFlag === 'Y') {
        return way
      } else if (payFlag === 'D' && planStatus === 'ACTIVE') {
        return this.$t('purchases.info.freePlan')
      } else {
        return '-'
      }
    },
  },
  methods: {
    percent(child, parent) {
      if (child === 0 && parent === 0) return 0
      return (child / parent) * 100
    },
  },
}
</script>

<style lang="scss">
.purchase-info {
  h5 {
    margin-bottom: 20px;
  }
  dt,
  h6 {
    float: left;
    & > * {
      display: inline-block;
      margin-right: 2px;
      vertical-align: middle;
    }
  }
  dt {
    color: $font-color-desc;
    font-size: 13px;
    line-height: 20px;
  }
  dd,
  .value {
    display: block;
    margin-bottom: 16px;
    line-height: 20px;
    text-align: right;
  }
  .value {
    margin-bottom: 12px;
  }
  .el-button {
    width: 100%;
    height: 36px;
    margin: 10px 0 6px;
  }
  .plans {
    margin-bottom: 21px;
    text-align: left;
    &:first-of-type {
      padding-top: 32px;
    }
    .el-progress {
      margin-bottom: 11px;
      .el-progress-bar__inner {
        background: #007cfe;
      }
    }
    .column-plan {
      display: inline-block;
    }
    .count {
      float: right;
      line-height: 35px;
      white-space: nowrap;
      & > span:first-child {
        color: #186ae2;
      }
      & > span:nth-child(2) {
        margin: 0 -0.1em;
        letter-spacing: 0.1em;
      }
    }
  }
}

.purchase-info.simple {
  .plans:first-of-type {
    padding-top: 0;
  }
  .plans {
    margin-bottom: 16px;
  }
  .plans .count > span:first-child {
    color: $font-color-desc;
  }
}
</style>
