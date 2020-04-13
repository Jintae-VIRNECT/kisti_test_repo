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
            <coupon-list @select="couponSelect" />
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
        await couponService.addCouponCode(this.form)
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
    couponSelect(column) {
      this.$alert(
        this.$t('coupon.useModal.desc'),
        this.$t('coupon.useModal.title'),
        {
          confirmButtonText: this.$t('coupon.useModal.submit'),
        },
      ).then(async () => {
        try {
          await couponService.useCoupon(column)
        } catch (e) {
          console.error(e)
          this.$notify.error({
            message: this.$t('coupon.message.useExpired'),
            position: 'bottom-left',
          })
        }
      })
    },
  },
}
</script>

<style lang="scss">
#coupon {
  .container .title {
    margin-bottom: 44px;
  }
  .banner .el-card__body {
    color: #fff;
    background: url('~assets/images/bg_banner.jpg');
    cursor: pointer;
    transition: 0.2s;

    img {
      transition: 0.2s;
    }

    &:hover {
      filter: brightness(1.2);
      img {
        margin-left: 4px;
      }
    }

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
  }
}
</style>
