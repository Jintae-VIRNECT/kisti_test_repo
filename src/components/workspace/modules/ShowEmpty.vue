<template>
  <div class="show-empty" :style="{ 'background-image': image }">
    <p class="show-empty-title">
      {{ title }}
    </p>
    <p class="show-empty-description">
      {{ description }}
    </p>
  </div>
</template>

<script>
export default {
  name: 'ShowEmpry',
  components: {},
  data() {
    return {}
  },
  props: {
    image: {
      type: String,
      default: '',
    },
    title: {
      type: String,
      default: '',
    },
    description: {
      type: String,
      default: '',
    },
  },
  methods: {},

  /* Lifecycles */
  mounted() {},
}
</script>

<style lang="scss" scoped>
.show-empty {
  width: 100%;
  height: 100%;
  background: url(~assets/image/img_user_empty.svg) 50% 35% #29292c no-repeat;
}
.show-empty-title {
  padding-top: 400px;
  color: #fafafa;
  font-size: 24px;
  text-align: center;
}
.show-empty-description {
  color: rgba(#fafafa, 0.5);
  font-weight: 300;
  font-size: 20px;
  text-align: center;
}
</style>
