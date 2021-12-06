<template>
  <article class="invite-pane">
    <section class="invite-pane__title">
      <h6>{{ $t('members.invitation.title') }}</h6>
      <p v-html="$t('members.invitation.desc')" />
      <div class="invite-pane__sub-title">
        <p>{{ $t('members.invitation.list') }}</p>
        <MemberAddUsage :availableMember="availableMember" :maximum="maximum" />
      </div>
    </section>
    <section class="invite-pane__content">
      <el-form
        ref="form"
        v-for="(form, index) in userInfoList"
        :key="index"
        class="virnect-workstation-form"
        :model="form"
        :rules="rules"
        @submit.native.prevent
      >
        <h6>
          <img src="~assets/images/icon/ic-person.svg" />
          <span>{{ `${$t('members.add.addUser')} ${index + 1}` }}</span>
          <el-button
            v-if="index"
            class="close-button"
            @click.prevent="clearMember(index)"
          >
            <i class="el-icon-close" />
          </el-button>
        </h6>
        <el-form-item class="horizon" prop="email" required>
          <template slot="label">
            <span>{{ $t('members.add.email') }}</span>
          </template>
          <el-input
            class="full"
            v-model="form.email"
            :placeholder="$t('members.add.emailPlaceholder')"
          />
        </el-form-item>
        <el-row>
          <el-col :span="8">
            <el-form-item class="horizon">
              <template slot="label">
                <span>{{ $t('members.setting.role') }}</span>
              </template>
              <MemberRoleSelect
                v-model="form.role"
                :disabled="!canChangeRole"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <label>
          <span>{{ $t('members.setting.givePlans') }}</span>
        </label>
        <el-row>
          <el-col :span="8">
            <el-form-item class="horizon" :label="plans.remote.label">
              <MemberPlanSelect
                v-model="form.planRemote"
                :label="plans.remote.label"
                :amount="availablePlans.remote"
                @change="choosePlan"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item class="horizon" :label="plans.make.label">
              <MemberPlanSelect
                v-model="form.planMake"
                :label="plans.make.label"
                :amount="availablePlans.make"
                @change="choosePlan"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item class="horizon" :label="plans.view.label">
              <MemberPlanSelect
                v-model="form.planView"
                :label="plans.view.label"
                :amount="availablePlans.view"
                @change="choosePlan"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </section>
    <section class="invite-pane__footer">
      <el-button @click="addMember">
        {{ $t('members.add.addMember') }}
      </el-button>
      <el-button
        type="primary"
        @click="submit"
        :disabled="userInfoList.length < 1"
      >
        {{ $t('members.add.submit') }}
        <span class="number">{{ userInfoList.length }}</span>
      </el-button>
    </section>
  </article>
</template>

<script>
import InviteMember from '@/models/workspace/InviteMember'
import plans from '@/models/workspace/plans'
import workspaceService from '@/services/workspace'
import { mapGetters } from 'vuex'

import formRulesMixin from '@/mixins/formRules'
import messageMixin from '@/mixins/message'

export default {
  mixins: [messageMixin, formRulesMixin],
  props: ['membersTotal', 'maximum'],
  data() {
    return {
      plans,
      userInfoList: [new InviteMember()],
      availablePlans: { remote: 0, make: 0, view: 0 },
    }
  },
  methods: {
    initAvailablePlans() {
      this.plansInfo.products.forEach(product => {
        this.availablePlans[product.value.toLowerCase()] = product.unUsedAmount
      })
    },
    choosePlan() {
      this.initAvailablePlans()
      this.userInfoList.forEach(user => {
        if (user.planRemote) this.availablePlans.remote -= 1
        if (user.planMake) this.availablePlans.make -= 1
        if (user.planView) this.availablePlans.view -= 1
      })
    },
    addMember() {
      if (this.availableMember >= this.maximum) {
        this.errorMessage('Error: 900')
      } else {
        this.userInfoList.push(new InviteMember())
      }
    },
    clearMember(index) {
      if (!index) return false
      this.userInfoList.splice(index, 1)
      this.choosePlan()
    },
    async reset() {
      this.userInfoList = [new InviteMember()]
      if (this.$refs.from) {
        this.$refs.form.forEach(form => form.resetFields())
      }

      if (!this.plansInfo.planStatus) {
        await this.$store.dispatch('plan/getPlansInfo')
      }
      this.initAvailablePlans()
    },
    async submit() {
      // 유효성 검사
      try {
        await Promise.all(this.$refs.form.map(form => form.validate()))
      } catch (e) {
        return false
      }
      // api 요청
      try {
        await workspaceService.inviteMembers(this.userInfoList)
        this.$message.success({
          message: this.$t('members.add.message.inviteSuccess'),
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
    this.userInfoList = this.userInfoList.filter(form => form.email)
    if (!this.userInfoList.length) this.reset()
  },
  computed: {
    ...mapGetters({
      activeWorkspace: 'auth/activeWorkspace',
      plansInfo: 'plan/plansInfo',
    }),
    canChangeRole() {
      return this.activeWorkspace.role === 'MASTER'
    },
    availableMember() {
      return this.membersTotal + this.userInfoList.length
    },
  },
}
</script>

<style lang="scss">
#__nuxt .invite-pane {
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
  .invite-pane__title {
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
  }
  .invite-pane__content {
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
  .invite-pane__sub-title {
    display: flex;
    justify-content: space-between;
    border-bottom: 1px solid #eaedf3;
    p {
      @include fontLevel(75);
      color: #0b1f48;
      margin-bottom: 8px;
    }
    .el-divider--horizontal {
      margin: 8px 0 16px 0;
    }
    &.tabs {
      border-bottom: 0;
    }
  }
  .invite-pane__footer {
    display: flex;
    padding: 24px;
    justify-content: space-between;
    border-top: 1px solid #edf0f7;
  }
}
</style>
