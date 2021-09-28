<template>
  <section class="member-setting-account-pane">
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
            :disabled="!editEnabled"
          >
            <VirnectThumbnail :size="80" :image="cdn(selectedProfile)" />
          </el-upload>
          <el-button
            v-if="editEnabled"
            @click="updateMembersProfile"
            class="member-setting-account-pane__image-change-button"
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
              v-model="member.nickname"
              :disabled="!editEnabled"
              :placeholder="member.nickname"
            />
            <el-button
              type="primary"
              v-if="editEnabled"
              @click="updateNickname"
              class="member-setting-account-pane__submit-button"
              :disabled="!valid"
              >{{ $t('members.setting.submit') }}</el-button
            >
            <span>{{ errors[0] }}</span>
          </ValidationProvider>
        </el-form-item>
        <el-form-item
          v-if="isUserTypeGuest(member.userType)"
          :label="$t('members.setting.guest.id')"
        >
          <el-input disabled :placeholder="member.userId" />
        </el-form-item>
        <el-form-item v-else :label="$t('members.setting.account')">
          <el-input disabled :placeholder="member.email" />
        </el-form-item>
        <el-form-item
          v-if="isUserTypeWorkspaceOnly(member.userType)"
          :label="$t('members.setting.password.title')"
          class="horizon"
        >
          <ValidationProvider
            rules="requiredPassword|passwordCheck"
            v-slot="{ errors, valid }"
          >
            <el-input
              show-password
              v-model="member.password"
              :placeholder="$t('members.setting.password.placeholder')"
            />

            <el-button
              type="primary"
              @click="updatePassword"
              class="member-setting-account-pane__submit-button"
              :disabled="!valid"
              >{{ $t('members.setting.submit') }}</el-button
            >
            <span> {{ errors[0] }}</span>
          </ValidationProvider>
        </el-form-item>
        <div v-if="deleteEnabled">
          <el-divider v-if="deleteEnabled" />
          <el-form-item :label="deleteTitle" class="horizon">
            <ValidationProvider
              rules="requiredDeleteDesc|deleteDescCheck"
              v-slot="{ valid }"
            >
              <span>{{ $t('members.setting.delete.desc') }}</span>
              <el-input
                v-model="member.deleteDesc"
                :placeholder="$t('members.setting.delete.placeholder')"
              />
              <el-button
                type="primary"
                @click="deleteMember"
                class="member-setting-account-pane__submit-button"
                :disabled="!valid"
                >{{ $t('members.setting.delete.title') }}</el-button
              >
            </ValidationProvider>
          </el-form-item>
        </div>
      </el-col>
    </el-form>
  </section>
</template>

<script>
import filterMixin from '@/mixins/filters'
import permissionMixin from '@/mixins/permission'
import messageMixin from '@/mixins/message'
import utilsMixin from '@/mixins/utils'
import workspaceService from '@/services/workspace'
export default {
  mixins: [filterMixin, permissionMixin, messageMixin, utilsMixin],
  data() {
    return {
      originProfile: this.profile,
      selectedProfile: this.profile,
      profileChanged: false,
    }
  },
  props: {
    member: Object,
    nickname: String,
    profile: String,
    showModal: Boolean,
  },
  computed: {
    editEnabled() {
      if (this.mine(this.member.userId)) {
        return true
      } else if (this.canEdit(this.member.userType, this.member.role)) {
        return true
      }
      return false
    },
    deleteEnabled() {
      if (this.isRoleManager(this.member.role)) {
        return false
      } else if (this.canEdit(this.member.userType, this.member.role)) {
        return true
      }
      return false
    },
    deleteTitle() {
      let title = ''
      if (this.isUserTypeGuest(this.member.userType)) {
        title = this.$t('members.setting.delete.guest')
      } else {
        title = this.$t('members.setting.delete.account')
      }
      return title
    },
    accountTitle() {
      let title = ''
      switch (this.member.userType) {
        case 'GUEST_USER':
          title = this.$t('members.setting.guest.title')
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
  watch: {
    /**
     * 모달 창이 닫히면 변수 초기화
     */
    showModal(val) {
      if (val === false) {
        this.selectedProfile = this.profile
        this.profileChanged = false
      }
    },
  },
  methods: {
    async updateMembersProfile() {
      const { uploadFiles } = this.$refs.upload
      const form = {
        userId: this.member.userId,
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
        this.$emit('update:profile', this.selectedProfile)
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
        this.selectedProfile = this.originProfile
      }
    },
    async deleteMember() {
      try {
        if (this.isUserTypeGuest(this.member.userType)) {
          await workspaceService.deleteGuestMember(this.member.userId)
        } else {
          await workspaceService.deleteMember(this.member.userId)
        }

        this.$alert(
          this.$t('members.delete.message.successContent'),
          this.$t('members.delete.message.successTitle'),
          {
            confirmButtonText: this.$t('common.confirm'),
            callback: () => this.$emit('deleteGuest'),
          },
        )
      } catch (e) {
        this.errorMessage(e)
      }
    },
    imageSelected(file) {
      if (this.isImageFile(file)) {
        const reader = new FileReader()
        reader.readAsDataURL(file.raw)
        reader.onload = () => {
          this.selectedProfile = reader.result
          this.profileChanged = true
        }
      }
    },
    canEdit(type, role) {
      if (this.canManage(role)) {
        // 일반 계정 일 때 수정 불가
        if (this.isUserTypeUser(type)) return false
        else return true
      }
      return false
    },
    async updateNickname() {
      const form = {
        userId: this.member.userId,
        nickname: this.member.nickname,
      }

      try {
        await workspaceService.updateMembersInfo(form)
        this.$notify.success({
          message: this.$t('members.setting.nickname.success'),
          position: 'bottom-left',
          duration: 2000,
        })
        this.$emit('update:nickname', form.nickname)
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
    async updatePassword() {
      try {
        await workspaceService.changeMembersPassword(
          this.member.userId,
          this.member.password,
        )
        this.$notify.success({
          message: this.$t('members.setting.password.success'),
          position: 'bottom-left',
          duration: 2000,
        })
        this.member.password = ''
      } catch (e) {
        this.$notify.error({
          message: this.$t('members.setting.password.fail') + `\n(${e})`,
          position: 'bottom-left',
          duration: 2000,
        })
      }
    },
  },
}
</script>

<style lang="scss">
#__nuxt .member-setting-account-pane {
  .el-divider {
    margin: 26px 0 20px;
  }
  .el-upload {
    button {
      width: 134px;
      height: 36px;
    }
  }
  &__submit-button {
    @include fontLevel(100);
    line-height: 14px;
    width: 66px;
    height: 38px;
    border-radius: 3px;
  }
  &__image-change-button {
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
