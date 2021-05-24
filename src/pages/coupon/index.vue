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
          <Banner
            :headerTxt="$t('coupon.banner.mainTitle')"
            :subTxt="$t('coupon.banner.subTitle')"
            :linkTxt="$t('coupon.banner.link')"
            :to="`${this.$url.www}/contact/inquiry`"
          />
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
                  :disabled="!addCouponForm.newCouponCode.trim()"
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
            <CouponList ref="table" :coupons="coupons" @select="couponSelect" />
            <SearchbarPage
              ref="page"
              :value.sync="coponsPage"
              :total="couponsTotal"
            />
          </el-card>
        </el-col>
      </el-row>
    </div>
    <CouponDetailModal
      :visible.sync="showCouponDetailModal"
      :coupon="activeCoupon"
      :allTickets="allTickets"
    />
  </div>
</template>

<script>
import couponService from '@/services/coupon'
import paymentService from '@/services/payment'
import searchMixin from '@/mixins/search'

export default {
  mixins: [searchMixin],
  data() {
    return {
      allTickets: [],
      coupons: [],
      coponsPage: 1,
      couponsTotal: 0,
      addCouponForm: {
        newCouponCode: '',
      },
      activeCoupon: {},
      showCouponDetailModal: false,
    }
  },
  methods: {
    changedSearchParams(searchParams) {
      this.searchCoupons(searchParams)
    },
    /**
     * 상품 리스트
     */
    async getAllTicketList() {
      this.allTickets = await paymentService.getAllTicketList()
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
        this.searchCoupons()
        this.addCouponForm.newCouponCode = ''
      } catch (e) {
        const errCode = e.toString().match(/^Error: ([0-9]+)/)[1]
        const message =
          {
            2900: this.$t('coupon.message.registerDayRistrict'),
            2904: this.$t('coupon.message.registerNotExist'),
            2905: this.$t('coupon.message.useAlready'),
            2907: this.$t('coupon.message.useExpired'),
            2914: this.$t('coupon.message.registerOrUsedAlready'),
          }[errCode] || `${errCode}: ${this.$t('coupon.message.registerFail')}`

        this.$notify.error({
          message,
          position: 'bottom-left',
          duration: 2000,
        })
      }
    },
    /**
     * 쿠폰 상세조회
     */
    couponSelect(row) {
      this.showCouponDetailModal = true
      this.activeCoupon = row
    },
  },
  created() {
    this.searchCoupons()
    this.getAllTicketList()
  },
}
</script>

<style lang="scss">
#coupon {
  .container .title {
    margin-bottom: 44px;
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
