<template>
  <section class="roominfo-view">
    <div>
      <p class="roominfo-view__title">
        협업 정보
      </p>
      <div class="roominfo-view__body">
        <template v-if="leader">
          <input-row
            type="text"
            title="협업 이름"
            :showCount="true"
            :count="20"
            :value.sync="name"
            validMessage="특수 문자는 협업 이름에서 제외시켜주세요."
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
            <p class="roominfo-figure__text">{{ name }}</p>
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
          <span class="data-value">2020.03.05</span>
        </div>
        <div class="roominfo-view__data">
          <span class="data-title">시작 시간</span>
          <span class="data-value">15:20:25</span>
        </div>
        <div class="roominfo-view__button" v-if="leader">
          <button
            class="btn"
            :disabled="!canSave"
            @click="$emit('update', { name, description })"
          >
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
    leader: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      name: '',
      description: '',
    }
  },
  computed: {
    canSave() {
      if (!this.room) return false
      if (this.name !== this.room.title) {
        return true
      }
      if (this.description !== this.room.description) {
        return true
      }
      if (this.image !== this.room.path) {
        return true
      }
      return false
    },
  },
  watch: {
    room: {
      handler(room) {
        this.name = room.title
        this.description = room.description
        this.image = room.path
        this.imageUrl = this.image
      },
      deep: true,
    },
    imageURL(url) {
      if (url === '') {
        // 기본 이미지
        this.$emit(
          'update:image',
          require('assets/image/img_default_group.svg'),
        )
      } else {
        this.$emit('update:image', url)
      }
    },
  },
  methods: {
    remove() {
      this.imageRemove()
      this.$emit('update:image', require('assets/image/img_default_group.svg'))
    },
  },

  /* Lifecycles */
  mounted() {
    if (this.room) {
      this.name = this.room.title
      this.description = this.room.description
      this.image = this.room.path
    }
  },
}
</script>
