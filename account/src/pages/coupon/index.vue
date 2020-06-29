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
          <el-card class="banner" @click.native="goGetCouponPage">
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
                :model="addCouponForm"
                @submit.native.prevent="addCouponCode"
              >
                <el-form-item :label="$t('coupon.addCouponCode.label')">
                  <el-input
                    v-model="addCouponForm.newCouponCode"
                    :placeholder="$t('coupon.addCouponCode.placeholder')"
                  />
                </el-form-item>
                <el-button
                  type="primary"
                  @click="addCouponCode"
                  :disabled="!addCouponForm.newCouponCode"
                >
                  {{ $t('coupon.addCouponCode.submit') }}
                </el-button>
              </el-form>
              <div class="caution">
                <h5>{{ $t('coupon.addCouponCode.cautionTitle') }}</h5>
                <el-divider />
                <ol>
                  <li
                    v-for="(content, index) in $t(
                      'coupon.addCouponCode.cautionContent',
                    )"
                    :key="index"
                  >
                    {{ content }}
                  </li>
                </ol>
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
            <coupon-list
              ref="table"
              :coupons="coupons"
              @select="couponSelect"
            />
            <searchbar-page
              ref="page"
              :value.sync="coponsPage"
              :total="couponsTotal"
            />
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import couponService from '@/services/coupon'
import CouponList from '@/components/coupon/CouponList'
import searchMixin from '@/mixins/search'

export default {
  mixins: [searchMixin],
  components: {
    CouponList,
  },
  data() {
    return {
      coupons: [],
      coponsPage: 1,
      couponsTotal: 0,
      addCouponForm: {
        newCouponCode: '',
      },
    }
  },
  methods: {
    changedSearchParams(searchParams) {
      console.log(searchParams)
      this.searchCoupons(searchParams)
    },
    goGetCouponPage() {
      window.open(`${this.$url.www}/coupon`)
    },
    /**
     * 쿠폰 리스트
     */
    async searchCoupons(searchParams) {
      const { list, total } = await couponService.searchCoupons(searchParams)
      this.coupons = list
      this.couponsTotal = total
    },
    /**
     * 쿠폰 정렬
     */
    async sortCoupons({ prop, order }) {
      this.searchParams.sort = order
        ? `${prop},${order.replace('ending', '')}`
        : null
      this.getCoupons()
    },
    /**
     * 쿠폰 등록
     */
    async addCouponCode() {
      try {
        await couponService.addCouponCode(this.addCouponForm.newCouponCode)
        this.$notify.success({
          message: this.$t('coupon.message.registerSuccess'),
          position: 'bottom-left',
          duration: 2000,
        })
      } catch (e) {
        const code = e.toString().match(/Error: ([0-9]*)/)[1]
        const messages = {
          '2000': this.$t('coupon.message.registerNotExist'),
          '2001': this.$t('coupon.message.registerAlready'),
          '2002': this.$t('coupon.message.registerExpired'),
        }
        this.$notify.error({
          message:
            messages[code] ||
            this.$t('coupon.message.registerFail') + `\n(${e})`,
          position: 'bottom-left',
          duration: 2000,
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
          duration: 2000,
        })
        this.getCoupons()
      } catch (e) {
        this.$notify.error({
          message: this.$t('coupon.message.useFail') + `\n(${e})`,
          position: 'bottom-left',
          duration: 2000,
        })
      }
    },
  },
  created() {
    this.searchCoupons()
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
      & > ol {
        padding-left: 1.1em;
        color: $font-color-desc;
        font-size: 11.8px;
        line-height: 2;
        word-break: break-all;
        list-style: decimal;
      }
    }
  }

  .coupon-list {
    th:nth-last-child(2) {
      text-align: center;
    }
  }
}
</style>
