<template>
  <article class="guest-pane">
    <section class="guest-pane__title">
      <h6>{{ $t('members.guest.title') }}</h6>
      <p v-html="$t('members.guest.desc')" />
      <ul>
        <li>
          <dl>
            <dt><img :src="plans.remote.logo" />Remote</dt>
            <dd>{{ $t('members.guest.removeDesc') }}</dd>
          </dl>
        </li>
        <li>
          <dl>
            <dt><img :src="plans.view.logo" />View</dt>
            <dd>{{ $t('members.guest.viewDesc') }}</dd>
          </dl>
        </li>
      </ul>
      <div class="guest-pane__sub-title">
        <p>{{ $t('members.guest.settings') }}</p>
        <MemberAddUsage :availableMember="currentMember" :maximum="maximum" />
      </div>
    </section>
    <section class="guest-pane__content">
      <el-tabs ref="elTabs" v-model="tabName" @tab-click="tabClick">
        <el-tab-pane :label="$t('members.guest.remoteTabName')" name="remote">
          <el-form class="virnect-workstation-form">
            <h6>{{ $t('members.guest.remoteTab.title') }}</h6>
            <el-row>
              <el-col :span="13">
                <el-form-item
                  class="horizon"
                  :label="$t('members.guest.numberOfGuest')"
                >
                  <MemberGuestSelect
                    :label="plans.remote.label"
                    :amount="availablePlans.remote"
                    :numOfGuest="numOfGuest"
                    @change="choosePlan"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>
        <el-tab-pane
          v-if="!$isOnpremise"
          :label="$t('members.guest.viewTabName')"
          name="view"
        >
          <el-form class="virnect-workstation-form">
            <h6>{{ $t('members.guest.viewTab.title') }}</h6>
            <el-row>
              <el-col :span="13">
                <el-form-item
                  class="horizon"
                  :label="$t('members.guest.numberOfGuest')"
                >
                  <MemberGuestSelect
                    :label="plans.view.label"
                    :amount="availablePlans.view"
                    :numOfGuest="numOfGuest"
                    @change="choosePlan"
                  />
                </el-form-item>
              </el-col>
            </el-row>
          </el-form>
        </el-tab-pane>
      </el-tabs>
    </section>
    <section class="guest-pane__footer">
      <el-button type="primary" @click="submit" :disabled="!numOfGuest">
        {{ $t('members.guest.submit') }}
        <span class="number" v-if="numOfGuest !== 0">{{ numOfGuest }}</span>
      </el-button>
    </section>
  </article>
</template>

<script>
import plans from '@/models/workspace/plans'
import workspaceService from '@/services/workspace'
import { mapGetters } from 'vuex'

import formRulesMixin from '@/mixins/formRules'
import messageMixin from '@/mixins/message'

export default {
  mixins: [formRulesMixin, messageMixin],
  props: ['membersTotal', 'maximum'],
  data() {
    return {
      plans,
      tabName: 'remote',
      availablePlans: { remote: 0, make: 0, view: 0 },
      numOfGuest: 0,
    }
  },
  methods: {
    // 탭 클릭 이벤트
    tabClick() {
      this.reset()
    },
    // 플랜정보 초기화
    initAvailablePlans() {
      this.plansInfo.products.forEach(product => {
        this.availablePlans[product.value.toLowerCase()] = product.unUsedAmount
      })
    },
    // 플랜 선택 이벤트
    choosePlan(plan) {
      this.initAvailablePlans()
      this.numOfGuest = plan.amount
    },
    async reset() {
      if (!this.plansInfo.planStatus) {
        await this.$store.dispatch('plan/getPlansInfo')
      }
      this.initAvailablePlans()
      this.numOfGuest = 0
      // mounted 시 강제로 active bar의 크기를 설정합니다.
      this.$refs.elTabs.$el.querySelector('.el-tabs__active-bar').style.width =
        '81px'
    },
    async submit() {
      const form = {}
      if (this.tabName === 'remote') form.planRemote = this.numOfGuest
      else if (this.tabName === 'view') form.planView = this.numOfGuest

      // api 요청
      try {
        await workspaceService.createGuest(form)
        this.$message.success({
          message: this.$tc(
            'members.add.message.createSuccess',
            this.numOfGuest,
          ),
          duration: 2000,
          showClose: true,
        })
        this.$emit('updated')
        this.reset()
      } catch (e) {
        const errCode = this.errorCode(e)
        // 결제센터로
        if (errCode === 2003) {
          this.$confirm(this.$t('members.add.message.noHavePlans'), {
            confirmButtonText: this.$t('common.paymentCenter'),
            customClass: 'no-title',
          }).then(() => {
            window.open(`${this.$url.pay}`)
          })
        } else {
          // 일반에러
          this.errorMessage(e)
        }
      }
    },
  },
  mounted() {
    this.reset()
  },
  computed: {
    ...mapGetters({
      activeWorkspace: 'auth/activeWorkspace',
      plansInfo: 'plan/plansInfo',
    }),
    canChangeRole() {
      return this.activeWorkspace.role === 'MASTER'
    },
    currentMember() {
      return this.membersTotal + this.numOfGuest
    },
  },
}
</script>

<style lang="scss">
#__nuxt .guest-pane {
  section {
    padding: 24px;
  }
  section:first-child {
    padding-bottom: 0;
  }
  .el-form {
    margin: 20px 0;
    padding: 20px 20px 4px;
    border: solid 1px #e6e9ee;
    box-shadow: 0 1px 3px 0 rgba(23, 43, 77, 0.1);

    .el-icon-close:before {
      font-weight: bold;
    }

    h6 {
      margin-bottom: 12px;
      & > span,
      & > img {
        vertical-align: middle;
      }
      & > button {
        float: right;
        font-size: 1.1em;
      }
    }
    .el-form-item {
      margin-bottom: 20px;
    }
    .el-input {
      width: 168px;
      &.full {
        width: 100%;
      }
      &.check {
        width: 425px;
      }
    }
  }
  .guest-pane__title {
    h6 {
      @include fontLevel(100);
      color: #0b1f48;
      margin-bottom: 8px;
    }
    p {
      @include fontLevel(75);
      color: #445168;
      margin-bottom: 16px;
    }
    ul {
      display: flex;
      width: 568px;
      justify-content: space-between;
      margin-bottom: 25px;
    }
    dl {
      width: 280px;
      height: 122px;
      padding: 14px 16px 16px 16px;
      border-radius: 3px;
      border: solid 1px #e0e5ef;
      background-color: rgba(244, 246, 250, 0.8);
    }
    dt {
      @include fontLevel(200);
      display: flex;
      align-content: center;
      align-items: center;
    }
    dl {
      @include fontLevel(50);
      line-height: 1.67;
    }
  }
  .guest-pane__content {
    overflow-y: scroll;
    max-height: 455px;
    padding: 0 5px 0 24px;
    width: 610px;
    .el-tabs .el-tabs__item {
      height: 40px;
      line-height: 40px;
      padding: 0 14px;
    }
  }
  .guest-pane__sub-title {
    display: flex;
    justify-content: space-between;
    border-bottom: 0;
    p {
      @include fontLevel(75);
      color: #0b1f48;
      margin-bottom: 8px;
    }
    .el-divider--horizontal {
      margin: 8px 0 16px 0;
    }
  }
  .guest-pane__footer {
    display: flex;
    padding: 24px;
    justify-content: flex-end;
    border-top: 1px solid #edf0f7;
  }
}
</style>
