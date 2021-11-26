<template>
  <el-row>
    <dl>
      <dt>{{ $t('projects.info.project.name') }}</dt>
      <dd class="project-uuid">{{ project.uuid }}</dd>
      <dt>{{ $t('projects.info.project.tracking') }}</dt>
      <dd>{{ project.targetType }}</dd>
    </dl>
    <!-- 임시로 모드영역 숨김처리 -->
    <dt v-if="false">{{ $t('projects.info.project.mode') }}</dt>
    <dd v-if="false">
      <ProjectMode :modeList="project.modeList" />
    </dd>
    <!-- 프로젝트 씬 정보 -->
    <dt>{{ $t('projects.info.project.configuration') }}</dt>
    <dl class="gray-row-project">
      <div>
        <dt>{{ $t('projects.info.project.sceneGroup') }}</dt>
        <dd>{{ project.propertySceneGroupTotal }}</dd>
      </div>
      <div>
        <dt>{{ $t('projects.info.project.scene') }}</dt>
        <dd>{{ project.propertySceneTotal }}</dd>
      </div>
      <div>
        <dt>{{ $t('projects.info.project.object') }}</dt>
        <dd>{{ project.propertyObjectTotal }}</dd>
      </div>
    </dl>
    <!-- 프로젝트 파일 크기 정보 -->
    <dl class="row">
      <div>
        <dt>{{ $t('projects.info.project.filesize') }}</dt>
        <dd>{{ project.size | byte2mb }}</dd>
      </div>
      <div>
        <dt>{{ $t('projects.info.project.update') }}</dt>
        <dd>{{ project.createdDate | localTimeFormat }}</dd>
      </div>
    </dl>
    <!-- 프로젝트 공유/편집 정보 -->
    <projectMemberSelect
      v-for="form in forms"
      :key="form.key"
      :selectLabel.sync="form.name"
      :selectOptions.sync="form.selectOptions"
      :memberPermission.sync="form.memberPermission"
      :members="members"
      :selectMembers.sync="form.selectMembers"
      @update:updateProjectAuth="updateProjectAuth"
      @update:updateProjectMember="updateProjectMember"
      @undo="undo"
    ></projectMemberSelect>
  </el-row>
</template>

<script>
import filters from '@/mixins/filters'
import projectService from '@/services/project'

export default {
  mixins: [filters],
  props: {
    project: Object,
    forms: Array,
    members: Array,
  },
  methods: {
    // 공유/편집 Auth 상태변경하는 함수
    async updateProjectAuth(designationType) {
      const succuessMsg = {
        share: this.$t('projects.info.message.updateShareSuccess'),
        edit: this.$t('projects.info.message.updateEditSuccess'),
      }[designationType]
      const errMsg = {
        share: this.$t('projects.info.message.updateShareFail'),
        edit: this.$t('projects.info.message.updateEditFail'),
      }[designationType]
      try {
        const hasPermission = await this.designationMemberTypeUpdate(
          designationType,
        )
        this.isUserHasPermission(hasPermission, succuessMsg, designationType)
      } catch (e) {
        this.$message.error({
          message: errMsg + `<br>(${e})`,
          duration: 2000,
          dangerouslyUseHTMLString: true,
          showClose: true,
        })
      }
    },
    // 공유/편집 지정멤버 상태변경하는 함수
    async updateProjectMember(designationType) {
      // 공유, 편집 form 배열에서 해당되는 타입의 form 객체를 가져옵니다.
      const formObject = this.forms.find(form => form.key === designationType)
      // 지정멤버 권한으로 설정되지 않았다면, 해당 함수를 종료합니다.
      if (formObject.memberPermission !== 'SPECIFIC_MEMBER') return false

      const succuessMsg = {
        share: this.$t('projects.info.message.updateShareMemberSuccess'),
        edit: this.$t('projects.info.message.updateEditMemberSuccess'),
      }[designationType]
      const errMsg = {
        share: this.$t('projects.info.message.updateShareMemberFail'),
        edit: this.$t('projects.info.message.updateEditMemberFail'),
      }[designationType]
      try {
        const hasPermission = await this.designationMemberTypeUpdate(
          designationType,
        )
        this.isUserHasPermission(hasPermission, succuessMsg, designationType)
      } catch (e) {
        this.$message.error({
          message: errMsg + `<br>(${e})`,
          dangerouslyUseHTMLString: true,
          duration: 2000,
          showClose: true,
        })
      }
    },
    /**
     * @description 공유/편집에 관련된 데이터를 서버에 요청하여 수정하는 메소드
     * @param {String} designationType share or edit
     * @returns {Boolean} hasSharePermission
     */
    async designationMemberTypeUpdate(designationType) {
      const formObject = this.forms.find(form => form.key === designationType)
      // 지정멤버 권한 변경시, 선택된 지정멤버 배열이 비었다면 Error를 던집니다.
      if (
        formObject.memberPermission === 'SPECIFIC_MEMBER' &&
        !formObject.selectMembers.length
      ) {
        throw new Error(this.$t('projects.info.message.emptySelectMember'))
      }
      try {
        const form = {
          [designationType]: {
            permission: formObject.memberPermission,
            userList: formObject.selectMembers,
          },
        }
        const { uploaderHasSharePermission } =
          await projectService.updateProject(this.project.uuid, form)
        return uploaderHasSharePermission
      } catch (e) {
        throw new Error(e.message, e.code)
      }
    },
    /**
     * @description 권한 편집을 한 유저가, 현재 프로젝트를 볼 수 있는지 공유 권한체크.
     * @param {Boolean} hasPermission
     * @param {String} succuessMsg
     * @param {String} designationType share or edit
     */
    isUserHasPermission(hasPermission, succuessMsg, designationType) {
      // 공유권한이 없으면, 권한 없다는 알럿과 함께 모달창을 닫는다.
      if (!hasPermission) {
        const hasPermissionMsg = {
          share: this.$t('projects.info.message.sharePermissionRelease'),
          edit: this.$t('projects.info.message.editPermissionRelease'),
        }[designationType]
        this.$message.error({
          message: hasPermissionMsg,
          duration: 2000,
          showClose: true,
        })
        this.$emit('updated')
        this.$emit('update:originalPermissionData', designationType)
        this.$emit('closed')
        // 공유권한이 있으면, 권한 변경이 잘 되었다는 알럿을 보여준다.
      } else {
        this.$message.success({
          message: succuessMsg,
          duration: 2000,
          showClose: true,
        })
        this.$emit('updated')
        this.$emit('update:originalPermissionData', designationType)
      }
    },
    undo(v) {
      this.$emit('undo', v)
    },
  },
}
</script>
