<template>
  <modal
    :visible.sync="visibleFlag"
    :showClose="true"
    width="60rem"
    height="43.3571rem"
    :beforeClose="beforeClose"
    class="service-setting-modal"
    :title="$t('환경 설정')"
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
          v-if="allowLocalRecord"
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

import SetLocalRecord from './partials/ServiceSetLocalRecord'
import SetPointing from './partials/ServiceSetPointing'
import SetServerRecord from './partials/ServiceSetServerRecord'
import SetTranslate from './partials/ServiceSetTranslate'

export default {
  name: 'SettingModal',
  mixins: [toastMixin],
  components: {
    Modal,

    SetLocalRecord,
    SetPointing,
    SetServerRecord,
    SetTranslate,
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
      'useTranslate',
      'allowLocalRecord',
    ]),

    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
  },

  watch: {
    modalSetting(flag) {
      this.visibleFlag = flag
    },
    allowLocalRecord(allow) {
      if (allow === false) {
        this.toastDefault(this.$t('service.record_blocked'))
        this.tabview = 'translate'
      }
    },
  },
  methods: {
    ...mapActions(['setTranslate', 'showModalSetting']),

    beforeClose() {
      this.showModalSetting(false)
    },

    init() {
      this.tabview = this.isLeader ? 'pointing' : 'local-record'
      if (!this.allowLocalRecord && !this.isLeader) {
        this.tabview = 'translate'
      }

      this.$nextTick(() => {
        this.initing = true
      })
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
