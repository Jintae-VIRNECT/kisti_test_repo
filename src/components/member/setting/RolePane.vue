<template>
  <section class="member-setting-role-pane">
    <h6>{{ $t('members.setting.workspaceRole') }}</h6>
    <el-form class="virnect-workstation-form" ref="form" :model="member">
      <el-col :span="18" v-if="isNotSeat">
        <el-form-item class="horizon">
          <template slot="label">
            <span>{{ $t('members.setting.workspaceRoleDesc') }}</span>
          </template>
          <MemberRoleSelect v-model="member.role" :disabled="!editEnabled" />
        </el-form-item>
        <el-form-item class="footer" v-if="editEnabled">
          <el-button v-show="kickEnabled" @click="$emit('kick')">
            {{ $t('members.setting.kick') }}
          </el-button>
          <el-button type="primary" @click="updateMembersRole">
            {{ $t('members.setting.submit') }}
          </el-button>
        </el-form-item>
      </el-col>
      <el-col :span="18" v-else>
        <el-form-item label="시트는 관리자 역할을 할 수 없습니다." />
      </el-col>
    </el-form>
  </section>
</template>

<script>
import permissionMixin from '@/mixins/permission'
import messageMixin from '@/mixins/message'
import workspaceService from '@/services/workspace'
export default {
  mixins: [permissionMixin, messageMixin],
  props: {
    member: Object,
  },
  computed: {
    editEnabled() {
      if (this.canManage(this.member.userType, this.member.role)) {
        return true
      }
      return false
    },
    kickEnabled() {
      return this.isUserTypeUser(this.member.userType)
    },
    isNotSeat() {
      return !this.isRoleSeat(this.member.role)
    },
  },
  methods: {
    async updateMembersRole() {
      const form = {
        userId: this.member.userId,
        role: this.member.role,
      }

      try {
        await workspaceService.updateMembersInfo(form)
        this.$message.success({
          message: this.$t('members.setting.message.updateSuccess'),
          duration: 2000,
          showClose: true,
        })
        this.$emit('update', this.member)
      } catch (e) {
        if (/^Error: 2000/.test(e)) {
          this.$confirm(this.$t('members.add.message.noHavePlans'), {
            confirmButtonText: this.$t('common.paymentCenter'),
            customClass: 'no-title',
          }).then(() => {
            window.open(`${this.$url.pay}`)
          })
        } else {
          this.errorMessage(e)
        }
      }
    },
  },
}
</script>

<style lang="scss">
#__nuxt .member-setting-role-pane {
  .el-form-item {
    &.footer {
      display: flex;
      justify-content: flex-end;
    }
  }
}
</style>
