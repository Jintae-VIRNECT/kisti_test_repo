<template>
  <el-row>
    <dl>
      <dt>{{ $t('projects.info.target.targetType') }}</dt>
      <dd>
        <span>{{ targetType2label4project(project.targetType) }}</span>
        <img
          v-if="project.targetInfo.path"
          src="~assets/images/icon/ic-print.svg"
          @click="print(project.targetInfo.path, project.targetInfo.width)"
        />
        <img
          v-if="project.targetInfo.path"
          src="~assets/images/icon/ic-file-download.svg"
          @click="download(project.targetInfo.path, project.name)"
        />
      </dd>
    </dl>

    <dt>{{ $t('projects.info.target.targetSize') }}</dt>
    <dd>
      <!-- 타겟 이미지 정보 -->
      <dl class="gray-row-target">
        <div>
          <dt>{{ $t('projects.info.target.width') }}:</dt>
          <dd>{{ project.targetInfo.width }} cm</dd>
        </div>
        <div>
          <dt>{{ $t('projects.info.target.height') }}:</dt>
          <dd>{{ project.targetInfo.length }} cm</dd>
        </div>
      </dl>
    </dd>
  </el-row>
</template>

<script>
import filters from '@/mixins/filters'
import utils from '@/mixins/utils'

export default {
  mixins: [filters, utils],
  props: {
    project: Object,
  },
}
</script>
