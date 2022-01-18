<template>
  <article class="create-pane">
    <section class="create-pane__title">
      <h6>{{ $t('members.create.workspaceMemberTitle') }}</h6>
      <p>
        {{ $t('members.create.workspaceMemberDesc') }}
      </p>
      <div class="create-pane__sub-title">
        <p>{{ $t('members.create.workspaceMemberList') }}</p>
        <MemberAddUsage :availableMember="availableMember" :maximum="maximum" />
      </div>
    </section>
    <section class="create-pane__content">
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
          <span>{{ `${$t('members.create.createMember')} ${index + 1}` }}</span>
          <el-button
            v-if="index"
            class="close-button"
            @submit.native.prevent
            @click.prevent="clearMember(index)"
          >
            <i class="el-icon-close" />
          </el-button>
        </h6>
        <el-form-item class="horizon" prop="id" required>
          <template slot="label">
            <span>{{ $t('members.create.id') }}</span>
          </template>
          <ValidationProvider rules="idCheck" v-slot="{ valid }">
            <el-input
              v-model="form.id"
              :class="cssVars"
              maxlength="20"
              :disabled="form.duplicateCheck"
              :placeholder="$t('members.create.idPlaceholder')"
              @keyup.enter.native="idEnterEvent(form, index, valid)"
            />
            <el-button
              type="primary"
              @click="form.duplicateCheck = false"
              v-show="form.duplicateCheck"
              :class="cssVars"
              >{{ $t('members.create.reEnter') }}</el-button
            >
            <el-button
              type="primary"
              v-show="!form.duplicateCheck"
              @click="checkMembersId(form)"
              :disabled="!form.id.length || !valid"
              :class="cssVars"
              >{{ $t('members.create.idCheck') }}</el-button
            >
          </ValidationProvider>
          <span>{{ $t('members.create.caution.validUserId') }}</span>
        </el-form-item>
        <el-form-item class="horizon" prop="password" required>
          <template slot="label">
            <span>{{ $t('members.create.password') }}</span>
          </template>
          <el-input
            v-model="form.password"
            class="full passowrd"
            show-password
            maxlength="20"
            :placeholder="$t('members.create.passwordPlaceholder')"
          />
          <span>{{ $t('members.setting.password.caution') }}</span>
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
    <section class="create-pane__caution" v-if="$isOnpremise">
      <el-row>
        <p v-html="$t('members.create.caution.createId')" />
        <p v-html="$t('members.create.caution.canModify')" />
      </el-row>
    </section>
    <section class="create-pane__footer">
      <el-button @click="addMember">
        {{ $t('members.add.addMember') }}
      </el-button>
      <el-button
        type="primary"
        @click="submit"
        :disabled="userInfoList.length < 1"
      >
        {{ $t('members.create.submit') }}
        <span class="number">{{ userInfoList.length }}</span>
      </el-button>
    </section>
  </article>
</template>

<script>
import CreateMember from '@/models/workspace/CreateMember'
import plans from '@/models/workspace/plans'
import workspaceService from '@/services/workspace'
import { mapGetters } from 'vuex'

import messageMixin from '@/mixins/message'
import formRulesMixin from '@/mixins/formRules'
export default {
  mixins: [messageMixin, formRulesMixin],
  props: ['membersTotal', 'maximum'],
  data() {
    return {
      plans,
      userInfoList: [new CreateMember()],
      availablePlans: { remote: 0, make: 0, view: 0 },
    }
  },
  methods: {
    async checkMembersId(member) {
      // 아이디를 입력하지 않고 중복체크 불가
      if (!member.id.length) return false

      // 서버에서 중복 체크하기 전에 계정 생성 목록에 중복된 아이디 체크
      if (this.userInfoList.filter(user => user.id === member.id).length >= 2) {
        return this.errorMessage('Error: 2202')
      }

      try {
        const result = await workspaceService.checkMembersId(member.id)
        if (result) {
          this.successMessage(this.$t('members.create.message.usableId'))
          member.duplicateCheck = true
        } else {
          member.duplicateCheck = false
        }
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
        this.userInfoList.push(new CreateMember())
      }
    },
    clearMember(index) {
      if (!index) return false
      this.userInfoList.splice(index, 1)
      this.choosePlan()
    },
    async reset() {
      this.userInfoList = [new CreateMember()]
      if (this.$refs.from) {
        this.$refs.form.forEach(form => form.resetFields())
      }

      if (!this.plansInfo.planStatus) {
        await this.$store.dispatch('plan/getPlansInfo')
      }
      this.initAvailablePlans()
    },
    async submit() {
      if (this.availableMember > this.maximum) {
        this.errorMessage('Error: 900')
        return
      }
      // 유효성 검사
      try {
        await Promise.all(this.$refs.form.map(form => form.validate()))
      } catch (e) {
        return false
      }

      // 계정 중복 체크 확인
      if (this.userInfoList.some(user => user.duplicateCheck === false)) {
        this.$message.error({
          message: this.$t('members.create.message.notCheckId'),
          duration: 4000,
          showClose: true,
        })
        return false
      }

      // api 요청
      try {
        await workspaceService.createMembers(this.userInfoList)
        this.$message.success({
          message: this.$t('members.create.message.successContent'),
          duration: 2000,
          showClose: true,
        })
        this.$emit('updated')
        this.reset()
      } catch (e) {
        const errCode = e.toString().match(/^Error: ([0-9]+)/)[1]
        // 결제센터로
        if (errCode === 2003) {
          this.$confirm(this.$t('members.add.message.noHavePlans'), {
            confirmButtonText: this.$t('common.paymentCenter'),
            customClass: 'no-title',
          }).then(() => {
            window.open(`${this.$url.pay}`)
          })
        }
        // 일반에러
        else {
          this.errorMessage(e)
        }
      }
    },
    idEnterEvent(form, index, valid) {
      if (valid) {
        this.checkMembersId(form)
      } else {
        this.userInfoList[index].duplicateCheck = false
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
    cssVars() {
      return this.$i18n.locale
    },
  },
}
</script>

<style lang="scss">
#__nuxt .create-pane {
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
      margin-bottom: 12px;
    }
    .el-input {
      width: 168px;
      &.full {
        width: 100%;
      }
      &.ko {
        width: 425px;
      }
      &.en {
        width: 374px;
      }
    }
    .el-button {
      margin: 0;
      font-size: 13px;
      &.en {
        width: 133px;
      }
    }
  }

  .create-pane__title {
    h6 {
      @include fontLevel(100);
      margin-bottom: 8px;
      color: #0b1f48;
    }
    p {
      @include fontLevel(75);
      margin-bottom: 16px;
      color: #445168;
    }
  }
  .create-pane__content {
    width: 610px;
    max-height: 498px;
    padding: 0 5px 0 24px;
    overflow-y: scroll;
    .el-tabs .el-tabs__item {
      height: 40px;
      padding: 0 14px;
      line-height: 40px;
    }
  }
  .create-pane__sub-title {
    display: flex;
    justify-content: space-between;
    border-bottom: 1px solid #eaedf3;
    p {
      @include fontLevel(75);
      margin-bottom: 8px;
      color: #0b1f48;
    }
    .el-divider--horizontal {
      margin: 8px 0 16px 0;
    }
    &.tabs {
      border-bottom: 0;
    }
  }
  .create-pane__caution {
    padding: 24px;
    @include fontLevel(75);
    border-radius: 3px;
  }
  .create-pane__footer {
    display: flex;
    justify-content: space-between;
    padding: 24px;
    border-top: 1px solid #edf0f7;
  }
}
</style>
