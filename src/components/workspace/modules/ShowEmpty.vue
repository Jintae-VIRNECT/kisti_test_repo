<template>
  <div class="show-empty">
    <img class="show-empty__image" :src="image" />
    <p class="show-empty__title">
      {{ title }}
    </p>
    <p class="show-empty__description">
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
  position: relative;
  width: 100%;
  height: 100%;
  background: #29292c;
}
.show-empty__image {
  position: absolute;
  top: 0;
  width: 100%;
  height: 100%;
}
.show-empty__title {
  padding-top: 22%;
  color: #fafafa;
  font-size: 24px;
  text-align: center;
}
.show-empty__description {
  color: rgba(#fafafa, 0.5);
  font-weight: 300;
  font-size: 18px;
  text-align: center;
}
</style>
