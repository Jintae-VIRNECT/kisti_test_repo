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
        v-for="(form, index) in userInfoList"
        :key="index"
        class="virnect-workstation-form"
        :model="form"
      >
        <el-divider />
        <h6>
          <img src="~assets/images/icon/ic-person.svg" />
          <span>{{ `${$t('members.add.addUser')} ${index + 1}` }}</span>
        </h6>
        <el-form-item class="horizon">
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
              <el-tag :class="role.value">{{ role.label }}</el-tag>
            </el-option>
          </el-select>
        </el-form-item>
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
            <el-form-item class="horizon" label="Make">
              <el-select v-model="form.makeType" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item class="horizon" label="View">
              <el-select v-model="form.viewType" disabled />
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
import roles from '@/models/workspace/roles'
import InviteMember from '@/models/workspace/InviteMember'
import workspaceService from '@/services/workspace'

export default {
  props: {
    visible: Boolean,
  },
  data() {
    return {
      showMe: false,
      roles: roles,
      userInfoList: [new InviteMember()],
    }
  },
  watch: {
    visible(val) {
      this.showMe = val
    },
    showMe(val) {
      this.$emit('update:visible', val)
      this.userInfoList = [new InviteMember()]
    },
  },
  methods: {
    addMember() {
      this.userInfoList.push(new InviteMember())
    },
    async submit() {
      try {
        await workspaceService.inviteMembers(this.userInfoList)
        this.$message.success({
          message: this.$t('members.add.message.inviteSuccess'),
          showClose: true,
        })
        this.$emit('updated', this.form)
      } catch (e) {
        this.$message.error({
          message: this.$t('members.add.message.inviteFail'),
          showClose: true,
        })
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
