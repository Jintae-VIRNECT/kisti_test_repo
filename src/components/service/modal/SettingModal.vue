<template>
  <modal
    :visible.sync="visibleFlag"
    :showClose="true"
    width="60rem"
    height="43.3571rem"
    :beforeClose="beforeClose"
    class="service-setting-modal"
    :title="$t('service.setting_service')"
  >
    <div class="service-setting">
      <section class="service-setting-nav">
        <button
          v-if="isLeader"
          class="service-setting-nav__menu"
          :class="{ active: tabview === 'pointing' }"
          :data-text="$t('service.setting_pointing')"
          @click="tabChange('pointing')"
        >
          {{ $t('service.setting_pointing') }}
        </button>
        <button
          v-if="isLocalRecordEnable"
          class="service-setting-nav__menu"
          :class="{ active: tabview === 'local-record' }"
          :data-text="$t('service.setting_local_record')"
          @click="tabChange('local-record')"
        >
          {{ $t('service.setting_local_record') }}
        </button>
        <button
          v-if="isLeader && useRecording"
          class="service-setting-nav__menu"
          :class="{ active: tabview === 'server-record' }"
          :data-text="$t('service.setting_server_record')"
          @click="tabChange('server-record')"
        >
          {{ $t('service.setting_server_record') }}
        </button>
        <button
          v-if="useTranslate"
          class="service-setting-nav__menu"
          :class="{ active: tabview === 'translate' }"
          :data-text="$t('service.setting_translate')"
          @click="tabChange('translate')"
        >
          {{ $t('service.setting_translate') }}
        </button>
      </section>

      <set-pointing v-if="tabview === 'pointing'"></set-pointing>

      <set-local-record v-if="tabview === 'local-record'"></set-local-record>

      <set-server-record v-if="tabview === 'server-record'"></set-server-record>

      <set-translate v-if="tabview === 'translate'"></set-translate>
    </div>
  </modal>
</template>

<script>
import Modal from 'Modal'

import toastMixin from 'mixins/toast'

import { mapGetters, mapActions } from 'vuex'
import { ROLE } from 'configs/remote.config'

export default {
  name: 'SettingModal',
  mixins: [toastMixin],
  components: {
    Modal,

    SetPointing: () => import('./partials/ServiceSetPointing'),
    SetLocalRecord: () => import('./partials/ServiceSetLocalRecord'),
    SetServerRecord: () => import('./partials/ServiceSetServerRecord'),
    SetTranslate: () => import('./partials/ServiceSetTranslate'),
  },
  data() {
    return {
      visibleFlag: false,

      tabview: '',
    }
  },

  computed: {
    ...mapGetters([
      'modalSetting',
      'useRecording',
      'useLocalRecording',
      'useTranslate',
      'allowLocalRecord',
    ]),

    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
    isLocalRecordEnable() {
      if (!this.useLocalRecording) {
        return false
      } else if (!this.isLeader && this.isSafari) {
        return false
      } else if (!this.isLeader && !this.allowLocalRecord) {
        return false
      } else {
        return true
      }
    },
  },

  watch: {
    modalSetting(flag) {
      this.visibleFlag = flag
    },
    allowLocalRecord(allow) {
      if (this.isLeader) return
      if (allow === false) {
        this.toastDefault(this.$t('service.record_blocked'))

        if (!this.useTranslate) {
          this.showModalSetting(false)
        } else {
          this.tabview = 'translate'
        }
      }
    },
  },
  methods: {
    ...mapActions(['setTranslate', 'showModalSetting']),

    beforeClose() {
      this.showModalSetting(false)
    },

    init() {
      this.$nextTick(() => {
        this.initing = true
      })

      if (this.isLeader) {
        this.tabview = 'pointing'
        return
      }

      if (this.useLocalRecording && this.allowLocalRecord && !this.isSafari) {
        this.tabview = 'local-record'
        return
      }

      if (this.useTranslate) {
        this.tabview = 'translate'
        return
      }

      //설정할 수 있는 기능이 없는 경우
      this.showModalSetting(false)
    },
    tabChange(view) {
      this.tabview = view
    },
  },

  created() {
    this.init()
  },
}
</script>
