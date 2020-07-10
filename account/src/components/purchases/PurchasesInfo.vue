<template>
  <div class="purchase-info">
    <h5>{{ $t('purchases.info.productPurchasesInfo') }}</h5>
    <dl>
      <dt>{{ $t('purchases.info.proudctPlan') }}</dt>
      <dd
        class="plans"
        v-for="product in plansInfo.products"
        :key="product.value"
      >
        <el-progress
          :percentage="(product.usedAmount / product.amount) * 100"
          :show-text="false"
        />
        <div class="column-plan">
          <img :src="product.logo" />
          <span>{{ product.label }}</span>
          <el-tag :class="product.licenseType" effect="plain">
            {{ product.licenseType }}
          </el-tag>
        </div>
        <div class="count">
          <span>{{ product.usedAmount }}</span>
          <span>/{{ product.amount }}</span>
        </div>
      </dd>
      <dt>{{ $t('purchases.info.arStorageCapacity') }}</dt>
      <dd>{{ paymentInfo.basisAvailable.storage }} GB</dd>
      <dt>{{ $t('purchases.info.arContentsViewCount') }}</dt>
      <dd>
        {{ paymentInfo.basisAvailable.callTime }}
        {{ $t('purchases.countsUnit') }}
      </dd>
      <dt>{{ $t('purchases.info.callTime') }}</dt>
      <dd>
        {{ paymentInfo.basisAvailable.viewCount }}
        {{ $t('purchases.hoursUnit') }}
      </dd>
    </dl>
    <el-divider />
    <h5>{{ $t('purchases.info.extendPurchasesInfo') }}</h5>
    <dl>
      <dt>{{ $t('purchases.info.arStorageCapacity') }}</dt>
      <dd>{{ paymentInfo.extendAvailable.storage }} GB</dd>
      <dt>{{ $t('purchases.info.arContentsViewCount') }}</dt>
      <dd>
        {{ paymentInfo.extendAvailable.callTime }}
        {{ $t('purchases.countsUnit') }}
      </dd>
      <dt>{{ $t('purchases.info.callTime') }}</dt>
      <dd>
        {{ paymentInfo.extendAvailable.viewCount }}
        {{ $t('purchases.hoursUnit') }}
      </dd>
    </dl>
    <el-divider />
    <h6>{{ $t('purchases.info.nextPaymentDate') }}</h6>
    <span class="value">{{ paymentInfo.nextPayDate | dateFormat }}</span>
    <el-divider />
    <h6>{{ $t('purchases.info.way') }}</h6>
    <span class="value">{{ paymentInfo.way || '-' }}</span>
    <el-divider />
    <a :href="$url.pay">
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
    plansInfo: {
      type: Object,
      default: () => ({}),
    },
    paymentInfo: {
      type: Object,
      default: () => ({
        basisAvailable: {},
        extendAvailable: {},
      }),
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
</style>
