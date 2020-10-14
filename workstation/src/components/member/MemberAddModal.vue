<template>
  <el-dialog
    class="member-add-modal"
    :visible.sync="showMe"
    :title="$t('members.add.title')"
    width="440px"
    top="11vh"
  >
    <div>
      <p>{{ $t('members.add.desc') }}</p>
      <p>
        <span>{{ $t('members.add.desc2') }}</span>
        <el-tooltip :content="$t('members.add.desc3')" placement="bottom-start">
          <img src="~assets/images/icon/ic-error.svg" />
        </el-tooltip>
      </p>
      <el-form
        ref="form"
        v-for="(form, index) in userInfoList"
        :key="index"
        class="virnect-workstation-form"
        :model="form"
        :rules="rules"
        :show-message="false"
      >
        <el-divider />
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
            <el-tooltip
              :content="$t('members.add.emailDesc')"
              placement="bottom-start"
            >
              <img src="~assets/images/icon/ic-error.svg" />
            </el-tooltip>
          </template>
          <el-input
            class="full"
            v-model="form.email"
            :placeholder="$t('members.add.emailPlaceholder')"
          />
        </el-form-item>
        <el-row>
          <el-col :span="12">
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
              <member-role-select v-model="form.role" />
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
          <el-col :span="12">
            <el-form-item class="horizon" :label="plans.remote.label">
              <el-select v-model="form.planRemote">
                <el-option
                  :value="false"
                  :label="$t('members.setting.givePlansEmpty')"
                />
                <el-option
                  :value="true"
                  :label="plans.remote.label"
                  :disabled="!plansInfo.remote.unUsedAmount"
                >
                  <span>{{ plans.remote.label }}</span>
                  <span class="right">
                    {{ plansInfo.remote.unUsedAmount }}
                  </span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item class="horizon" :label="plans.make.label">
              <el-select v-model="form.planMake">
                <el-option
                  :value="false"
                  :label="$t('members.setting.givePlansEmpty')"
                />
                <el-option
                  :value="true"
                  :label="plans.make.label"
                  :disabled="!plansInfo.make.unUsedAmount"
                >
                  <span>{{ plans.make.label }}</span>
                  <span class="right">
                    {{ plansInfo.make.unUsedAmount }}
                  </span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item class="horizon" :label="plans.view.label">
              <el-select v-model="form.planView">
                <el-option
                  :value="false"
                  :label="$t('members.setting.givePlansEmpty')"
                />
                <el-option
                  :value="true"
                  :label="plans.view.label"
                  :disabled="!plansInfo.view.unUsedAmount"
                >
                  <span>{{ plans.view.label }}</span>
                  <span class="right">
                    {{ plansInfo.view.unUsedAmount }}
                  </span>
                </el-option>
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <div slot="footer">
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
  },
  mixins: [modalMixin],
  data() {
    return {
      plans,
      roles: role.options.filter(({ value }) => value !== 'MASTER'),
      userInfoList: [new InviteMember()],
      rules: {
        email: [{ required: true, trigger: 'blur' }],
      },
    }
  },
  computed: {
    ...mapGetters({
      activeWorkspace: 'auth/activeWorkspace',
      plansInfo: 'plan/plansInfo',
    }),
  },
  methods: {
    opened() {
      this.userInfoList = [new InviteMember()]
      this.$refs.form.forEach(form => form.resetFields())
      if (!this.plansInfo.planStatus) {
        this.$store.dispatch('plan/getPlansInfo')
      }
    },
    addMember() {
      this.userInfoList.push(new InviteMember())
    },
    clearMember(index) {
      this.userInfoList.splice(index, 1)
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
    margin: 0 0 -8px;

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
      width: 180px;
      &.full {
        width: 100%;
      }
    }
  }
  .el-dialog__footer {
    border-top: solid 1px #edf0f7;

    .el-button:first-child {
      float: left;
    }
  }
}
</style>
