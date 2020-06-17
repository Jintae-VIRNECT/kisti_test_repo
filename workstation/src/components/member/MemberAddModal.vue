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
              <el-select v-model="form.role">
                <el-option
                  class="column-role"
                  v-for="role in roles"
                  :key="role.value"
                  :value="role.value"
                >
                  <el-tag :class="role.value">{{ $t(role.label) }}</el-tag>
                </el-option>
              </el-select>
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
            <el-form-item class="horizon" :label="plans.make.label">
              <el-select v-model="form.planMake">
                <el-option
                  :value="false"
                  :label="$t('members.setting.givePlansEmpty')"
                />
                <el-option :value="true" :label="plans.make.label" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item class="horizon" :label="plans.view.label">
              <el-select v-model="form.planView" disabled>
                <el-option :value="true" :label="plans.view.label" />
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
      <el-button type="primary" @click="submit">
        {{ $t('members.add.submit') }}
        <span class="number">{{ userInfoList.length }}</span>
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import modalMixin from '@/mixins/modal'
import { role } from '@/models/workspace/Member'
import InviteMember from '@/models/workspace/InviteMember'
import workspaceService from '@/services/workspace'
import plans from '@/models/workspace/plans'
import urls from 'WC-Modules/javascript/api/virnectPlatform/urls'

export default {
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
  methods: {
    opened() {
      this.userInfoList = [new InviteMember()]
      this.$refs.form.forEach(form => form.resetFields())
    },
    addMember() {
      this.userInfoList.push(new InviteMember())
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
          showClose: true,
        })
        this.$emit('updated', this.form)
        this.showMe = false
      } catch (e) {
        if (/^Error: 2003/.test(e)) {
          this.$confirm(this.$t('members.add.message.noHavePlans'), {
            confirmButtonText: this.$t('common.paymentCenter'),
            customClass: 'no-title',
          }).then(() => {
            window.open(`${urls.pay[process.env.TARGET_ENV]}`)
          })
        } else if (/^Error: 1002/.test(e)) {
          this.$message.error({
            message: this.$t('members.add.message.memberAlready'),
            showClose: true,
          })
        } else {
          this.$message.error({
            message: this.$t('members.add.message.inviteFail') + `\n(${e})`,
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
    font-size: 12.6px;

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
    button .number {
      margin-left: 2px;
      padding: 1px 7px;
      color: #0052cc;
      font-weight: bold;
      font-size: 12px;
      background: rgba(255, 255, 255, 0.9);
      border-radius: 10px;
    }
  }
}
</style>
