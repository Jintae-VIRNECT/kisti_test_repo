<template>
  <section class="roominfo-view">
    <p class="roominfo-view__title">
      {{ $t('workspace.info_remote') }}
    </p>
    <div class="roominfo-view__body">
      <template v-if="isLeader">
        <input-row
          type="text"
          :title="$t('workspace.remote_name')"
          :count="20"
          :value.sync="title"
          :valid.sync="titleValid"
          validate="validName"
          :validMessage="titleValidMessage"
          @focusOut="checkEmpty"
          required
          showCount
        ></input-row>
        <input-row
          type="textarea"
          :title="$t('workspace.remote_description')"
          :showCount="true"
          :count="50"
          :value.sync="description"
        ></input-row>
        <input-row type="buttons" :title="$t('button.image_regist')">
          <div>
            <input
              ref="inputImage"
              type="file"
              name="file"
              accept="image/gif,image/jpeg,image/png"
              style="display: none;"
              @change="uploadImage($event)"
            />
            <button class="btn line imageinput regist" @click="imageUpload">
              {{ $t('button.image_regist') }}
            </button>
            <button class="btn line imageinput delete" @click="remove">
              {{ $t('button.image_remove') }}
            </button>
          </div>
        </input-row>
      </template>
      <template v-else>
        <figure class="roominfo-figure">
          <p class="roominfo-figure__title">
            {{ $t('workspace.remote_name') }}
          </p>
          <p class="roominfo-figure__text">{{ title }}</p>
        </figure>
        <figure class="roominfo-figure">
          <p class="roominfo-figure__title">
            {{ $t('workspace.remote_description') }}
          </p>
          <p class="roominfo-figure__text">{{ description }}</p>
        </figure>
      </template>
    </div>
    <div class="roominfo-view__footer">
      <div class="roominfo-view__data" v-if="useOpenRoom">
        <span class="data-title">{{ $t('workspace.info_mode') }}</span>
        <span class="data-value">{{
          isOpenRoom
            ? $t('workspace.info_mode_open')
            : $t('workspace.info_mode_collabo')
        }}</span>
      </div>
      <div class="roominfo-view__data">
        <span class="data-title">{{ $t('workspace.info_remote_date') }}</span>
        <span class="data-value">{{ createdDate }}</span>
      </div>
      <div class="roominfo-view__data" v-if="!isHistory">
        <span class="data-title">{{ $t('workspace.info_remote_start') }}</span>
        <span class="data-value">{{ createdTime }}</span>
      </div>
      <template v-else>
        <div class="roominfo-view__data">
          <span class="data-title">{{
            $t('workspace.info_remote_start_end')
          }}</span>
          <span class="data-value">{{
            `${createdTime} / ${inactiveTime}`
          }}</span>
        </div>
        <div class="roominfo-view__data">
          <span class="data-title">{{
            $t('workspace.info_remote_duration')
          }}</span>
          <span class="data-value">{{ durationTime }}</span>
        </div>
      </template>
      <div class="roominfo-view__button" v-if="isLeader">
        <button class="btn" :disabled="!canSave" @click="saveInfo">
          {{ $t('button.save') }}
        </button>
      </div>
    </div>
  </section>
</template>

<script>
import InputRow from 'InputRow'
import imageMixin from 'mixins/uploadImage'
import { ROOM_STATUS } from 'configs/status.config'
import { mapGetters } from 'vuex'
export default {
  name: 'ModalRoomInfo',
  mixins: [imageMixin],
  components: {
    InputRow,
  },
  props: {
    room: {
      type: Object,
      default: null,
    },
    image: {
      type: String,
      default: '',
    },
    isLeader: {
      type: Boolean,
      default: false,
    },
    isHistory: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      title: '',
      description: '',
      createdDate: '',
      createdTime: '',
      inactiveTime: '',
      durationTime: '',
      titleValid: false,
    }
  },
  computed: {
    ...mapGetters(['useOpenRoom']),
    isOpenRoom() {
      return (
        this.room &&
        this.room.sessionType &&
        this.room.sessionType === ROOM_STATUS.OPEN
      )
    },
    titleValidMessage() {
      if (this.title.length < 2) {
        return this.$t('workspace.remote_name_valid1')
      } else {
        return this.$t('workspace.remote_name_valid2')
      }
    },
    canSave() {
      if (!this.room) return false
      if (this.titleValid) {
        return false
      }
      if (this.title !== this.room.title) {
        return true
      }
      if (this.description !== this.room.description) {
        return true
      }
      if (this.image !== this.room.profile) {
        return true
      }
      return false
    },
  },
  watch: {
    room: {
      handler(room) {
        this.init(room)
      },
      deep: true,
    },
    imageURL(url) {
      if (url === '') {
        // 기본 이미지
        this.$emit('update:image', '')
      } else {
        this.$emit('update:image', url)
      }
    },
  },
  methods: {
    init() {
      this.title = this.room.title
      this.description = this.room.description
      this.imageURL = this.room.profile
      this.createdDate = this.$dayjs(this.room.activeDate + '+00:00').format(
        'YYYY.MM.DD',
      )
      this.createdTime = this.$dayjs(this.room.activeDate + '+00:00').format(
        'HH:mm:ss',
      )
      if (this.isHistory) {
        this.inactiveTime = this.$dayjs(
          this.room.unactiveDate + '+00:00',
        ).format('HH:mm:ss')
        this.durationTime = this.$dayjs(this.room.durationSec * 1000)
          .utc()
          .format('HH:mm:ss')
      }
    },
    remove() {
      if (this.image !== 'default') {
        this.imageRemove()
        this.$emit('update:image', 'default')
      }
    },
    saveInfo() {
      const params = {
        uuid: this.account.uuid,
        title: this.title,
        description: this.description,
        sessionId: this.room.sessionId,
        workspaceId: this.workspace.uuid,
      }
      if (this.room.profile !== this.imageURL) {
        params.image = this.imageFile
      }
      this.$emit('update', params)
    },
    checkEmpty() {
      if (this.title === '') {
        this.title = this.room.title
      }
    },
  },

  /* Lifecycles */
  mounted() {
    if (this.room) {
      this.init()
    }
  },
}
</script>
