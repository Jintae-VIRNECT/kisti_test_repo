<template>
  <section class="setting-section">
    <div class="setting__title">로컬 녹화 설정</div>
    <div class="setting-horizon-wrapper">
      <figure class="setting__figure">
        <p class="setting__label">최대 녹화 시간</p>
        <r-select
          class="setting__r-selecter"
          @changeValue="setRecLength"
          :options="localRecTimeOpt"
          :value="'value'"
          :text="'text'"
          :defaultValue="localRecordLength"
        >
        </r-select>
      </figure>

      <figure class="setting__figure">
        <p class="setting__label">녹화 영상 해상도</p>
        <r-select
          class="setting__r-selecter"
          @changeValue="setRecResolution"
          :options="localRecResOpt"
          :value="'value'"
          :text="'text'"
          :defaultValue="recordResolution"
        >
        </r-select>
      </figure>
    </div>
  </section>
</template>
<script>
import RSelect from 'RemoteSelect'
import { mapGetters, mapActions } from 'vuex'
export default {
  data() {
    return {
      localRecTimeOpt: [
        {
          value: '5',
          text: '5분',
        },
        {
          value: '10',
          text: '10분',
        },
        {
          value: '15',
          text: '15분',
        },
        {
          value: '30',
          text: '30분',
        },
      ],

      localRecResOpt: [
        {
          value: '360p',
          text: '360p',
        },
        {
          value: '480p',
          text: '480p',
        },
        {
          value: '720p',
          text: '720p',
        },
      ],
    }
  },
  components: {
    RSelect,
  },
  computed: {
    ...mapGetters(['localRecordLength', 'recordResolution']),
  },
  methods: {
    ...mapActions(['setLocalRecordLength', 'setRecordResolution']),
    setRecLength(newRecLength) {
      this.setLocalRecordLength(newRecLength.value)
    },
    setRecResolution(newResolution) {
      this.setRecordResolution(newResolution.value)
    },
  },
  created() {
    const time = localStorage.getItem('recordingTime')
    if (time) {
      this.setLocalRecordLength(time)
    }
    const resolution = localStorage.getItem('recordingResolution')
    if (resolution) {
      this.setRecResolution(resolution)
    }
  },
}
</script>
