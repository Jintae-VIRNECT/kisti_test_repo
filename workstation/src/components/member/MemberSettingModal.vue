<template>
  <el-dialog
    class="member-setting-modal"
    :visible.sync="showMe"
    :title="$t('members.setting.title')"
    width="440px"
    top="11vh"
  >
    <div>
      <h6>{{ $t('members.setting.info') }}</h6>
      <dl>
        <dt>{{ $t('members.setting.nickname') }}</dt>
        <dd class="column-user">
          <div class="avatar">
            <div
              class="image"
              :style="`background-image: url(${data.profile})`"
            />
          </div>
          <span>{{ data.nickname }}</span>
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
              <el-select v-model="form.role" :disabled="form.role === 'MASTER'">
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
        <dt>
          <span>{{ $t('members.setting.givePlans') }}</span>
          <el-tooltip
            :content="$t('members.setting.givePlansDesc')"
            placement="bottom-start"
          >
            <img src="~assets/images/icon/ic-error.svg" />
          </el-tooltip>
        </dt>
        <el-row>
          <el-col :span="12">
            <el-form-item class="horizon" label="Make">
              <el-select disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item class="horizon" label="View">
              <el-select disabled />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <div slot="footer">
      <el-button @click="$emit('kick')">
        {{ $t('members.setting.kick') }}
      </el-button>
      <el-button type="primary" @click="submit">
        {{ $t('members.setting.submit') }}
      </el-button>
    </div>
  </el-dialog>
</template>

<script>
import { role } from '@/models/workspace/Member'
import workspaceService from '@/services/workspace'

export default {
  props: {
    data: Object,
    visible: Boolean,
  },
  data() {
    return {
      showMe: false,
      roles: role.options.filter(({ value }) => value !== 'MASTER'),
      form: {
        uuid: this.data.uuid,
        role: this.data.role,
      },
    }
  },
  watch: {
    visible(val) {
      this.showMe = val
    },
    showMe(val) {
      this.$emit('update:visible', val)
    },
  },
  methods: {
    async submit() {
      try {
        await workspaceService.updateMembersRole(this.form)
        this.$message.success({
          message: this.$t('members.setting.message.updateSuccess'),
          showClose: true,
        })
        this.$emit('updated', this.form)
      } catch (e) {
        this.$message.error({
          message: this.$t('members.setting.message.updateFail'),
          showClose: true,
        })
      }
    },
  },
}
</script>

<style lang="scss">
#__nuxt .member-setting-modal {
  h6 {
    margin-bottom: 16px;
    color: #445168;
  }
  dt,
  .el_input__label {
    margin-bottom: 10px;
    color: $font-color-desc;
    font-size: 12.6px;

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
    .avatar {
      width: 28px;
      height: 28px;
    }
    span {
      font-size: 18px;
    }
  }
  .el-divider {
    margin: 26px 0 20px;
  }

  .el-form {
    margin: 20px 0;
    .el-form-item {
      margin-bottom: 20px;
    }
    .el-input {
      width: 180px;
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
