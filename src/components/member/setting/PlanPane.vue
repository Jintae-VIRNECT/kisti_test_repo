<template>
  <section class="member-setting-plan-pane">
    <h6>{{ $t('members.setting.givePlans') }}</h6>
    <el-form class="virnect-workstation-form">
      <el-col :span="18">
        <el-form-item class="horizon" :label="plans.remote.label">
          <MemberPlanSelect
            v-model="member.licenseRemote"
            :label="plans.remote.label"
            :amount="remote"
            :isSeat="isSeat"
          />
        </el-form-item>
        <el-form-item class="horizon" :label="plans.make.label">
          <MemberPlanSelect
            v-model="member.licenseMake"
            :label="plans.make.label"
            :amount="make"
            :isSeat="isSeat"
          />
        </el-form-item>
        <el-form-item class="horizon" :label="plans.view.label">
          <MemberPlanSelect
            v-model="member.licenseView"
            :label="plans.view.label"
            :amount="view"
            :isSeat="isSeat"
          />
        </el-form-item>
        <el-form-item class="footer" v-if="editEnabled">
          <el-button type="primary" @click="updateMembersPlan">
            {{ $t('members.setting.submit') }}
          </el-button>
        </el-form-item>
      </el-col>
    </el-form>
  </section>
</template>

<script>
import plans from '@/models/workspace/plans'
import workspaceService from '@/services/workspace'
import permissionMixin from '@/mixins/permission'
import messageMixin from '@/mixins/message'
export default {
  mixins: [permissionMixin, messageMixin],
  data() {
    return {
      plans,
    }
  },
  props: {
    member: Object,
    originPlan: Object,
    remote: Number,
    make: Number,
    view: Number,
  },
  computed: {
    isSeat() {
      return this.roleIsSeat(this.member.role)
    },
    editEnabled() {
      if (this.canManage(this.member.userType, this.member.role)) {
        if (this.isSeat) {
          return false
        } else return true
      }
      return false
    },
  },
  methods: {
    async updateMembersPlan() {
      const updatePlan = {}
      Object.keys(this.originPlan).forEach(key => {
        if (this.originPlan[key] !== this.member[key]) {
          updatePlan[key] = this.member[key]
        }
      })

      // 변경된 플랜이 없으면 리턴
      if (Object.keys(updatePlan).length === 0) {
        return false
      }

      const form = {
        userId: this.member.userId,
        ...updatePlan,
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
#__nuxt .member-setting-plan-pane {
  .el-form-item {
    &.footer {
      display: flex;
      justify-content: flex-end;
    }
  }
}
</style>
