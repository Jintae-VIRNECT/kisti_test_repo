<template>
  <section>
    <div class="setting__title">로컬 녹화 설정</div>
    <div class="setting-horizon-wrapper">
      <figure class="setting__figure">
        <p class="setting__label">최대 녹화 시간</p>
        <r-select
          class="setting__r-selecter"
          v-on:changeValue="setRecLength"
          :options="localRecTimeOpt"
          :value="'value'"
          :text="'text'"
        >
        </r-select>
      </figure>

      <figure class="setting__figure">
        <p class="setting__label">녹화 영상 해상도</p>
        <r-select
          class="setting__r-selecter"
          v-on:changeValue="setRecResolution"
          :options="localRecResOpt"
          :value="'value'"
          :text="'text'"
        >
        </r-select>
      </figure>
    </div>
  </section>
</template>
<script>
import RSelect from 'RemoteSelect'
export default {
  props: {
    defaultRecLength: {
      type: Number,
      default: 5,
    },
    defaultRecordRec: {
      type: String,
      default: '360p',
    },
  },
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
    setRecLength: function(newRecLength) {
      this.$emit('setRecLength', { rec_length: newRecLength })
    },
    setRecResolution: function(newResolution) {
      this.$emit('setRecResolution', { rec_res: newResolution })
    },
  },
}
</script>
