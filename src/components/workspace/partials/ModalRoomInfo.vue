<template>
  <section class="roominfo-view">
    <div>
      <p class="roominfo-view__title">
        협업 정보
      </p>
      <div class="roominfo-view__body">
        <template v-if="isLeader">
          <input-row
            type="text"
            title="협업 이름"
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
            title="협업 설명"
            :showCount="true"
            :count="50"
            :value.sync="description"
          ></input-row>
          <input-row type="buttons" title="이미지 등록">
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
                이미지 등록
              </button>
              <button class="btn line imageinput delete" @click="remove">
                이미지 삭제
              </button>
            </div>
          </input-row>
        </template>
        <template v-else>
          <figure class="roominfo-figure">
            <p class="roominfo-figure__title">협업 이름</p>
            <p class="roominfo-figure__text">{{ title }}</p>
          </figure>
          <figure class="roominfo-figure">
            <p class="roominfo-figure__title">협업 설명</p>
            <p class="roominfo-figure__text">{{ description }}</p>
          </figure>
        </template>
      </div>
      <div class="roominfo-view__footer">
        <div class="roominfo-view__data">
          <span class="data-title">협업 진행일</span>
          <span class="data-value">{{ createdDate }}</span>
        </div>
        <div class="roominfo-view__data" v-if="!isHistory">
          <span class="data-title">시작 시간</span>
          <span class="data-value">{{ createdTime }}</span>
        </div>
        <template v-else>
          <div class="roominfo-view__data">
            <span class="data-title">시작 / 종료 시간</span>
            <span class="data-value">{{
              `${createdTime} / ${inactiveTime}`
            }}</span>
          </div>
          <div class="roominfo-view__data">
            <span class="data-title">진행시간</span>
            <span class="data-value">{{ durationTime }}</span>
          </div>
        </template>
        <div class="roominfo-view__button" v-if="isLeader">
          <button class="btn" :disabled="!canSave" @click="saveInfo">
            저장하기
          </button>
        </div>
      </div>
    </div>
  </section>
</template>

<script>
import InputRow from 'InputRow'
import imageMixin from 'mixins/uploadImage'
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
    titleValidMessage() {
      if (this.title.length < 2) {
        return '협업 이름은 2자 이상 입력해주세요.'
      } else {
        return '특수 문자는 협업 이름에서 제외시켜주세요.'
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
        this.title = room.title
        this.description = room.description
        // this.imageUrl = room.profile
        this.createdDate = this.$dayjs(room.activeDate).format('YYYY.MM.DD')
        this.createdTime = this.$dayjs(room.activeDate).format('hh:mm:ss')
        if (this.isHistory) {
          this.inactiveTime = this.$dayjs(room.unactiveDate).format('hh:mm:ss')
          this.durationTime = this.$dayjs(this.room.durationSec * 1000)
            .utc()
            .format('HH:mm:ss')
        }
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
    remove() {
      this.imageRemove()
    },
    saveInfo() {
      const params = {
        title: this.title,
        description: this.description,
        sessionId: this.room.sessionId,
        workspaceId: this.workspace.uuid,
      }
      if (this.room.profile !== this.imageUrl) {
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
      this.title = this.room.title
      this.description = this.room.description
      this.imageUrl = this.room.profile
      this.createdDate = this.$dayjs(this.room.activeDate).format('YYYY.MM.DD')
      this.createdTime = this.$dayjs(this.room.activeDate).format('hh:mm:ss')
      if (this.isHistory) {
        this.inactiveTime = this.$dayjs(this.room.unactiveDate).format(
          'hh:mm:ss',
        )
        this.durationTime = this.$dayjs(this.room.durationSec * 1000)
          .utc()
          .format('HH:mm:ss')
      }
    }
  },
}
</script>
