<template>
  <section class="workspace-setting-section">
    <div class="workspace-setting-title">녹화 설정</div>

    <div class="workspace-setting-horizon-wrapper">
      <div>
        <div class="workspace-setting-label">로컬 녹화 시간</div>
        <r-select
          class="workspace-setting-r-selecter"
          v-on:changeValue="handleRecTime"
          :options="localRecTimeOpt"
          :value="'value'"
          :text="'text'"
        >
        </r-select>
      </div>

      <div>
        <div class="workspace-setting-label">녹화 영상 해상도</div>
        <r-select
          class="workspace-setting-r-selecter"
          v-on:changeValue="handleRecRes"
          :options="localRecResOpt"
          :value="'value'"
          :text="'text'"
        >
        </r-select>
      </div>
    </div>
  </section>
</template>
<script>
import RSelect from 'RemoteSelect'
export default {
  data: function() {
    return {
      localRecordingTime: '',
      localRecordingResolution: '',

      //데이터 미정
      localRecTimeOpt: [
        {
          value: 5,
          text: '5분',
        },
        {
          value: 10,
          text: '10분',
        },
        {
          value: 15,
          text: '15분',
        },
      ],

      localRecResOpt: [
        {
          value: 720,
          text: '720p',
        },
        {
          value: 1080,
          text: '1080p',
        },
        {
          value: 2080,
          text: '2K',
        },
      ],
    }
  },
  components: {
    RSelect,
  },
  methods: {
    handleRecTime: function(newTime) {
      this.localRecordingTime = newTime
    },
    handleRecRes: function(newRes) {
      this.localRecordingResolution = newRes
    },
  },
}
</script>

<style scoped></style>
