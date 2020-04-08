<template>
  <div
    class="profile-image"
    :style="{ width: `${size}px`, height: `${size}px` }"
  >
    <div
      class="profile-image__color"
      v-if="color && color.length > 0"
      :style="{ 'background-color': color }"
    >
      <p>{{ groupName.slice(0, 1) }}</p>
    </div>
    <img class="profile-image__image" v-else :src="img" />
    <button class="profile-image__button">등록</button>
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
    img: {
      type: String,
      default: '',
    },
    groupName: {
      type: String,
      default: '버넥트',
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
