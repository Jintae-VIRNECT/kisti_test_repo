<template>
  <section class="setting-section">
    <div class="setting-horizon-wrapper">
      <figure class="setting__figure">
        <div class="setting__figure--wrapper">
          <p class="setting__label">녹화 영상 해상도</p>
          <popover
            placement="right"
            trigger="hover"
            popperClass="setting__custom-popover"
            width="25.4286rem"
          >
            <div slot="reference" class="setting__tooltip--icon"></div>
            <div class="setting__tooltip--body">
              <p class="setting__tooltip--text">
                720p(HD)급이상 해상도 설정 시, PC의 성능에 따라 서비스가
                원활하지 않을 수 있습니다.
              </p>
            </div>
          </popover>
        </div>
        <r-select
          class="setting__r-selecter"
          @changeValue="setRecResolution"
          :options="localRecResOpt"
          :value="'value'"
          :text="'text'"
          :defaultValue="localRecord.resolution"
        >
        </r-select>
      </figure>
    </div>
  </section>
</template>
<script>
import RSelect from 'RemoteSelect'
import Popover from 'Popover'
import { mapGetters, mapActions } from 'vuex'
import { localRecResOpt } from 'utils/recordOptions'
export default {
  name: 'WorkspaceSetResolution',
  data() {
    return {
      localRecResOpt: localRecResOpt,
    }
  },
  components: {
    RSelect,
    Popover,
  },
  computed: {
    ...mapGetters(['localRecord']),
  },
  methods: {
    ...mapActions(['setRecord']),

    setRecResolution(newResolution) {
      this.setRecord({
        interval: newResolution.value,
      })
      this.$localStorage.setRecord('resolution', newResolution.value)
    },
  },
}
</script>
