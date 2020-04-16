<template>
  <div id="coupon" class="virnect-card">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>{{ $t('menu.account') }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{ $t('menu.coupon') }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('menu.coupon') }}</h2>
        <p>{{ $t('coupon.desc') }}</p>
      </div>
      <el-row>
        <el-col class="container__left">
          <!-- 배너 -->
          <el-card class="banner" @click="goGetCouponPage">
            <span>{{ $t('coupon.banner.subTitle') }}</span>
            <h4>{{ $t('coupon.banner.mainTitle') }}</h4>
            <a>
              <span>{{ $t('coupon.banner.link') }}</span>
              <img
                class="icon"
                src="~assets/images/icon/ic-arrow-forward.svg"
              />
            </a>
          </el-card>
          <!-- 쿠폰 입력 -->
          <el-card class="add-coupon">
            <div slot="header">
              <h3>{{ $t('coupon.addCouponCode.title') }}</h3>
            </div>
            <div>
              <el-form
                class="virnect-login-form"
                ref="form"
                :model="form"
                @submit.native.prevent="addCouponCode"
              >
                <el-form-item :label="$t('coupon.addCouponCode.label')">
                  <el-input
                    v-model="form.newCouponCode"
                    :placeholder="$t('coupon.addCouponCode.placeholder')"
                  />
                </el-form-item>
                <el-button
                  type="primary"
                  @click="addCouponCode"
                  :disabled="!form.newCouponCode"
                >
                  {{ $t('coupon.addCouponCode.submit') }}
                </el-button>
              </el-form>
              <div class="caution">
                <h5>{{ $t('coupon.addCouponCode.cautionTitle') }}</h5>
                <el-divider />
                <p v-html="$t('coupon.addCouponCode.cautionContent')" />
              </div>
            </div>
          </el-card>
        </el-col>
        <!-- 쿠폰 리스트 -->
        <el-col class="container__right">
          <el-card class="el-card--table">
            <div slot="header">
              <h3>{{ $t('coupon.couponList.title') }}</h3>
              <el-tooltip
                :content="$t('coupon.couponList.desc')"
                placement="bottom-start"
              >
                <img src="~assets/images/icon/ic-error.svg" />
              </el-tooltip>
            </div>
            <coupon-list :coupons="coupons" @select="couponSelect" />
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import couponService from '@/services/coupon'
import CouponList from '@/components/coupon/CouponList'

export default {
  components: {
    CouponList,
  },
  data() {
    return {
      coupons: [],
      form: {
        newCouponCode: '',
      },
    }
  },
  methods: {
    goGetCouponPage() {
      window.open('virnect.com')
    },
    /**
     * 쿠폰 등록
     */
    async addCouponCode() {
      try {
        await couponService.addCouponCode(this.form.newCouponCode)
        this.$notify.success({
          message: this.$t('coupon.message.registerSuccess'),
          position: 'bottom-left',
        })
      } catch (e) {
        console.error(e)
        this.$notify.error({
          message: this.$t('coupon.message.registerFail'),
          position: 'bottom-left',
        })
      }
    },
    /**
     * 쿠폰 사용
     */
    async couponSelect(column) {
      try {
        await this.$confirm(
          this.$t('coupon.useModal.desc'),
          this.$t('coupon.useModal.title'),
          {
            confirmButtonText: this.$t('coupon.useModal.submit'),
            showCancelButton: false,
          },
        )
      } catch (e) {
        return false
      }

      try {
        await couponService.useCoupon(column.id)
        this.$notify.success({
          message: this.$t('coupon.message.useSuccess'),
          position: 'bottom-left',
        })
        this.coupons = await couponService.getCouponList()
      } catch (e) {
        console.error(e)
        this.$notify.error({
          message: e,
          position: 'bottom-left',
        })
      }
    },
  },
  async beforeCreate() {
    this.coupons = await couponService.getCouponList()
  },
}
</script>

<style lang="scss">
#coupon {
  .container .title {
    margin-bottom: 44px;
  }
  .banner .el-card__body {
    position: relative;
    color: #fff;
    background: url('~assets/images/bg_banner.jpg');
    cursor: pointer;

    & > span {
      font-weight: normal;
      font-size: 14px;
    }
    & > h4 {
      font-weight: bold;
      font-size: 20px;
    }
    & > a {
      display: inline-block;
      margin-top: 12px;
      color: #fff;
      font-weight: normal;
      font-size: 12px;
      & > * {
        display: inline-block;
        vertical-align: middle;
      }
    }

    // effect
    &:after {
      position: absolute;
      top: 0;
      left: 0;
      width: 100%;
      height: 100%;
      background-image: linear-gradient(140deg, #fff, transparent);
      opacity: 0;
      content: '';
    }
    &:after,
    img {
      transition: 0.3s;
    }
    &:hover {
      &:after {
        opacity: 0.3;
      }
      img {
        margin-left: 4px;
      }
    }
  }

  .add-coupon {
    .el-card__body {
      padding: 24px 30px;
    }
    .el-button {
      display: block;
      width: 100%;
    }
    .caution {
      margin-top: 42px;

      .el-divider--horizontal {
        margin: 12px 0;
      }
      & > h5 {
        color: $font-color-content;
      }
      & > p {
        color: $font-color-desc;
        font-size: 11.8px;
        line-height: 2;
        word-break: break-all;
      }
    }
  }

  .coupon-list {
    tr {
      cursor: pointer;
    }
    th:nth-last-child(2) {
      text-align: center;
    }
  }
}
</style>
