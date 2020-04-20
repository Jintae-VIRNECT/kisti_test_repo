<template>
  <section>
    <div class="setting__title">로컬 녹화 설정</div>
    <div class="setting-horizon-wrapper">
      <div>
        <div class="setting__label">최대 녹화 시간</div>
        <r-select
          class="setting__r-selecter"
          v-on:changeValue="saveRecLength"
          :options="localRecTimeOpt"
          :value="'value'"
          :text="'text'"
        >
        </r-select>
      </div>

      <div>
        <div class="setting__label">녹화 영상 해상도</div>
        <r-select
          class="setting__r-selecter"
          v-on:changeValue="saveRecResolution"
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
        {
          value: 30,
          text: '30분',
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
    saveRecLength: function(newRecLength) {
      this.$emit('selectedRecLength', { rec_length: newRecLength })
    },
    saveRecResolution: function(newResolution) {
      this.$emit('selectedResolution', { rec_res: newResolution })
    },
  },
}
</script>

<style scoped></style>
