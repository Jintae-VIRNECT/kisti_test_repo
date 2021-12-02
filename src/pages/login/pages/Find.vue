<template>
  <div class="container find-tab-wrapper">
    <el-row type="flex" justify="center" align="middle" class="row-bg">
      <el-col>
        <h2 class="title">{{ $t('find.title') }}</h2>
        <p class="disc" v-html="$t('find.pageInfo')"></p>

        <section class="find-wrap">
          <div class="find-head">
            <button
              :class="{ active: tab == 'email' }"
              @click="tabChange('email')"
            >
              <span>{{ $t('find.findEmail') }}</span>
            </button>
            <button
              :class="{ active: tab == 'reset_password' }"
              @click="tabChange('reset_password')"
            >
              <span>{{ $t('find.resetPassword.title') }}</span>
            </button>
          </div>
          <EmailTab v-if="tab == 'email'" />
          <ResetTab v-if="tab == 'reset_password'" />
        </section>
        <el-button class="inquiry-btn" v-if="false"
          ><i class="el-icon-warning-outline"></i
          >{{ $t('find.inquiry') }}</el-button
        >
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { onMounted, ref } from '@vue/composition-api'
import EmailTab from 'components/findTab/EmailTab'
import ResetTab from 'components/findTab/ResetTab'
export default {
  props: {
    findCategory: String,
  },
  components: { EmailTab, ResetTab },
  setup(props, { root }) {
    const tab = ref('email')
    const tabChange = tabs => {
      tab.value = tabs
      root.$router.push({ name: 'findTab', params: { findCategory: tabs } })
    }

    onMounted(() => {
      if (props.findCategory === 'email') return (tab.value = 'email')
      else return (tab.value = 'reset_password')
    })

    return {
      tab,
      tabChange,
    }
  },
}
</script>

<style lang="scss">
.find-tab-wrapper {
  .row-bg > div {
    width: 460px;
    font-weight: 500;
    @media (max-width: $mobile) {
      padding-right: 0;
      padding-left: 0;
    }
  }
  .find-wrap {
    margin-top: 52px;
    border: 1px solid #eaedf3;
    border-radius: 4px;

    @media (max-width: $mobile) {
      margin-top: 32px;
      border: none;
    }
  }
  .find-head {
    font-size: 0;
    border-bottom: 1px solid #eaedf3;
    button {
      width: 50%;
      height: 60px;
      color: #0d2a58;
      font-size: 16px;
      border: 0;
      opacity: 0.6;
      &.active {
        opacity: 1;
      }
    }
    .active span {
      position: relative;
      &:after {
        position: absolute;
        bottom: -21px;
        left: 0;
        width: 100%;
        height: 3px;
        background: #1468e2;
        content: '';
      }
    }
  }
  .el-radio {
    margin-top: 36px;
    margin-right: 0;
    white-space: inherit;
  }
  .find-body {
    padding-top: 44px;
    padding-right: 40px;
    padding-bottom: 52px;
    padding-left: 40px;
    font-size: 16px;
    text-align: left;
    @media (max-width: $mobile) {
      padding-top: 28px;
      padding-right: 24px;
      padding-left: 24px;
      font-size: 15px;
    }

    .info-text {
      margin-bottom: 4px;
      color: #103573;
      + .input-title {
        margin-top: 40px;
      }
    }
  }
  .inquiry-btn {
    color: #6f7681;
    i {
      margin-right: 6px;
      padding-bottom: 3px;
      font-weight: bold;
      font-size: 18px;
      vertical-align: middle;
    }
  }
  .el-button.next-btn {
    margin-top: 52px;
  }
  .input-title {
    margin-top: 16px;
  }

  .user-email-holder {
    position: relative;
    margin: 32px 0 24px;
    padding: 26px 28px;
    background: #f2f4f7;
    p + p {
      margin-top: 16px;
    }
  }
  .auth-before,
  .mailfind-before {
    .info-text {
      color: #103573;
    }
    .user-email-holder span {
      display: block;
      padding-left: 100px;
      // text-align: center;
    }
  }
}
</style>
