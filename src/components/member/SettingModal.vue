<template>
  <el-dialog
    class="member-setting-modal"
    :visible.sync="showMe"
    :title="$t('members.setting.title')"
    width="680px"
    top="11vh"
    :close-on-click-modal="false"
  >
    <el-row type="flex">
      <el-col :span="7" class="member-setting-modal--left">
        <button
          @click="showTab('account')"
          class="member-setting-modal__tab-button"
          :class="{
            'member-setting-modal__tab-button--clicked': tabName === 'account',
          }"
        >
          {{ $t('members.setting.account') }}
        </button>
        <button
          @click="showTab('plan')"
          class="member-setting-modal__tab-button"
          :class="{
            'member-setting-modal__tab-button--clicked': tabName === 'plan',
          }"
        >
          {{ $t('members.setting.givePlans') }}
        </button>
        <button
          @click="showTab('role')"
          class="member-setting-modal__tab-button"
          :class="{
            'member-setting-modal__tab-button--clicked': tabName === 'role',
          }"
        >
          {{ $t('members.setting.workspaceRole') }}
        </button>
      </el-col>
      <el-col :span="17" class="member-setting-modal--right">
        <article v-show="tabName === 'account'">
          <section>
            <h6>{{ accountTitle }}</h6>
            <el-form
              class="virnect-workstation-form"
              ref="form"
              label-position="top"
              label-width="120px"
            >
              <el-col>
                <el-form-item :label="$t('members.setting.image.title')">
                  <el-upload
                    ref="upload"
                    action="#"
                    accept=".jpg,.png"
                    :auto-upload="false"
                    :on-change="imageSelected"
                    :show-file-list="false"
                    :disabled="!mine && !canEditRole"
                  >
                    <VirnectThumbnail :size="80" :image="cdn(profile)" />
                  </el-upload>
                  <el-button
                    v-if="canEditRole"
                    @click="updateMembersProfile"
                    class="member-setting-modal__image-change-button"
                    :disabled="!profileChanged"
                    >{{ $t('members.setting.image.edit') }}</el-button
                  >
                </el-form-item>
                <el-form-item
                  :label="$t('members.setting.nickname.title')"
                  class="horizon"
                >
                  <ValidationProvider
                    rules="requiredNickname|nicknameCheck"
                    v-slot="{ errors, valid }"
                  >
                    <el-input
                      v-model="form.nickname"
                      :disabled="!mine && !canEditRole"
                      :placeholder="data.nickname"
                    />
                    <el-button
                      type="primary"
                      v-if="mine || canEditRole"
                      @click="updateNickname"
                      class="member-setting-modal__submit-button"
                      :disabled="!valid"
                      >{{ $t('members.setting.submit') }}</el-button
                    >
                    <span>{{ errors[0] }}</span>
                  </ValidationProvider>
                </el-form-item>
                <el-form-item
                  v-if="form.userType === 'SEAT_USER'"
                  :label="$t('members.setting.seat.title')"
                >
                  <el-input disabled :placeholder="form.userId" />
                </el-form-item>
                <el-form-item
                  v-if="form.userType !== 'SEAT_USER'"
                  :label="$t('members.setting.account')"
                >
                  <el-input disabled :placeholder="data.email" />
                </el-form-item>
                <el-form-item
                  v-if="form.userType !== 'SEAT_USER'"
                  :label="$t('members.setting.password.title')"
                  class="horizon"
                >
                  <ValidationProvider
                    rules="requiredPassword|passwordCheck"
                    v-slot="{ errors, valid }"
                  >
                    <el-input
                      show-password
                      v-model="form.password"
                      :placeholder="$t('members.setting.password.placeholder')"
                    />

                    <el-button
                      type="primary"
                      @click="updatePassword"
                      class="member-setting-modal__submit-button"
                      :disabled="!valid"
                      >{{ $t('members.setting.submit') }}</el-button
                    >
                    <span> {{ errors[0] }}</span>
                  </ValidationProvider>
                </el-form-item>
                <div v-if="canDeleteRole">
                  <el-divider v-if="canDeleteRole" />
                  <el-form-item
                    v-if="data.userType === 'SEAT_USER'"
                    :label="$t('members.setting.delete.seat')"
                    class="horizon"
                  >
                    <ValidationProvider
                      rules="requiredDeleteDesc|deleteDescCheck"
                      v-slot="{ valid }"
                    >
                      <span>{{ $t('members.setting.delete.desc') }}</span>
                      <el-input
                        v-model="form.deleteDesc"
                        :placeholder="$t('members.setting.delete.placeholder')"
                      />
                      <el-button
                        type="primary"
                        v-if="canUpdate"
                        @click="deleteSeatMember"
                        class="member-setting-modal__submit-button"
                        :disabled="!valid"
                        >{{ $t('members.setting.delete.title') }}</el-button
                      >
                    </ValidationProvider>
                  </el-form-item>
                  <el-form-item
                    :label="$t('members.setting.delete.account')"
                    v-else-if="data.userType === 'WORKSPACE_ONLY_USER'"
                    class="horizon"
                  >
                    <ValidationProvider
                      rules="requiredDeleteDesc|deleteDescCheck"
                      v-slot="{ valid }"
                    >
                      <span>{{ $t('members.setting.delete.desc') }}</span>
                      <el-input
                        v-model="form.deleteDesc"
                        :placeholder="$t('members.setting.delete.placeholder')"
                      />
                      <el-button
                        type="primary"
                        @click="deleteAccountMember"
                        class="member-setting-modal__submit-button"
                        :disabled="!valid"
                        >{{ $t('members.setting.delete.title') }}</el-button
                      >
                    </ValidationProvider>
                  </el-form-item>
                </div>
              </el-col>
            </el-form>
          </section>
        </article>
        <article v-show="tabName === 'plan'">
          <h6>{{ $t('members.setting.givePlans') }}</h6>
          <el-form
            class="virnect-workstation-form"
            ref="form"
            :model="form"
            @submit.native.prevent="submit"
          >
            <el-col :span="18">
              <el-form-item class="horizon" :label="plans.remote.label">
                <MemberPlanSelect
                  v-model="form.licenseRemote"
                  :label="plans.remote.label"
                  :amount="plansInfo.remote.unUsedAmount"
                  :isSeat="form.role === 'SEAT'"
                />
              </el-form-item>
              <el-form-item class="horizon" :label="plans.make.label">
                <MemberPlanSelect
                  v-model="form.licenseMake"
                  :label="plans.make.label"
                  :amount="plansInfo.make.unUsedAmount"
                  :isSeat="form.role === 'SEAT'"
                />
              </el-form-item>
              <el-form-item class="horizon" :label="plans.view.label">
                <MemberPlanSelect
                  v-model="form.licenseView"
                  :label="plans.view.label"
                  :amount="plansInfo.view.unUsedAmount"
                  :isSeat="form.role === 'SEAT'"
                />
              </el-form-item>
              <el-form-item class="footer" v-if="canChangePlan">
                <el-button type="primary" @click="updateMembersPlan">
                  {{ $t('members.setting.submit') }}
                </el-button>
              </el-form-item>
            </el-col>
          </el-form>
        </article>
        <article v-show="tabName === 'role'">
          <h6>{{ $t('members.setting.workspaceRole') }}</h6>
          <el-form
            class="virnect-workstation-form"
            ref="form"
            :model="form"
            @submit.native.prevent="updateMembersRole"
          >
            <el-col :span="18" v-if="form.role !== 'SEAT'">
              <el-form-item class="horizon">
                <template slot="label">
                  <span>{{ $t('members.setting.workspaceRoleDesc') }}</span>
                </template>
                <MemberRoleSelect
                  v-model="form.role"
                  :disabled="!canChangeRole"
                />
              </el-form-item>
              <el-form-item class="footer" v-if="canChangeRole">
                <el-button v-show="canKick" @click="$emit('kick')">
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
        </article>
      </el-col>
    </el-row>
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

