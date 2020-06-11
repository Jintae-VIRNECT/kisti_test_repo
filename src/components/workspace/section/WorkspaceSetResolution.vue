<template>
  <section class="setting-section">
    <div class="setting-horizon-wrapper">
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
    ...mapGetters(['recordResolution']),
  },
  methods: {
    ...mapActions(['setRecordResolution']),

    setRecResolution(newResolution) {
      this.setRecordResolution(newResolution.value)
    },
  },
  created() {
    const resolution = localStorage.getItem('recordingResolution')
    if (resolution) {
      this.setRecResolution(resolution)
    }
  },
}
</script>
