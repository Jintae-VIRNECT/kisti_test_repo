<template>
  <el-dialog
    class="member-setting-modal"
    :visible.sync="showMe"
    :title="$t('members.setting.title')"
    width="628px"
    top="11vh"
    :close-on-click-modal="false"
  >
    <div>
      <h6>{{ $t('members.setting.info') }}</h6>
      <dl>
        <dt>{{ $t('members.setting.nickname') }}</dt>
        <dd class="column-user">
          <VirnectThumbnail :size="28" :image="cdn(data.profile)" />
          <span>{{ data.nickname }}</span>
          <el-button
            v-if="$isOnpremise && canKick"
            @click="$emit('change-password')"
          >
            {{ $t('members.password.title') }}
          </el-button>
        </dd>
        <dt>{{ $t('members.setting.email') }}</dt>
        <dd>{{ data.email }}</dd>
      </dl>
      <el-divider />
      <h6>{{ $t('members.setting.setting') }}</h6>
      <el-form
        class="virnect-workstation-form"
        ref="form"
        :model="form"
        @submit.native.prevent="submit"
      >
        <el-row :gutter="8">
          <el-col :span="12" :sm="8">
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
              <MemberRoleSelect
                v-model="form.role"
                :disabled="!canChangeRole"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <dt>
          <span>{{ $t('members.setting.givePlans') }}</span>
          <el-tooltip
            :content="$t('members.setting.givePlansDesc')"
            placement="bottom-start"
          >
            <img src="~assets/images/icon/ic-error.svg" />
          </el-tooltip>
        </dt>
        <el-row :gutter="8">
          <el-col :span="12" :sm="8">
            <el-form-item class="horizon" :label="plans.remote.label">
              <MemberPlanSelect
                v-model="form.licenseRemote"
                :label="plans.remote.label"
                :amount="plansInfo.remote.unUsedAmount"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12" :sm="8">
            <el-form-item class="horizon" :label="plans.make.label">
              <MemberPlanSelect
                v-model="form.licenseMake"
                :label="plans.make.label"
                :amount="plansInfo.make.unUsedAmount"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12" :sm="8">
            <el-form-item class="horizon" :label="plans.view.label">
              <MemberPlanSelect
                v-model="form.licenseView"
                :label="plans.view.label"
                :amount="plansInfo.view.unUsedAmount"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <div slot="footer">
      <el-button v-show="canKick" @click="$emit('kick')">
        {{
          $isOnpremise ? $t('members.delete.title') : $t('members.setting.kick')
        }}
      </el-button>
      <el-button type="primary" @click="submit">
        {{ $t('common.update') }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import modalMixin from '@/mixins/modal'
import { role } from '@/models/workspace/Member'
import workspaceService from '@/services/workspace'
import { mapGetters } from 'vuex'
import EditMember from '@/models/workspace/EditMember'
import plans from '@/models/workspace/plans'
import filterMixin from '@/mixins/filters'

export default {
  mixins: [filterMixin, modalMixin],
  props: {
    data: Object,
  },
  data() {
    return {
      plans,
      roles: role.options.filter(({ value }) => value !== 'MASTER'),
      form: {},
      test: [],
    }
  },
  computed: {
    ...mapGetters({
      activeWorkspace: 'auth/activeWorkspace',
      plansInfo: 'plan/plansInfo',
    }),
    canChangeRole() {
      return (
        this.form.role !== 'MASTER' && this.activeWorkspace.role === 'MASTER'
      )
    },
    canKick() {
      return this.$isOnpremise
        ? this.activeWorkspace.role === 'MASTER' &&
            this.data.userType === 'MEMBER_USER'
        : this.data.role !== 'MASTER' &&
            this.activeWorkspace.role !== 'MEMBER' &&
            this.activeWorkspace.role !== this.data.role
    },
  },
  methods: {
    opened() {
      this.form = new EditMember(this.data)
      if (!this.plansInfo.planStatus) {
        this.$store.dispatch('plan/getPlansInfo')
      }
    },
    async submit() {
      try {
        await workspaceService.updateMembersRole(this.form)
        this.$message.success({
          message: this.$t('members.setting.message.updateSuccess'),
          duration: 2000,
          showClose: true,
        })
        this.$emit('updated', this.form)
      } catch (e) {
        if (/^Error: 2000/.test(e)) {
          this.$confirm(this.$t('members.add.message.noHavePlans'), {
            confirmButtonText: this.$t('common.paymentCenter'),
            customClass: 'no-title',
          }).then(() => {
            window.open(`${this.$url.pay}`)
          })
        } else if (/^Error: 1003/.test(e)) {
          this.$message.error({
            message: this.$t('members.setting.message.notChangeMasterPlan'),
            duration: 2000,
            showClose: true,
          })
        } else {
          this.$message.error({
            message: /^Error: 1007/.test(e)
              ? this.$t('members.setting.message.notHaveAnyPlan')
              : this.$t('members.setting.message.updateFail') + `\n(${e})`,
            duration: 2000,
            showClose: true,
          })
        }
      }
    },
  },
}
</script>

<style lang="scss">
#__nuxt .member-setting-modal {
  .el-dialog__body {
    overflow-y: scroll;
  }
  h6 {
    margin-bottom: 16px;
    color: #445168;
  }
  dt,
  .el_input__label {
    margin-bottom: 10px;
    color: $font-color-desc;
    font-size: 13px;

    & > span,
    & > img {
      display: inline-block;
      vertical-align: middle;
    }
  }
  dd {
    margin-bottom: 20px;
  }
  .column-user {
    display: flex;

    & > span {
      flex: auto;
      font-size: 18px;
      overflow-wrap: anywhere;
    }
    .el-button {
      height: 34px;
      padding: 0 20px;
      span {
        margin-left: 0;
      }
    }
  }
  .el-divider {
    margin: 26px 0 20px;
  }

  .el-form {
    margin: 20px 0 0;
    .el-form-item {
      margin-bottom: 20px;
    }
  }
  .el-dialog__footer {
    border-top: solid 1px #edf0f7;

    .el-button:first-child {
      float: left;
    }
  }
}

body .el-popper.el-select-dropdown.member-setting-select__dropdown {
  .el-select-dropdown__list {
    padding: 6px 0;
  }
  .el-select-dropdown__item.hover {
    background: #e9ecf1;
  }
}
</style>