import { ValidationProvider, extend } from 'vee-validate'
import { required } from 'vee-validate/dist/rules'
export default {
  mixins: [filterMixin, modalMixin],
  props: {
    data: Object,
  },
  components: {
    ValidationProvider,
  },
  data() {
    return {
      plans,
      roles: role.options.filter(({ value }) => value !== 'MASTER'),
      form: {},
      profile: this.data.profile,
      profileChanged: false,
      test: [],
      file: null,
      tabName: 'account',
      rules: {
        nickname: [
          {
            validator: (rule, value, callback) => {
              if (!/^.{1,20}$/.test(value)) {
                callback(new Error(this.$t('members.setting.nickname.caution')))
              } else if (/[<>]/.test(value)) {
                callback(new Error(this.$t('members.setting.nickname.caution')))
              } else {
                callback()
              }
            },
          },
        ],
        password: [
          {
            validator: (rule, value, callback) => {
              let typeCount = 0
              if (/[0-9]/.test(value)) typeCount++
              if (/[a-z]/.test(value)) typeCount++
              if (/[A-Z]/.test(value)) typeCount++
              if (/[$.$,$!$@$#$$$%]/.test(value)) typeCount++

              if (typeCount < 3) {
                callback(new Error(this.$t('members.setting.password.caution')))
              }
              if (!/^.{8,20}$/.test(value)) {
                callback(new Error(this.$t('members.setting.password.caution')))
              }
              if (/(.)\1\1\1/.test(value)) {
                callback(new Error(this.$t('members.setting.password.caution')))
              }
              if (/(0123|1234|2345|3456|4567|5678|6789|7890)/.test(value)) {
                callback(new Error(this.$t('members.setting.password.caution')))
              }
              if (/(0987|9876|8765|7654|6543|5432|4321|3210)/.test(value)) {
                callback(new Error(this.$t('members.setting.password.caution')))
              }

              callback()
            },
          },
        ],
        deleteDesc: [
          {
            validator: (rule, value, callback) => {
              if (value !== 'Delete Account') {
                callback(new Error(this.$t('members.setting.delete.desc')))
              } else {
                callback()
              }
            },
          },
        ],
      },
      imageChangeModal: false,
    }
  },
  created() {
    // vee-validate에서 required 제공

    // vee-validate 를 이용한 처리
    extend('requiredPassword', {
      ...required,
      message: this.$t('members.setting.password.caution'),
    })
    extend('passwordCheck', {
      validate: this.passwordCheck,
      message: this.$t('members.setting.password.caution'),
    })

    extend('requiredNickname', {
      ...required,
      message: this.$t('members.setting.nickname.caution'),
    })
    extend('nicknameCheck', {
      validate: this.nicknameCheck,
      message: this.$t('members.setting.nickname.caution'),
    })

    extend('requiredDeleteDesc', {
      ...required,
      message: this.$t('members.setting.delete.desc'),
    })
    extend('deleteDescCheck', {
      validate: this.deleteDescCheck,
      message: this.$t('members.setting.delete.desc'),
    })
  },
  computed: {
    ...mapGetters({
      activeWorkspace: 'auth/activeWorkspace',
      plansInfo: 'plan/plansInfo',
      auth: 'auth/auth',
    }),
    canChangePlan() {
      if (this.form.role === 'SEAT') {
        return false
      } else if (this.activeWorkspace.role === 'MASTER') {
        return true
      } else if (this.activeWorkspace.role === 'MANAGER') {
        if (this.form.role === 'MASTER') return false
        else return true
      }
      return false
    },
    canChangeRole() {
      if (this.form.role === 'MASTER') {
        return false
      } else if (this.activeWorkspace.role === 'MASTER') {
        return true
      } else if (this.activeWorkspace.role === 'MANAGE') {
        return true
      }
      return false
    },
    canDeleteRole() {
      if (this.form.userType === 'USER') {
        return false
      } else if (this.form.role === 'MASTER') {
        return false
      } else if (this.activeWorkspace.role === 'MASTER') {
        return true
      } else if (this.activeWorkspace.role === 'MANAGER') {
        return true
      }
      return false
    },
    canEditRole() {
      if (this.form.userType === 'USER') {
        return false
      } else if (this.activeWorkspace.role === 'MASTER') {
        return true
      } else if (
        this.form.role === 'MASTER' &&
        this.activeWorkspace.role !== 'MASTER'
      ) {
        return false
      } else if (this.activeWorkspace.role === 'MANAGER') {
        return true
      }
      return false
    },
    mine() {
      return this.form.userId === this.activeWorkspace.uuid
    },
    canUpdate() {
      return (
        (this.form.role !== 'MASTER' &&
          this.activeWorkspace.role === 'MASTER') ||
        this.activeWorkspace.role === 'MANAGER'
      )
    },
    canKick() {
      return this.$isOnpremise
        ? this.activeWorkspace.role === 'MASTER' &&
            this.data.userType === 'MEMBER_USER'
        : this.data.role !== 'MASTER' &&
            this.data.userType === 'USER' &&
            this.activeWorkspace.role !== 'MEMBER' &&
            this.activeWorkspace.role !== this.data.role
    },
    accountTitle() {
      let title = ''
      switch (this.data.userType) {
        case 'SEAT_USER':
          title = this.$t('members.setting.seat.title')
          break
        case 'USER':
          title = this.$t('members.setting.personalAccount')
          break
        case 'WORKSPACE_ONLY_USER':
          title = this.$t('members.setting.workspaceAccount')
          break

        default:
          break
      }
      return title
    },
  },
  methods: {
    opened() {
      this.form = new EditMember(this.data)
      this.originPlan = {
        licenseRemote: this.form.licenseRemote,
        licenseMake: this.form.licenseMake,
        licenseView: this.form.licenseView,
      }
      if (!this.plansInfo.planStatus) {
        this.$store.dispatch('plan/getPlansInfo')
      }
      console.log(this.activeWorkspace)
    },
    showTab(tabName) {
      this.tabName = tabName
    },
    imageSelected(file) {
      if (this.checkSelectdFile(file)) {
        const reader = new FileReader()
        reader.readAsDataURL(file.raw)
        reader.onload = () => {
          this.profile = reader.result
          this.profileChanged = true
        }
      }
    },
    nicknameCheck(nickname) {
      if (nickname.trim().length < 1 || nickname.trim().length > 20) {
        return false
      } else if (/[<>]/.test(nickname)) {
        return false
      }
      return true
    },
    passwordCheck(password) {
      let typeCount = 0
      if (/[0-9]/.test(password)) typeCount++
      if (/[a-z]/.test(password)) typeCount++
      if (/[A-Z]/.test(password)) typeCount++
      if (/[$.$,$!$@$#$$$%]/.test(password)) typeCount++

      if (typeCount < 3) return false
      if (!/^.{8,20}$/.test(password)) return false
      if (/(.)\1\1\1/.test(password)) return false
      if (/(0123|1234|2345|3456|4567|5678|6789|7890)/.test(password))
        return false
      if (/(0987|9876|8765|7654|6543|5432|4321|3210)/.test(password))
        return false
      return true
    },
    deleteDescCheck(desc) {
      if ('Delete Account' !== desc) {
        return false
      }
      return true
    },
    /**
     * @author YongHo Kim <yhkim@virnect.com>
     * @description 선택한 파일의 조건을 확인하고 조건에 부합하는 파일이라면 true를 반환, 아니라면 false를 반환
     * @param {object} file
     * @returns {boolean} 조건에 부합하는 파일인지 확인하고 결과를 리턴
     */
    checkSelectdFile(file) {
      const isImage =
        file.raw.type === 'image/jpeg' || file.raw.type === 'image/png'
      let message = ''
      if (!isImage) {
        message = this.$t('members.setting.image.notAllowFileExtension')
        this.$notify.error({
          message,
          position: 'bottom-left',
          duration: 2000,
        })
        return false
      }

      const isLimitSize = file.raw.size / 1024 / 1024 < 5 // 서버에서 제한한 파일의 크기 5MB
      if (!isLimitSize) {
        message = this.$t('members.setting.image.notAllowFileSize')
        this.$notify.error({
          message,
          position: 'bottom-left',
          duration: 2000,
        })
        return false
      }

      return isImage && isLimitSize
    },
    async updateMembersRole() {
      const form = {
        userId: this.form.userId,
        role: this.form.role,
      }

      try {
        await workspaceService.updateMembersInfo(form)
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
        } else if (/^Error: 1021/.test(e)) {
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
    async updateMembersPlan() {
      /**
       * 에러 받는 로직 리팩터링 필요
       */
      // 기존 플랜과 변경된 플랜을 비교하는 로직, 더 깔끔하게 할 수 있는 방법은?
      // 버튼 비활성화 > 변경하면 버튼 활성화 ?
      const updatePlan = {}
      Object.keys(this.originPlan).forEach(key => {
        if (this.originPlan[key] !== this.form[key]) {
          updatePlan[key] = this.form[key]
        }
      })

      // 변경된 플랜이 없으면 리턴
      if (Object.keys(updatePlan).length === 0) {
        console.log('변경된 플랜이 없다.')
        return false
      }

      const form = {
        userId: this.form.userId,
        ...updatePlan,
      }

      try {
        await workspaceService.updateMembersInfo(form)
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
        } else if (/^Error: 1021/.test(e)) {
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
    async updatePassword() {
      try {
        await workspaceService.changeMembersPassword(
          this.form.userId,
          this.form.password,
        )
        this.$notify.success({
          message: this.$t('members.setting.password.success'),
          position: 'bottom-left',
          duration: 2000,
        })
        this.form.password = ''
      } catch (e) {
        this.$notify.error({
          message: this.$t('members.setting.password.fail') + `\n(${e})`,
          position: 'bottom-left',
          duration: 2000,
        })
      }
    },
    async updateNickname() {
      const from = {
        userId: this.form.userId,
        nickname: this.form.nickname,
      }

      try {
        await workspaceService.updateMembersInfo(from)
        this.$notify.success({
          message: this.$t('members.setting.nickname.success'),
          position: 'bottom-left',
          duration: 2000,
        })
        this.data.nickname = this.form.nickname

        // 헤더 정보 업데이트
        this.$store.dispatch('auth/getAuth')
      } catch (e) {
        this.$notify.error({
          message: this.$t('members.setting.nickname.fail') + `\n(${e})`,
          position: 'bottom-left',
          duration: 2000,
        })
      }
    },
    async updateMembersProfile() {
      const { uploadFiles } = this.$refs.upload
      const form = {
        userId: this.form.userId,
        profile: uploadFiles.length
          ? uploadFiles[uploadFiles.length - 1].raw
          : null,
        updateAsDefaultImage: uploadFiles.length ? true : false,
      }
      try {
        await workspaceService.updateMembersProfile(form)
        this.$notify.success({
          message: this.$t('members.setting.image.success'),
          position: 'bottom-left',
          duration: 2000,
        })
        this.data.profile = this.profile
      } catch (e) {
        let message = this.$t('members.setting.image.fail') + `\n(${e})`
        if (/^Error: 4005/.test(e))
          message = this.$t('members.setting.image.notAllowFileExtension')
        if (/^Error: 4006/.test(e))
          message = this.$t('members.setting.image.notAllowFileSize')

        this.$notify.error({
          message,
          position: 'bottom-left',
          duration: 2000,
        })
      }
    },
    async deleteSeatMember() {
      try {
        await workspaceService.deleteSeatMember(this.form.userId)

        this.$alert(
          this.$t('members.delete.message.successContent'),
          this.$t('members.delete.message.successTitle'),
          {
            confirmButtonText: this.$t('common.confirm'),
            callback: () => this.$emit('deleteSeat'),
          },
        )
      } catch (e) {
        const errMsg =
          {
            1003: this.$t('members.delete.message.wrongPassword'),
          }[e.code] ||
          this.$t('members.delete.message.fail') + ` [ERROR CODE : ${e.code}]`
        // 에러
        this.$message.error({
          message: errMsg,
          duration: 4000,
          showClose: true,
        })
      }
    },
    async deleteAccountMember() {
      try {
        await workspaceService.deleteMember(this.form.userId)

        this.$alert(
          this.$t('members.delete.message.successContent'),
          this.$t('members.delete.message.successTitle'),
          {
            confirmButtonText: this.$t('common.confirm'),
            callback: () => this.$emit('deleteSeat'),
          },
        )
      } catch (e) {
        const errMsg =
          {
            1003: this.$t('members.delete.message.wrongPassword'),
          }[e.code] ||
          this.$t('members.delete.message.fail') + ` [ERROR CODE : ${e.code}]`
        // 에러
        this.$message.error({
          message: errMsg,
          duration: 4000,
          showClose: true,
        })
      }
    },
  },
}
</script>

<style lang="scss">
#__nuxt .member-setting-modal {
  .el-dialog__body {
    overflow-y: scroll;
    padding: 0;
    .el-row--flex {
      min-height: 487px;
    }
  }
  .el-form-item {
    margin-bottom: 20px;
    &.footer {
      display: flex;
      justify-content: flex-end;
    }
  }
  .el-divider {
    margin: 26px 0 20px;
  }
  .el-upload {
    button {
      width: 134px;
      height: 36px;
    }
  }
  .el-form-item__content {
    display: flex;
    flex-wrap: wrap;
    align-items: center;
    .el-input {
      width: 320px;
      margin-right: 8px;
    }
  }
  .el-form-item__label:first-child {
    padding-bottom: 4px;
  }
  .el-form-item__label {
    padding: 0;
  }

  dt {
    @include fontLevel(75);
    margin-top: 16px;
    color: #445168;
  }
  dt:first-child {
    margin-top: 0;
  }
  dd {
    display: flex;
    margin-top: 14px;
    flex-wrap: wrap;
    align-items: center;

    .el-input.is-disabled .el-input__inner {
      border-color: #f5f7fa;
    }
    small {
      @include fontLevel(50);
      opacity: 0.8;
      color: #5e6b81;
      width: 320px;
      margin-top: 4px;
    }
    span {
      @include fontLevel(75);
      margin-bottom: 4px;
    }
  }

  h6 {
    @include fontLevel(100);
    color: #0b1f48;
    margin-bottom: 16px;
  }
  &--left {
    border-right: 1px solid #eaeef2;
    padding: 12px;
    button:first-child {
      margin-top: 0;
    }
  }
  &--right {
    padding: 24px;
  }
  .member-setting-modal__tab-button {
    width: 160px;
    height: 38px;
    margin: 2px 0;
    padding: 9px 12px;
    border-radius: 3px;
    text-align: left;
    &:hover {
      background-color: #eff2f7;
    }
  }
  .member-setting-modal__tab-button--clicked {
    @extend .member-setting-modal__tab-button;
    background-color: #eff2f7;
  }
  &__image {
    margin-top: 8px;
  }
  .member-setting-modal__submit-button {
    @include fontLevel(100);
    line-height: 14px;
    width: 66px;
    height: 38px;
    border-radius: 3px;
  }
  .member-setting-modal__image-change-button {
    @include fontLevel(75);
    line-height: 13px;
    width: 134px;
    height: 36px;
    padding: 8px 16px;
    border-radius: 3px;
    margin-left: 24px;
  }
}
</style>
