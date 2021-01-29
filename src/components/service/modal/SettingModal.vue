<template>
  <modal
    :visible.sync="visibleFlag"
    :showClose="true"
    width="840px"
    height="607px"
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
          class="service-setting-nav__menu"
          :class="{ active: tabview === 'local-record' }"
          :data-text="$t('service.setting_local_record')"
          @click="tabChange('local-record')"
        >
          {{ $t('service.setting_local_record') }}
        </button>
        <button
          class="service-setting-nav__menu"
          :class="{ active: tabview === 'server-record' }"
          :data-text="$t('service.setting_server_record')"
          @click="tabChange('server-record')"
        >
          {{ $t('service.setting_server_record') }}
        </button>
        <button
          class="service-setting-nav__menu"
          :class="{ active: tabview === 'translate' }"
          :data-text="$t('service.setting_translate')"
          @click="tabChange('translate')"
        >
          {{ $t('service.setting_translate') }}
        </button>
      </section>

      <!-- 포인팅 리더 only -->
      <set-pointing v-if="tabview === 'pointing'"></set-pointing>

      <!-- 로컬 녹화 / 로컬녹화중에는 사용 불가능해야함.-->
      <set-local-record v-if="tabview === 'local-record'"></set-local-record>

      <!-- 서버 녹화 관련 옵션 -->
      <set-server-record
        v-if="isLeader && useRecording && tabview === 'server-record'"
      ></set-server-record>

      <!-- 번역 관련 기능 (전체 기능 포함 필요함)-->
      <!-- 서비스 서버 컴패니 코드에서 번역 쓸건지 체크  -->
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

      tabview: 'pointing',
    }
  },

  computed: {
    ...mapGetters(['modalSetting', 'useRecording']),

    isLeader() {
      return this.account.roleType === ROLE.LEADER
    },
  },

  watch: {
    modalSetting(flag) {
      this.visibleFlag = flag
    },
  },
  methods: {
    ...mapActions(['setTranslate', 'showModalSetting']),

    beforeClose() {
      this.showModalSetting(false)
    },

    showToast() {
      this.toastNotify(this.$t('service.setting_save'))
    },
    init() {
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
