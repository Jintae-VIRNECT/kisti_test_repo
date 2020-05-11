<template>
  <div
    class="profile-image group"
    :class="customClass"
    :style="{ width: `${imageSize}`, height: `${imageSize}` }"
  >
    <img
      v-if="image && image.length > 0"
      class="profile-image__image"
      :src="image"
    />
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
      type: [Number, String],
      default: 120,
    },
    // defaultImage: {
    //   type: String,
    //   default: require('assets/image/img_default_group.svg'),
    // },
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
    imageSize() {
      if (typeof this.size === 'string') {
        return this.size
      } else {
        return this.size + 'px'
      }
    },
    // showImage() {
    //   if (this.image && this.image.length > 0) {
    //     return this.image
    //   } else {
    //     return this.defaultImage
    //   }
    // },
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
.profile-image__image {
  width: 100%;
  height: 100%;
  // background-color: #fff;
  // border-radius: 50%;
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
    background: url(~assets/image/ic_trash.svg) 50%/1.429em no-repeat;
    background-color: rgba($color_darkgray_1000, 0.8);
  }
}
</style>
