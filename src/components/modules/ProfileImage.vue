<template>
  <div
    class="profile-image"
    :class="customClass"
    :style="{ width: `${size}px`, height: `${size}px` }"
  >
    <img class="profile-image__image" :src="showImage" />
    <button v-if="deleteBtn" class="profile-image__button" @click="deleteImage">
      이미지 삭제
    </button>
  </div>
</template>

<script>
export default {
  name: 'ProfileImage',
  components: {},
  props: {
    size: {
      type: Number,
      default: 120,
    },
    defaultImage: {
      type: String,
      default: require('assets/image/img_default_group.svg'),
    },
    image: {
      type: String,
      default: '',
    },
    customClass: {
      type: String,
      default: '',
    },
    deleteBtn: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {}
  },
  computed: {
    showImage() {
      if (this.image && this.image.length > 0) {
        return this.image
      } else {
        return this.defaultImage
      }
    },
  },
  methods: {
    deleteImage() {
      this.$emit('delete')
    },
  },

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss" scoped>
@import '~assets/style/mixin';
.profile-image {
  position: relative;
  margin: 0 auto;
  @include image();
}
.profile-image__color {
  width: 100%;
  height: 100%;
  > p {
    width: 100%;
    height: 100%;
    margin: auto;
    color: rgba(#fdfdfd, 0.7);
    font-size: 52px;
    line-height: 120px;
    text-align: center;
  }
}
.profile-image__image {
  width: 100%;
  height: 100%;
  background-color: #fff;
  border-radius: 50%;
}
.profile-image__button {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  color: #fff;
  background-color: transparent;
  @include ir();
  &:hover {
    background: url(~assets/image/ic_trash.svg) 50%/20px no-repeat;
    background-color: rgba($color_darkgray_1000, 0.8);
  }
}
</style>
