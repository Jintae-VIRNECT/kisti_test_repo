<template>
  <div
    class="profile-image"
    :class="customClass"
    :style="{ width: `${size}px`, height: `${size}px` }"
  >
    <img
      class="profile-image__image"
      v-if="image && image.length > 0"
      :src="image"
    />
    <div
      class="profile-image__color"
      v-else
      :style="{ 'background-color': color }"
    >
      <p>{{ groupName.slice(0, 1) }}</p>
    </div>
    <button class="profile-image__button" v-if="btnText.length > 0">
      {{ btnText }}
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
    color: {
      type: String,
      default: '#0f75f5', // ''
    },
    image: {
      type: String,
      default: '',
    },
    groupName: {
      type: String,
      default: '버넥트',
    },
    customClass: {
      type: String,
      default: '',
    },
    btnText: {
      type: String,
      default: '',
    },
  },
  data() {
    return {}
  },
  computed: {},
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss" scoped>
.profile-image {
  position: relative;
  margin: 0 auto;
  overflow: hidden;
  border-radius: 50%;
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
}
.profile-image__button {
  position: absolute;
  bottom: 0;
  width: 100%;
  height: 34px;
  color: #fff;
  background-color: rgba(#000, 0.29);
}
</style>
