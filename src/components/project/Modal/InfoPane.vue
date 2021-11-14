<template>
  <el-row>
    <dl>
      <dt>{{ $t('projects.info.project.name') }}</dt>
      <dd class="project-uuid">{{ project.uuid }}</dd>
      <dt>{{ $t('projects.info.project.tracking') }}</dt>
      <dd>{{ project.targetType }}</dd>
      <dt>{{ $t('projects.info.project.mode') }}</dt>
      <dd>
        <ProjectMode :modeList="project.modeList" />
      </dd>
    </dl>
    <!-- 프로젝트 씬 정보 -->
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
        await this.designationMemberTypeUpdate(designationType)
        this.$message.success({
          message: succuessMsg,
          duration: 2000,
          showClose: true,
        })
        this.$emit('updated')
      } catch (e) {
        this.$message.error({
          message: errMsg + `\n(${e})`,
          duration: 2000,
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
        await this.designationMemberTypeUpdate(designationType)
        this.$message.success({
          message: succuessMsg,
          duration: 2000,
          showClose: true,
        })
        this.$emit('updated')
      } catch (e) {
        this.$message.error({
          message: errMsg + `\n(${e})`,
          duration: 2000,
          showClose: true,
        })
      }
    },
    // 공유/편집에 관련된 데이터를 서버에 요청하여 수정하는 메소드
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
        await projectService.updateProject(this.project.uuid, form)
      } catch (e) {
        throw new Error(e)
      }
    },
  },
}
</script>
