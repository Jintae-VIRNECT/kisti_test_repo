<template>
  <div class="purchase-info">
    <h5>{{ $t('purchases.info.productPurchasesInfo') }}</h5>
    <dl>
      <dt>{{ $t('purchases.info.proudctPlan') }}</dt>
      <dd class="plans">
        <el-progress :percentage="100" :show-text="false" />
        <div class="column-plan">
          <img :src="products.remote.logo" />
          <span>{{ products.remote.label }}</span>
          <el-tag class="BASIC" effect="plain">BASIC</el-tag>
        </div>
        <div class="count">
          <span>{{ 6 }}</span>
          <span>/{{ 6 }}</span>
        </div>
      </dd>
      <dd class="plans">
        <el-progress :percentage="70" :show-text="false" />
        <div class="column-plan">
          <img :src="products.make.logo" />
          <span>{{ products.make.label }}</span>
          <el-tag class="BASIC" effect="plain">BASIC</el-tag>
        </div>
        <div class="count">
          <span>{{ 4 }}</span>
          <span>/{{ 6 }}</span>
        </div>
      </dd>
      <dd class="plans">
        <el-progress :percentage="40" :show-text="false" />
        <div class="column-plan">
          <img :src="products.view.logo" />
          <span>{{ products.view.label }}</span>
          <el-tag class="BASIC" effect="plain">BASIC</el-tag>
        </div>
        <div class="count">
          <span>{{ 3 }}</span>
          <span>/{{ 6 }}</span>
        </div>
      </dd>
      <dt>{{ $t('purchases.info.arStorageCapacity') }}</dt>
      <dd>{{ 10 }} GB</dd>
      <dt>{{ $t('purchases.info.arContentsViewCount') }}</dt>
      <dd>
        {{ (100000).toLocaleString() }}
        {{ $t('purchases.countsUnit') }}
      </dd>
      <dt>{{ $t('purchases.info.callTime') }}</dt>
      <dd>{{ 10 }} {{ $t('purchases.hoursUnit') }}</dd>
    </dl>
    <el-divider />
    <h5>{{ $t('purchases.info.extendPurchasesInfo') }}</h5>
    <dl>
      <dt>{{ $t('purchases.info.arStorageCapacity') }}</dt>
      <dd>{{ 10 }} GB</dd>
      <dt>{{ $t('purchases.info.arContentsViewCount') }}</dt>
      <dd>
        {{ (100000).toLocaleString() }}
        {{ $t('purchases.countsUnit') }}
      </dd>
      <dt>{{ $t('purchases.info.callTime') }}</dt>
      <dd>{{ 10 }} {{ $t('purchases.hoursUnit') }}</dd>
    </dl>
    <el-divider />
    <h6>{{ $t('purchases.info.nextPaymentDate') }}</h6>
    <span class="value">{{ new Date() | dateFormat }}</span>
    <el-divider />
    <h6>{{ $t('purchases.info.way') }}</h6>
    <span class="value">{{ '신용카드' }}</span>
    <el-divider />
    <el-button type="simple">
      {{ $t('purchases.info.changePlans') }}
    </el-button>
  </div>
</template>

<script>
import filters from '@/mixins/filters'
import products from '@/models/products'

export default {
  mixins: [filters],
  data() {
    return {
      products,
    }
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
