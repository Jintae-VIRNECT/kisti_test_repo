<template>
  <div class="container">
    <el-row type="flex" justify="center" align="middle" class="row-bg">
      <el-col>
        <h2 class="title">{{ $t('terms.page.title') }}</h2>
        <p class="disc">{{ $t('terms.page.pageInfo') }}</p>

        <div class="terms-body">
          <el-checkbox v-model="allTerms" class="all-terms">{{
            $t('terms.page.entire')
          }}</el-checkbox>
          <el-checkbox v-model="serviceAgree" class="must-check">{{
            $t('terms.page.termsContents')
          }}</el-checkbox>
          <div class="terms-contents terms-wraper">
            <vue-markdown
              class="policy-body"
              :source="$t('terms.md')"
            ></vue-markdown>
          </div>

          <el-checkbox v-model="privacyAgree" class="must-check">{{
            $t('terms.page.privacyPolicyContents')
          }}</el-checkbox>
          <div class="policy-contents terms-wraper">
            <vue-markdown
              class="policy-body policy"
              :source="$t('policy.md')"
            ></vue-markdown>
          </div>
          <el-checkbox v-model="marketingAgree">{{
            $t('terms.page.marketingReceive')
          }}</el-checkbox>
        </div>

        <el-button
          class="next-btn block-btn"
          type="info"
          @click="
            $router.push({
              name: 'signup',
              params: {
                marketInfoReceive: marketingAgree,
                policyAgree: privacyAgree,
              },
              query: $route.query,
            })
          "
          :disabled="!privacyAgree || !serviceAgree"
          >{{ $t('login.next') }}</el-button
        >
      </el-col>
    </el-row>
  </div>
</template>

<script>
import VueMarkdown from 'vue-markdown'

export default {
  components: {
    VueMarkdown,
  },
  data() {
    return {
      allTerms: null,
      serviceAgree: false,
      privacyAgree: false,
      marketingAgree: false,
    }
  },
  watch: {
    allTerms() {
      if (
        this.allTerms &&
        !this.serviceAgree &&
        !this.privacyAgree &&
        !this.marketingAgree
      ) {
        this.serviceAgree = true
        this.privacyAgree = true
        this.marketingAgree = true
      }
      if (
        !this.allTerms &&
        this.serviceAgree &&
        this.privacyAgree &&
        this.marketingAgree
      ) {
        this.serviceAgree = false
        this.privacyAgree = false
        this.marketingAgree = false
      }
      if (
        this.allTerms &&
        (this.serviceAgree || this.privacyAgree || this.marketingAgree)
      ) {
        this.serviceAgree = true
        this.privacyAgree = true
        this.marketingAgree = true
      }
      if (
        !this.allTerms ||
        !this.serviceAgree ||
        !this.privacyAgree ||
        !this.marketingAgree
      ) {
        this.allTerms = false
      }
    },
    serviceAgree() {
      if (!this.serviceAgree) return (this.allTerms = false)
      if (this.serviceAgree && this.privacyAgree && this.marketingAgree) {
        this.allTerms = true
      }
    },
    privacyAgree() {
      if (!this.privacyAgree) return (this.allTerms = false)
      if (this.serviceAgree && this.privacyAgree && this.marketingAgree) {
        this.allTerms = true
      }
    },
    marketingAgree() {
      if (!this.marketingAgree) return (this.allTerms = false)
      if (this.serviceAgree && this.privacyAgree && this.marketingAgree) {
        this.allTerms = true
      }
    },
  },
  computed: {
    checkAll() {
      if (this.serviceAgree && this.privacyAgree && this.marketingAgree) {
        return true
      } else return false
    },
  },
}
</script>

<style lang="scss" scoped>
p {
  font-family: $noto;
}

.el-button.next-btn {
  margin-top: 60px;
}

.terms-body {
  margin-top: 55px;
  text-align: left;
  @media (max-width: $mobile) {
    margin-top: 32px;
  }

  .el-checkbox {
    display: block;
    margin-top: 26px;
    margin-bottom: 12px;
  }
}
.terms-wraper {
  height: 132px;
  margin-top: 11px;
  padding: 14px;
  overflow-y: scroll;
  font-size: 13px;
  border: 2px solid #ecf0f5;
}
@import '~assets/css/modules/policy.scss';
</style>
