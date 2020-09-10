<template>
  <section class="setting-section">
    <div class="setting-section__body first">
      <figure class="setting__figure">
        <div class="setting__figure--wrapper">
          <p class="setting__label">
            {{ $t('workspace.setting_record_resolution') }}
          </p>
          <tooltip
            customClass="tooltip-guide"
            :content="$t('workspace.setting_record_resolution_tooltip')"
            placement="right"
            effect="blue"
          >
            <img
              slot="body"
              class="setting__tooltip--icon"
              src="~assets/image/ic_tool_tip.svg"
            />
          </tooltip>
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
import Tooltip from 'Tooltip'
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
    Tooltip,
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
