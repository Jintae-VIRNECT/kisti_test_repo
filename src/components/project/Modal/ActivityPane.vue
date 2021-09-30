<template>
  <el-row>
    <div
      class="activity"
      v-for="(activity, index) in activityList"
      :key="index"
    >
      <el-col :span="4">
        <VirnectThumbnail :size="36" :image="activity.img" />
      </el-col>
      <el-col :span="21">
        <dl>
          <dt>
            {{
              $t('projects.info.activity.nickname', {
                nickname: activity.nickname,
              })
            }}
          </dt>
          <dd>
            {{
              $t(`${activityLabel(activity)}`, {
                member: activity.member,
              })
            }}
          </dd>
          <span>
            {{ activity.updated | localTimeFormat }}
          </span>
        </dl>
        <el-divider />
      </el-col>
    </div>
    <div v-if="!activityList.length">
      <img src="~assets/images/empty/img-content-empty.jpg" />
      <p>{{ $t('projects.info.activity.empty') }}</p>
    </div>
  </el-row>
</template>

<script>
import { activityTypes } from '@/models/project/Project'

export default {
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
    activityLabel(activity) {
      return activityTypes.find(a => {
        return a.value === activity.value
      }).label
    },
  },
}
</script>
