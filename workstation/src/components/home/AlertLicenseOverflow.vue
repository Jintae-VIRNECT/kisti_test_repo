<template>
  <el-dialog
    class="license-overflow-modal"
    :visible.sync="showMe"
    width="440px"
    top="11vh"
  >
    <div>
      <h4 v-html="$t('home.licenseOverflow.title')" />
      <p v-html="$t('home.licenseOverflow.desc')" />

      <dl class="plans">
        <dd v-for="product in plansInfo.products" :key="product.value">
          <el-progress
            :class="{ red: product.usedAmount > product.amount }"
            :percentage="(product.usedAmount / product.amount) * 100"
            :show-text="false"
          />
          <div class="column-plan">
            <img :src="product.logo" />
            <span>{{ product.label }}</span>
          </div>
          <div class="count">
            <span :class="{ red: product.usedAmount > product.amount }">
              {{ product.usedAmount }}
            </span>
            <span>/{{ product.amount }}</span>
          </div>
        </dd>
      </dl>

      <el-button type="primary" @click="$router.push('/members')">
        {{ $t('home.licenseOverflow.submit') }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import modalMixin from '@/mixins/modal'
import { mapGetters } from 'vuex'

export default {
  mixins: [modalMixin],
  computed: {
    ...mapGetters({
      plansInfo: 'plan/plansInfo',
    }),
  },
}
</script>

<style lang="scss">
#__nuxt .license-overflow-modal {
  .el-dialog__header {
    border-bottom: none;
  }
  .el-dialog__body {
    max-height: 60vh;
    padding: 6px 40px 40px;
    text-align: center;

    h4 {
      margin-bottom: 8px;
      font-weight: 600;
      font-size: 16px;
      line-height: 1.5;
    }
    p {
      font-size: 13px;
    }
    .el-button {
      width: 100%;
      height: 40px;
      margin: 24px 0 8px;
    }
  }
  .plans {
    width: 280px;
    margin: 40px auto 0;
    text-align: left;
    dd {
      margin-bottom: 16px;
    }
    .el-progress {
      margin-bottom: 11px;
      .el-progress-bar__inner {
        background: #007cfe;
      }
      &.red .el-progress-bar__inner {
        background: $color-danger;
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
        &.red {
          color: $color-danger;
        }
      }
      & > span:nth-child(2) {
        margin: 0 -0.1em;
        letter-spacing: 0.1em;
      }
    }
  }
}
</style>
