<template>
  <el-dialog
    class="member-add-modal"
    :visible.sync="showMe"
    :title="$t('members.add.title')"
    width="628px"
    top="11vh"
    :close-on-click-modal="false"
  >
    <div>
      <p>{{ $t('members.add.desc') }}</p>
      <el-form
        ref="form"
        v-for="(form, index) in userInfoList"
        :key="index"
        class="virnect-workstation-form"
        :model="form"
        :rules="rules"
      >
        <h6>
          <img src="~assets/images/icon/ic-person.svg" />
          <span>{{ `${$t('members.add.addUser')} ${index + 1}` }}</span>
          <button @click.prevent="clearMember(index)">
            <i class="el-icon-close" />
          </button>
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
                <el-tooltip
                  :content="$t('members.setting.roleDesc')"
                  placement="bottom-start"
                >
                  <img src="~assets/images/icon/ic-error.svg" />
                </el-tooltip>
              </template>
              <member-role-select
                v-model="form.role"
                :disabled="!canChangeRole"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <label>
          <span>{{ $t('members.setting.givePlans') }}</span>
          <el-tooltip
            :content="$t('members.setting.givePlansDesc')"
            placement="bottom-start"
          >
            <img src="~assets/images/icon/ic-error.svg" />
          </el-tooltip>
        </label>
        <el-row>
          <el-col :span="8">
            <el-form-item class="horizon" :label="plans.remote.label">
              <member-plan-select
                v-model="form.planRemote"
                :label="plans.remote.label"
                :amount="availablePlans.remote"
                @change="choosePlan"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item class="horizon" :label="plans.make.label">
              <member-plan-select
                v-model="form.planMake"
                :label="plans.make.label"
                :amount="availablePlans.make"
                @change="choosePlan"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item class="horizon" :label="plans.view.label">
              <member-plan-select
                v-model="form.planView"
                :label="plans.view.label"
                :amount="availablePlans.view"
                @change="choosePlan"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <div slot="footer">
      <div class="available">
        <img src="~assets/images/icon/ic-person.svg" />
        <span v-html="$tc('members.add.available', availableMember)" />
      </div>
      <el-button @click="addMember">
        {{ $t('members.add.addMember') }}
      </el-button>
      <el-button
        type="primary"
        @click="submit"
        :disabled="!userInfoList.length"
      >
        {{ $t('members.add.submit') }}
        <span class="number">{{ userInfoList.length }}</span>
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import MemberRoleSelect from '@/components/member/MemberRoleSelect'
import MemberPlanSelect from '@/components/member/MemberPlanSelect'
import modalMixin from '@/mixins/modal'
import { role } from '@/models/workspace/Member'
import InviteMember from '@/models/workspace/InviteMember'
import workspaceService from '@/services/workspace'
import plans from '@/models/workspace/plans'
import urls from 'WC-Modules/javascript/api/virnectPlatform/urls'
import { mapGetters } from 'vuex'

export default {
  components: {
    MemberRoleSelect,
    MemberPlanSelect,
  },
  mixins: [modalMixin],
  props: {
    membersTotal: Number,
  },
  data() {
    return {
      plans,
      roles: role.options.filter(({ value }) => value !== 'MASTER'),
      availablePlans: { remote: 0, make: 0, view: 0 },
      userInfoList: [new InviteMember()],
      rules: {
        email: [{ required: true, trigger: 'blur', type: 'email' }],
      },
    }
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
      return 49 - this.membersTotal - this.userInfoList.length
    },
  },
  methods: {
    async reset() {
      this.userInfoList = [new InviteMember()]
      this.$refs.form.forEach(form => form.resetFields())
      if (!this.plansInfo.planStatus) {
        await this.$store.dispatch('plan/getPlansInfo')
      }
      this.initAvailablePlans()
    },
    opened() {
      this.userInfoList = this.userInfoList.filter(form => form.email)
      if (!this.userInfoList.length) this.reset()
    },
    addMember() {
      if (this.availableMember < 1) {
        this.$message.error({
          dangerouslyUseHTMLString: true,
          message: this.$t('members.add.message.memberOverflow'),
          duration: 3000,
          showClose: true,
        })
      } else {
        this.userInfoList.push(new InviteMember())
      }
    },
    clearMember(index) {
      this.userInfoList.splice(index, 1)
      this.choosePlan()
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
        this.$emit('updated', this.form)
        this.reset()
        this.showMe = false
      } catch (e) {
        const errCode = e.toString().match(/^Error: ([0-9]+)/)[1]
        // 결제센터로
        if (errCode === 2003) {
          this.$confirm(this.$t('members.add.message.noHavePlans'), {
            confirmButtonText: this.$t('common.paymentCenter'),
            customClass: 'no-title',
          }).then(() => {
            window.open(`${urls.pay[this.$config.VIRNECT_ENV]}`)
          })
        }
        // 일반에러
        else {
          const errMsg = {
            1002: this.$t('members.add.message.memberAlready'),
            1007: this.$t('members.add.message.notHaveAnyPlan'),
            1008: this.$t('members.add.message.memberOverflow'),
          }[errCode]
          this.$message.error({
            message: errMsg
              ? errMsg
              : this.$t('members.add.message.inviteFail') + `\n(${e})`,
            duration: 4000,
            showClose: true,
          })
        }
      }
    },
  },
}
</script>

<style lang="scss">
#__nuxt .member-add-modal {
  .el-dialog__body {
    padding-right: 24px;
    overflow-y: scroll;
  }
  p {
    letter-spacing: -0.3px;
    & > span,
    & > img {
      vertical-align: middle;
    }
  }
  .el-divider {
    margin: 24px 0;
  }

  label,
  .el_input__label {
    margin-bottom: 8px;
    color: $font-color-desc;
    font-size: 13px;

    & > span,
    & > img {
      display: inline-block;
      vertical-align: middle;
    }
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
      margin-bottom: 20px;
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
    }
  }
  .el-dialog__footer {
    border-top: solid 1px #edf0f7;

    .available {
      float: left;
      & > img {
        vertical-align: middle;
        transform: scale(0.85);
        opacity: 0.6;
      }
      & > span {
        color: $font-color-desc;
        vertical-align: middle;
        size: 13px;
      }
      & > span > i {
        color: $color-primary;
      }
    }

    .el-button:last-child {
      float: right;
    }
  }
}
</style>
