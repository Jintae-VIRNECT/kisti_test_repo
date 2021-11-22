<template>
  <el-row>
    <div
      class="activity"
      v-for="(activity, index) in activityList"
      :key="index"
    >
      <el-col :span="4">
        <VirnectThumbnail :size="36" :image="activity.userProfileImage" />
      </el-col>
      <el-col :span="21">
        <dl>
          <dt>
            {{
              $t('projects.info.activity.nickname', {
                nickname: activity.userNickname,
              })
            }}
          </dt>
          <dd>
            {{ activity.message }}
          </dd>
          <span>
            {{ activity.createdDate | localTimeFormat }}
          </span>
        </dl>
        <el-divider />
      </el-col>
    </div>
    <div v-if="!activityList.length">
      <img src="~assets/images/empty/img-content-empty.jpg" />
      <p>{{ $t('projects.info.activity.empty') }}</p>
    </div>
    <infinite-loading
      v-if="activityList.length"
      @infinite="infiniteHandler"
    ></infinite-loading>
  </el-row>
</template>

<script>
import filters from '@/mixins/filters'
import { activityTypes } from '@/models/project/ProjectActivityLog'

export default {
  mixins: [filters],
  props: {
    activityList: Array,
  },
  data() {
    return {
      // 프로젝트의 공유, 편집 관련된 info.
      activityTypes,
    }
  },
  methods: {
    // 유저의 활동 타입에 따라, 맞는 label 값 반환.
    infiniteHandler($state) {
      const activityCount = this.activityList.length
      this.$emit('update:updateProjectActivityLogs')

      setTimeout(() => {
        activityCount !== this.activityList.length
          ? $state.loaded()
          : $state.complete()
      }, 500)
    },
  },
}
</script>
