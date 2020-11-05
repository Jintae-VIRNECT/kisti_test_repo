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
          :options="target === 'local' ? localRecResOpt : serverRecResOpt"
          value="value"
          text="text"
          :selectedValue.sync="recordResolution"
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
import { localRecResOpt, serverRecResOpt } from 'utils/recordOptions'
export default {
  name: 'WorkspaceSetResolution',
  data() {
    return {
      localRecResOpt: localRecResOpt,
      serverRecResOpt: serverRecResOpt,
      recordResolution: '',
    }
  },
  props: {
    target: {
      type: String,
      required: true,
    },
  },
  components: {
    RSelect,
    Tooltip,
  },
  computed: {
    ...mapGetters(['localRecord', 'serverRecord']),
  },
  watch: {
    recordResolution(resolution) {
      this.setRecResolution(resolution)
    },
  },
  methods: {
    ...mapActions(['setRecord', 'setServerRecord']),

    setRecResolution(resolution) {
      if (this.target === 'local') {
        this.setRecord({
          resolution: resolution,
        })
        this.$localStorage.setRecord('resolution', resolution)
      } else {
        this.setServerRecord({
          resolution: resolution,
        })
        this.$localStorage.setServerRecord('resolution', resolution)
      }
    },
  },
  created() {
    if (this.target === 'local') {
      if (this.localRecord.resolution) {
        this.recordResolution = this.localRecord.resolution
      }
    } else {
      if (this.serverRecord.resolution) {
        this.recordResolution = this.serverRecord.resolution
      }
    }
  },
}
</script>
