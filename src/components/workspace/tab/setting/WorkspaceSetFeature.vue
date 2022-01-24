<template>
  <div>
    <section class="setting-section list feature">
      <div v-if="!isMobileSize" class="setting-section__title">
        {{ $t('workspace.setting_camera_control') }}
      </div>
      <div class="setting-section__body horizon">
        <figure class="setting__figure">
          <div class="setting__figure--wrapper">
            <p class="setting__label">
              {{
                isMobileSize
                  ? $t('workspace.setting_camera_restrict')
                  : $t('workspace.setting_camera_restrict_enable')
              }}
            </p>
            <tooltip
              v-if="!isMobileSize"
              customClass="tooltip-guide"
              :content="$t('workspace.setting_camera_restrict_info')"
              :placement="isMobileDevice ? 'bottom' : 'right'"
              effect="blue"
              :guide="true"
            >
              <img
                slot="body"
                guide
                class="setting__tooltip--icon"
                src="~assets/image/ic_tool_tip.svg"
              />
            </tooltip>
          </div>
          <check
            :text="$t('workspace.setting_camera_restrict_enable')"
            :value.sync="useScreenControl"
          ></check>
        </figure>
      </div>
    </section>
  </div>
</template>
<script>
import Check from 'Check'
import Tooltip from 'Tooltip'
import toastMixin from 'mixins/toast'
import { mapActions, mapGetters } from 'vuex'

export default {
  name: 'WorkspaceSetTranslate',
  mixins: [toastMixin],
  components: {
    Check,
    Tooltip,
  },
  data() {
    return {
      useScreenControl: false,
    }
  },
  computed: {
    ...mapGetters(['useScreenStrict']),
  },
  watch: {
    useScreenControl(flag) {
      this.setScreenStrict(flag)
      window.myStorage.setItem('screenStrict', flag)
    },
  },
  methods: {
    ...mapActions(['setScreenStrict']),
  },
  created() {
    this.useScreenControl = this.useScreenStrict
  },
}
</script>
