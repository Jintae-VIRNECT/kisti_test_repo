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
              <el-select v-model="form.role" :disabled="!canChangeRole">
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
            <el-form-item class="horizon" :label="plans.make.label">
              <el-select v-model="form.licenseMake">
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
              <el-select v-model="form.licenseView" disabled>
                <el-option
                  :value="false"
                  :label="$t('members.setting.givePlansEmpty')"
                />
                <el-option :value="true" :label="plans.view.label" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </div>
    <div slot="footer">
      <el-button v-show="canKick" @click="$emit('kick')">
        {{ $t('members.setting.kick') }}
      </el-button>
      <el-button type="primary" @click="submit">
        {{ $t('members.setting.submit') }}
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
import urls from 'WC-Modules/javascript/api/virnectPlatform/urls'

export default {
  mixins: [modalMixin],
  props: {
    data: Object,
  },
  data() {
    return {
      plans,
      roles: role.options.filter(({ value }) => value !== 'MASTER'),
      form: {},
    }
  },
  computed: {
    ...mapGetters({
      activeWorkspace: 'workspace/activeWorkspace',
    }),
    canChangeRole() {
      return (
        this.form.role !== 'MASTER' && this.activeWorkspace.role === 'MASTER'
      )
    },
    canKick() {
      return (
        this.data.role !== 'MASTER' &&
        this.activeWorkspace.role !== 'MEMBER' &&
        this.activeWorkspace.role !== this.data.role
      )
    },
  },
  methods: {
    opened() {
      this.form = new EditMember(this.data)
    },
    async submit() {
      try {
        await workspaceService.updateMembersRole(this.form)
        this.$message.success({
          message: this.$t('members.setting.message.updateSuccess'),
          showClose: true,
        })
        this.$emit('updated', this.form)
      } catch (e) {
        if (/^Error: 2000/.test(e)) {
          this.$confirm(this.$t('members.add.message.noHavePlans'), {
            confirmButtonText: this.$t('common.paymentCenter'),
            customClass: 'no-title',
          }).then(() => {
            window.open(`${urls.pay[process.env.TARGET_ENV]}`)
          })
        } else {
          this.$message.error({
            message: this.$t('members.add.message.updateFail') + `\n(${e})`,
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
