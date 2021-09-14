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
        <dd>{{ project.updatedDate | localTimeFormat }}</dd>
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
        shared: this.$t('projects.info.message.updateShareSuccess'),
        edited: this.$t('projects.info.message.updateEditSuccess'),
      }[designationType]
      const errMsg = {
        shared: this.$t('projects.info.message.updateShareFail'),
        edited: this.$t('projects.info.message.updateEditFail'),
      }[designationType]
      try {
        // TODO 프로젝트 공유/편집 Auth 상태변경 서비스 연동

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
      const succuessMsg = {
        shared: this.$t('projects.info.message.updateShareMemberSuccess'),
        edited: this.$t('projects.info.message.updateEditMemberSuccess'),
      }[designationType]
      const errMsg = {
        shared: this.$t('projects.info.message.updateShareMemberFail'),
        edited: this.$t('projects.info.message.updateEditMemberFail'),
      }[designationType]
      try {
        // TODO 프로젝트 공유/편집 지정멤버 변경 서비스 연동
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
  },
}
</script>
